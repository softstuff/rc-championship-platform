package rc.championship.mylaps.emulator;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stefan
 */
public class EmulatorEngine implements TransferListener {
    private static final Logger LOG = Logger.getLogger(EmulatorEngine.class.getName());
    
    @Override
    public void sent(String data, ClientConnection source) {
        LOG.log(Level.FINE, "sent {0}", data);
    }

    @Override
    public void recived(String data, ClientConnection source) {
        LOG.log(Level.FINE, "recived {0}", data);
    }

    @Override
    public void clientConnected(ClientConnection connection) {
        LOG.log(Level.FINE, "clientConnected");
        String data = createInitMsg();
        connection.send(data);
    }

    @Override
    public void clientDisconnected(ClientConnection connection) {
        LOG.log(Level.FINE, "clientDisconnected");
    }

    private String createInitMsg() {
        return null;
    }
    
}
