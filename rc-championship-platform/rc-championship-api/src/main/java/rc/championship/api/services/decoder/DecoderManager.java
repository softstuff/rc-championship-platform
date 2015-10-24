package rc.championship.api.services.decoder;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import rc.championship.api.model.Decoder;
import rc.championship.api.model.Lap;
import rc.championship.api.model.MessageConverter;
import rc.championship.api.services.LapManager;

/**
 *
 * @author Stefan
 */
public abstract class DecoderManager {

    public static final String PROP_DECODER_ATTACHED = "PROP_DECODER_ATTACHED";
    public static final String PROP_DECODER_DETACHED = "PROP_DECODER_DETACHED"; 
    public static final String PROP_DECODER_CONNECTED = "PROP_DECODER_CONNECTED"; 
    public static final String PROP_DECODER_DISCONNECTED = "PROP_DECODER_DISCONNECTED"; 
    public static final String PROP_MESSAGE_RECIVED = "PROP_MESSAGE_RECIVED"; 
    public static final String PROP_MESSAGE_TRANSMITTED = "PROP_MESSAGE_TRANSMITTED"; 
    public static final String PROP_RECIVED_CORRUPT_DATA = "PROP_RECIVED_CORRUPT_DATA"; 
    
    
    public static Optional<DecoderPlayer> getDefaultPlayer(Decoder decoder){
        for (DecoderManager impl : getAllDecoderManagers()) {
            if(impl.canHandle(decoder)){
                return Optional.of(impl.getOrCreatePlayer(decoder));
            }
        }
        return Optional.empty();
    }
    
    public static Collection<DecoderPlayer> getAllPlayers(){
        Collection<DecoderPlayer> all = new ArrayList<>();
        for (DecoderManager impl : getAllDecoderManagers()) {
            all.addAll(impl.getActivePlayers());
        }
        return all;
    }
    
    public static Collection<? extends DecoderManager> getAllDecoderManagers(){
        return Lookup.getDefault().lookupAll(DecoderManager.class);
    }
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);   

    protected final Logger log = Logger.getLogger(getClass().getName());
    protected final Map<Decoder,DecoderPlayer> players = new HashMap<>();
    protected final LapManager lapManager;
    protected final ListMultimap<Decoder, DecoderMessage> messages;

    private final DecoderListener decoderOutputPrinter = new DecoderOutputPrinter();
    private final DecoderListener decoderEventLogger = new DecoderEventLogger();
    private final DecoderListener decoderEventDispatcher = new DecoderListener(){

        @Override
    public void connected(Decoder decoder) {
        pcs.firePropertyChange(PROP_DECODER_CONNECTED, null, decoder);
    }

    @Override
    public void disconnected(String reason, Decoder decoder) {
        pcs.firePropertyChange(PROP_DECODER_DISCONNECTED, null, decoder);
    }

    @Override
    public void recived(DecoderMessage message) {
        messages.put(message.getDecoder(), message);
        if(message.getCommand() == DecoderMessage.Command.Passing){
            Lap lap = MessageConverter.toLap(message);
            lapManager.add(lap);
        }
        pcs.firePropertyChange(PROP_MESSAGE_RECIVED, null, message);
    }

    @Override
    public void transmitted(DecoderMessage message) {
        messages.put(message.getDecoder(), message);
        pcs.firePropertyChange(PROP_MESSAGE_TRANSMITTED, null, message);
    }

    @Override
    public void receivedCorruptData(Integer from, Integer start, String hexData, Decoder source) {
        pcs.firePropertyChange(PROP_RECIVED_CORRUPT_DATA, null, source);
    }
        
    };
    
    public DecoderManager() {
        lapManager = Lookup.getDefault().lookup(LapManager.class);
        if(lapManager == null){
            log.severe("Cannot get a LapManager object");
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
    
    public List<DecoderMessage> getCurrentMessages(Decoder decoder) {
        return messages.get(decoder);
    }
    
    public abstract boolean canHandle(Decoder decoder);

    public DecoderPlayer getOrCreatePlayer(Decoder decoder){
        DecoderPlayer player = this.players.get(decoder);
        if(player == null){
            player = createPlayer(decoder);
            player.register(decoderEventDispatcher);
            player.register(decoderOutputPrinter);
            player.register(decoderEventLogger);
        }
        return player;
    }
    
    public abstract DecoderPlayer createPlayer(Decoder decoder);

    public Collection<DecoderPlayer> getActivePlayers(){
        return players.values();
    }
    
}
