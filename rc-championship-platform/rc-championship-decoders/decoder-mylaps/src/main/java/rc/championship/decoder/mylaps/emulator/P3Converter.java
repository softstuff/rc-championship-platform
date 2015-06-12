package rc.championship.decoder.mylaps.emulator;

import eu.plib.P3tools.MsgProcessor;
import eu.plib.P3tools.data.msg.v2.Time;
import eu.plib.Ptools.Message;
import java.util.Date;
import java.util.Optional;
import rc.championship.api.services.decoder.DecoderMessage;

/**
 *
 * @author Stefan
 */
public class P3Converter {
    public static DecoderMessage convertToMessage(byte[] msgData) {
        Message p3Msg = new MsgProcessor().parse(msgData);
        
        String json = p3Msg.toString();
        
        return new DecoderMessage(json);
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
}
