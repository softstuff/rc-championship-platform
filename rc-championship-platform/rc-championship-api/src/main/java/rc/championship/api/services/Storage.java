package rc.championship.api.services;

/**
 *
 * @author Stefan
 */
public interface Storage {
    
    String getProperty(String key, String defaultValue) throws StorageException;
    
    boolean setProperty(String key, String value);
    
    void shutdown();
    
}
