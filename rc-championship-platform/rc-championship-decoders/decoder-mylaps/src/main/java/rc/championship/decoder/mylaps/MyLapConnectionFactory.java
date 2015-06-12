package rc.championship.decoder.mylaps;

import rc.championship.decoder.mylaps.client.MyLapsDecoderConnector;
import org.openide.util.lookup.ServiceProvider;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderConnectionFactory;
import rc.championship.api.services.decoder.DecoderConnector;
import rc.championship.api.services.decoder.DecoderEmulator;
import rc.championship.api.services.decoder.DecoderRecorder;
import rc.championship.decoder.mylaps.emulator.MyLapsDecoderEmulator;

/**
 *
 * @author Stefan
 */
@ServiceProvider(service = DecoderConnectionFactory.class)
public class MyLapConnectionFactory implements DecoderConnectionFactory {

    @Override
    public String getDecoderName() {
        return "MyLaps";
    }

    @Override
    public DecoderConnector createConnector(Decoder decoder) {
         return new MyLapsDecoderConnector(decoder);
    }
    
    @Override
    public DecoderEmulator createEmulator() {
         return new MyLapsDecoderEmulator();
    }

    @Override
    public DecoderRecorder createRecorder() {
        return new MyLapsDecoderRecorder();
    }
    
    
    

}
