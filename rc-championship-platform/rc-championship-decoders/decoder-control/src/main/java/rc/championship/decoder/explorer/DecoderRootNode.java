package rc.championship.decoder.explorer;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import javax.swing.Action;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.actions.NewAction;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;
import org.openide.util.lookup.Lookups;
import rc.championship.api.model.Decoder;

/**
 *
 * @author Stefan
 */
@NbBundle.Messages({
    "LBL_NodeName=Decoders"})
public class DecoderRootNode extends AbstractNode {

    public DecoderRootNode(DecoderFactory factory) {
        super(Children.create(factory, true), Lookups.singleton(factory));
        setName("Decoders");
    }

    private DecoderFactory getNodeFactory() {
        return getLookup().lookup(DecoderFactory.class);
    }

    //https://platform.netbeans.org/tutorials/nbm-feedreader.html

    @Override
    public Action[] getActions(boolean bln) {
        return new Action[]{
            SystemAction.get(NewAction.class)
        };
//        List<? extends Action> rootActions = Utilities.actionsForPath("Actions/RootActions");
//        return rootActions.toArray(new Action[rootActions.size()]);
    }
//    @Override
//    public Action[] getActions(boolean context) {
//        return new Action[]{SystemAction.get(DeleteAction.class)};
//    }

    @NbBundle.Messages({
        "LBL_NewProp=New connection",
        "LBL_NewProp_dialog_title1=Host:",
        "LBL_NewProp_dialog_title2=Port:",
        "LBL_NewProp_dialog_title3=Name:",
        "LBL_NewProp_dialog_title4=Decoder:",
        "MSG_NewProp_dialog_host=Set Cluster Host",
        "MSG_NewProp_dialog_port=Set Cluster Port"})
    @Override
    public NewType[] getNewTypes() {
        return new NewType[]{
            new NewType() {
                @Override
                public String getName() {
                    return Bundle.LBL_NewProp();
                }

                @Override
                public void create() throws IOException {
                    AddDecoderPanel panel = new AddDecoderPanel();
                    DialogDescriptor dialogDescriptor = new DialogDescriptor(panel, Bundle.LBL_NewProp());
                    Object result = DialogDisplayer.getDefault().notify(dialogDescriptor);
                    if (result != NotifyDescriptor.OK_OPTION) {
                        return;
                    }
                    Decoder newDecoder = panel.createDecoder();

                    try {
                        DecoderFactory nodeFactory = getNodeFactory();
                        nodeFactory.addNewDecoder(newDecoder);

                        StatusDisplayer.getDefault().setStatusText(newDecoder.getDecoderName() + " saved");

//                    NbPreferences.forModule(CassandraRootNode.class).put("cassandraCluster", key+":"+value);
//                    PropertiesNotifier.changed();
                    } catch (BackingStoreException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        };
    }

}
