package rc.championship.decoder.explorer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import javax.swing.Action;
import org.openide.actions.DeleteAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import rc.championship.api.model.Decoder;

/**
 *
 * @author Stefan
 */
    public class DecoderNode extends AbstractNode {

    public DecoderNode(Decoder decoder, DecoderFactory factory) {
        super(Children.LEAF, Lookups.fixed(decoder,factory));
    }

    private Decoder getDecoder() {
        return getLookup().lookup(Decoder.class);
    }

    private DecoderFactory getNodeFactory(){
        return getLookup().lookup(DecoderFactory.class);
    }
    
    @Override
    public String getDisplayName() {
        Decoder decoder = getDecoder();
        return String.format("%s %s:%d", decoder.getDecoderName(), decoder.getHost(), decoder.getPort());
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> myActions = new ArrayList<>(
                Utilities.actionsForPath("Actions/Decoder"));
        myActions.add(SystemAction.get(DeleteAction.class));
        return myActions.toArray(new Action[0]);
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public void destroy() throws IOException {
        Decoder decoder = getDecoder();

        try {
            DecoderFactory nodeFactory = getNodeFactory();
            nodeFactory.remove(decoder);
            
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
        
    }

}
