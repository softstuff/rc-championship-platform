package rc.championship.api.services.decoder;

import rc.championship.api.model.Decoder;

/**
 *
 * @author Stefan
 */
public interface DecoderConnectionFactory {
    
    String getDecoderName();
    
    DecoderConnector createConnector(Decoder decoder);
}
