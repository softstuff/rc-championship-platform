package rc.championship.storage;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
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
import rc.championship.api.model.Lap;
import rc.championship.api.services.Storage;
import rc.championship.api.services.StorageException;
import rc.championship.api.services.decoder.StoredMessage;

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
        this(System.getProperty("h2.url", "jdbc:h2:~/rc-championship.h2.db"));
    }

    public H2StorageImpl(String url) {
        this.url = url;
        init();
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

    @Override
    public boolean storeMessage(StoredMessage message) {
        try (Connection conn = connectionPool.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement("insert into MessageHistory (time, type, command, data) values (?,?,?,?)", 
                    Statement.RETURN_GENERATED_KEYS)){
                stmt.setLong(1, message.getTime());
                stmt.setString(2, message.getType());
                stmt.setString(3, message.getCommand());
                stmt.setString(4, message.getData());
                boolean stored = stmt.executeUpdate() == 1;
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    message.id(rs.getLong(1));
                }
                return stored;
            }
        } catch (SQLException ex) {
            throw new StorageException("failed to storeMessage "+message, ex);
        }
    }

    @Override
    public boolean storeLap(Lap lap) {
        try (Connection conn = connectionPool.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement("insert into LapHistory (decoder, time, transponder, number, hit, strength,temprature, voltage) values (?,?,?,?,?,?,?,?)", 
                    Statement.RETURN_GENERATED_KEYS)){
                int col = 1;
                stmt.setLong(col++, lap.getDecoderId().get());
                stmt.setTimestamp(col++, new Timestamp(lap.getTime().get().getTime()));
                stmt.setLong(col++, lap.getTransponder().get());
                stmt.setLong(col++, lap.getNumber().get());
                stmt.setLong(col++, lap.getHit().get());
                stmt.setLong(col++, lap.getStrength().get());
                stmt.setDouble(col++, lap.getTemprature().get());
                stmt.setDouble(col++, lap.getVoltage().get());
                boolean stored = stmt.executeUpdate() == 1;
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    lap.setId(Optional.of(rs.getLong(1)));
                }
                return stored;
            }
        } catch (SQLException ex) {
            throw new StorageException("failed to store lap "+lap, ex);
        }
    }
    
    
    
    @NotNull
    @Override
    public List<StoredMessage> getMessagesAfter(long time, boolean exclusive, int rowToFetch) {
        try (Connection conn = connectionPool.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement("select id, time, type, command, data from MessageHistory where time"+(exclusive?">":">=")+"? order by time")){
                stmt.setLong(1, time);
                stmt.setMaxRows(rowToFetch);
                List<StoredMessage> results = new ArrayList<>(rowToFetch);
                try(ResultSet rs = stmt.executeQuery()) {
                    while(rs.next()){
                        results.add(parseStoredMessage(rs));
                    }
                }
                return results;
            }
        } catch (SQLException ex) {
            throw new StorageException("failed to getMessagesAfter "+ex.getMessage(), ex);
        }
    }
    
    @Override
    public List<StoredMessage> getMessagesBefore(long time, boolean exclusive, int rowToFetch) {
        try (Connection conn = connectionPool.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement("select id, time, type, command, data from MessageHistory where time"+(exclusive?"<":"<=")+"? order by time desc ")){
                stmt.setLong(1, time);
                stmt.setMaxRows(rowToFetch);
                List<StoredMessage> results = new ArrayList<>(rowToFetch);
                try(ResultSet rs = stmt.executeQuery()) {
                    while(rs.next()){
                        results.add(parseStoredMessage(rs));
                    }
                }
                return results;
            }
        } catch (SQLException ex) {
            throw new StorageException("failed to getMessagesBefore "+ex.getMessage(), ex);
        }
    }
    
    @Override
    public List<StoredMessage> getMessagesPageAfter(long id, int rowToFetch) {
        try (Connection conn = connectionPool.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement("select id, time, type, command, data from MessageHistory where id>? order by id")){
                stmt.setLong(1, id);
                stmt.setMaxRows(rowToFetch);
                List<StoredMessage> results = new ArrayList<>(rowToFetch);
                try(ResultSet rs = stmt.executeQuery()) {
                    while(rs.next()){
                        results.add(parseStoredMessage(rs));
                    }
                }
                return results;
            }
        } catch (SQLException ex) {
            throw new StorageException("failed to getMessagesBefore "+ex.getMessage(), ex);
        }
    }
    
    @Override
    public List<StoredMessage> getMessagesPageBefore(long fromId, int rowToFetch) {
        try (Connection conn = connectionPool.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement("select id, time, type, command, data from MessageHistory where id < ? order by id desc")){
                stmt.setLong(1, fromId);
                stmt.setMaxRows(rowToFetch);
                List<StoredMessage> results = new ArrayList<>(rowToFetch);
                try(ResultSet rs = stmt.executeQuery()) {
                    while(rs.next()){
                        results.add(parseStoredMessage(rs));
                    }
                }
                return results;
            }
        } catch (SQLException ex) {
            throw new StorageException("failed to getMessagesBefore "+ex.getMessage(), ex);
        }
    }

    private StoredMessage parseStoredMessage(ResultSet rs) throws SQLException {
        StoredMessage msg = new StoredMessage()
                .id(rs.getLong("id"))
                .time(rs.getLong("time"))
                .type(rs.getString("type"))
                .command(rs.getString("command"))
                .data(rs.getString("data"));
        return msg;
    }
 
    // LiquiBase data types for H2
//    protected DataType getDataType(String columnTypeString, Boolean autoIncrement, String dataTypeName, String precision, String additionalInformation) {
//    // Translate type to database-specific type, if possible
//    DataType returnTypeName = null;
//    if (dataTypeName.equalsIgnoreCase("BIGINT")) {
//        returnTypeName = getBigIntType();
//    } else if (dataTypeName.equalsIgnoreCase("NUMBER") || dataTypeName.equalsIgnoreCase("NUMERIC")) {
//        returnTypeName = getNumberType();
//    } else if (dataTypeName.equalsIgnoreCase("BLOB")) {
//        returnTypeName = getBlobType();
//    } else if (dataTypeName.equalsIgnoreCase("BOOLEAN")) {
//        returnTypeName = getBooleanType();
//    } else if (dataTypeName.equalsIgnoreCase("CHAR")) {
//        returnTypeName = getCharType();
//    } else if (dataTypeName.equalsIgnoreCase("CLOB")) {
//        returnTypeName = getClobType();
//    } else if (dataTypeName.equalsIgnoreCase("CURRENCY")) {
//        returnTypeName = getCurrencyType();
//    } else if (dataTypeName.equalsIgnoreCase("DATE") || dataTypeName.equalsIgnoreCase(getDateType().getDataTypeName())) {
//        returnTypeName = getDateType();
//    } else if (dataTypeName.equalsIgnoreCase("DATETIME") || dataTypeName.equalsIgnoreCase(getDateTimeType().getDataTypeName())) {
//        returnTypeName = getDateTimeType();
//    } else if (dataTypeName.equalsIgnoreCase("DOUBLE")) {
//        returnTypeName = getDoubleType();
//    } else if (dataTypeName.equalsIgnoreCase("FLOAT")) {
//        returnTypeName = getFloatType();
//    } else if (dataTypeName.equalsIgnoreCase("INT")) {
//        returnTypeName = getIntType();
//    } else if (dataTypeName.equalsIgnoreCase("INTEGER")) {
//        returnTypeName = getIntType();
//    } else if (dataTypeName.equalsIgnoreCase("LONGBLOB")) {
//        returnTypeName = getLongBlobType();
//    } else if (dataTypeName.equalsIgnoreCase("LONGVARBINARY")) {
//        returnTypeName = getBlobType();
//    } else if (dataTypeName.equalsIgnoreCase("LONGVARCHAR")) {
//        returnTypeName = getClobType();
//    } else if (dataTypeName.equalsIgnoreCase("SMALLINT")) {
//        returnTypeName = getSmallIntType();
//    } else if (dataTypeName.equalsIgnoreCase("TEXT")) {
//        returnTypeName = getClobType();
//    } else if (dataTypeName.equalsIgnoreCase("TIME") || dataTypeName.equalsIgnoreCase(getTimeType().getDataTypeName())) {
//        returnTypeName = getTimeType();
//    } else if (dataTypeName.toUpperCase().contains("TIMESTAMP")) {
//        returnTypeName = getDateTimeType();
//    } else if (dataTypeName.equalsIgnoreCase("TINYINT")) {
//        returnTypeName = getTinyIntType();
//    } else if (dataTypeName.equalsIgnoreCase("UUID")) {
//        returnTypeName = getUUIDType();
//    } else if (dataTypeName.equalsIgnoreCase("VARCHAR")) {
//        returnTypeName = getVarcharType();
//    } else if (dataTypeName.equalsIgnoreCase("NVARCHAR")) {
//        returnTypeName = getNVarcharType();
//    } else {
//        return new CustomType(columnTypeString,0,2);
//    }

}
