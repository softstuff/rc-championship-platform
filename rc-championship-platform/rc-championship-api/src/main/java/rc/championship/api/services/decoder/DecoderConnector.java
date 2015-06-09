package rc.championship.api.services.decoder;

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
    
    void connect();

    void disconnect();
}
