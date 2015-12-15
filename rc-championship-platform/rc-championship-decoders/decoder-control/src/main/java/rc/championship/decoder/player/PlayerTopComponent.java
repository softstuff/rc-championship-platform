package rc.championship.decoder.player;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderListener;
import rc.championship.api.services.decoder.DecoderManager;
import rc.championship.api.services.decoder.DecoderMessage;
import rc.championship.api.services.decoder.DecoderPlayer;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//rc.championship.decoder.player//Player//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "PlayerTopComponent",
        iconBase = "icons/control_play.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "properties", openAtStartup = true)
@ActionID(category = "Window", id = "rc.championship.decoder.player.PlayerTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_PlayerAction",
        preferredID = "PlayerTopComponent"
)
@Messages({
    "CTL_PlayerAction=Player",
    "CTL_PlayerTopComponent=Player Window",
    "HINT_PlayerTopComponent=This is a Player window",
    "NoDecoderSelected=No decoder selected",
    "MoreThenOneDecoderSelected=More then one decoder selected",
    "StatusDisconnected=Disconnected",
    "ActionDisconnect=Disconnect",
    "StatusConnected=Connected",
    "ActionConnect=Connect",
    "NoPlayerForThisDecoderWasFound=No player for this decoder was found",
    "IsPlayingFromFile=Is playing from file" 
})
public final class PlayerTopComponent extends TopComponent implements LookupListener, DecoderListener {

    private final Logger log = Logger.getLogger(getClass().getName());
    private final Lookup.Result<Decoder> lookupResult;
    private Decoder decoder;
    private DecoderPlayer player;
    private String errorMessage;

    public PlayerTopComponent() {
        initComponents();
        setName(Bundle.CTL_PlayerTopComponent());
        setToolTipText(Bundle.HINT_PlayerTopComponent());
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_KEEP_PREFERRED_SIZE_WHEN_SLIDED_IN, Boolean.TRUE);

        lookupResult = Utilities.actionsGlobalContext().lookupResult(Decoder.class);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        decoderLabel = new javax.swing.JLabel();
        playerPanel = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        connectButton = new javax.swing.JButton();
        playFileButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(decoderLabel, org.openide.util.NbBundle.getMessage(PlayerTopComponent.class, "PlayerTopComponent.decoderLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(statusLabel, org.openide.util.NbBundle.getMessage(PlayerTopComponent.class, "PlayerTopComponent.statusLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(PlayerTopComponent.class, "PlayerTopComponent.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(connectButton, org.openide.util.NbBundle.getMessage(PlayerTopComponent.class, "PlayerTopComponent.connectButton.text")); // NOI18N
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(playFileButton, org.openide.util.NbBundle.getMessage(PlayerTopComponent.class, "PlayerTopComponent.playFileButton.text")); // NOI18N
        playFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playFileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout playerPanelLayout = new javax.swing.GroupLayout(playerPanel);
        playerPanel.setLayout(playerPanelLayout);
        playerPanelLayout.setHorizontalGroup(
            playerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(playerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(playerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(playFileButton)
                    .addComponent(connectButton)
                    .addComponent(statusLabel))
                .addContainerGap(126, Short.MAX_VALUE))
        );
        playerPanelLayout.setVerticalGroup(
            playerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(playerPanelLayout.createSequentialGroup()
                .addGroup(playerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(statusLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(connectButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(playFileButton)
                .addGap(0, 318, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(playerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(decoderLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(decoderLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        
        new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                if(player.isConnected()){
                    player.disconnect("user selecton");
                    errorMessage = null;
                } else {
                    try{
                        player.connect();
                        errorMessage = null;
                    } catch(IOException ex){
                        errorMessage = "Failed to connect, "+ex.getMessage();
                        log.log(Level.WARNING, errorMessage, ex);
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                updateView();
            }
            
        }.execute();
        
    }//GEN-LAST:event_connectButtonActionPerformed

    private void playFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playFileButtonActionPerformed
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File file = fileChooser.getSelectedFile();
            new SwingWorker<Void, Void>(){

                @Override
                protected Void doInBackground() throws Exception {
                    
                    try{
                        player.play(file);
                        errorMessage = null;
                    } catch(IOException ex){
                        errorMessage = "Failed to connect, "+ex.getMessage();
                        log.log(Level.WARNING, errorMessage, ex);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    updateView();
                }                
            };
        } 
    }//GEN-LAST:event_playFileButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton connectButton;
    private javax.swing.JLabel decoderLabel;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton playFileButton;
    private javax.swing.JPanel playerPanel;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        log.info("componentOpened");
        lookupResult.addLookupListener(this);
        updateDecoderStatus();
    }

    @Override
    public void componentClosed() {
        log.info("componentClosed");
        lookupResult.removeLookupListener(this);
    }

    @Override
    protected void componentActivated() {
        log.info("componentActivated");
        lookupResult.removeLookupListener(this);        
        super.componentActivated(); 
    }
    
    @Override
    protected void componentDeactivated() {
        log.info("componentDeactivated");
        lookupResult.addLookupListener(this);
        updateDecoderStatus();
        super.componentDeactivated(); 
    }
    
    @Override
    protected void componentHidden() {
        log.info("componentHidden");
        lookupResult.removeLookupListener(this);
        super.componentHidden(); 
    }

    @Override
    protected void componentShowing() {
        log.info("componentShowing");
        updateDecoderStatus();
        super.componentShowing(); 
    }
    
    

    void writeProperties(java.util.Properties p) {
        log.info("writeProperties");
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        log.info("readProperties");
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        log.info("resultChanged");
        updateDecoderStatus();
    }

    private void updateDecoderStatus() {
        errorMessage = null;
        log.info("updateDecoderStatus");
        Collection<? extends Decoder> decoders = lookupResult.allInstances();
        if (decoders.isEmpty()) {
            errorMessage = Bundle.NoDecoderSelected();
        } else if (decoders.size() > 1) {
            errorMessage = Bundle.MoreThenOneDecoderSelected();
        } else {
            setDecoder(decoders.iterator().next());
        }
        updateView();
    }

    private void setDecoder(Decoder decoder) {
        if (this.decoder != decoder) {
            log.info("setDecoder "+decoder);
            if(player != null){
                player.unregister(this);
            }
            this.decoder = decoder;
            Optional<DecoderPlayer> optPlayer = DecoderManager.getDefaultPlayer(decoder);
            if (!optPlayer.isPresent()) {
                errorMessage = Bundle.NoPlayerForThisDecoderWasFound();
            } else {                
                player = optPlayer.get();
                player.register(this);
            }   
        }
    }

    private void updateView() {       
        if(errorMessage != null){
            decoderLabel.setText(errorMessage);
            playerPanel.setVisible(false);
        } else {
            decoderLabel.setText(decoder.getDisplayName());
            playerPanel.setVisible(true);

            if(player.isPlaying()){
                statusLabel.setText(Bundle.IsPlayingFromFile());
            } else {
                if( player.isConnected()){
                    statusLabel.setText(Bundle.StatusConnected());
                    connectButton.setText(Bundle.ActionDisconnect());

                } else {
                    statusLabel.setText(Bundle.StatusDisconnected());
                    connectButton.setText(Bundle.ActionConnect());
                }            
            }
        }
    }

    @Override
    public void connected(Decoder source) {
        if(source == this.decoder){
            updateView();
        }
    }

    @Override
    public void disconnected(String reason, Decoder source) {
        if(source == this.decoder){
            updateView();
            statusLabel.setText("Disconnected: "+reason);
        }
    }

    @Override
    public void recived(DecoderMessage message) {
    }

    @Override
    public void transmitted(DecoderMessage message) {
    }

    @Override
    public void receivedCorruptData(Integer from, Integer start, String hexData, Decoder source) {
    }

    @Override
    public void playbackStarted(Decoder source, File file) {
    }

    @Override
    public void playbackEnded(Decoder source, File file) {
    }
}