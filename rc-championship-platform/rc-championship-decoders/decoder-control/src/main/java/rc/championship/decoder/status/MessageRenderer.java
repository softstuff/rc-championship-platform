package rc.championship.decoder.status;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import rc.championship.api.services.decoder.DecoderMessage;

/**
 *
 * @author Stefan
 */
public class MessageRenderer {
    public static MessageRenderer wrap(DecoderMessage msg){
        return new MessageRenderer(msg);
    }
    private final DecoderMessage msg;

    private MessageRenderer(DecoderMessage msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        
        switch(msg.getCommand()){
            case Time: return "Time "+getTimeStamp(msg.getString("RTC_TIME"))+  " msg=" + msg.toJson();
            case Passing: return "Passing "+getTimeStamp(msg.getString("RTC_TIME"))+  " msg=" + msg.toJson();
        }
        return "MessageRenderer{" + "msg=" + msg.toJson() + '}';
    }
//    .set("RTC_TIME",  Long.toHexString(System.currentTimeMillis()*1000).toUpperCase())DatatypeConverter.print

    private String getTimeStamp(Optional<String> value) {
        if(!value.isPresent()){
            return "N/A";
        }
        String hex = value.get();
        Long timestamp = Long.decode("0x"+hex);
        long ms = timestamp / 1000;
        Date date = new Date(ms);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date);
        
    }
    
}
