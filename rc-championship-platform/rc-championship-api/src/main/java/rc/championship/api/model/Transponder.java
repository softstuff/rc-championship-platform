package rc.championship.api.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Stefan
 */
public class Transponder {
    public static final String DRIVER_NAME_PROP = "driverName";
    public static final String VEHICLE_CLASS_PROP = "vehicleClass";
    public static final String IDENTITY_PROP = "identity";
    public static final String VOLTAGE_PROP = "voltage";
    public static final String TEMP_PROP = "temp";
    
    
    private String driverName;
    private String vehicleClass;
    private String identity;
    private Float voltage;
    private Float temp;
    
    private final List listeners = Collections.synchronizedList(new LinkedList());

    public Transponder(String driverName, String vehicleClass, String identity) {
        this.driverName = driverName;
        this.vehicleClass = vehicleClass;
        this.identity = identity;
    }

    
    
    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getVehicleClass() {
        return vehicleClass;
    }

    public void setVehicleClass(String vehicleClass) {
        fire(VEHICLE_CLASS_PROP, this.vehicleClass, this.vehicleClass = vehicleClass);
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        fire(IDENTITY_PROP, this.identity, this.identity = identity);
    }

    public Float getVoltage() {
        return voltage;
    }

    public void setVoltage(Float voltage) {
        fire(VOLTAGE_PROP, this.voltage, this.voltage = voltage);
    }

    public Float getTemp() {
        return temp;
    }

    public void setTemp(Float temp) {
        fire(TEMP_PROP, this.temp, this.temp = temp);
    }

    @Override
    public String toString() {
        return "Transponder{" + "driverName=" + driverName + ", identity=" + identity + '}';
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        listeners.add(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        listeners.remove(pcl);
    }

    private void fire(String propertyName, Object old, Object nue) {
        //Passing 0 below on purpose, so you only synchronize for one atomic call:
        PropertyChangeListener[] pcls = (PropertyChangeListener[]) listeners.toArray(new PropertyChangeListener[0]);
        for (int i = 0; i < pcls.length; i++) {
            pcls[i].propertyChange(new PropertyChangeEvent(this, propertyName, old, nue));
        }
    }
    
    
}
