package rc.championship.api.services.decoder;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import org.openide.util.Lookup;
import rc.championship.api.model.Decoder;
import rc.championship.api.model.Lap;
import rc.championship.api.model.MessageConverter;
import rc.championship.api.services.LapManager;

/**
 *
 * @author Stefan
 */
public class DecoderMessageManager {
        
    private final LapManager lapManager;
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private final ListMultimap<Decoder, DecoderMessage> messages = ArrayListMultimap.create();

    private final DecoderListener decoderListener = new DecoderListener() {

        @Override
        public void connected(Decoder source) {
        }

        @Override
        public void disconnected(String reason, Decoder source) {
        }

        @Override
        public void recived(DecoderMessage message, Decoder source) {
            messages.put(source, message);
            if(message.getCommand() == DecoderMessage.Command.Passing){
                Lap lap = MessageConverter.toLap(message);
                lapManager.add(lap);
            }
        }

        @Override
        public void transmitted(DecoderMessage message, Decoder source) {
            messages.put(source, message);
        }

        @Override
        public void receivedCorruptData(Integer from, Integer start, String hexData, Decoder source) {
        }
    };
    
            
    public DecoderMessageManager() {
        this.lapManager = Lookup.getDefault().lookup(LapManager.class);
    }
    
    
    public void attach(Decoder decoder){
        decoder.register(decoderListener);
    }
    
    public void detach(Decoder decoder){
        decoder.unregister(decoderListener);
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
}
