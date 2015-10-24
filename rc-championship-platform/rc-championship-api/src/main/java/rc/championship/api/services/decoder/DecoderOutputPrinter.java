package rc.championship.api.services.decoder;

import java.text.DateFormat;
import java.util.Date;
import org.openide.windows.IOProvider;
import rc.championship.api.model.Decoder;

/**
 *
 * @author Stefan
 */
public class DecoderOutputPrinter implements DecoderListener {
    @Override
    public void connected(Decoder source) {
       info(source, "connected");
    }

    @Override
    public void disconnected(String reason, Decoder source) {
        info(source, "disconnected %s", reason);
    }

    @Override
    public void recived(DecoderMessage message) {
        info(message.getDecoder(), "recived %s %s", message.getCommand().name(), message);
    }

    @Override
    public void transmitted(DecoderMessage message) {
        info(message.getDecoder(), "transmitted %s %s", message.getCommand().name(), message);
    }

    @Override
    public void receivedCorruptData(Integer from, Integer start, String hexData, Decoder source) {
        error(source, "receivedCorruptData");
    }
    
    public static void info(Decoder decoder, String msg, Object ... args){
        IOProvider.getDefault().getIO(decoder.getDisplayName(), false)
                .getOut()
                .format("%s - %s", 
                        DateFormat.getTimeInstance().format(new Date())
                        , String.format(msg, args));
    }
    
    public static void error(Decoder decoder, String msg, Object ... args){
        IOProvider.getDefault().getIO(decoder.getDisplayName(), false)
                .getErr()
                .format("%s - %s", 
                        DateFormat.getTimeInstance().format(new Date())
                        , String.format(msg, args));
    }
}
