package rc.championship.decoder.mylaps.client;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.WeakListeners;
import org.openide.windows.IOProvider;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderConnector;
import rc.championship.api.services.decoder.DecoderListener;

public class MyLapsDecoderConnector implements DecoderConnector {

    private static final Logger LOG = Logger.getLogger(MyLapsDecoderConnector.class.getName());
    private static final int KEEP_ALIVE_INTERVALL = 30;
    
    private ScheduledExecutorService executor;
        
    private boolean started;
    private final Decoder decoder;
    private final Set<DecoderListener> listeners = new HashSet<>();
    private DecoderConnection connection;
    
    ScheduledFuture<?> keepAliveSchedule;
    private Runnable keepAliveWatch = new Runnable() {

        @Override
        public void run() {
            if(isStarted() && !isConnected()){
                try {
                    connect();
                } catch (IOException ex) {
                    logExceptionToOutput("unexpected exception while restarting connection", ex);
                }
            }
        }        
    };

    public MyLapsDecoderConnector(Decoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Decoder getDecoder() {
        return decoder;
    }
    
    @Override
    public void register(DecoderListener listener){
        listeners.add(WeakListeners.create(DecoderListener.class, listener, this));
    }
    
    @Override
    public void unregister(DecoderListener listener){
        listeners.remove(listener);
    }

    public boolean isStarted() {
        return started;
    }    
    
    @Override
    public void connect() throws IOException {
            if(isStarted()){
                throw new IllegalStateException("Connection is already started");
            }
            if(connection==null){
                connection = new DecoderConnection(decoder, listeners);
            }
            connection.connect(executor);
            keepAliveSchedule = executor.schedule(keepAliveWatch, KEEP_ALIVE_INTERVALL, TimeUnit.SECONDS);
        
            
            started = true;
            fireConnectedEvent();
    }

    @Override
    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    @Override
    public void disconnect() {
        keepAliveSchedule.cancel(true);
        connection.disconnect("User selected");
        started = false;
        fireDisconnectedEvent("User selected");        
    }
    
    private void fireConnectedEvent(){
        listeners.forEach(listener->{listener.connected(decoder);});
        logToOutput("Connected to %s", decoder);
    }
    
    private void fireDisconnectedEvent(String reason){
        listeners.forEach(listener->{listener.disconnected(reason,decoder);});
        logToOutput("Disconnected from %s", decoder);
    }
    
    private void logToOutput(String format, Object ... args){
        IOProvider.getDefault().getIO("MyLaps decoder connection", false).getOut().format(format, args);
    }

    private void logExceptionToOutput(String msg, Throwable ex) {
        IOProvider.getDefault().getIO("MyLaps decoder connection", false).getErr().println(msg);
        LOG.log(Level.SEVERE, msg, ex);
    }
    
}
