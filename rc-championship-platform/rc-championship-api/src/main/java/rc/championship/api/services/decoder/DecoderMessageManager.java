package rc.championship.api.services.decoder;

import java.beans.PropertyChangeListener;
import java.util.List;
import rc.championship.api.model.Decoder;

/**
 *
 * @author Stefan
 */
public interface DecoderMessageManager {
        
    public static final String PROP_DECODER_ATTACHED = "PROP_DECODER_ATTACHED";
    public static final String PROP_DECODER_DETACHED = "PROP_DECODER_DETACHED"; 
    public static final String PROP_DECODER_CONNECTED = "PROP_DECODER_CONNECTED"; 
    public static final String PROP_DECODER_DISCONNECTED = "PROP_DECODER_DISCONNECTED"; 
    public static final String PROP_MESSAGE_RECIVED = "PROP_MESSAGE_RECIVED"; 
    public static final String PROP_MESSAGE_TRANSMITTED = "PROP_MESSAGE_TRANSMITTED"; 
    public static final String PROP_RECIVED_CORRUPT_DATA = "PROP_RECIVED_CORRUPT_DATA"; 
    
    public void attach(Decoder decoder);
    public void detach(Decoder decoder);
    
    public void addPropertyChangeListener(PropertyChangeListener listener);
    public void removePropertyChangeListener(PropertyChangeListener listener);

    public List<DecoderMessage> getCurrentMessages(Decoder decoder);
    public List<Decoder> getAttacedDecoders();
    
}
