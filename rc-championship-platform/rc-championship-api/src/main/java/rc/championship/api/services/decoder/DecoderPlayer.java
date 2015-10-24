package rc.championship.api.services.decoder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import rc.championship.api.model.Decoder;

/**
 *
 * @author Stefan
 */
public interface DecoderPlayer {
    
    Decoder getDecoder();
    
    void register(DecoderListener listener);
    
    void unregister(DecoderListener listener);
    
    boolean isConnected();
    
    void connect() throws IOException;

    void disconnect(String reason);

    void send(DecoderMessage msg, long timeout, TimeUnit timeUnit) throws IOException, InterruptedException;
    
    void play(File file) throws IOException;
        
    boolean isPlaying();
        
    
}
