package rc.championship.decoder.mylaps.emulator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import org.apache.commons.io.IOUtils;
import rc.championship.api.util.NbLogger;

/**
 *
 * @author Stefan
 */
public class DecoderServer implements Runnable {
    private static final NbLogger LOG = NbLogger.getEmulatorLogger(DecoderServer.class);
    
    private final ServerListener serverListener;
    
    private ServerSocketChannel serverSocketChannel;

    public DecoderServer(ServerListener serverListener) {
        this.serverListener = serverListener;
    }

        @Override
        public void run() {
            try{
                LOG.log(Level.FINER, "client listener thread is running");
                while(isRunning()){
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    serverListener.newClient(socketChannel);                    
                }
                LOG.log(Level.FINER, "client listener thread has stopped");
            } catch (Exception ex) {
                if(isRunning()){
                    serverListener.serverError(ex);
                    LOG.log(Level.SEVERE, "client listener stopped cause exeption", ex);
                }
            } finally {
                IOUtils.closeQuietly(serverSocketChannel);
                serverListener.shutdown();
            }
        }
        
        void start(String hostname, int serverPort) throws IOException{
            LOG.log(Level.FINER, "start listen for client on port {0}", serverPort);
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(hostname, serverPort));
//            serverSocketChannel.socket().bind(new InetSocketAddress("192.168.1.201", serverPort));
        }
        
        void shutdown(){
            LOG.log(Level.FINER, "stop listen for client");
            
            IOUtils.closeQuietly(serverSocketChannel);
            serverSocketChannel = null;
        }

        boolean isRunning() {
            return serverSocketChannel != null && serverSocketChannel.isOpen();                    
        }
    
}
