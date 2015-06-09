package rc.championship.api.services.decoder;



/**
 *
 * @author Stefan
 */
public interface TrackConnectorFactory {
    
    String getTypeOfConnector();
    
    TrackConnector create(String host, int port);
}
