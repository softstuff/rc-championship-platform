package rc.championship.decoder.mylaps;

import org.openide.util.lookup.ServiceProvider;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderConnectionFactory;
import rc.championship.api.services.decoder.DecoderConnector;

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

}
