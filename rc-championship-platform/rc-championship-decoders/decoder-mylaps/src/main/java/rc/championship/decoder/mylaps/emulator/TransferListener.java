package rc.championship.decoder.mylaps.emulator;

import rc.championship.api.services.decoder.DecoderMessage;

/**
 *
 * @author Stefan
 */
public interface TransferListener {

    void sent(DecoderMessage data, ClientConnection source);

    void recived(DecoderMessage data, ClientConnection source);

    void recivedCorruptData(Integer from, Integer start, String hexData, ClientConnection source);

    void clientConnected(ClientConnection source);

    void clientDisconnected(ClientConnection source);
}
