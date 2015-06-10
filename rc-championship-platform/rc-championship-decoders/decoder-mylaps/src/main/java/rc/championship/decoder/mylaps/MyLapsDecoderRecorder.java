package rc.championship.decoder.mylaps;

import java.io.OutputStream;
import java.util.logging.Logger;
import rc.championship.api.services.decoder.DecoderConnection;
import rc.championship.api.services.decoder.DecoderRecorder;
import rc.championship.api.services.decoder.DecoderRecorderFilter;

/**
 *
 * @author Stefan
 */
public class MyLapsDecoderRecorder implements DecoderRecorder {

    private Logger log = Logger.getLogger(getClass().getName());
    private boolean recording;
    @Override
    public void startRecording(OutputStream stream, DecoderConnection connection, DecoderRecorderFilter recivedFilter, DecoderRecorderFilter transmittedFilter) {
        recording = true;
        log.info("startRecording");
    }

    @Override
    public void stopRecording() {
        recording = false;
        log.info("stopRecording");
    }

    @Override
    public boolean isRecording() {
        return recording;
    }
    
}
