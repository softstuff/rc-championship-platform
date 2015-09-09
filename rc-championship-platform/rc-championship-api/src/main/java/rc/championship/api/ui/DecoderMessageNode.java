package rc.championship.api.ui;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;
import rc.championship.api.services.decoder.DecoderMessage;

/**
 *
 * @author Stefan
 */
public class DecoderMessageNode extends AbstractNode {

    public DecoderMessageNode(DecoderMessage msg) {
        super(Children.LEAF, Lookups.singleton(msg));
    }
    
    private DecoderMessage getDecoderMessage(){
        return getLookup().lookup(DecoderMessage.class);
    }
    
}
