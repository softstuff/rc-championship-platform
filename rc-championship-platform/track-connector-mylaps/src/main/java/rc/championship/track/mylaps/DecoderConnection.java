package rc.championship.track.mylaps;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.openide.windows.IOProvider;

/**
 *
 * @author Stefan
 */
public class DecoderConnection {

    private static final Logger LOG = Logger.getLogger(DecoderConnection.class.getName());
    private static final int MAX_NO_OF_MSG_IN_CACHE = 1000;
    private static final int MAX_CACHE_TIMEOUT = 30;

    private final String host;
    private final int port;

    private SocketChannel channel;
    private ConnectionTrigger connectionTrigger;

    private BlockingQueue<ByteBuffer> outgoing;
    private BlockingQueue<ByteBuffer> incoming;
    
    public static interface ConnectionTrigger {
        void connected(DecoderConnection source);
        void disconnected(String reason, DecoderConnection source);
    }

    DecoderConnection(String host, int port) {
        this.host = host;
        this.port = port;
        this.outgoing = new ArrayBlockingQueue<>(MAX_NO_OF_MSG_IN_CACHE);
        this.incoming = new ArrayBlockingQueue<>(MAX_NO_OF_MSG_IN_CACHE);
    }

    public BlockingQueue<ByteBuffer> getOutgoing() {
        return outgoing;
    }

    public BlockingQueue<ByteBuffer> getIncoming() {
        return incoming;
    }

    
    
    public void setConnectionTrigger(ConnectionTrigger connectionTrigger) {
        this.connectionTrigger = connectionTrigger;
    }
    
    
    
    /**
     * Blocks until connection or throws exception
     *
     * @throws IOException
     */
    void connect(ExecutorService service) throws IOException {
        if (channel != null) {
            throw new IllegalStateException("Still has a channel");
        }
        channel = SocketChannel.open();
        SocketAddress endpoint = new InetSocketAddress(host, port);
        channel.connect(endpoint);

        service.execute(new IncomingReader());
        service.execute(new OutgoingWriter());

        connectionTrigger.connected(this);
        // for no blocking connection
//        while(! channel.finishConnect() ){
//    //wait, or do something else...    
//}
    }

    boolean isConnected() {
        return channel.isConnected();
    }
    

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
    public boolean send(ByteBuffer toSend, long timeout, TimeUnit timeUnit) throws IOException, InterruptedException {
        boolean accepted = outgoing.offer(toSend, timeout, timeUnit);
        return accepted;
    }

    public ByteBuffer read(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return incoming.poll(timeout, timeUnit);
    }

    public void disconnect(String reason) {
        if (isConnected()) {
            logToOutput("Disconnecting, %s", reason);
            IOUtils.closeQuietly(channel);
        }
        if(connectionTrigger != null){
            connectionTrigger.disconnected(reason, this);
        }
    }

    private class IncomingReader implements Runnable {

        @Override
        public void run() {

            logToOutput("start incomming reader");
            try {

                if (!isConnected()) {
                    throw new IllegalStateException("channel is not connected on start of reader thread");
                }
                while (isConnected()) {

                    ByteBuffer data = ByteBuffer.allocate(48);
                    int bytes = channel.read(data);

                    if (bytes == 0) {
                        Thread.sleep(100);
                        continue;
                    }

                    if (bytes == -1) {
                        disconnect("input reader got end of the stream msg");
                        break;
                    }

                    ByteBuffer in = data.asReadOnlyBuffer();
                    boolean accepted = incoming.offer(in, MAX_CACHE_TIMEOUT, TimeUnit.SECONDS);

                    if (!accepted) {
                        disconnect("Incomming message queue is full, disconnecting");
                        break;
                    }
                }

            } catch (Exception ex) {
                logExceptionToOutput("unexpected exception", ex);
                disconnect("unexpected exeption");
            } finally {
                logToOutput("exit incomming connection");
                disconnect("exit incomming connection");
            }
        }
    }

    private class OutgoingWriter implements Runnable {

        @Override
        public void run() {

            logToOutput("start message sender");
            try {

                if (!isConnected()) {
                    throw new IllegalStateException("channel is not connected on start of sender thread");
                }
                while (isConnected()) {
                    ByteBuffer msgToSend = outgoing.poll(MAX_CACHE_TIMEOUT, TimeUnit.SECONDS);
                    if (msgToSend == null) {
                        if (outgoing.size() >= MAX_NO_OF_MSG_IN_CACHE) {
                            disconnect("Outgoing message queue is full, disconnecting");
                            break;
                        } else {
                            Thread.sleep(100);
                        }
                    } else {
                        msgToSend.flip();
                        while (msgToSend.hasRemaining()) {
                            channel.write(msgToSend);
                        }
                    }
                }

            } catch (Exception ex) {
                logExceptionToOutput("unexpected exception in sender thread", ex);
                disconnect("unexpected exeption in sender thread");
            } finally {
                logToOutput("exit sender thread");
                disconnect("exit sender thread");
            }
        }
    }

    private void logToOutput(String format, Object... args) {
        IOProvider.getDefault().getIO("MyLaps decoder connection", false).getOut().format(format+"%n", args);
        LOG.log(Level.INFO, format, args);
    }

    private void errorLogToOutput(String format, Object... args) {
        IOProvider.getDefault().getIO("MyLaps decoder connection", false).getErr().format(format+"%n", args);
        LOG.log(Level.WARNING, format, args);
    }

    private void logExceptionToOutput(String msg, Throwable ex) {
        IOProvider.getDefault().getIO("MyLaps decoder connection", false).getErr().println(msg);
        LOG.log(Level.SEVERE, msg, ex);
    }

    @Override
    public String toString() {
        return "DecoderConnection{" + "host=" + host + ", port=" + port + '}';
    }

    
}
