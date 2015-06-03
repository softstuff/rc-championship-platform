package rc.championship.api.model;

/**
 *
 * @author Stefan
 */
public class Lap {
    private Transponder transponder;
    private long time;
    private int number;

    public Lap() {
    }

    public Lap(Transponder transponder, long time, int number) {
        this.transponder = transponder;
        this.time = time;
        this.number = number;
    }

    public Transponder getTransponder() {
        return transponder;
    }

    public void setTransponder(Transponder transponder) {
        this.transponder = transponder;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    
    
    
}
