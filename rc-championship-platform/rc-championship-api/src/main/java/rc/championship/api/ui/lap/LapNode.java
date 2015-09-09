package rc.championship.api.ui.lap;

import java.util.Date;
import java.util.Optional;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;
import rc.championship.api.model.Lap;

public class LapNode extends AbstractNode {

    public LapNode(Lap lap) {
        super(Children.LEAF, Lookups.singleton(lap));
    }
    
    private Lap getLap(){
        return getLookup().lookup(Lap.class);
    }

    public Optional<Long> getNumber() {
        return getLap().getNumber();
    }

    public void setNumber(Optional<Long> number) {
        getLap().setNumber(number);
    }

    public Optional<Date> getTime() {
        return getLap().getTime();
    }

    public void setTime(Optional<Date> time) {
        getLap().setTime(time);
    }

    public Optional<Long> getStrength() {
        return getLap().getStrength();
    }

    public void setStrength(Optional<Long> strength) {
        getLap().setStrength(strength);
    }

    public Optional<Long> getHit() {
        return getLap().getHit();
    }

    public void setHit(Optional<Long> hit) {
        getLap().setHit(hit);
    }

    public Optional<Long> getDecoderId() {
        return getLap().getDecoderId();
    }

    public void setDecoderId(Optional<Long> decoderId) {
        getLap().setDecoderId(decoderId);
    }

    public Optional<Double> getVoltage() {
        return getLap().getVoltage();
    }

    public void setVoltage(Optional<Double> voltage) {
        getLap().setVoltage(voltage);
    }

    public Optional<Double> getTemprature() {
        return getLap().getTemprature();
    }

    public void setTemprature(Optional<Double> temprature) {
        getLap().setTemprature(temprature);
    }

    public Optional<Long> getTransponder() {
        return getLap().getTransponder();
    }

    public void setTransponder(Optional<Long> transponder) {
        getLap().setTransponder(transponder);
    }

    
    
}
