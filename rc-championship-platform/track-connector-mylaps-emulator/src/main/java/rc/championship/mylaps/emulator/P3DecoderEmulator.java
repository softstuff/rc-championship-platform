package rc.championship.mylaps.emulator;

import java.io.File;
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
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;
import rc.championship.api.util.NbLogger;

/**
 *
 * @author Stefan
 */
public class P3DecoderEmulator {
    
    private static final NbLogger LOG = NbLogger.getEmulatorLogger(P3DecoderEmulator.class);
    
    private final Executor executor = Executors.newCachedThreadPool();
    private ClientListenerService clientListenerService;
    private final List<TransferListener> transferListeners = new CopyOnWriteArrayList<>();
    private final List<ClientConnection> clients = new CopyOnWriteArrayList<>();
    private boolean paused;
    private File emulatorFile;
    
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
        
    public void start(String hostname, int port) throws IOException{
        if(clientListenerService!=null) {
            throw new IllegalStateException("server is already running");
        }
        LOG.log(Level.FINER, "start");
        
        clientListenerService = new ClientListenerService();
        clientListenerService.start(hostname, port);
        executor.execute(clientListenerService);
        
    }

    boolean isRunning() {
        return clientListenerService != null && clientListenerService.isRunning();
    }

    void stop() {
        clientListenerService.stop();
        clientListenerService = null;
    }

    void play(File file) {
        LOG.log(Level.FINE, "play %s", file.getAbsoluteFile());
        this.emulatorFile = file;
        ///TODO start playing file
    }

    boolean isPlaying() {
        return emulatorFile != null;
    }

    void stopPlaying() {
        LOG.log(Level.FINE, "stopPlaying %s", emulatorFile.getAbsoluteFile());
        ///TODO stop playing file
        emulatorFile = null;
    }

    boolean isPaused() {
        return isPlaying() && paused;
    }

    void resume() {
        LOG.log(Level.FINE, "resume playing %s", emulatorFile.getAbsoluteFile());
        ///TODO stop pausing play file
        paused = false;
    }

    void pause() {
        LOG.log(Level.FINE, "pause playing %s", emulatorFile.getAbsoluteFile());
        ///TODO start pausing play file
        paused = true;
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
                if(clientListenerService != null && clientListenerService.isRunning()){
                    LOG.log(Level.SEVERE, "client listener stopped cause exeption", ex);
                }
            } 
        }
        
        void start(String hostname, int serverPort) throws IOException{
            LOG.log(Level.FINER, "start listen for client on port {0}", serverPort);
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(hostname, serverPort));
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
