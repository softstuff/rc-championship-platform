package rc.championship.api.services.decoder;

import java.util.EventListener;
import rc.championship.api.model.Decoder;

/**
 *
 * @author Stefan
 */
public interface DecoderListener extends EventListener{
    void connected(Decoder source);
    void disconnected(String reason, Decoder source);
    void recived(DecoderMessage message, Decoder source);
    void transmitted(DecoderMessage message, Decoder source);
    void receivedCorruptData(Integer from, Integer start, String hexData, Decoder source);
}
