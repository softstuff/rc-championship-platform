package rc.championship.api.services;


import java.util.List;
import javax.validation.constraints.NotNull;
import rc.championship.api.model.Lap;
import rc.championship.api.services.decoder.StoredMessage;

/**
 *
 * @author Stefan
 */
public interface Storage {
    
    String getProperty(@NotNull String key, String defaultValue) throws StorageException;
    
    boolean setProperty(@NotNull String key, String value);
    
    void shutdown();

    public boolean storeMessage(@NotNull StoredMessage message);
    
    public boolean storeLap(@NotNull Lap lap);

    @NotNull List<StoredMessage> getMessagesAfter(long time, boolean exclusive, int rowToFetch);

    @NotNull List<StoredMessage> getMessagesBefore(long time, boolean exclusive, int rowToFetch);

    @NotNull List<StoredMessage> getMessagesPageAfter(long id, int rowToFetch);

    @NotNull List<StoredMessage> getMessagesPageBefore(long fromId, int rowToFetch);

    
}
