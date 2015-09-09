package rc.championship.decoder.explorer;

import java.util.List;
import java.util.prefs.BackingStoreException;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderServices;

/**
 *
 * @author Stefan
 */
public class DecoderFactory extends ChildFactory<Decoder>{

    @Override
    protected boolean createKeys(List<Decoder> toPopulate) {
        
        try {
            DecoderServices ds = getDecoderStorage();
            if(ds != null){
                toPopulate.addAll(ds.getDecoders());
            }
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true; 
    }

    private static DecoderServices getDecoderStorage() {
        return Lookup.getDefault().lookup(DecoderServices.class);
    }

    @Override
    protected Node createNodeForKey(Decoder decoder) {
        return new DecoderNode(decoder, this);         
    }

    public void addNewDecoder(Decoder newDecoder) throws BackingStoreException {
        DecoderServices ds = getDecoderStorage();
        List<Decoder> decoders = ds.getDecoders();
        decoders.add(newDecoder);
        ds.store(decoders);
        
        refresh(true);
    }

    void remove(Decoder decoder) throws BackingStoreException {
        DecoderServices ds = getDecoderStorage();
        ds.remove(decoder);
        
        refresh(true);        
    }
    
    
    
}
