package rc.championship.api.ui.lap;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import rc.championship.api.model.Lap;
import rc.championship.api.services.LapManager;

public final class LapNodeFactory extends ChildFactory<Lap> implements PropertyChangeListener {

    private final LapManager lapManager;
    
    public LapNodeFactory() {
        lapManager = Lookup.getDefault().lookup(LapManager.class);
        lapManager.addPropertyChangeListener((PropertyChangeListener) this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        refresh(true);
    }
    
    @Override
    protected boolean createKeys(List<Lap> toPopulate) {        
        toPopulate.addAll(lapManager.getLastLaps());
        return true;
    }

    @Override
    protected Node createNodeForKey(Lap key) {
        return new LapNode(key);
    }
    
    
    
    
}
