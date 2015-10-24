package rc.championship.decoder.history;

import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import rc.championship.api.services.decoder.StoredMessage;

/**
 *
 * @author Stefan
 */
public class StoredMessageNode {
    
    private SimpleLongProperty id;
    private SimpleLongProperty time;
    private SimpleStringProperty type;
    private SimpleStringProperty command;
    private SimpleStringProperty data;
    
    private final StoredMessage msg;

    public StoredMessageNode(StoredMessage msg) {
        this.msg = msg;
        id = new ReadOnlyLongWrapper(msg, "id");
        time = new ReadOnlyLongWrapper(msg, "time");
        type = new ReadOnlyStringWrapper(msg, "type");
        command = new ReadOnlyStringWrapper(msg, "command");
        data = new ReadOnlyStringWrapper(msg, "data");
    }

    public SimpleLongProperty getId() {
        return id;
    }

    public SimpleLongProperty getTime() {
        return time;
    }

    public SimpleStringProperty getType() {
        return type;
    }

    public SimpleStringProperty getCommand() {
        return command;
    }

    public SimpleStringProperty getData() {
        return data;
    }
    
    
    
    
}
