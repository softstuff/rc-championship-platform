package rc.championship.decoder.mylaps.emulator;

import java.nio.channels.SocketChannel;

/**
 *
 * @author Stefan
 */
public interface ServerListener {

    public void newClient(SocketChannel socketChannel);

    public void serverError(Exception ex);

    public void shutdown();
    
}
