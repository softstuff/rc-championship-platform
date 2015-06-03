package rc.championship.mylaps.emulator;

import java.io.IOException;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.IOProvider;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//rc.championship.mylaps.emulator//MyLapsDecoderEmulator//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "MyLapsDecoderEmulatorTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "rc.championship.mylaps.emulator.MyLapsDecoderEmulatorTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_MyLapsDecoderEmulatorAction",
        preferredID = "MyLapsDecoderEmulatorTopComponent"
)
@Messages({
    "CTL_MyLapsDecoderEmulatorAction=MyLapsDecoderEmulator",
    "CTL_MyLapsDecoderEmulatorTopComponent=MyLapsDecoderEmulator Window",
    "HINT_MyLapsDecoderEmulatorTopComponent=This is a MyLapsDecoderEmulator window"
})
public final class MyLapsDecoderEmulatorTopComponent extends TopComponent implements TransferListener {

    
    public MyLapsDecoderEmulatorTopComponent() {
        initComponents();
        setName(Bundle.CTL_MyLapsDecoderEmulatorTopComponent());
        setToolTipText(Bundle.HINT_MyLapsDecoderEmulatorTopComponent());
        emulator.registerListener((TransferListener)this);
        
        jTextFieldPort.setInputVerifier(new InputVerifier() {

            @Override
            public boolean verify(JComponent input) {
                 String text = ((JTextField) input).getText();
                try {
                    int value = Integer.parseInt(text);
                    return value > 1024 && value < 65000;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        emulator = new rc.championship.mylaps.emulator.P4DecoderEmulator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabelHost = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldPort = new javax.swing.JTextField();
        startStopButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MyLapsDecoderEmulatorTopComponent.class, "MyLapsDecoderEmulatorTopComponent.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(MyLapsDecoderEmulatorTopComponent.class, "MyLapsDecoderEmulatorTopComponent.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabelHost, org.openide.util.NbBundle.getMessage(MyLapsDecoderEmulatorTopComponent.class, "MyLapsDecoderEmulatorTopComponent.jLabelHost.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(MyLapsDecoderEmulatorTopComponent.class, "MyLapsDecoderEmulatorTopComponent.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(startStopButton, org.openide.util.NbBundle.getMessage(MyLapsDecoderEmulatorTopComponent.class, "MyLapsDecoderEmulatorTopComponent.startStopButton.text")); // NOI18N
        startStopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startStopButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelHost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(startStopButton)
                                    .addComponent(jTextFieldPort, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabelHost))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(startStopButton)
                .addContainerGap(30, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void startStopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startStopButtonActionPerformed
        
        if(emulator.isRunning()){
            emulator.stop();
            startStopButton.setText( NbBundle.getMessage(MyLapsDecoderEmulatorTopComponent.class, "MyLapsDecoderEmulatorTopComponent.startStopButton.running.text") );
        } else {
            try {
                String text = jTextFieldPort.getText();
                int port = Integer.parseInt(text);
                emulator.start(port);
                startStopButton.setText( NbBundle.getMessage(MyLapsDecoderEmulatorTopComponent.class, "MyLapsDecoderEmulatorTopComponent.startStopButton.text") );
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_startStopButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rc.championship.mylaps.emulator.P4DecoderEmulator emulator;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelHost;
    private javax.swing.JTextField jTextFieldPort;
    private javax.swing.JButton startStopButton;
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

    @Override
    public void sent(String data, ClientConnection source) {
        log("%s sent: %s%n", source, data);
    }

    @Override
    public void recived(String data, ClientConnection source) {
        log("%s recived: %s%n", source, data);
    }

    @Override
    public void clientConnected(ClientConnection connection) {
        log("clientConnected %s%n", connection);
    }

    @Override
    public void clientDisconnected(ClientConnection connection) {
        log("clientDisconnected %s%n", connection);
    }

    private void log(String format, Object ... args){
        IOProvider.getDefault().getIO("MyLaps emulator", false).getOut().format(format, args);
    }
}
