package rc.championship.api.ui;

import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import rc.championship.api.model.Transponder;

/**
 *
 * @author Stefan
 */
public class TransponderChildFactory extends ChildFactory<Transponder>{

    private final List<Transponder> transponders;
    
    public TransponderChildFactory(List<Transponder> transponders) {
        this.transponders = transponders;
    }

    @Override
    protected boolean createKeys(List<Transponder> toPopulate) {
        toPopulate.addAll(transponders);
        return true;
    }

    @Override
    protected Node createNodeForKey(Transponder key) {
        return new TransponderNode(key);
    }
    
    
}
