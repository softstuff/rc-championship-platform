package rc.championship.decoder.emulator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderServices;

public class EmulatorPresentationModel {

    public static final String PROP_DECODER = "decoder";
    public static final String PROP_CONNECTOR = "connector";
    public static final String PROP_TRANSPONDER = "transponder";
    public static final String PROP_NEXTPASSING = "nextPassing";
    public static final String PROP_AUTONOOFDRIVERS = "autoNoOfDrivers";
    public static final String PROP_AUTOLAPTIME = "autoLaptime";
    public static final String PROP_AUTOLAPSPREAD = "autoLapSpread";
    public static final String PROP_DECODERID = "decoderId";
    
    private Integer autoLaptime = 30;

    private Integer autoNoOfDrivers = 4;
    
    private Integer autoLapSpread = 5;
    
    private Integer decoderId;

    private final List<Decoder> allDecoders = new ArrayList<>();
    private Decoder decoder;
    private int port = 5403;

    private Passing nextPassing;

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public EmulatorPresentationModel() {
        refreshDecoderList();
        nextPassing = new Passing();
        nextPassing.passingNumber=1;
        nextPassing.transponder = 654321;
        decoderId = 123456;
    }
        
    public Integer getDecoderId() {
        return decoderId;
    }

    public void setDecoderId(Integer decoderId) {
        Integer oldDecoderId = this.decoderId;
        this.decoderId = decoderId;
        pcs.firePropertyChange(PROP_DECODERID, oldDecoderId, decoderId);
    }


    public List<Decoder> getAllDecoders() {
        return allDecoders;
    }

    public Decoder getDecoder() {
        return decoder;
    }

    public void setDecoder(Decoder decoder) {
        Decoder old = this.decoder;
        this.decoder = decoder;
        pcs.firePropertyChange(PROP_DECODER, old, decoder);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Passing getNextPassing() {
        return nextPassing;
    }

    public void setNextPassing(Passing nextPassing) {
        Passing oldNextPassing = this.nextPassing;
        this.nextPassing = nextPassing;
        pcs.firePropertyChange(PROP_NEXTPASSING, oldNextPassing, nextPassing);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    final void refreshDecoderList() {
        try {
            allDecoders.clear();
            DecoderServices ds = Lookup.getDefault().lookup(DecoderServices.class);
            if (ds != null) {
                allDecoders.addAll(ds.getDecoders());
            }
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    
    public Integer getAutoNoOfDrivers() {
        return autoNoOfDrivers;
    }

    public void setAutoNoOfDrivers(Integer autoNoOfDrivers) {
        Integer oldAutoNoOfDrivers = this.autoNoOfDrivers;
        this.autoNoOfDrivers = autoNoOfDrivers;
        pcs.firePropertyChange(PROP_AUTONOOFDRIVERS, oldAutoNoOfDrivers, autoNoOfDrivers);
    }
    

    public Integer getAutoLaptime() {
        return autoLaptime;
    }

    public void setAutoLaptime(Integer autoLaptime) {
        Integer oldAutoLaptime = this.autoLaptime;
        this.autoLaptime = autoLaptime;
        pcs.firePropertyChange(PROP_AUTOLAPTIME, oldAutoLaptime, autoLaptime);
    }
    
    

    public Integer getAutoLapSpread() {
        return autoLapSpread;
    }

    public void setAutoLapSpread(Integer autoLapSpread) {
        Integer oldAutoLapSpread = this.autoLapSpread;
        this.autoLapSpread = autoLapSpread;
        pcs.firePropertyChange(PROP_AUTOLAPSPREAD, oldAutoLapSpread, autoLapSpread);
    }



    
    static class Passing {
    //"passingNumber":"3125","transponder":"95B15B","RTC_Time":"0","strength":"24","hits":"1B","flags":"1","recordType":"Passing","TOR":"1","crcOk":true,"unknownFields":{},"emptyFields":{},"VERSION":"2","SPARE":"3300","FLAGS":"0000","decoderId":"750E0400"}
        public Integer passingNumber;
        public Integer transponder;
        public Integer RTC_ID;
        public Long RTC_Time;
        public Long UTC_Time;
        public Short strength;
        public Short hits;
        public Short flags;
        public String transponderCode;
        public Integer userFlags;
        public Byte driverId;
        public Byte sport;
        public Byte voltage;
        public Byte temperature;
        public Byte carIdUnused;
        public Byte carId;

        public Integer getTransponder() {
            return transponder;
        }

        public void setTransponder(Integer transponder) {
            this.transponder = transponder;
        }
        
        
    }
}
