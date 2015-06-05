package rc.championship.api.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author Stefan
 */
public class NbLogger {
    
    public static NbLogger getEmulatorLogger(Class owner){
        return new NbLogger("MyLaps emulator", owner);
    }
    
    private final Class owner;
    private final String name;
    private final InputOutput io;
    private final Logger logger;
    
    public NbLogger(String writerName, Class owner){
        this.name = writerName;
        this.owner = owner;
        this.io = IOProvider.getDefault().getIO(writerName, false);
        this.logger = Logger.getLogger(owner.getName());
    }
    
    public void log(Level level, String format, Object ... args){
        String msg = String.format(format, args);
        io.getOut().println(msg);
        logger.log(level, msg);
        
    }
}
