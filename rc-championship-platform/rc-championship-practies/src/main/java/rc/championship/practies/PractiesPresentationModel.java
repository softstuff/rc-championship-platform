package rc.championship.practies;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import org.openide.LifecycleManager;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.LapManager;
import rc.championship.api.services.decoder.DecoderServices;


public final class PractiesPresentationModel implements PropertyChangeListener {
    

    public static final String PROP_DECODER = "decoder";
    
    
    private Decoder decoder;
    private boolean decoderListNeedReload = true;
    private final List<Decoder> allDecoders = new ArrayList<>();
    private LapManager lapManager;
    private DecoderServices decoderServices;
    
    private Logger log = Logger.getLogger(getClass().getName());
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public PractiesPresentationModel() {
        
        initDecoderServices();        
        initLapManager();
            
    }
    
    private void initLapManager() {
        this.lapManager = Lookup.getDefault().lookup(LapManager.class);
        if(lapManager == null){
            log.severe("Cannot get a LapManager object");
            LifecycleManager.getDefault().exit();
        }
    }

    private void initDecoderServices() {
        decoderServices = Lookup.getDefault().lookup(DecoderServices.class);
        
        if(decoderServices == null){
            Logger.getLogger(getClass().getName()).severe("failed to find DecoderServices object");
            LifecycleManager.getDefault().exit();
        }
    }
    public void startListsenForDecoderListChanges(){
        decoderServices.addPropertyChangeListener(this);
        reloadDecoderList();
    }
    
    public void stopListsenForDecoderListChanges(){
        decoderServices.removePropertyChangeListener(this);
    }
        

    public Decoder getDecoder() {
        return decoder;
    }

    public void setDecoder(Decoder decoder) {
        Decoder oldDecoder = this.decoder;
        this.decoder = decoder;
        propertyChangeSupport.firePropertyChange(PROP_DECODER, oldDecoder, decoder);
    }
    
    public List<Decoder> getAllDecoders(){
        if(decoderListNeedReload){
            reloadDecoderList();
        }
        return allDecoders;
    }
    
    private void reloadDecoderList() {
        try {
            allDecoders.clear();
            allDecoders.addAll(decoderServices.getDecoders());
            decoderListNeedReload = false;
            
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(DecoderServices.PROP_DECODER_LIST_CHANGED)){
            decoderListNeedReload = true;
        }
    }

    boolean hasDecoder() {
        return decoder != null;
    }
}
