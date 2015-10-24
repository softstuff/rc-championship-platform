package rc.championship.api.services.decoder;

import java.util.Objects;

/**
 *
 * @author Stefan
 */
public class StoredMessage {

    private Long id;
    private Long time;
    private String type;
    private String command;
    private String data;

    public StoredMessage id(Long id) {
        this.id = id;
        return this;
    }

    public StoredMessage time(Long time) {
        this.time = time;
        return this;
    }

    public StoredMessage type(String type) {
        this.type = type;
        return this;
    }

    public StoredMessage command(String command) {
        this.command = command;
        return this;
    }

    public StoredMessage data(String data) {
        this.data = data;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Long getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getCommand() {
        return command;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "StoredMessage{" + "id=" + id + ", time=" + time + ", type=" + type + ", command=" + command + ", data=" + data + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StoredMessage other = (StoredMessage) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    
    
    
}
