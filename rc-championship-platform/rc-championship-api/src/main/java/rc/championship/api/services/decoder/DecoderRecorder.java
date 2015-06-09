package rc.championship.api.services.decoder;
import java.io.OutputStream;

/**
 * Represents a way to record messages to and from the decoder
 */
public interface DecoderRecorder {
    
    void startRecording(OutputStream recording, DecoderConnection connection, DecoderRecorderFilter recivedFilter, DecoderRecorderFilter transmittedFilter);
    
    void stopRecording();
    
    boolean isRecording();
}
