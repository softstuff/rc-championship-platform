package rc.championship.track.mylaps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.windows.IOProvider;
import rc.championship.api.model.Transponder;
import rc.championship.api.services.ConnectorListener;
import rc.championship.api.services.TrackConnector;



public class MylapsTrackConnector implements TrackConnector {

    private static final Logger LOG = Logger.getLogger(MylapsTrackConnector.class.getName());
    private ScheduledExecutorService executor;
    
    private final Set<ConnectorListener> clients;
    private List<Transponder> transponders;
    private boolean started;
    private boolean connected;

    private DecoderConnection connection;
    private String host = "localhost";
    private int port = 5403;
    
    private class KeepAliveWatch implements Runnable {

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
        
    }
   
    

    public MylapsTrackConnector(String host, int port) {
        this.host = host;
        this.port = port;
        clients = new HashSet<>();
        transponders = new ArrayList<>();
    }


    @Override
    public String getConnectorName() {
        return "Mylaps Track Connector";
    }

    @Override
    public void register(ConnectorListener listener) {
        clients.add(listener);
    }

    @Override
    public void deregister(ConnectorListener listener) {
        clients.remove(listener);
    }
    
    @Override
    public void start() {
        try {
            LOG.fine("start begin");
            if (executor != null && !executor.isTerminated()) {
                executor.shutdownNow();
            }
            executor = Executors.newScheduledThreadPool(10);
            started = true;
            clients.stream().forEach((client) -> {
                client.started(this);
            });
            
            connect();
            
            executor.scheduleAtFixedRate(new KeepAliveWatch(), KEEP_ALIVE_INTERVALL, KEEP_ALIVE_INTERVALL, TimeUnit.SECONDS);

            
            logToOutput("Connected to %s:%d", host, port);
            LOG.fine("start begin");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    private void connect() throws IOException {
        if(connection != null && connection.isConnected()){
            throw new IllegalStateException("Old connection is still alive");
        }
        
        logToOutput("Connecting to %s:%d", host, port);
        connection = new DecoderConnection(host, port);
        connection.setConnectionTrigger(new DecoderConnection.ConnectionTrigger() {
            
            @Override
            public void disconnected(String reason, DecoderConnection source) {
                logToOutput("Disconnected, %s, from %s", reason, source);
                clients.stream().forEach((client) -> {
                    client.disconnected(MylapsTrackConnector.this);
                });
            }
            
            @Override
            public void connected(DecoderConnection source) {
                logToOutput("Connected to %s", source);
                clients.stream().forEach((client) -> {
                    client.connected(MylapsTrackConnector.this);
                });
            }
        });
        
        connection.connect(executor);
    }
    private static final int KEEP_ALIVE_INTERVALL = 30;

    @Override
    public void stop() throws InterruptedException {
        if (started) {
            logToOutput("stop");
            started = false;
            connection.disconnect("Stopping track connector");
            executor.shutdown();
            executor.awaitTermination(KEEP_ALIVE_INTERVALL, TimeUnit.SECONDS);
            executor.shutdownNow();

        }
    }

    @Override
    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    @Override
    public boolean isStarted() {
        return started;
    }
    
    private void logToOutput(String format, Object ... args){
        IOProvider.getDefault().getIO("MyLaps decoder connection", false).getOut().format(format, args);
    }

    private void logExceptionToOutput(String msg, Throwable ex) {
        IOProvider.getDefault().getIO("MyLaps decoder connection", false).getErr().println(msg);
        LOG.log(Level.SEVERE, msg, ex);
    }
}