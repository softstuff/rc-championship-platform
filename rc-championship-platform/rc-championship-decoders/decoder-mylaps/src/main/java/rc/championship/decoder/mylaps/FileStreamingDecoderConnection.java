package rc.championship.decoder.mylaps;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import org.openide.util.Exceptions;
import rc.championship.api.services.decoder.DecoderListener;
import rc.championship.api.services.decoder.DecoderMessage;

/**
 *
 * @author Stefan
 */
public class FileStreamingDecoderConnection implements DecoderConnection {
    
    private static final Logger LOG = Logger.getLogger(FileStreamingDecoderConnection.class.getName());
    private final File file;
    private final Collection<DecoderListener> listeners;
    private boolean playing;

    public FileStreamingDecoderConnection(File file, Collection<DecoderListener> listeners) {
        this.file = file;
        this.listeners = listeners;
    }    

    @Override
    public void connect(ExecutorService service) throws IOException {
        LOG.log(Level.INFO, "Begin play file {0}", file.getAbsolutePath());
        playing = true;
        service.execute(() -> doPlayback());
    }

    @Override
    public void disconnect(String reason) {
        playing = false;
        LOG.log(Level.INFO, "Stop play file {0}", file.getAbsolutePath());
    }

    @Override
    public boolean isConnected() {
        return playing;
    }

    @Override
    public boolean send(DecoderMessage toSend, long timeout, TimeUnit timeUnit) throws IOException, InterruptedException {
        LOG.log(Level.WARNING, "Ignore sending message while playing from file: {0}", file.getAbsolutePath());
        return false;
    }
    
    
    private void doPlayback() {
        try (JsonReader jsonReader = Json.createReader(new FileReader(file))){
            JsonArray data = jsonReader.readArray();
            for (int i = 0; i < data.size(); i++) {
                JsonObject row = data.getJsonObject(i);
                final String jsonData;
                if (row.containsKey("delay")) {
                    int delay = row.getInt("delay");                    
                    Thread.sleep(delay);
                } 
                if (row.containsKey("direction")) {
                    String direction = row.getString("direction");
                    
                    if(!direction.equalsIgnoreCase("recived")){
                        continue; // ignore sent messages
                    }
                }
                JsonObject jsonObject = row.getJsonObject("data");
                jsonData = serialize(jsonObject);
                    
                DecoderMessage msg = new DecoderMessage(jsonData);
                fireMsgRecived(msg);

            };
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } 
    }

    String serialize(JsonObject object) throws IOException {
        try (StringWriter sw = new StringWriter();
                JsonWriter writer = Json.createWriter(sw)) {
            writer.writeObject(object);
            writer.close();
            sw.flush();
            return sw.toString();
        }
    }

    private void fireMsgRecived(DecoderMessage msg) {
        listeners.forEach(listener -> {
            listener.recived(msg);
        });
    }
}
