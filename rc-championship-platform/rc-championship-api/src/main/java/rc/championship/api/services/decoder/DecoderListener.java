package rc.championship.api.services.decoder;

import rc.championship.api.model.Decoder;

/**
 *
 * @author Stefan
 */
public interface DecoderListener {
    void connected(Decoder source);
    void disconnected(String reason, Decoder source);
    void recived(DecoderMessage message, Decoder source);
    void transmitted(DecoderMessage message, Decoder source);
}
