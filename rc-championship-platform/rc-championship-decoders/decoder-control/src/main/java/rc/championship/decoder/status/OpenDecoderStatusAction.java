package rc.championship.decoder.status;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;
import rc.championship.api.model.Decoder;

@ActionID(
        category = "Decoder",
        id = "rc.championship.decoder.status.OpenDecoderStatusAction"
)
@ActionRegistration(
        iconBase = "icons/information.png",
        displayName = "#CTL_OpenDecoderStatusAction"
)
@Messages("CTL_OpenDecoderStatusAction=Status")
public final class OpenDecoderStatusAction implements ActionListener {

    private final Decoder context;

    public OpenDecoderStatusAction(Decoder context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        
        DecoderStatusTopComponent outputWindow = (DecoderStatusTopComponent)WindowManager.getDefault().findTopComponent(DecoderStatusTopComponent.IDENTIFIER);
        if (outputWindow == null){
            outputWindow = new DecoderStatusTopComponent();
        }
        
        outputWindow.setDecoder(context);          
        if( !outputWindow.isOpened()){
            outputWindow.open();
        }
        outputWindow.requestActive(); 
    }
}
