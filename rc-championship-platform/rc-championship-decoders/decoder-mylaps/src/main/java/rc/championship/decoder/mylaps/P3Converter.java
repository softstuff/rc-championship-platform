package rc.championship.decoder.mylaps;

import eu.plib.P3tools.MsgProcessor;
import eu.plib.P3tools.data.msg.Time;
import eu.plib.Ptools.Message;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Optional;
import rc.championship.api.services.decoder.DecoderMessage;

/**
 *
 * @author Stefan
 */
public class P3Converter {
    public static DecoderMessage convertToMessage(byte[] msgData) {
        Message p3Msg = convertToP3Message(msgData);
        
        String json = p3Msg.toString();
        
        return new DecoderMessage(json);
    }
    
    static Message convertToP3Message(byte[] msgData) {
        Message p3Msg = new MsgProcessor().parse(msgData);
        return p3Msg;
    }

    private DecoderMessage parseTime(Time p3) {
        return new DecoderMessage.Builder(DecoderMessage.Command.Time)
            .set("rtc_time", toDate(p3.RTC_TIME))
            .set("rtc_time", toDate(p3.UTC_TIME))
            .build();
    }
    private Optional<Date> toDate(Long ms){
        if(ms == null){
            return Optional.empty();
        }
        return Optional.of(new Date(ms));
    }
    
    public ByteBuffer convertToBytes(DecoderMessage dataToSend) {
        prepateData(dataToSend);
        String json = dataToSend.toJson();
        MsgProcessor msgProcessor = new MsgProcessor();
        Message p3Msg = msgProcessor.parseJson(json);
        byte[] data = msgProcessor.build(p3Msg);
        Message sent = msgProcessor.parse(data);
        return ByteBuffer.wrap(data);
    }

    private void prepateData(DecoderMessage dataToSend) {
        Optional<String> decoderIdOpt = dataToSend.getString("decoderId");
        if(decoderIdOpt.isPresent()){
            String decoderId = decoderIdOpt.get();
            if(decoderId.length() % 2 == 1){
                dataToSend.set("decoderId", "0"+decoderId);
            }
        }
    }
}
