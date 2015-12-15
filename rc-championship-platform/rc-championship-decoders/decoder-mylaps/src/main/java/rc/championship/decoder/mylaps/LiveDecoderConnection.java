package rc.championship.decoder.mylaps;

import eu.plib.P3tools.data.Const;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderListener;
import rc.championship.api.services.decoder.DecoderMessage;

/**
 *
 * @author Stefan
 */
public class LiveDecoderConnection implements DecoderConnection {

    private static final Logger LOG = Logger.getLogger(LiveDecoderConnection.class.getName());
    private static final int MAX_NO_OF_MSG_IN_CACHE = 1000;
    private static final int MAX_CACHE_TIMEOUT = 30;

    private final Decoder decoder;

    private SocketChannel channel;
//    private ConnectionTrigger connectionTrigger;
    private final Collection<DecoderListener> listeners;

    private BlockingQueue<DecoderMessage> outgoing;
    private BlockingQueue<DecoderMessage> incoming;

//    public static interface ConnectionTrigger {
//
//        void connected(DecoderConnection source);
//
//        void disconnected(String reason, DecoderConnection source);
//    }

    LiveDecoderConnection(Decoder decoder, Collection<DecoderListener> listeners) {
        this.decoder = decoder;
        this.outgoing = new ArrayBlockingQueue<>(MAX_NO_OF_MSG_IN_CACHE);
        this.incoming = new ArrayBlockingQueue<>(MAX_NO_OF_MSG_IN_CACHE);
        this.listeners = listeners;
    }

    public BlockingQueue<DecoderMessage> getOutgoing() {
        return outgoing;
    }

    public BlockingQueue<DecoderMessage> getIncoming() {
        return incoming;
    }

//    public void setConnectionTrigger(ConnectionTrigger connectionTrigger) {
//        this.connectionTrigger = connectionTrigger;
//    }

    /**
     * Blocks until connection or throws exception
     *
     * @throws IOException
     */
    @Override
    public void connect(ExecutorService service) throws IOException {
        if (channel != null) {
            channel.close();
        }
        
        channel = SocketChannel.open();
        SocketAddress endpoint = new InetSocketAddress(decoder.getHost(), decoder.getPort());
        channel.connect(endpoint);

        service.execute(new IncomingReader());
        service.execute(new OutgoingWriter());

//        if(connectionTrigger!=null){
//            connectionTrigger.connected(this);
//        }
        // for no blocking connection
//        while(! channel.finishConnect() ){
//    //wait, or do something else...    
//}
    }

    @Override
    public boolean isConnected() {
        return channel != null && channel.isConnected();
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
    @Override
    public boolean send(DecoderMessage toSend, long timeout, TimeUnit timeUnit) throws IOException, InterruptedException {
        boolean accepted = outgoing.offer(toSend, timeout, timeUnit);
        return accepted;
    }

    public DecoderMessage read(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return incoming.poll(timeout, timeUnit);
    }

    @Override
    public void disconnect(String reason) {
        if (isConnected()) {
            SocketChannel old = channel;
            channel = null;
            logToOutput("Disconnecting, %s", reason);
            IOUtils.closeQuietly(old);
            listeners.forEach(listerner -> listerner.disconnected(reason, decoder));
        }
//        if (connectionTrigger != null) {
//            connectionTrigger.disconnected(reason, this);
//        }
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

                    processBuffer(data, bytes);

//                    ByteBuffer in = data.asReadOnlyBuffer();
//                    boolean accepted = incoming.offer(in, MAX_CACHE_TIMEOUT, TimeUnit.SECONDS);
//
//                    if (!accepted) {
//                        disconnect("Incomming message queue is full, disconnecting");
//                        break;
//                    }
                }

            } catch (Exception ex) {
                if(channel != null){
                    logExceptionToOutput("unexpected exception", ex);
                    disconnect("unexpected exeption");
                }
            } finally {
                logToOutput("exit incomming connection");
                disconnect("exit incomming connection");
            }
        }

    }

    void processBuffer(ByteBuffer readBuffer, int bytes) {

        int from = 0;
        while (from<bytes) {
            int startOfRecord = findPositition(Const.SOR, from, bytes, readBuffer);
            if (startOfRecord < 0) {
                String hexBinary = DatatypeConverter.printHexBinary(readBuffer.array());
                LOG.log(Level.WARNING, "failed to find start of message: %s", hexBinary);
                continue;
            }
            int endOfRecord = findPositition(Const.EOR, from, bytes, readBuffer);
            if (endOfRecord <= 0) {
                String hexBinary = DatatypeConverter.printHexBinary(readBuffer.array());
                LOG.log(Level.WARNING, "failed to find end of message: %s", hexBinary);
                continue;
            }
            if (startOfRecord != from) {

            }
            endOfRecord +=1;
            readBuffer.flip();
            byte[] msgData = new byte[endOfRecord - startOfRecord];
            readBuffer.get(msgData, startOfRecord, endOfRecord);
            
            from = endOfRecord+1;
//            readBuffer.flip();
            DecoderMessage msg = P3Converter.convertToMessage(msgData);
            msg.setDecoder(decoder);
            fireDataReadEvent(msg);
        }
    }
    
    
    private void fireDataReadEvent(DecoderMessage data) {
        LOG.log(Level.FINER, "recived a message {0} from {1}", new Object[]{data, this});
        
        listeners.forEach(listerner -> listerner.recived(data));
    }
    
    private void fireCorruptDataEvent(Integer from, Integer start, ByteBuffer data) {
        String hexData = DatatypeConverter.printHexBinary(data.array());
        LOG.log(Level.WARNING, "detected corrupt data from:{0} start:{1} data:{2}", new Object[]{from, start, data});
        listeners.forEach(listerner -> listerner.receivedCorruptData(from, start, hexData, decoder));
    }
    
    private int findPositition(byte findMe, int from, int bytes, ByteBuffer readBuffer) {

        for (int i = from; i < bytes; i++) {
            if(readBuffer.get(i) == findMe){
                return i;
            }
        }
        return -1;
    }

    private class OutgoingWriter implements Runnable {

        @Override
        public void run() {

            
            logToOutput("start message sender");
            try {
                P3Converter converter = new P3Converter();

                if (!isConnected()) {
                    throw new IllegalStateException("channel is not connected on start of sender thread");
                }
                while (isConnected()) {
                    DecoderMessage msgToSend = outgoing.poll(MAX_CACHE_TIMEOUT, TimeUnit.SECONDS);
                    if (msgToSend == null) {
                        if (outgoing.size() >= MAX_NO_OF_MSG_IN_CACHE) {
                            disconnect("Outgoing message queue is full, disconnecting");
                            break;
                        } else {
                            Thread.sleep(100);
                        }
                    } else {
                        ByteBuffer data = converter.convertToBytes(msgToSend);
                        data.flip();
                        while (data.hasRemaining() && isConnected()) {
                            channel.write(data);
                        }
                    }
                }

            } catch (Exception ex) {
                if(channel != null){
                    logExceptionToOutput("unexpected exception in sender thread", ex);
                    disconnect("unexpected exeption in sender thread");
                }
            } finally {
                logToOutput("exit sender thread");
                disconnect("exit sender thread");
            }
        }

    }

    private void logToOutput(String format, Object... args) {
//        IOProvider.getDefault().getIO("MyLaps decoder connection", false).getOut().format(format + "%n", args);
        LOG.log(Level.INFO, format, args);
    }

    private void errorLogToOutput(String format, Object... args) {
//        IOProvider.getDefault().getIO("MyLaps decoder connection", false).getErr().format(format + "%n", args);
        LOG.log(Level.WARNING, format, args);
    }

    private void logExceptionToOutput(String msg, Throwable ex) {
//        IOProvider.getDefault().getIO("MyLaps decoder connection", false).getErr().println(msg);
        LOG.log(Level.SEVERE, msg, ex);
    }

    @Override
    public String toString() {
        return "DecoderConnection " + decoder;
    }

}
