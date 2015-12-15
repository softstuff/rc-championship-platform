/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rc.championship.api.services.decoder;

import rc.championship.api.model.Decoder;

/**
 *
 * @author Stefan
 */
public interface DecoderPlayerFactory {
    public boolean canHandle(Decoder decoder);
    
    public DecoderPlayer createPlayer(Decoder decoder);
}
