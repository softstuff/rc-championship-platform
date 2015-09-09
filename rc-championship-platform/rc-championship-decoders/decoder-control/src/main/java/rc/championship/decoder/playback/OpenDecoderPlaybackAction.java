package rc.championship.decoder.playback;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;
import rc.championship.api.model.Decoder;

@ActionID(
        category = "Decoder",
        id = "rc.championship.decoder.playback.OpenDecoderPlaybackAction"
)
@ActionRegistration(
        iconBase = "icons/information.png",
        displayName = "#CTL_OpenDecoderPlaybackAction"
)
@Messages("CTL_OpenDecoderPlaybackAction=Playback")
public final class OpenDecoderPlaybackAction implements ActionListener {

    private final Decoder context;

    public OpenDecoderPlaybackAction(Decoder context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        
        DecoderPlaybackTopComponent outputWindow = (DecoderPlaybackTopComponent)WindowManager.getDefault().findTopComponent(DecoderPlaybackTopComponent.IDENTIFIER);
        if (outputWindow == null){
            outputWindow = new DecoderPlaybackTopComponent();
        }
        
        outputWindow.setDecoder(context);          
        if( !outputWindow.isOpened()){
            outputWindow.open();
        }
        outputWindow.requestActive(); 
    }
}
