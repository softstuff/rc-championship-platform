package rc.championship.decoder.status;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderListener;
import rc.championship.api.services.decoder.DecoderMessage;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//rc.championship.decoder.status//DecoderStatus//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "DecoderStatusTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "rc.championship.decoder.status.DecoderStatusTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_DecoderStatusAction",
        preferredID = "DecoderStatusTopComponent"
)
@Messages({
    "CTL_DecoderStatusAction=DecoderStatus",
    "CTL_DecoderStatusTopComponent=DecoderStatus Window",
    "HINT_DecoderStatusTopComponent=This is a DecoderStatus window"
})
public final class DecoderStatusTopComponent extends TopComponent {

    private static final Logger LOG = Logger.getLogger(DecoderStatusTopComponent.class.getName());
    
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
        public void recived(DecoderMessage message, Decoder source) {
            appendDecoderLog(NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.ReceivedMsg", message, source));
        }

        @Override
        public void receivedCorruptData(Integer from, Integer start, String hexData, Decoder source) {
            appendDecoderLog(NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.ReceivedCorruptData",from, start, hexData, source));
        }
        
        

        @Override
        public void transmitted(DecoderMessage message, Decoder source) {
            appendDecoderLog(NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.TransmittMsg", message, source));
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
        
    private void appendDecoderLog(String row){
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
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

        model = new rc.championship.decoder.status.StatusPresentationModel();
        jComboBox1 = new javax.swing.JComboBox();
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
        jLabel5 = new javax.swing.JLabel();
        refreshButton = new javax.swing.JButton();

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${allDecoders}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, model, eLProperty, jComboBox1);
        bindingGroup.addBinding(jComboBoxBinding);
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, model, org.jdesktop.beansbinding.ELProperty.create("${decoder}"), jComboBox1, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(statusLabel, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.statusLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(connectButton, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.connectButton.text")); // NOI18N
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(hostLabel, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.hostLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(portLabel, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.portLabel.text")); // NOI18N

        decoderLogTextArea.setColumns(20);
        decoderLogTextArea.setRows(5);
        jScrollPane1.setViewportView(decoderLogTextArea);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
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
                        .addComponent(connectButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.jLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(refreshButton, org.openide.util.NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.refreshButton.text")); // NOI18N
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refreshButton)
                        .addGap(0, 158, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5)
                    .addComponent(refreshButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        Decoder decoder = model.getDecoder();
        if(decoder != null ){       
            if(decoder.isConnected()){
                appendDecoderLog(NbBundle.getMessage(DecoderStatusTopComponent.class, "DecoderStatusTopComponent.ClickDisconnect"));
                decoder.disconnect();
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

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        model.refreshDecoderList();
    }//GEN-LAST:event_refreshButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton connectButton;
    private javax.swing.JTextArea decoderLogTextArea;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private rc.championship.decoder.status.StatusPresentationModel model;
    private javax.swing.JLabel portLabel;
    private javax.swing.JButton refreshButton;
    private javax.swing.JLabel statusLabel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
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
}
