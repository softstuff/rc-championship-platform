package rc.championship.decoder.status;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderListener;
import rc.championship.api.services.decoder.DecoderMessage;
import rc.championship.api.services.decoder.DecoderServices;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//rc.championship.decoder.status//DecoderStatus//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = DecoderStatusTopComponent.IDENTIFIER,
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
//@ActionID(category = "Window", id = "rc.championship.decoder.status.DecoderStatusTopComponent")
//@ActionReference(path = "Menu/Window" /*, position = 333 */)
//@TopComponent.OpenActionRegistration(
//        displayName = "#CTL_DecoderStatusAction",
//        preferredID = "DecoderStatusTopComponent"
//)
@Messages({
    "CTL_DecoderStatusAction=DecoderStatus",
    "CTL_DecoderStatusTopComponent=DecoderStatus Window",
    "HINT_DecoderStatusTopComponent=This is a DecoderStatus window"
})
public final class DecoderStatusTopComponent extends TopComponent {

    private static final Logger LOG = Logger.getLogger(DecoderStatusTopComponent.class.getName());
    public static final String IDENTIFIER = "DecoderStatusTopComponent";
    
    public static String generatePerferedTcIdFor(Decoder decoder) {
        return DecoderStatusTopComponent.IDENTIFIER+"_"+decoder.getIdentifyer();
    }
    StringBuilder decoderLog = new StringBuilder();
    private DecoderListener connectorListener = new DecoderListener() {

        @Override
        public void connected(Decoder source) {
            statusLabel.setText(NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.Connected"));
            connectButton.setText(NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.Disconnect"));
            appendDecoderLog(NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.Connected"));
        }

        @Override
        public void disconnected(String reason, Decoder source) {
            statusLabel.setText(NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.Disconnected"));
            connectButton.setText(NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.Connect"));
            appendDecoderLog(NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.Disconnected", reason));
        }

        @Override
        public void recived(DecoderMessage message) {
            appendDecoderLog(NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.ReceivedMsg", MessageRenderer.wrap(message), message.getDecoder()));
            if(message.getCommand() == DecoderMessage.Command.Status){
                voltageLabel.setText(message.getDouble("inputVoltage").get()/10+"");
                noiseLabel.setText(message.getHexInt("noise").get()+"");
                temperatureLabel.setText(message.getHexInt("temperature").get()+"");
            }
        }

        @Override
        public void receivedCorruptData(Integer from, Integer start, String hexData, Decoder source) {
            appendDecoderLog(NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.ReceivedCorruptData",from, start, hexData, source));
        }

        @Override
        public void transmitted(DecoderMessage message) {
            appendDecoderLog(NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.TransmittMsg", message, message.getDecoder()));
        }

        @Override
        public void playbackStarted(Decoder source, File file) {
            appendDecoderLog("Playback stared");
        }
        
        @Override
        public void playbackEnded(Decoder source, File file) {
            appendDecoderLog("Playback ended");
        }

        
        
        
    };
    
    
    public DecoderStatusTopComponent() {
        initComponents();
        setName(Bundle.CTL_DecoderStatusTopComponent());
        setToolTipText(Bundle.HINT_DecoderStatusTopComponent());
        
        
        model.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(StatusPresentationModel.PROP_DECODER)){
                    if(evt.getOldValue() != null){
                        ((Decoder)evt.getOldValue()).unregister(connectorListener);
                    }
                    if(evt.getNewValue() != null){
                        ((Decoder)evt.getNewValue()).register(connectorListener);
                    }
                    decoderLog = new StringBuilder();
                    appendDecoderLog("Selected decoder "+evt.getNewValue());
                }
            }
        });
    }

    @Override
    protected String preferredID() {
        return generatePerferedTcIdFor(model.getDecoder());
    }
    
    
    
    public Decoder getDecoder(){
        return model.getDecoder();
    }
    
    public void setDecoder(Decoder decoder) {
        
        model.setDecoder(decoder);
        invalidate();
    }
        
    private void appendDecoderLog(String row){
        DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        decoderLog.append(String.format("%s - %s%n", df.format(new Date()), row));
        decoderLogTextArea.setText(decoderLog.toString());
        decoderLogTextArea.invalidate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        model = createModel();
        decoderComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        connectButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        hostLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        portLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        decoderLogTextArea = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        voltageLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        noiseLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        temperatureLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();

        decoderComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decoderComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.jLabel2.text")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, model, org.jdesktop.beansbinding.ELProperty.create("${decoder.connected}"), statusLabel, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.openide.awt.Mnemonics.setLocalizedText(connectButton, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.connectButton.text")); // NOI18N
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.jLabel3.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, model, org.jdesktop.beansbinding.ELProperty.create("${decoder.host}"), hostLabel, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.jLabel4.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, model, org.jdesktop.beansbinding.ELProperty.create("${decoder.port}"), portLabel, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        decoderLogTextArea.setColumns(20);
        decoderLogTextArea.setRows(5);
        jScrollPane1.setViewportView(decoderLogTextArea);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.jLabel6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(voltageLabel, "12.0"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.jLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(noiseLabel, "23"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.jLabel8.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(temperatureLabel, "21"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.jLabel9.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, model, org.jdesktop.beansbinding.ELProperty.create("${decoder.decoderName}"), nameLabel, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4))
                                .addGap(24, 24, 24)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(portLabel)
                                    .addComponent(hostLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(connectButton))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(voltageLabel))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(noiseLabel))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(temperatureLabel))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 411, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(hostLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(portLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(statusLabel)
                    .addComponent(connectButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(voltageLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(noiseLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(temperatureLabel))
                .addGap(67, 67, 67)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(decoderComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(decoderComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 492, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        Decoder decoder = model.getDecoder();
        if(decoder != null ){       
            if(decoder.isConnected()){
                appendDecoderLog(NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.ClickDisconnect"));
                decoder.disconnect("User selected");
            } else {
                appendDecoderLog(NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.ClickConnect"));
                try{
                    decoder.connect();
                } catch(IOException ex){
                    appendDecoderLog(NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.ConnectionFailed",ex.getMessage()));
                    LOG.log(Level.WARNING, "failed to connect to decoder: "+decoder, ex);
                }
            }
        }
    }//GEN-LAST:event_connectButtonActionPerformed

    private void decoderComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decoderComboBoxActionPerformed

//        Decoder decoder = model.getDecoder();
//        if(decoder == null){
//            controlPanel.setVisible(false);
//            return;
//        }
//        Optional<DecoderConnectionFactory> factory = decoder.getConnectorFactory();
//        if(factory == null || !factory.isPresent()){
//            controlPanel.setVisible(false);
//            return;
//        }
//        recorder = factory.get().createRecorder();
//        controlPanel.setVisible(true);
    }//GEN-LAST:event_decoderComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton connectButton;
    private javax.swing.JComboBox decoderComboBox;
    private javax.swing.JTextArea decoderLogTextArea;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private rc.championship.decoder.status.StatusPresentationModel model;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel noiseLabel;
    private javax.swing.JLabel portLabel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel temperatureLabel;
    private javax.swing.JLabel voltageLabel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        model.refreshDecoderList();
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        if(model.getDecoder() != null){
            p.setProperty("decoderId", model.getDecoder().getIdentifyer());
        }
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        String decoderId = p.getProperty("decoderId");
        
        if(decoderId != null) {
            DecoderServices ds = Lookup.getDefault().lookup(DecoderServices.class);
            Optional<Decoder> decoderFromStorage;
            try {
                decoderFromStorage = ds.getDecoders(decoderId);
                if(decoderFromStorage.isPresent()){
                    model.setDecoder(decoderFromStorage.get());
                } else {
                    model.setDecoder(null);
                }

            } catch (BackingStoreException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        // TODO read your settings according to their version
    }

    private StatusPresentationModel createModel() {
        if(model == null){
            model = new rc.championship.decoder.status.StatusPresentationModel();
        }
        return model;
    }
}
