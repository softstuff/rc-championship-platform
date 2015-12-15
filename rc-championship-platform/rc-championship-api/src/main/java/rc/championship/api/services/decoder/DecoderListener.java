package rc.championship.api.services.decoder;

import java.io.File;
import java.util.EventListener;
import rc.championship.api.model.Decoder;

/**
 *
 * @author Stefan
 */
public interface DecoderListener extends EventListener{
    void connected(Decoder source);
    void disconnected(String reason, Decoder source);
    void recived(DecoderMessage message);
    void transmitted(DecoderMessage message);
    void receivedCorruptData(Integer from, Integer start, String hexData, Decoder source);

    public void playbackEnded(Decoder source, File file);

    public void playbackStarted(Decoder source, File file);
}
