/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rc.championship.decoder.mylaps;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderListener;
import rc.championship.api.services.decoder.DecoderMessage;

/**
 *
 * @author Stefan
 */
public class LiveDecoderConnectionTest {
    

    @Test
    public void testProcessInputBuffer() {
        Decoder decoder = mock(Decoder.class);        
        byte[] data = convertHexString("8e:02:1f:00:53:17:00:00:24:00:01:08:18:72:13:9c:eb:16:05:00:04:02:00:00:81:04:97:22:04:00:8f"+
                                       "8e:02:1f:00:53:17:00:00:24:00:01:08:18:72:13:9c:eb:16:05:00:04:02:00:00:81:04:97:22:04:00:8f");
        ByteBuffer buffer = ByteBuffer.wrap(data);
        
        LiveDecoderConnection connection = new LiveDecoderConnection(decoder, null);
        List<DecoderMessage> processedMessages = connection.processInputBuffer(buffer, data.length);
        
        assertThat(processedMessages, notNullValue());
        assertThat(processedMessages.size(), is(2));
        DecoderMessage first = processedMessages.get(0);
        DecoderMessage second = processedMessages.get(1);
        
        assertThat(first.getDecoder(), is(decoder));
        assertThat(first.getJson(), is("{\"SPARE\":\"1F00\",\"recordType\":\"Time\",\"origin\":\"P3\",\"flags\":0,\"RTC_TIME\":1432576075199000,\"decoderId\":\"97220400\",\"unknownFields\":{},\"flags-text\":\"\",\"emptyFields\":[],\"crcOk\":true,\"VERSION\":\"2\",\"RTC_TIME-text\":\"Mon May 25 19:47:55 CEST 2015\",\"FLAGS\":\"0000\"}"));
        
        assertThat(second.getDecoder(), is(decoder));
        assertThat(second.getJson(), is("{\"SPARE\":\"1F00\",\"recordType\":\"Time\",\"origin\":\"P3\",\"flags\":0,\"RTC_TIME\":1432576075199000,\"decoderId\":\"97220400\",\"unknownFields\":{},\"flags-text\":\"\",\"emptyFields\":[],\"crcOk\":true,\"VERSION\":\"2\",\"RTC_TIME-text\":\"Mon May 25 19:47:55 CEST 2015\",\"FLAGS\":\"0000\"}"));
    }
    
    private byte[] convertHexString(String data) {
        String cleaed = data.replaceAll(":", "");
        return DatatypeConverter.parseHexBinary(cleaed);
    }
}
