package rc.championship.decoder.playback;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;
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
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
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
@TopComponent.Registration(mode = "output", openAtStartup = true)
@ActionID(category = "Window", id = "rc.championship.decoder.playback.DecoderPlaybackTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_DecoderPlaybackAction",
        preferredID = "DecoderPlaybackTopComponent"
)
@Messages({
    "CTL_DecoderPlaybackAction=DecoderPlayback",
    "CTL_DecoderPlaybackTopComponent=DecoderPlayback Window",
    "HINT_DecoderPlaybackTopComponent=This is a DecoderPlayback window",
    "isConnected=Connected",
    "isDisconnected=Disconnected",
    "isPlaying=Playing",
    "StatusDisconnected=Disconnected",
    "ActionDisconnect=Disconnect",
    "StatusConnected=Connected",
    "ActionConnect=Connect",
    "NoPlayerForThisDecoderWasFound=No player for this decoder was found",
    "IsPlayingFromFile=Is playing from file" 
})
public final class DecoderPlaybackTopComponent extends TopComponent implements LookupListener{
    public static final String IDENTIFIER = "DecoderPlaybackTopComponent";
    
    private final Logger log = Logger.getLogger(getClass().getName());
    private final Lookup.Result<Decoder> lookupResult;
    
    private JFXPanel fxPanel;
    private MessageListViewController fxController;

    public DecoderPlaybackTopComponent() {
        
                
        lookupResult = Utilities.actionsGlobalContext().lookupResult(Decoder.class);
        
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
            URL location = getClass().getResource("MessageListView.fxml");
            FXMLLoader loader = new FXMLLoader(location);
            Parent root = (Parent) loader.load();
            fxController = loader.getController();
//            Scene scene = new Scene(root);
//            Stage newStage = new Stage();
//            newStage.setScene(newScene);
//            newStage.show();
            
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            fxmlLoader.setLocation(location);
//            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
//            
//            Parent root = fxmlLoader.load(location);
            Scene scene = new Scene(root, Color.LIGHTBLUE);
            fxPanel.setScene(scene);
//            fxController = fxmlLoader.getController();               
            fxController.setDecoders(lookupResult.allInstances());
        } catch(IOException ex){
            Exceptions.printStackTrace(ex);
        }
    }

    
    @Override
    protected void componentActivated() {
        lookupResult.removeLookupListener(this);
    }
    
    @Override
    protected void componentDeactivated() {
        lookupResult.addLookupListener(this);
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
        lookupResult.addLookupListener(this);
    }

    @Override
    public void componentClosed() {
        lookupResult.removeLookupListener(this);
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


    @Override
    public void resultChanged(LookupEvent ev) {        
        log.info("resultChanged");
        if(fxController != null){
            fxController.setDecoders(lookupResult.allInstances());
        }
    }
    
}
