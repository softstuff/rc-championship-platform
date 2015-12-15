package rc.championship.api.services;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import rc.championship.api.model.Lap;

/**
 *
 * @author Stefan
 */
public interface LapManager {

    public static final String PROP_NEW_LAP = "newLap";
    public static final String PROP_RESET_LAP_COUNTER = "resetLapCounter";
    
    public Collection<? extends Lap> getLastLaps();

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    void resetLapCounter();

    public void add(Lap lap);
}
