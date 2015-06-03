package rc.championship.track.mylaps;

import org.openide.util.lookup.ServiceProvider;
import rc.championship.api.services.TrackConnector;
import rc.championship.api.services.TrackConnectorFactory;

@ServiceProvider(service = TrackConnectorFactory.class)
public class MylapsTrackConnectorFactory implements TrackConnectorFactory {

    @Override
    public String getTypeOfConnector() {
        return "MyLaps";
    }
    
    @Override
    public TrackConnector create(String host,int port) {
        return new MylapsTrackConnector(host, port);
    }

    @Override
    public String toString() {
        return getTypeOfConnector();
    }
    
    
    
}
