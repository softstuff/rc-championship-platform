package rc.championship.api.services.decoder;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import rc.championship.api.model.Decoder;

/**
 *
 * @author Stefan
 */
public interface DecoderServices {
    public static final String PROP_DECODER_LIST_CHANGED = "PROP_DECODER_LIST_CHANGED";
    
    List<Decoder> getDecoders() throws BackingStoreException;
    
    void store(List<Decoder> decoders) throws BackingStoreException;

    public void remove(Decoder decoder) throws BackingStoreException;

    public Optional<Decoder> getDecoders(String decoderId) throws BackingStoreException;

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);
    
}
