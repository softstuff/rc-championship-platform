package rc.championship.decoder;

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
import rc.championship.api.services.decoder.DecoderConnectionFactory;
import rc.championship.api.services.decoder.DecoderServices;

@ServiceProvider(service = DecoderServices.class)
public class DecoderServicesImpl implements DecoderServices{
    
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private final Preferences prefs;

    public DecoderServicesImpl() {
        prefs = Preferences.userNodeForPackage(DecoderServicesImpl.class);
    }
    
    public List<Decoder> getDecoders() throws BackingStoreException {
        List<Decoder> result = new ArrayList<>();
        
        if(prefs.nodeExists("decoders")){
            Preferences decoders = prefs.node("decoders");
            for(String path : decoders.childrenNames()){
                Preferences savedDecoder = decoders.node(path);
                String host = savedDecoder.get("host", "");
                int port = savedDecoder.getInt("port", 0);
                String decoderName = savedDecoder.get("decoderName", "");
                Optional<DecoderConnectionFactory> factory = getFactoryFor(decoderName);
                result.add(new Decoder(host, port, decoderName, factory, savedDecoder.name()));
            }
        }
        return result;
    }
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
        
    }
    
    private void store(Decoder decoder) throws BackingStoreException{
        Preferences pref = prefs.node("decoders/"+decoder.getIdentifyer());
        pref.put("host", decoder.getHost());
        pref.putInt("port", decoder.getPort());
        pref.put("decoderName", decoder.getDecoderName());
    }
    
    public void remove(Decoder decoder) throws BackingStoreException{
        Preferences pref = prefs.node("decoders/"+decoder.getIdentifyer());
        pref.removeNode();
        pref.flush();;
    }

    private Optional<DecoderConnectionFactory> getFactoryFor(String decoderName) {
        Collection<? extends DecoderConnectionFactory> factories = Lookup.getDefault().lookupAll(DecoderConnectionFactory.class);
        for(DecoderConnectionFactory factory : factories) {
            if(factory.getDecoderName().equalsIgnoreCase(decoderName)){
                return Optional.of(factory);
            }
        }
        return Optional.empty();
    }
    
}
