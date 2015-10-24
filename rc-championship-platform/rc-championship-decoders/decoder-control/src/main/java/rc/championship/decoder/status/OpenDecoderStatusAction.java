package rc.championship.decoder.status;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import rc.championship.api.model.Decoder;

@ActionID(
        category = "Decoder",
        id = "rc.championship.decoder.status.OpenDecoderStatusAction"
)
@ActionRegistration(
        iconBase = "rc/championship/decoder/status/information.png",
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
        
        Set<TopComponent> opened = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent topComponent : opened) {
            if(topComponent instanceof DecoderStatusTopComponent){
                if(((DecoderStatusTopComponent)topComponent).getDecoder().equals(context)){
                    topComponent.open();
                    topComponent.requestActive();
                    return;
                }                    
            }
        }
        
        DecoderStatusTopComponent outputWindow = new DecoderStatusTopComponent();
        outputWindow.setDecoder(context);
        outputWindow.open();
        outputWindow.requestActive(); 
    }
}
