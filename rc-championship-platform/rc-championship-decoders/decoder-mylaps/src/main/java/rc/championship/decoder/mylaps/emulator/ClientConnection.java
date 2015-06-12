package rc.championship.decoder.mylaps.emulator;

import eu.plib.P3tools.data.Const;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import javax.xml.bind.DatatypeConverter;
import rc.championship.api.services.decoder.DecoderMessage;
import rc.championship.api.util.NbLogger;

/**
 *
 * @author Stefan
 */
class ClientConnection implements Runnable {

    private static final NbLogger LOG = NbLogger.getEmulatorLogger(ClientConnection.class);

    private final SocketChannel socketChannel;
    private final Queue<DecoderMessage> toSend = new ArrayDeque<>();
    private final Collection<TransferListener> transferListeners;

    public ClientConnection(SocketChannel socketChannel, Collection<TransferListener> transferListeners) {
        this.socketChannel = socketChannel;
        this.transferListeners = transferListeners;
    }

    @Override
    public void run() {

        try {
            LOG.log(Level.FINER, "start ClientConnectionImpl {0}", this);
            transferListeners.forEach(listerner -> listerner.clientConnected(this));

            ByteBuffer readBuffer = ByteBuffer.allocate(1024);;
            while (socketChannel.isOpen()) {
                DecoderMessage dataToSend = toSend.poll();
                if (dataToSend != null) {
                    ByteBuffer sendBuffer = convertToBytes(dataToSend);
                    sendBuffer.flip();
                    socketChannel.write(sendBuffer);
                    fireDataSentEvent(dataToSend);
                }

                int bytes = socketChannel.read(readBuffer);
                if (bytes > 0) {
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
                        byte[] msgData = new byte[endOfRecord-startOfRecord];
                        readBuffer.get(msgData, startOfRecord, endOfRecord);

                        DecoderMessage msg = P3Converter.convertToMessage(msgData);
                        fireDataReadEvent(msg);
                    }
                }
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, " ClientConnectionImpl failed", ex);
        } finally {
            transferListeners.forEach(listerner -> listerner.clientDisconnected(this));
        }

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
    
    private void fireCorruptDataEvent(Integer from, Integer start, ByteBuffer data) {
        String hexData = DatatypeConverter.printHexBinary(data.array());
        LOG.log(Level.WARNING, "detected corrupt data from:{0} start:{1} data:{2}", new Object[]{from, start, data});
        transferListeners.forEach(listerner -> listerner.recivedCorruptData(from, start, hexData, this));
    }

    private ByteBuffer convertToBytes(DecoderMessage dataToSend) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private List<ByteBuffer> splitToMessage(ByteBuffer readBuffer, int bytes) {
        List<ByteBuffer> result = new LinkedList<>();
        for (int index = 0; index < bytes; index++) {
            if (readBuffer.get(bytes) == Const.EOR) {

                byte[] msg = new byte[255];
                ByteBuffer msgBytes = readBuffer.get(msg, index, index);
                result.add(msgBytes);

            }
        }
        return result;
    }

//    private List<DecoderMessage> parseToMessage(ByteBuffer readBuffer, int bytes) {
//
//        splitToMessage(readBuffer, b) //                        String data = new String(array, Charsets.US_ASCII);                        
//                //                        fireDataReadEvent(data);    
//                ///TODO implement p3 protocol
//
//    }

    private int findPositition(byte findMe, int from, int bytes, ByteBuffer readBuffer) {

        for (int i = from; i < bytes; i++) {
            if(readBuffer.get(i) == findMe){
                return i;
            }
        }
        return -1;
    }

    
}
