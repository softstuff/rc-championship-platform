package rc.championship.api.services.decoder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
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

    void send(DecoderMessage msg, long timeout, TimeUnit timeUnit) throws IOException, InterruptedException;
}
