package rc.championship.api.services.decoder;

import org.junit.Test;

/**
 *
 * @author Stefan
 */
public class DecoderMessageTest {
    
    public DecoderMessageTest() {
    }

    @Test
    public void testBuilder() {
        
        
//          {"recordType":"Time","VERSION":"2","RTC_TIME":"518962482CFF8","decoderId":"97220400","FLAGS":"0000"}
//        {"RTC_TIME":"516EB9C137218","flags":"0","recordType":"Time","TOR":"24","crcOk":true,"unknownFields":{},"emptyFields":{},"VERSION":"2","SPARE":"1F00","FLAGS":"0000","decoderId":"97220400"}
//        RTC_TIME 1432576075199000 ns
//        "RTC_TIME":"518962482CFF8"
        
        DecoderMessage msg = new DecoderMessage.Builder(DecoderMessage.Command.Time)        
                .set("RTC_TIME",  Long.toHexString(System.currentTimeMillis()*1000).toUpperCase())
                .set("decoderId", "97220400")
                .set("FLAGS", "0000")
                .build();
        
        String json = msg.toJson();
        
//        assertThat(json, is(""));
    }
    
}
