package rc.championship.api.model;

import java.util.Date;
import java.util.Optional;
import rc.championship.api.services.decoder.DecoderMessage;

/**
 *
 * @author Stefan
 */
public class MessageConverter {
    
    public static Lap toLap(DecoderMessage msg) {
        if(msg.getCommand() != DecoderMessage.Command.Passing){
            throw new IllegalArgumentException("Message is not of command Passing");
        }
        Lap lap = new Lap();
        lap.setNumber(msg.getLong("passingNumber"));
        lap.setTime(getTimeStamp(msg.getString("RTC_TIME")));
        lap.setDecoderId(getHexString(msg.getString("decoderId(")));
        lap.setTransponder(getHexString(msg.getString("transponder(")));
        lap.setHit(getHexString(msg.getString("hits(")));
        lap.setStrength(getHexString(msg.getString("strength(")));
//        lap.setVoltage(getHexString(msg.getString("decoderId(")));
//        lap.setTemprature(getHexString(msg.getString("decoderId(")));
        
        return lap;
    }
    
    private static Optional<Long> getHexString(Optional<String> hex){
        if(!hex.isPresent()){
            return Optional.empty();
        }
        
        Long value = Long.decode("0x"+hex.get());
        return Optional.of(value);
    }
    
    private static Optional<Date> getTimeStamp(Optional<String> value) {
        
        Optional<Long> timestamp = getHexString(value);
        if(!timestamp.isPresent()){
            return Optional.empty();
        }
        long ms = timestamp.get() / 1000;
        Date date = new Date(ms);
        return Optional.of(date);
//        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date);
        
    }
}
