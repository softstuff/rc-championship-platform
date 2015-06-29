package rc.championship.decoder.mylaps.emulator;

import eu.plib.P3tools.data.msg.v2.Time;
import eu.plib.Ptools.Message;
import java.nio.ByteBuffer;
import javax.xml.bind.DatatypeConverter;
import static org.hamcrest.Matchers.is;
import org.hamcrest.core.IsInstanceOf;
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
    
    @Test
    public void convertToBytes(){
//        String json = "{\"passingNumber\":\"3125\",\"transponder\":\"95B15B\",\"RTC_Time\":\"0\",\"strength\":\"24\",\"hits\":\"1B\",\"flags\":\"1\",\"recordType\":\"Passing\",\"TOR\":\"1\",\"crcOk\":true,\"unknownFields\":{},\"emptyFields\":{},\"VERSION\":\"2\",\"SPARE\":\"3300\",\"FLAGS\":\"0000\",\"decoderId\":\"750E0400\"}";
        String json = "{\"SPARE\":\"0000\",\"recordType\":\"Time\",\"VERSION\":\"2\",\"RTC_TIME\":\"519AF21671058\",\"decoderId\":\"01e240\"}";
        DecoderMessage msg = new DecoderMessage(json);
        
        ByteBuffer bytes = instance.convertToBytes(msg);
        byte[] data = bytes.array();
        Message p3 = P3Converter.convertToP3Message(data);
        assertThat(p3, IsInstanceOf.instanceOf(Time.class) );
//        assertThat(p3, IsInstanceOf.instanceOf(Passing.class) );
        
    }

    private byte[] convertHexString(String data) {
        String cleaed = data.replaceAll(":", "");
        return DatatypeConverter.parseHexBinary(cleaed);
    }
    
}
