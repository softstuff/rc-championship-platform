package rc.championship.practies;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import rc.championship.api.model.Lap;
import rc.championship.api.services.LapManager;

/**
 *
 * @author Stefan
 */
public final class PractiesLapManager implements LapManager {
    private static final String PROP_NEW_LAP = "newLap";
    private static final String PROP_RESET_LAP_COUNTER = "resetLapCounter";
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private List<Lap> lastLaps = new CopyOnWriteArrayList<>();

    @Override
    public Collection<? extends Lap> getLastLaps() {
        return lastLaps;
    }
    
    @Override
    public void resetLapCounter(){
        lastLaps.clear();
        pcs.firePropertyChange(PROP_RESET_LAP_COUNTER, false, true);
    }
    
    @Override
    public void add(Lap lap){
        lastLaps.add(lap);
        pcs.firePropertyChange(PROP_NEW_LAP, null, lap);
    }
    
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener){
        pcs.addPropertyChangeListener(listener);
    }
    
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener){
        pcs.removePropertyChangeListener(listener);
    }
    
}
