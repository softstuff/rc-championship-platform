package rc.championship.api.services.decoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a message going in or out from the decoder
 */
public class DecoderMessage {
    public enum Command {
      Passing  
    };
    
    private final Command command;
    private final Map<String, Object> fields;

    private DecoderMessage(Command command, Map<String, Object> fields) {
        this.command = command;
        this.fields = fields;
    }
    
    public Set<String> getFields(){
        return fields.keySet();
    }
    
    public boolean hasField(String field){
        return fields.containsKey(field);
    }
    
    public <T> T get(String field){
        return (T)fields.get(field);
    }
    
    public static class Builder {
        private final Command command;
        private final Map<String, Object> fields = new HashMap<>();

        public Builder(Command command) {
            this.command = command;
        }
        
        public <T> Builder set(String field, T value){
            fields.put(field, value);
            return this;
        }
        
        public DecoderMessage build(){
            return new DecoderMessage(command, new HashMap<>(fields));
        }
    }
    
    
}
