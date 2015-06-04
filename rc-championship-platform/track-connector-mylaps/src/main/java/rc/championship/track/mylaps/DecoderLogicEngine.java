package rc.championship.track.mylaps;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import eu.plib.P3tools.MsgProcessor;
import eu.plib.P3tools.data.msg.v2.Passing;
import eu.plib.Ptools.Message;
import rc.championship.api.model.Lap;
import rc.championship.api.services.ConnectorListener;
import rc.championship.api.services.TrackConnector;

/**
 *
 * @author Stefan
 */
public class DecoderLogicEngine implements Runnable, ConnectorListener {

    private static final Logger LOG = Logger.getLogger(DecoderLogicEngine.class.getName());

    private static final int TIMEOUT = 200;
    
    private final MylapsTrackConnector connector;

    public DecoderLogicEngine(MylapsTrackConnector connector) {
        this.connector = connector;        
    }
    
    @Override
    public void run() {
        connector.register(DecoderLogicEngine.this);
        try{
        while(connector.isStarted()){
            ByteBuffer msg = connector.readMessage(TIMEOUT, TimeUnit.MILLISECONDS);
            processInconing(msg);
        }
        }catch(Exception  ex){
            
        } finally {
            connector.unregister(DecoderLogicEngine.this);
        }
        
    }

    @Override
    public void connected(TrackConnector source) {
        
    }

    @Override
    public void disconnected(TrackConnector source) {
        
    }

    @Override
    public void recorded(Lap lap, TrackConnector source) {
        
    }

    @Override
    public void started(TrackConnector source) {
        
    }

    private void processInconing(ByteBuffer msg) {
        Message parsedMsg = new MsgProcessor(true).parse(msg.array());

        String parsedMsgJson = parsedMsg.toString();
        LOG.fine("Received msg "+parsedMsgJson);

        if (parsedMsg.getType().getName().equals(Passing.class.getName())) {
            Passing passing = (Passing) parsedMsg;
            LOG.fine("Passing message transponder: "+passing.transponder);
        }
//        ByteBuffer response = null;
//        boolean accepted = connector.sendMessage(response, TIMEOUT, TimeUnit.MILLISECONDS);
    }
    
    
}
