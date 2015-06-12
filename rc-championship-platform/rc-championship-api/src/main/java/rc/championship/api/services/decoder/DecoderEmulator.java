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
    
    public boolean isStarted();
    void startDecoder(String host, int port);
    void stopDecoder();
    
    void play(OutputStream output);
    void stop();
    boolean isPlaying();
    
    void pause();
    void resume();
    boolean isPaused();

    
    
    
}
