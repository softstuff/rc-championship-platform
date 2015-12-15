package rc.championship.api.services.decoder;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Stefan
 * @deprecated 
 */
public interface DecoderEmulator {
    
    void register(DecoderListener listener);
    void unregister(DecoderListener listener);
    
    void send(DecoderMessage ... messages);
    
    public boolean isStarted();
    void startDecoder(String host, int port) throws IOException;
    void stopDecoder();
    
    void play(File file);
    void stopPlaying();
    boolean isPlaying();
    
    void pause();
    void resume();
    boolean isPaused();

    
    
    
}
