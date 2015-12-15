package rc.championship.decoder;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderPlayerFactory;
import rc.championship.api.services.decoder.DecoderServices;

@ServiceProvider(service = DecoderServices.class)
public class DecoderServicesImpl implements DecoderServices{
    
    
    
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    private final Preferences prefs;

    public DecoderServicesImpl() {
        prefs = Preferences.userNodeForPackage(DecoderServicesImpl.class);
    }
    
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    @Override
    public List<Decoder> getDecoders() throws BackingStoreException {
        List<Decoder> result = new ArrayList<>();
        
        if(prefs.nodeExists("decoders")){
            Preferences decoders = prefs.node("decoders");
            
            for(String path : decoders.childrenNames()){
                Preferences savedDecoder = decoders.node(path);
                Decoder decoder= readDecoder(savedDecoder);
                result.add(decoder);
            }
        }
        return result;
    }
    
    @Override
    public void store(List<Decoder> decoders) throws BackingStoreException{
        prefs.sync();
        
        if(prefs.nodeExists("decoders")) {
            prefs.node("decoders").removeNode();
            prefs.flush();
        }
        
        for(Decoder decoder : decoders){
            store(decoder);
        }
        
        prefs.flush();
        fireDecoderListChanged();
    }
    
    private Decoder readDecoder(Preferences savedDecoder){
        String host = savedDecoder.get("host", "");
        int port = savedDecoder.getInt("port", 0);
        String decoderName = savedDecoder.get("decoderName", "");
        Optional<DecoderPlayerFactory> factory = getFactoryFor(decoderName);
        return new Decoder(host, port, decoderName, factory, savedDecoder.name());
    }
    private void store(Decoder decoder) throws BackingStoreException{
        Preferences pref = prefs.node("decoders/"+decoder.getIdentifyer());
        pref.put("host", decoder.getHost());
        pref.putInt("port", decoder.getPort());
        pref.put("decoderName", decoder.getDecoderName());
    }
    
    @Override
    public void remove(Decoder decoder) throws BackingStoreException{
        Preferences pref = prefs.node("decoders/"+decoder.getIdentifyer());
        pref.removeNode();
        pref.flush();
        fireDecoderListChanged();
    }

    @Override
    public Optional<Decoder> getDecoders(String decoderId) throws BackingStoreException {
        if(prefs.nodeExists("decoders/"+decoderId)) {
            return Optional.ofNullable(readDecoder(prefs.node("decoders/"+decoderId)));
        }
        return Optional.empty();
    }
    
    

    private Optional<DecoderPlayerFactory> getFactoryFor(String decoderName) {
        Collection<? extends DecoderPlayerFactory> factories = Lookup.getDefault().lookupAll(DecoderPlayerFactory.class);
        for(DecoderPlayerFactory factory : factories) {
            if(factory.getDecoderName().equalsIgnoreCase(decoderName)){
                return Optional.of(factory);
            }
        }
        return Optional.empty();
    }

    private void fireDecoderListChanged() {
        propertyChangeSupport.firePropertyChange(PROP_DECODER_LIST_CHANGED, null, "changed");
    }
    
}
