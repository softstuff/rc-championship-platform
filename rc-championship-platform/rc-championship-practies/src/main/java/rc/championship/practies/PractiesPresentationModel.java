package rc.championship.practies;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import rc.championship.api.services.TrackConnector;
import rc.championship.api.services.TrackConnectorFactory;


public class PractiesPresentationModel {
    
    private String host;
    private String port;
    
    private TrackConnectorFactory selectedFactory;
    private TrackConnector connector;

    public PractiesPresentationModel() {
        host = getHostName();
        port = "5403";
        Collection<? extends TrackConnectorFactory> allTrackConnectorFactories = getAllTrackConnectorFactories();
        if(allTrackConnectorFactories != null && allTrackConnectorFactories.size() == 1){
            selectedFactory = allTrackConnectorFactories.iterator().next();
        }
    }
    
    
    private String getHostName() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostName();
        } catch (UnknownHostException ex) {
            Exceptions.printStackTrace(ex);
            return "localhost";
        }
    }
    
    

    public TrackConnectorFactory getSelectedFactory() {
        return selectedFactory;
    }

    public void setSelectedFactory(TrackConnectorFactory selectedFactory) {
        this.selectedFactory = selectedFactory;
    }
    
    public final Collection<? extends TrackConnectorFactory> getAllTrackConnectorFactories(){
        Collection<? extends TrackConnectorFactory> factories = Lookup.getDefault().lookupAll(TrackConnectorFactory.class);
        return factories;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public TrackConnector getConnector() {
        return connector;
    }

    public void setConnector(TrackConnector connector) {
        this.connector = connector;
    }
    
    TrackConnector connect() {
        int portValue = Integer.parseInt(port);
        return connector = selectedFactory.create(host, portValue);
    }
    
    boolean isConnectorSelected(){
        return connector != null;
    }
    
}
