package rc.championship.mylaps.emulator;

/**
 *
 * @author Stefan
 */
public interface TransferListener {
    void sent(String data, ClientConnection source);
        
        void recived(String data, ClientConnection source);

        void clientConnected(ClientConnection connection);

        void clientDisconnected(ClientConnection connection);
}
