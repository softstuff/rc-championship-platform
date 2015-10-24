package rc.championship.api.services.decoder;

import java.util.logging.Logger;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import rc.championship.api.model.Decoder;
import rc.championship.api.model.Lap;
import rc.championship.api.model.MessageConverter;
import rc.championship.api.services.Storage;

/**
 *
 * @author Stefan
 */
public class DecoderEventLogger implements DecoderListener {

    protected final Logger log = Logger.getLogger(getClass().getName());
    private Storage storage;
    public DecoderEventLogger() {
        storage = Lookup.getDefault().lookup(Storage.class);
        if(storage == null){
            log.severe("Cannot get a Storage object");
            LifecycleManager.getDefault().exit();
        }
    }

    @Override
    public void connected(Decoder source) {
        StoredMessage toStore = new StoredMessage()
                .time(System.currentTimeMillis())
                .type("connected");
        
        storage.storeMessage(toStore);
    }

    @Override
    public void disconnected(String reason, Decoder source) {
        StoredMessage toStore = new StoredMessage()
                .time(System.currentTimeMillis())
                .type("disconnected");
        
        storage.storeMessage(toStore);
    }

    @Override
    public void recived(DecoderMessage message) {
        StoredMessage toStore = new StoredMessage()
                .time(System.currentTimeMillis())
                .type("recived")
                .type(message.getCommand().name())
                .data(message.getJson());
        
        storage.storeMessage(toStore);
        
        if(message.getCommand() == DecoderMessage.Command.Passing){
            Lap lap = MessageConverter.toLap(message);
            storage.storeLap(lap);
        }
    }

    @Override
    public void transmitted(DecoderMessage message) {
        StoredMessage toStore = new StoredMessage()
                .time(System.currentTimeMillis())
                .type("transmitted")
                .type(message.getCommand().name())
                .data(message.getJson());
        
        storage.storeMessage(toStore);
    }

    @Override
    public void receivedCorruptData(Integer from, Integer start, String hexData, Decoder source) {
        StoredMessage toStore = new StoredMessage()
                .time(System.currentTimeMillis())
                .type("receivedCorruptData")
                .data(String.format("from %d start %d on '%s'", from, start, hexData));
        
        storage.storeMessage(toStore);
    }
    
}
