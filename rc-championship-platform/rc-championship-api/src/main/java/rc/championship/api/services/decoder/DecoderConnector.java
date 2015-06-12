package rc.championship.api.services.decoder;

import java.io.IOException;
import rc.championship.api.model.Decoder;

/**
 *
 * @author Stefan
 */
public interface DecoderConnector {    
    
    Decoder getDecoder();
    
    void register(DecoderListener listener);
    
    void unregister(DecoderListener listener);
    
    boolean isConnected();
    
    void connect() throws IOException;

    void disconnect();
}
