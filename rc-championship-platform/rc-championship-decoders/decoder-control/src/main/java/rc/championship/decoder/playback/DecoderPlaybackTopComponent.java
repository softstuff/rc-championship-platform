package rc.championship.decoder.playback;

import java.awt.BorderLayout;
import java.io.IOException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javax.swing.ActionMap;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import rc.championship.api.model.Decoder;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//rc.championship.decoder.playback//DecoderPlayback//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = DecoderPlaybackTopComponent.IDENTIFIER,
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "rc.championship.decoder.playback.DecoderPlaybackTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_DecoderPlaybackAction",
        preferredID = "DecoderPlaybackTopComponent"
)
@Messages({
    "CTL_DecoderPlaybackAction=DecoderPlayback",
    "CTL_DecoderPlaybackTopComponent=DecoderPlayback Window",
    "HINT_DecoderPlaybackTopComponent=This is a DecoderPlayback window"
})
public final class DecoderPlaybackTopComponent extends TopComponent {
    public static final String IDENTIFIER = "DecoderPlaybackTopComponent";
    
    private JFXPanel fxPanel;
    private Decoder decoder;

    public DecoderPlaybackTopComponent() {
        initComponents();
        setName(Bundle.CTL_DecoderPlaybackTopComponent());
        setToolTipText(Bundle.HINT_DecoderPlaybackTopComponent());
                
        ActionMap map = this.getActionMap ();
//        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
//        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
//        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
//        map.put("delete", ExplorerUtils.actionDelete(manager, true)); // or false

        // following line tells the top component which lookup should be associated with it
//        associateLookup (ExplorerUtils.createLookup (manager, map));   
        
        setLayout(new BorderLayout());
        init();
    }
    
    
    private void init() {
        fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        Platform.runLater(()-> createScene());
    }
    
    
    private void createScene() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MessageListView.fxml"));
            Scene scene = new Scene(root, Color.LIGHTBLUE);
            fxPanel.setScene(scene);
            
        } catch(IOException ex){
            Exceptions.printStackTrace(ex);
        }
    }

    
    @Override
    protected void componentActivated() {
        // It is good idea to switch all listeners on and off when the    // component is shown or hidden. In the case of TopComponent use:    protected void componentActivated() {
        
    }
    
    @Override
    protected void componentDeactivated() {
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 751, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 471, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    Decoder getDecoder() {
        return decoder;
    }

    void setDecoder(Decoder decoder) {
        this.decoder = decoder;
        loadNodes();
    }

    private void loadNodes() {
        
    }

    
}
