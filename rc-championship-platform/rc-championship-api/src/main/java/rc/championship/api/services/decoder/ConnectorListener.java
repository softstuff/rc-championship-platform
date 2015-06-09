package rc.championship.api.services.decoder;

import rc.championship.api.model.Lap;

/**
 *
 * @author Stefan
 */
public interface ConnectorListener {
    void connected(TrackConnector source);
    void disconnected(TrackConnector source);
    void recorded(Lap lap, TrackConnector source);
    void started(TrackConnector source);
}
