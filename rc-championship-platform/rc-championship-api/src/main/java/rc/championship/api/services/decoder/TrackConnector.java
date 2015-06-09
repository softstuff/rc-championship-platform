package rc.championship.api.services.decoder;



import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Stefan
 */
public interface TrackConnector {
    String getConnectorName();
    void start();
    void stop();
    boolean isConnected();
    boolean isStarted();
    void register(ConnectorListener listener);
    void unregister(ConnectorListener listener);
    ByteBuffer readMessage(int timeout, TimeUnit timeUnit) throws InterruptedException;
    boolean sendMessage(ByteBuffer response, int timeout, TimeUnit timeUnit)throws IOException, InterruptedException;
}
