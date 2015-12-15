package rc.championship.api.services.decoder;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import javax.validation.constraints.NotNull;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import rc.championship.api.model.Decoder;
import rc.championship.api.model.Lap;
import rc.championship.api.model.MessageConverter;
import rc.championship.api.services.LapManager;
import rc.championship.api.services.Storage;

/**
 *
 * @author Stefan
 */
@ServiceProvider(service = DecoderManager.class)
public class DecoderManager {

    public static final String PROP_DECODER_ATTACHED = "PROP_DECODER_ATTACHED";
    public static final String PROP_DECODER_DETACHED = "PROP_DECODER_DETACHED"; 
    public static final String PROP_DECODER_CONNECTED = "PROP_DECODER_CONNECTED"; 
    public static final String PROP_DECODER_DISCONNECTED = "PROP_DECODER_DISCONNECTED"; 
    public static final String PROP_MESSAGE_RECIVED = "PROP_MESSAGE_RECIVED"; 
    public static final String PROP_MESSAGE_TRANSMITTED = "PROP_MESSAGE_TRANSMITTED"; 
    public static final String PROP_RECIVED_CORRUPT_DATA = "PROP_RECIVED_CORRUPT_DATA"; 
    
    
    public static Optional<DecoderPlayer> getDefaultPlayer(Decoder decoder){
        DecoderManager manager = getDefaultDecoderManager();
        return manager.getOrCreatePlayer(decoder);
    }
        
    private static Collection<? extends DecoderPlayerFactory> getAllDecoderPlayerFactories(){
        return Lookup.getDefault().lookupAll(DecoderPlayerFactory.class);
    }
    
    public static DecoderManager getDefaultDecoderManager(){
        return Lookup.getDefault().lookup(DecoderManager.class);
    }
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);   

    protected final Logger log = Logger.getLogger(getClass().getName());
    protected final Map<Decoder,DecoderPlayer> players = new HashMap<>();
    protected final LapManager lapManager;
    private Storage storage;
    protected final ListMultimap<Decoder, DecoderMessage> messages;

    private final DecoderListener decoderOutputPrinter = new DecoderOutputPrinter();
    private final DecoderListener decoderEventDispatcher = new DecoderListener(){

        @Override
    public void connected(Decoder decoder) {
        StoredMessage toStore = new StoredMessage()
                .time(System.currentTimeMillis())
                .type("connected");
        
        storage.storeMessage(toStore);
        pcs.firePropertyChange(PROP_DECODER_CONNECTED, null, decoder);
    }

    @Override
    public void disconnected(String reason, Decoder decoder) {
        StoredMessage toStore = new StoredMessage()
                .time(System.currentTimeMillis())
                .type("disconnected");
        
        storage.storeMessage(toStore);
        pcs.firePropertyChange(PROP_DECODER_DISCONNECTED, null, decoder);
    }

    @Override
    public void recived(DecoderMessage message) {
        messages.put(message.getDecoder(), message);
        StoredMessage toStore = new StoredMessage()
                .time(System.currentTimeMillis())
                .type("recived")
                .type(message.getCommand().name())
                .data(message.getJson());
        
        storage.storeMessage(toStore);
        
        if(message.getCommand() == DecoderMessage.Command.Passing){
            Lap lap = MessageConverter.toLap(message);
            storage.storeLap(lap);
            lapManager.add(lap);
        }
        pcs.firePropertyChange(PROP_MESSAGE_RECIVED, null, message);
    }

    @Override
    public void transmitted(DecoderMessage message) {
        messages.put(message.getDecoder(), message);
        StoredMessage toStore = new StoredMessage()
                .time(System.currentTimeMillis())
                .type("transmitted")
                .type(message.getCommand().name())
                .data(message.getJson());
        
        storage.storeMessage(toStore);
        pcs.firePropertyChange(PROP_MESSAGE_TRANSMITTED, null, message);
    }

    @Override
    public void receivedCorruptData(Integer from, Integer start, String hexData, Decoder source) {
        StoredMessage toStore = new StoredMessage()
                .time(System.currentTimeMillis())
                .type("receivedCorruptData")
                .data(String.format("from %d start %d on '%s'", from, start, hexData));
        
        storage.storeMessage(toStore);
        pcs.firePropertyChange(PROP_RECIVED_CORRUPT_DATA, null, source);
    }

        @Override
        public void playbackEnded(Decoder source, File file) {
        }

        @Override
        public void playbackStarted(Decoder source, File file) {
        }
    
        
    };
    
    public DecoderManager() {
        lapManager = Lookup.getDefault().lookup(LapManager.class);
        if(lapManager == null){
            log.severe("Cannot get a LapManager object");
            LifecycleManager.getDefault().exit();
        }
        storage = Lookup.getDefault().lookup(Storage.class);
        if(storage == null){
            log.severe("Cannot get a Storage object");
            LifecycleManager.getDefault().exit();
        }
        this.messages = ArrayListMultimap.create();
    }
        
    public void addPropertyChangeListener(PropertyChangeListener listener){
        pcs.addPropertyChangeListener(listener);
    }    
        
    public void removePropertyChangeListener(PropertyChangeListener listener){
        pcs.removePropertyChangeListener(listener);
    }
    
    public List<DecoderMessage> getCurrentMessages(@NotNull Decoder decoder) {
        return messages.get(decoder);
    }
    
    @NotNull
    public Optional<DecoderPlayer> getOrCreatePlayer(@NotNull Decoder decoder){
        DecoderPlayer player = this.players.get(decoder);
        if(player == null){
            Optional<DecoderPlayer> opt = createPlayer(decoder);
            if(opt.isPresent()){
                player = opt.get();
                player.register(decoderEventDispatcher);
                player.register(decoderOutputPrinter);
                return Optional.of(player);
            }
            return Optional.empty();
        }
        return Optional.of(player);
    }
    
    @NotNull
    public Optional<DecoderPlayer> createPlayer(@NotNull Decoder decoder) {
        for (DecoderPlayerFactory player : getAllDecoderPlayerFactories()) {
            if(player.canHandle(decoder)){
                return Optional.of(player.createPlayer(decoder));
            }
        }
        return Optional.empty();
    }

    public Collection<DecoderPlayer> getActivePlayers(){
        return players.values();
    }
    
}
