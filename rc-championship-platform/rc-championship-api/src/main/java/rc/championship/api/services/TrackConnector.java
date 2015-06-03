package rc.championship.api.services;

/**
 *
 * @author Stefan
 */
public interface TrackConnector {
    String getConnectorName();
    void start();
    void stop() throws InterruptedException;
    boolean isConnected();
    boolean isStarted();
    void register(ConnectorListener listener);
    void deregister(ConnectorListener listener);
}
