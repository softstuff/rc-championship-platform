package rc.championship.storage;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import org.h2.jdbcx.JdbcConnectionPool;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rc.championship.api.services.Storage;
import rc.championship.api.services.StorageException;

/**
 *
 * @author Stefan
 */
@ServiceProvider(service = Storage.class)
public class H2StorageImpl implements Storage {

    private static final Logger LOG = LoggerFactory.getLogger(H2StorageImpl.class);
    
    private final String url;
    private JdbcConnectionPool connectionPool;

    public H2StorageImpl() {
        url = System.getProperty("h2.url", "jdbc:h2:~/rc-championship.h2.db");
    }

    public H2StorageImpl(String url) {
        this.url = url;
    }
    
    
    String getUrl() {
        return url;
    }

    private void init(){
        if(connectionPool == null){
            LOG.debug("Init H2 database with url: {}", url);
            upgrade();
            connectionPool = JdbcConnectionPool.create(getUrl(), "sa", "sa");
        }
    }

    
    
    @Override
    public String getProperty(String key, String defaultValue)  {
        init();
        
        try (Connection conn = connectionPool.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement("select val from properties where key=?")){
                stmt.setString(1, key);
                try(ResultSet rs = stmt.executeQuery()) {
                    if(rs.next()){
                        return rs.getString("val");
                    }
                }
            }
        } catch (SQLException ex) {
            throw new StorageException("failed to query for property "+key, ex);
        }
        return defaultValue;
    }

    @Override
    public boolean setProperty(String key, String value) {
        init();
        if(value == null){
            try (Connection conn = connectionPool.getConnection()) {
                try(PreparedStatement stmt = conn.prepareStatement("delete from properties where key=?")){
                    stmt.setString(1, key);
                    return stmt.executeUpdate() == 1;
                }
            } catch (SQLException ex) {
                throw new StorageException("failed to delete for property"+key+" "+value, ex);
            }
        } else {
            boolean isPersisted = getProperty(key, null) != null;
            String sql = isPersisted ? "update properties set(val=?) where key=?" : "insert into properties (val,key) values(?,?)";
            try (Connection conn = connectionPool.getConnection()) {
                try(PreparedStatement stmt = conn.prepareStatement(sql)){
                    stmt.setString(1, value);
                    stmt.setString(2, key);
                    return stmt.executeUpdate() == 1;
                }
            } catch (SQLException ex) {
                throw new StorageException("failed to persist for property"+key+" "+value, ex);
            }
        }
    }
    

    void upgrade() {
        try{
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(getUrl(), "sa", "sa");

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(conn));

            String pathToChangelog = Paths.get(getClass().getResource("db/changelog/db.changelog-master.xml").toURI()).toAbsolutePath().toString();
//            FileSystems.getDefault().provider().getPath(uri).toAbsolutePath().toString()
            Liquibase liquibase = new Liquibase(pathToChangelog,
                    new FileSystemResourceAccessor(), database);
            liquibase.update((Contexts)null);
        } catch(ClassNotFoundException | SQLException | LiquibaseException | URISyntaxException ex){
            throw new StorageException("Failed to upgrade h2 database "+getUrl()+", caused by: "+ex.getMessage(), ex);
        }
    }

    @Override
    public void shutdown() {
        if (connectionPool != null) {
            connectionPool.dispose();
            connectionPool = null;
        }
    }

}
