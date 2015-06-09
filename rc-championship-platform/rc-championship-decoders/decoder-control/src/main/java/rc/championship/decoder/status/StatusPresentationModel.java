package rc.championship.decoder.status;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderServices;

/**
 *
 * @author Stefan
 */
public class StatusPresentationModel  {

    
    public static final String PROP_DECODER = "decoder";
    public static final String PROP_CONNECTOR = "connector";
    
    private List<Decoder> allDecoders = new ArrayList<>();
    private Decoder decoder;

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public StatusPresentationModel() {
        refreshDecoderList();
    }

    public List<Decoder> getAllDecoders() {
        return allDecoders;
    }

    public Decoder getDecoder() {
        return decoder;
    }

    public void setDecoder(Decoder decoder) {
        Decoder old = this.decoder;
        this.decoder = decoder;
        pcs.firePropertyChange(PROP_DECODER, old, decoder);
    }
      
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    
    
    void refreshDecoderList() {
        try {
            allDecoders.clear();
            DecoderServices ds = Lookup.getDefault().lookup(DecoderServices.class);
            if(ds != null){
                allDecoders.addAll(ds.getDecoders());
            }
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    
}
