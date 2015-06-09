package rc.championship.api.services.decoder;

import java.io.OutputStream;

/**
 *
 * @author Stefan
 */
public interface DecoderEmulator {
    
    void register(DecoderListener listener);
    void unregister(DecoderListener listener);
    
    void send(DecoderMessage ... messages);
    
    void startEmulator(String host, int port);
    void stopEmulator();
    
    void play(OutputStream output);
    void stop();
    boolean isPlaying();
    
    void pause();
    void resume();
    boolean isPaused();
    
    
}
