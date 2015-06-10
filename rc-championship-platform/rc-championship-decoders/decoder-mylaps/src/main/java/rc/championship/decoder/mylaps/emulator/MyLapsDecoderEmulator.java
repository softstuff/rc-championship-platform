package rc.championship.decoder.mylaps.emulator;

import java.io.OutputStream;
import org.openide.windows.IOProvider;
import rc.championship.api.services.decoder.DecoderEmulator;
import rc.championship.api.services.decoder.DecoderListener;
import rc.championship.api.services.decoder.DecoderMessage;


public class MyLapsDecoderEmulator implements DecoderEmulator {

    private boolean started;
    private boolean playing;
    private boolean paused;
    
    @Override
    public void register(DecoderListener listener) {
        log("register");
    }

    @Override
    public void unregister(DecoderListener listener) {
        log("unregister");
    }

    @Override
    public void send(DecoderMessage... messages) {
        log("send");
    }

    @Override
    public boolean isStarted() {
        log("isStarted");
        return started;
    }

    @Override
    public void startEmulator(String host, int port) {
        log("startEmulator");
        started = true;
    }

    @Override
    public void stopEmulator() {
        log("stopEmulator");
        started = false;
    }

    @Override
    public void play(OutputStream output) {
        log("play");
        playing = true;
    }

    @Override
    public void stop() {
        log("stop");
        playing = false;
    }

    @Override
    public boolean isPlaying() {
        log("isPlaying");
        return playing;
    }

    @Override
    public void pause() {
        log("pause");
        paused = true;
    }

    @Override
    public void resume() {
        log("resume");
        paused = false;
    }

    @Override
    public boolean isPaused() {
        log("isPaused");
        return paused;
    }
    
    private void log(String format, Object ... args){
        String msg = String.format(format, args);        
        IOProvider.getDefault().getIO("MyLaps emulator", false).getOut().println(msg);        
    }
}
