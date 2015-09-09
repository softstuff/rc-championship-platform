package rc.championship.decoder.explorer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

/**
 *
 * @author Stefan
 */

//http://wiki.netbeans.org/DeclarativeActionRegistration
@ActionRegistration(
  displayName="#CTL_AddDecoderAction"
)
@ActionID(
  category="RootActions",
  id="add.decoder.action" 
)
@NbBundle.Messages({
    "CTL_AddDecoderAction=Add decoder"
})
public class AddDecoderAction implements ActionListener {

    
    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null, "Hello from " );
    }
    
}
