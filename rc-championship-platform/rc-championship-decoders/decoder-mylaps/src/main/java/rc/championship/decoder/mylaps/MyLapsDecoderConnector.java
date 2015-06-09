package rc.championship.decoder.mylaps;

import java.util.HashSet;
import java.util.Set;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderConnector;
import rc.championship.api.services.decoder.DecoderListener;

public class MyLapsDecoderConnector implements DecoderConnector {

    
    
    private boolean connected;
    private final Decoder decoder;
    private Set<DecoderListener> listeners = new HashSet<>();

    public MyLapsDecoderConnector(Decoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Decoder getDecoder() {
        return decoder;
    }
    
    @Override
    public void register(DecoderListener listener){
        listeners.add(listener);
    }
    
    @Override
    public void unregister(DecoderListener listener){
        listeners.remove(listener);
    }
    
    @Override
    public void connect() {
        connected = true;
        
        fireConnectedEvent();
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void disconnect() {
        connected = false;
        
        fireDisconnectedEvent("User selected");
    }
    
    private void fireConnectedEvent(){
        listeners.forEach(listener->{listener.connected(decoder);});
    }
    
    private void fireDisconnectedEvent(String reason){
        listeners.forEach(listener->{listener.disconnected(reason,decoder);});
    }
    
}
