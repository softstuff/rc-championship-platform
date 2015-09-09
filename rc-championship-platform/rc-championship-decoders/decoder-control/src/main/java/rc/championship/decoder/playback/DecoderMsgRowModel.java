package rc.championship.decoder.playback;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Stefan
 */
public final class DecoderMsgRowModel {

    private final SimpleStringProperty time;
    private final SimpleStringProperty type;
    private final SimpleStringProperty data;
    
    public DecoderMsgRowModel(){
        time = new SimpleStringProperty("");
        type = new SimpleStringProperty("");
        data = new SimpleStringProperty("");
    }

    DecoderMsgRowModel(String time, String type, String data) {
        this();
        setTime(time);
        setType(type);
        setData(data);        
    }

    public String getTime() {
        return time.get();
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getData() {
        return data.get();
    }

    public void setData(String data) {
        this.data.set(data);
    }
    
    
}
