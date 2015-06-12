package rc.championship.decoder.mylaps.emulator;

import javax.xml.bind.DatatypeConverter;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import rc.championship.api.services.decoder.DecoderMessage;

/**
 *
 * @author Stefan
 */
public class P3ConverterIT {
    
    private P3Converter instance;
    
    @Before
    public void init(){
        instance = new P3Converter();
    }

    @Test
    public void testConvertToMessage() {
        byte[] data = convertHexString("8e:02:1f:00:53:17:00:00:24:00:01:08:18:72:13:9c:eb:16:05:00:04:02:00:00:81:04:97:22:04:00:8f");
        DecoderMessage msg = instance.convertToMessage(data);
        assertThat(msg.getCommand(), is(DecoderMessage.Command.Time));
//        assertThat(msg.get, containsInAnyOrder());
    }

    private byte[] convertHexString(String data) {
        String cleaed = data.replaceAll(":", "");
        return DatatypeConverter.parseHexBinary(cleaed);
    }
    
}
