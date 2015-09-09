package rc.championship.api.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderMessage;
import rc.championship.api.services.decoder.DecoderMessageManager;

/**
 *
 * @author Stefan
 */
public class DecoderMessageNodeFactory extends ChildFactory<DecoderMessage> implements PropertyChangeListener {
    private final DecoderMessageManager msgManager;
    private final Decoder decoder;
    
    public DecoderMessageNodeFactory(Decoder decoder) {
        this.decoder = decoder;
        msgManager = Lookup.getDefault().lookup(DecoderMessageManager.class);
        msgManager.addPropertyChangeListener((PropertyChangeListener) this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        refresh(true);
    }
    
    @Override
    protected boolean createKeys(List<DecoderMessage> toPopulate) {        
        toPopulate.addAll(msgManager.getCurrentMessages(decoder));
        return true;
    }

    @Override
    protected Node createNodeForKey(DecoderMessage key) {
        return new DecoderMessageNode(key);
    }
    
    
}
