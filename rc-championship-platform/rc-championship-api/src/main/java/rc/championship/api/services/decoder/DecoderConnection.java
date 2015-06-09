package rc.championship.api.services.decoder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Stefan
 */
public interface DecoderConnection {
    String getConnectorName();
    void start();
    void stop();
    boolean isStarted();
    boolean isConnected();
    DecoderMessage readMessage(int timeout, TimeUnit timeUnit) throws InterruptedException;
    boolean sendMessage(DecoderMessage message, int timeout, TimeUnit timeUnit)throws IOException, InterruptedException;
}
