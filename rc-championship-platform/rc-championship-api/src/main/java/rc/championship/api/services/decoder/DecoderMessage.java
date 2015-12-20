package rc.championship.api.services.decoder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.json.JSONObject;
import rc.championship.api.model.Decoder;

/**
 * Represents a message going in or out from the decoder
 */
public class DecoderMessage {

    private Decoder decoder;

    

    public enum Command {
        Reset, Status, Passing, Version, ResendPassings, ClearPassings, AuxiliarySettings, ServerSettings, Session, NetworkSettings, ConnectionWatchdog, FunctionUnlock, Ping, Time, GeneralSettings, Signals, LoopTrigger, GPS, FirstContact, Timeline, Error, Unknown
    };

    private Command command = Command.Unknown;
    private final JSONObject json;

    private DecoderMessage(Command command, Map<String, Object> fields) {
        this.command = command;
        this.json = new JSONObject(fields);
    }

    public DecoderMessage(String json) {
        this.json = new JSONObject(json);
        Optional<String> tor = getString("recordType");
        if (tor.isPresent()) {
            try{
                this.command = Command.valueOf(tor.get());
            } catch(IllegalArgumentException ignore) {
            }
        }
    }
    
    public Command getCommand() {
        return command;
    }

    public Set<String> getFields() {
        return json.keySet();
    }

    public boolean hasField(String field) {
        return json.has(field);
    }

    public boolean isNull(String field) {
        return json.isNull(field);
    }

    public Optional<Boolean> getBoolean(String field) {
        if (isNull(field)) {
            return Optional.empty();
        }
        return Optional.ofNullable(json.getBoolean(field));
    }

    public Optional<Double> getDouble(String field) {
        if (isNull(field)) {
            return Optional.empty();
        }
        return Optional.ofNullable(json.getDouble(field));
    }

    public Optional<Integer> getInt(String field) {
        if (isNull(field)) {
            return Optional.empty();
        }
        return Optional.ofNullable(json.getInt(field));
    }

    public Optional<Long> getLong(String field) {
        if (isNull(field)) {
            return Optional.empty();
        }
        return Optional.ofNullable(json.getLong(field));
    }

    
    public String getString(String field, String def) {
        Optional<String> value = getString(field);
        if(value.isPresent()){
            return value.get();
        }
        return def;
    }
    
    public final Optional<String> getString(String field) {
        if (isNull(field)) {
            return Optional.empty();
        }
        return Optional.ofNullable(json.getString(field));
    }

    public Optional<Date> getDate(String field) {
        Optional<Long> ms = getLong(field);
        if (ms.isPresent()) {
            return Optional.of(new Date(ms.get()));
        }
        return Optional.empty();
    }

    public <T> Optional<T> get(String field) {
        if (isNull(field)) {
            return Optional.empty();
        }
        return Optional.ofNullable((T) json.get(field));
    }
    
    public <T> void set(String field, T value) {
        if(value == null){
            json.remove(field);
        } else {
            json.put(field, value);
        }
    }
    
    public String toJson() {
        return json.toString();
    }

    public static class Builder {

        private final Command command;
        private final Map<String, Object> fields = new HashMap<>();

        public Builder(Command command) {
            this.command = command;
            set("recordType", command.name());
            set("SPARE", "0000");
            set("VERSION", "2");
        }

        public <T> Builder set(String field, T value) {
            fields.put(field, value);
            return this;
        }

        public DecoderMessage build() {
            return new DecoderMessage(command, new HashMap<>(fields));
        }
    }
    
    public Optional<Integer> getHexInt(String key) {
        Optional<String> optValue = getString(key);
        if(!optValue.isPresent()){
            return Optional.empty();
        }
        String hex = optValue.get();
        int value = Integer.parseInt(hex, 16);
        return Optional.of(value);
    }

    
    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    public Decoder getDecoder() {
        return decoder;
    }

    public String getJson() {
        return json.toString();
    }
    
}
