package rc.championship.decoder.mylaps;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.openide.util.WeakListeners;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderListener;
import rc.championship.api.services.decoder.DecoderMessage;
import rc.championship.api.services.decoder.DecoderOutputPrinter;
import rc.championship.api.services.decoder.DecoderPlayer;

/**
 *
 * @author Stefan
 */
class MyLapsDecoderPlayer implements DecoderPlayer{

    private static final Logger LOG = Logger.getLogger(MyLapsDecoderPlayer.class.getName());
        
    private ScheduledExecutorService executor;        
    private final Decoder decoder;
    private final Set<DecoderListener> listeners = new HashSet<>();
    private DecoderConnection connection;
    
    
    
    
    public MyLapsDecoderPlayer(Decoder decoder) {
        this.decoder = decoder;
        executor = Executors.newScheduledThreadPool(10);
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
    
    @Override
    public void connect() throws IOException {
            if(isConnected()){
                throw new IllegalStateException("Connection is already started");
            }
            if(connection==null){
                connection = new LiveDecoderConnection(decoder, listeners);
            }
            connection.connect(executor);       
            
//            fireConnectedEvent();
    }

    @Override
    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    @Override
    public void disconnect(String reason) {
        if(isConnected()){
            connection.disconnect(reason);
        }
    }
    
    @Override
    public void send(DecoderMessage msg, long timeout, TimeUnit timeUnit) throws IOException, InterruptedException {
        connection.send(msg, timeout, timeUnit);
    }
    

    @Override
    public void play(File file) throws IOException {
        DecoderOutputPrinter.info(decoder,"Start playback of file: ", file.getAbsolutePath() );
        if(isConnected()){
            disconnect("Prepare for playing file");
        }
        connection = new FileStreamingDecoderConnection(file, listeners);
        connection.connect(executor);
        
    }

    @Override
    public boolean isPlaying() {
        return connection instanceof FileStreamingDecoderConnection && connection.isConnected();
    }

    
    
}
