package rc.championship.decoder.mylaps;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import rc.championship.api.services.decoder.DecoderMessage;

/**
 *
 * @author Stefan
 */
public interface DecoderConnection {

    //    public void setConnectionTrigger(ConnectionTrigger connectionTrigger) {
    //        this.connectionTrigger = connectionTrigger;
    //    }
    /**
     * Blocks until connection or throws exception
     *
     * @throws IOException
     */
    void connect(ExecutorService service) throws IOException;

    void disconnect(String reason);

    boolean isConnected();

    /**
     * Will block if outgoing queue is full
     *
     * @param toSend message to send
     * @param timeout amount
     * @param timeUnit time unit
     * @return true if the message accepted on send queue
     * @throws IOException
     * @throws InterruptedException
     */
    boolean send(DecoderMessage toSend, long timeout, TimeUnit timeUnit) throws IOException, InterruptedException;
    
}
