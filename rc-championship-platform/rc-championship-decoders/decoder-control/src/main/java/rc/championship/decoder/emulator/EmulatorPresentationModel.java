package rc.championship.decoder.emulator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderServices;


public class EmulatorPresentationModel {
    public static final String PROP_DECODER = "decoder";
    public static final String PROP_CONNECTOR = "connector";
    
    private final List<Decoder> allDecoders = new ArrayList<>();
    private Decoder decoder;
    private int port = 5403;

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public EmulatorPresentationModel() {
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    
    
    final void refreshDecoderList() {
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
