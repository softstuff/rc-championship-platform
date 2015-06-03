package rc.championship.api.ui;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.NAME;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.apache.commons.lang3.StringUtils;
import org.openide.ErrorManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.WeakListeners;
import org.openide.util.actions.Presenter;
import org.openide.util.lookup.Lookups;
import rc.championship.api.model.Transponder;

/**
 *
 * @author Stefan
 */
public class TransponderNode extends AbstractNode implements PropertyChangeListener {

    public TransponderNode(Transponder transponder) {
        super(Children.LEAF, Lookups.singleton(transponder));
        setDisplayName(StringUtils.isEmpty(transponder.getDriverName()) ? transponder.getIdentity() : transponder.getDriverName() );
        registerForPropertyChangeEvent(transponder);
    }
    
    private void registerForPropertyChangeEvent(Transponder transponder){
        transponder.addPropertyChangeListener(WeakListeners.propertyChange(this, transponder));
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("date".equals(evt.getPropertyName())) {
            this.fireDisplayNameChange(null, getDisplayName());
        }
    }


    @Override
    public Action[] getActions(boolean popup) {
        return new Action[]{new MyAction()};
    }

    private class MyAction extends AbstractAction implements Presenter.Popup {

        public MyAction() {
            putValue(NAME, "Do Something");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //not used
        }

        @Override
        public JMenuItem getPopupPresenter() {
            JMenu result = new JMenu("Submenu");  //remember JMenu is a subclass of JMenuItem
            result.add(new JMenuItem(this));
            result.add(new JMenuItem(this));
            return result;
        }

    }

    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        Transponder transponder = getLookup().lookup(Transponder.class);

        try {

            Property driverNameProp = new PropertySupport.Reflection(transponder, String.class, "driverName");
            Property identityProp = new PropertySupport.Reflection(transponder, String.class, "identity");

            driverNameProp.setName("driverName");
            identityProp.setName("identity");
            set.put(driverNameProp);
            set.put(identityProp);

        } catch (NoSuchMethodException ex) {
            ErrorManager.getDefault();
        }

        sheet.put(set);
        return sheet;

    }

    
}
