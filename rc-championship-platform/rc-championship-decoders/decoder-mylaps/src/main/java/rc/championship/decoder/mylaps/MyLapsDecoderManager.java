package rc.championship.decoder.mylaps;

import org.openide.util.lookup.ServiceProvider;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderManager;
import rc.championship.api.services.decoder.DecoderPlayer;

@ServiceProvider(service = DecoderManager.class)
public class MyLapsDecoderManager extends DecoderManager{
    
    @Override
    public boolean canHandle(Decoder decoder){
        return decoder.getDecoderName().equalsIgnoreCase("mylaps");
    }
    
    @Override
    public DecoderPlayer createPlayer(Decoder decoder){
        if(decoder == null){
            throw new IllegalArgumentException("Decoder can not be null");
        }
        if(!canHandle(decoder)){
            throw new IllegalArgumentException("Decoder '"+decoder+"' is not supproted by this DecoderManager "+getClass().getSimpleName());
        }        
        
        return new MyLapsDecoderPlayer(decoder);
    }
        
}
