package rc.championship.mylaps.emulator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Stefan
 */
public class P3DecoderEmulator {
    private static final Logger LOG = Logger.getLogger(P3DecoderEmulator.class.getName());
    
    private final Executor executor = Executors.newCachedThreadPool();
    private ClientListenerService clientListenerService;
    private final List<TransferListener> transferListeners = new CopyOnWriteArrayList<>();
    private final List<ClientConnection> clients = new CopyOnWriteArrayList<>();
    
    public void registerListener(TransferListener transferListener) {
        if(!transferListeners.contains(transferListener)){
            transferListeners.add(transferListener);
            LOG.log(Level.FINER, "registerListener {0}", transferListener);
        }
    }
    
    public void unregisterListener(TransferListener transferListener) {
        transferListeners.remove(transferListener);
        LOG.log(Level.FINER, "unregisterListener {0}", transferListener);
    }
        
    public void start(int port) throws IOException{
        if(clientListenerService!=null) {
            throw new IllegalStateException("server is already running");
        }
        LOG.log(Level.FINER, "start");
        
        clientListenerService = new ClientListenerService();
        clientListenerService.start(port);
        executor.execute(clientListenerService);
        
    }

    boolean isRunning() {
        return clientListenerService != null && clientListenerService.isRunning();
    }

    void stop() {
        clientListenerService.stop();
        clientListenerService = null;
    }
    
    private class ClientConnectionImpl implements Runnable, ClientConnection {
        final SocketChannel socketChannel;
        Queue<String> toSend = new ArrayDeque<>();

        public ClientConnectionImpl(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }
        
        @Override
        public void run() {
            
            try {
                LOG.log(Level.FINER, "start ClientConnectionImpl {0}", this);
                transferListeners.forEach(listerner-> listerner.clientConnected(this));
                
                while(socketChannel.isOpen()){
                    String dataToSend = toSend.poll();
                    if(dataToSend != null) {
                        byte[] parseHexBinary = DatatypeConverter.parseHexBinary(dataToSend);
                        ByteBuffer sendByffer = ByteBuffer.allocate(100);
                        sendByffer.put(parseHexBinary);
                        sendByffer.flip();
                        socketChannel.write(sendByffer);
                        fireDataSentEvent(dataToSend);
                    }
                    ByteBuffer readBuffer = ByteBuffer.allocate(100);
                    int bytes = socketChannel.read(readBuffer);
                    if(bytes>0){
                        byte[] array = readBuffer.array();
//                        String data = new String(array, Charsets.US_ASCII);                        
//                        fireDataReadEvent(data);    
                        ///TODO implement p3 protocol
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, " ClientConnectionImpl failed", ex);
            } finally {
                transferListeners.forEach(listerner-> listerner.clientDisconnected(this));
            }
            
        }
        public void send(String data){
            toSend.offer(data);
        }

        private void fireDataSentEvent(String data) {
            LOG.log(Level.FINER, "got a message to send {0} from {1}", new Object[]{data, this});
            transferListeners.forEach(listerner-> listerner.sent(data, this));
        }

        private void fireDataReadEvent(String data) {
            LOG.log(Level.FINER, "recived a message {0} from {1}", new Object[]{data, this});
            transferListeners.forEach(listerner-> listerner.recived(data, this));
        }
    }
    private class ClientListenerService implements Runnable {
        ServerSocketChannel serverSocketChannel;

        @Override
        public void run() {
            try{
                LOG.log(Level.FINER, "client listener thread is running");
                while(serverSocketChannel != null && serverSocketChannel.isOpen()){
                    SocketChannel socketChannel = 
                            serverSocketChannel.accept();
                    ClientConnectionImpl clientConnection = new ClientConnectionImpl(socketChannel);
                    clients.add(clientConnection);
                    executor.execute(clientConnection);
                }
                LOG.log(Level.FINER, "client listener thread has stopped");
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "client listener stopped cause exeption", ex);
            } 
        }
        
        void start(int serverPort) throws IOException{
            LOG.log(Level.FINER, "start listen for client on port {0}", serverPort);
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress("127.0.0.1", serverPort));
//            serverSocketChannel.socket().bind(new InetSocketAddress("192.168.1.201", serverPort));
        }
        
        void stop(){
            LOG.log(Level.FINER, "stop listen for client");
            IOUtils.closeQuietly(serverSocketChannel);
            serverSocketChannel = null;
        }

        private boolean isRunning() {
            return serverSocketChannel != null && serverSocketChannel.isOpen();                    
        }
        
    }
    
}
