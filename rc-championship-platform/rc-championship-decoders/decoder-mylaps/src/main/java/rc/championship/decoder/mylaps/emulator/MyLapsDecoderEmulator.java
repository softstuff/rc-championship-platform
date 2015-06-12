package rc.championship.decoder.mylaps.emulator;

import java.io.OutputStream;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.openide.util.WeakListeners;
import org.openide.windows.IOProvider;
import rc.championship.api.services.decoder.DecoderEmulator;
import rc.championship.api.services.decoder.DecoderListener;
import rc.championship.api.services.decoder.DecoderMessage;
import rc.championship.api.util.NbLogger;


public class MyLapsDecoderEmulator implements DecoderEmulator  {

    private static final NbLogger LOG = NbLogger.getEmulatorLogger(DecoderServer.class);
    
    private boolean playing;
    private boolean paused;
        
    private final Executor executor = Executors.newCachedThreadPool();
    
    private DecoderServer decoderServer;
    private final List<TransferListener> transferListeners = new CopyOnWriteArrayList<>();
    private final List<ClientConnection> clients = new CopyOnWriteArrayList<>();
    private final List<DecoderListener> listeners = new CopyOnWriteArrayList<>();
    
    private final TransferListener transferListener = new TransferListener() {

        @Override
        public void sent(DecoderMessage data, ClientConnection source) {
            LOG.log(Level.FINE, "sent %s", data);
        }

        @Override
        public void recived(DecoderMessage data, ClientConnection source) {
            LOG.log(Level.FINE, "recived %s", data);
        }

        @Override
        public void recivedCorruptData(Integer from, Integer start, String hexData, ClientConnection source) {
            LOG.log(Level.FINE, "recivedCorruptData from:%d start:%d %s", hexData);
        }

        @Override
        public void clientConnected(ClientConnection connection) {
            LOG.log(Level.FINE, "clientConnected");
        }

        @Override
        public void clientDisconnected(ClientConnection connection) {
            LOG.log(Level.FINE, "clientDisconnected");
        }

    };
    
    private final ServerListener serverListener = new ServerListener() {

            @Override
            public void newClient(SocketChannel socketChannel) {
                ClientConnection clientConnection = new ClientConnection(socketChannel, transferListeners);
                clients.add(clientConnection);
                executor.execute(clientConnection);
                log("new client connected %s", clientConnection);
            }

            @Override
            public void serverError(Exception ex) {
                log("emulator server crashed %s", ex.getMessage());
                
            }

            @Override
            public void shutdown() {
                log("emulator shutdown");
            }
        };
    
    
    @Override
    public void register(DecoderListener listener) {
        log("register");
        listeners.add(WeakListeners.create(DecoderListener.class, listener, this));
    }

    @Override
    public void unregister(DecoderListener listener) {
        log("unregister");
        listeners.remove(listener);
    }

    @Override
    public void send(DecoderMessage... messages) {
        log("send");
    }

    @Override
    public boolean isStarted() {
        log("isStarted");
        return decoderServer.isRunning();
    }

    @Override
    public void startDecoder(String host, int port) {
        log("startEmulator");
        if(decoderServer != null && decoderServer.isRunning()){
            throw new IllegalStateException("emulatorServer is running");
        }
        decoderServer = new DecoderServer(serverListener);
        executor.execute(decoderServer);
    }

    @Override
    public void stopDecoder() {
        log("stopEmulator");
        decoderServer.shutdown();        
    }

    @Override
    public void play(OutputStream output) {
        if(!isStarted()){
            throw new IllegalStateException("Server is not started");
        }
        if(clients.isEmpty()){
            throw new IllegalStateException("No clients is conncted");
        }
        log("play");
        playing = true;
    }

    @Override
    public void stop() {
        log("stop");
        playing = false;
    }

    @Override
    public boolean isPlaying() {
        log("isPlaying");
        return playing;
    }

    @Override
    public void pause() {
        log("pause");
        paused = true;
    }

    @Override
    public void resume() {
        log("resume");
        paused = false;
    }

    @Override
    public boolean isPaused() {
        log("isPaused");
        return paused;
    }
    
    private void log(String format, Object ... args){
        String msg = String.format(format, args);        
        IOProvider.getDefault().getIO("MyLaps emulator", false).getOut().println(msg);        
    }

}
