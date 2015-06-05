package rc.championship.track.mylaps;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import rc.championship.api.model.Lap;
import rc.championship.api.services.ConnectorListener;
import rc.championship.api.services.TrackConnector;
import rc.championship.mylaps.emulator.ClientConnection;
import rc.championship.mylaps.emulator.P3DecoderEmulator;
import rc.championship.mylaps.emulator.TransferListener;

/**
 *
 * @author Stefan
 */
public class MylapsTrackConnectorTest {
    
    public MylapsTrackConnectorTest() throws FileNotFoundException, IOException {
        LogManager.getLogManager().readConfiguration(MylapsTrackConnectorTest.class.getResourceAsStream("/logging.properties"));
    }
    
    @Before
    public void setUp() {
    }
    
    
    @Test
    @Ignore
    public void testProtocol() throws IOException, InterruptedException{
        
        P3DecoderEmulator emulator = new P3DecoderEmulator();
        emulator.registerListener(new TransferListener() {

            @Override
            public void sent(String data, ClientConnection source) {
                System.out.format("emulator sent %s", data);
            }

            @Override
            public void recived(String data, ClientConnection source) {
                System.out.format("emulator recived %s", data);
            }

            @Override
            public void clientConnected(ClientConnection connection) {
                System.out.format("emulator clientConnected");
                
                connection.send("8e021b00931000001c0001081820515d68fd946b8104972204008f");
                
            }

            @Override
            public void clientDisconnected(ClientConnection connection) {
                System.out.format("emulator clientDisconnect");
            }
        });
        
        emulator.start(23432);
        
        MylapsTrackConnector track = new MylapsTrackConnector("localhost", 23432);
        track.register(new ConnectorListener() {

            @Override
            public void connected(TrackConnector source) {
                System.out.format("track connected");
            }

            @Override
            public void disconnected(TrackConnector source) {
                System.out.format("track disconnected");
            }

            @Override
            public void recorded(Lap lap, TrackConnector source) {
                System.out.format("track recorded");
            }

            @Override
            public void started(TrackConnector source) {
                System.out.format("track started");
            }
        });
        
//        track.start("192.168.1.201", 23432);
//        track.start("localhost", 23432);
        track.start();
        Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        track.stop();
        
    }

    @Test
    public void testGetConnectorName() {
    }

    @Test
    public void testRegister() {
    }

    @Test
    public void testDeregister() {
    }

    @Test
    public void testStart() {
    }

    @Test
    public void testStop() {
    }

    @Test
    public void testConnect() throws Exception {
    }

    @Test
    public void testDisconnect() {
    }

    @Test
    public void testIsConnected() {
    }

    @Test
    public void testIsStarted() {
    }
    
}
