package rc.championship.api.model;

import java.util.Date;
import java.util.Optional;

/**
 *
 * @author Stefan
 */
public class Lap {

    private Optional<Long> id;
    private Optional<Long> number;
    private Optional<Date> time;
    private Optional<Long> strength;
    private Optional<Long> hit;
    private Optional<Long> decoderId;
    private Optional<Double> voltage;
    private Optional<Double> temprature;
    private Optional<Long> transponder;
    

    public Lap() {
    }

    public Lap(Optional<Long> id, Optional<Long> number, Optional<Date> time, Optional<Long> strength, Optional<Long> hit, Optional<Long> decoderId, Optional<Double> voltage, Optional<Double> temprature, Optional<Long> transponder) {
        this.id = id;
        this.number = number;
        this.time = time;
        this.strength = strength;
        this.hit = hit;
        this.decoderId = decoderId;
        this.voltage = voltage;
        this.temprature = temprature;
        this.transponder = transponder;
    }
    

    public Optional<Long> getId() {
        return id;
    }

    public void setId(Optional<Long> id) {
        this.id = id;
    }

    
    
    public Optional<Long> getNumber() {
        return number;
    }

    public void setNumber(Optional<Long> number) {
        this.number = number;
    }

    public Optional<Date> getTime() {
        return time;
    }

    public void setTime(Optional<Date> time) {
        this.time = time;
    }

    public Optional<Long> getStrength() {
        return strength;
    }

    public void setStrength(Optional<Long> strength) {
        this.strength = strength;
    }

    public Optional<Long> getHit() {
        return hit;
    }

    public void setHit(Optional<Long> hit) {
        this.hit = hit;
    }

    public Optional<Long> getDecoderId() {
        return decoderId;
    }

    public void setDecoderId(Optional<Long> decoderId) {
        this.decoderId = decoderId;
    }

    public Optional<Double> getVoltage() {
        return voltage;
    }

    public void setVoltage(Optional<Double> voltage) {
        this.voltage = voltage;
    }

    public Optional<Double> getTemprature() {
        return temprature;
    }

    public void setTemprature(Optional<Double> temprature) {
        this.temprature = temprature;
    }

    public Optional<Long> getTransponder() {
        return transponder;
    }

    public void setTransponder(Optional<Long> transponder) {
        this.transponder = transponder;
    }

    
}
