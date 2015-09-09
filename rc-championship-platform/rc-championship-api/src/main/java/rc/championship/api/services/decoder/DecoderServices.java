package rc.championship.api.services.decoder;

import java.util.List;
import java.util.prefs.BackingStoreException;
import rc.championship.api.model.Decoder;

/**
 *
 * @author Stefan
 */
public interface DecoderServices {
    
    List<Decoder> getDecoders() throws BackingStoreException;
    
    void store(List<Decoder> decoders) throws BackingStoreException;

    public void remove(Decoder decoder) throws BackingStoreException;
    
}
