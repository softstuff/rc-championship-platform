/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rc.championship.platform.decoder.lap.publisher;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import rc.championship.api.model.Lap;
import rc.championship.api.services.LapManager;
import rc.championship.api.util.NbLogger;

/**
 *
 * @author Stefan
 */
public abstract class LapPublisher {

    public final static int MAX_QUEUE_SIZE = 1000;

    protected final NbLogger logger;
    protected final BlockingQueue<Lap> publishQueue;
    private boolean started;

    public LapPublisher() {
        publishQueue = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);
        logger = new NbLogger("Lap publisher", getClass());     
        
        LapManager manager = Lookup.getDefault().lookup(LapManager.class);
        if (manager == null) {
            logger.log(Level.SEVERE, "Cannot get a LapManager object");
            LifecycleManager.getDefault().exit();
            return;
        }
        manager.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            try {
                if(isEnabled()){
                   if(!started) {
                       start();
                   }
                    if ( evt.getPropertyName().equals(LapManager.PROP_NEW_LAP)) {
                        addToQueue((Lap) evt.getNewValue());
                    }
                }
            } catch (Exception ex) {
                logger.log(Level.WARNING, "Unexpected exception, " + ex.getMessage());
            }
        });
    }

    protected abstract String getName();
    
    protected void start() {
        started = true;
    }
     
    protected void stop() {
        started = false;
    }

    protected boolean isStarted() {
        return started;
    }

    
    private void addToQueue(Lap lap) {
        if (!publishQueue.offer(lap)) {
            logger.log(Level.WARNING, "Publish queue was full");
            queueFull();
        }
        if (publishQueue.offer(lap)) {
            publishQueue.add(lap);
            logger.log(Level.FINE, "Lap was added to %s queue", getName());
        }
    }

    protected void queueFull() {
        logger.log(Level.FINE, "Clear %s publish queue, %d removed", getName(), publishQueue.size());
        publishQueue.clear();
    }
    
    private Boolean enabled;
    public boolean isEnabled() {
        if(enabled == null){
            enabled = readEnabled();
        }
        return enabled;
    }
    private boolean readEnabled() {
        return NbPreferences.forModule(getClass()).getBoolean("publicher.enabled", true);
    }
    
    public void setEnabled(boolean enabled){
        if(enabled == this.enabled){
            return;
        }
        NbPreferences.forModule(getClass()).putBoolean("publicher.enabled", enabled);
        this.enabled = enabled;
    }

}
