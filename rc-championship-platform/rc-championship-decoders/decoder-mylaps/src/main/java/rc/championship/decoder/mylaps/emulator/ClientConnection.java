package rc.championship.decoder.mylaps.emulator;

import eu.plib.P3tools.data.Const;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;
import rc.championship.api.services.decoder.DecoderMessage;
import rc.championship.api.util.NbLogger;

/**
 *
 * @author Stefan
 */
class ClientConnection {

    private static final NbLogger LOG = NbLogger.getEmulatorLogger(ClientConnection.class);

    private final SocketChannel socketChannel;
    private final Queue<DecoderMessage> toSend = new ArrayDeque<>();
    private final Collection<TransferListener> transferListeners;
    private final P3Converter converter = new P3Converter();
    
    private Runnable sender = new Runnable(){

        @Override
        public void run() {
            try{
                while (socketChannel.isOpen()) {
                    DecoderMessage dataToSend = toSend.poll();
                    if (dataToSend == null) {
                        Thread.sleep(200);
                        continue;
                    }
                    
                    ByteBuffer sendBuffer = converter.convertToBytes(dataToSend);
//                    sendBuffer.flip();
                    socketChannel.write(sendBuffer);
                    fireDataSentEvent(dataToSend);

                }
            } catch (Exception ex) {
                if(socketChannel.isConnected()){
                    LOG.log(Level.SEVERE, " ClientConnectionImpl incomming failed, %s", ex.getMessage());
                }
            } finally {
                transferListeners.forEach(listerner -> listerner.clientDisconnected(ClientConnection.this));
            }
        }
    };

        
    private Runnable reader = new Runnable(){

    @Override
    public void run() {

        try {
            LOG.log(Level.FINER, "start ClientConnectionImpl {0}", this);
            transferListeners.forEach(listerner -> listerner.clientConnected(ClientConnection.this));

            ByteBuffer readBuffer = ByteBuffer.allocate(1024);;
            while (socketChannel.isOpen()) {
                
                int bytes = socketChannel.read(readBuffer);
                if(bytes <= 0){
                    Thread.sleep(200);
                    continue;
                }
                
                int from = 0;
                while (true) {
                    int startOfRecord = findPositition(Const.SOR, from, bytes, readBuffer);
                    if (startOfRecord <= 0) {
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
                    
                    if(startOfRecord != from){

                    }
                    readBuffer.flip();
                    byte[] msgData = new byte[endOfRecord-startOfRecord+1];
                    readBuffer.get(msgData, startOfRecord, endOfRecord+1);
                    readBuffer.flip();

                    DecoderMessage msg = P3Converter.convertToMessage(msgData);
                    fireDataReadEvent(msg);
                }
            }
        } catch (Exception ex) {
            if(socketChannel.isConnected()){
                LOG.log(Level.SEVERE, " ClientConnectionImpl outgoing failed", ex);
            }
        } finally {
            transferListeners.forEach(listerner -> listerner.clientDisconnected(ClientConnection.this));
        }

    }
};
    
    
    public ClientConnection(SocketChannel socketChannel, Collection<TransferListener> transferListeners) {
        this.socketChannel = socketChannel;
        this.transferListeners = transferListeners;
    }

    public void send(DecoderMessage data) {
        toSend.offer(data);
    }

    private void fireDataSentEvent(DecoderMessage data) {
        LOG.log(Level.FINER, "got a message to send {0} from {1}", new Object[]{data, this});
        transferListeners.forEach(listerner -> listerner.sent(data, this));
    }

    private void fireDataReadEvent(DecoderMessage data) {
        LOG.log(Level.FINER, "recived a message {0} from {1}", new Object[]{data, this});
        transferListeners.forEach(listerner -> listerner.recived(data, this));
    }
    
    
    private int findPositition(byte findMe, int from, int bytes, ByteBuffer readBuffer) {

        for (int i = from; i < bytes; i++) {
            if(readBuffer.get(i) == findMe){
                return i;
            }
        }
        return -1;
    }

    void startThreads(Executor executor) {
        executor.execute(sender);
        executor.execute(reader);
    }

    void close(){
        IOUtils.closeQuietly(socketChannel);
    }
    
}
