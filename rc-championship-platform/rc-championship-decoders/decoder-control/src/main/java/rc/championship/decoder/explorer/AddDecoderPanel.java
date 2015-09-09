package rc.championship.decoder.explorer;

import java.awt.Component;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import org.openide.util.Lookup;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderConnectionFactory;

/**
 *
 * @author Stefan
 */
public class AddDecoderPanel extends javax.swing.JPanel {

    /**
     * Creates new form AddDecoderPanel
     */
    public AddDecoderPanel() {
        initComponents();
        Collection<? extends DecoderConnectionFactory> factories = Lookup.getDefault().lookupAll(DecoderConnectionFactory.class);
        factories.forEach( factory -> factoryComboBox.addItem(factory));
        if(!factories.isEmpty()){
            factoryComboBox.setSelectedIndex(0);
        }
        factoryComboBox.setRenderer(new BasicComboBoxRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof DecoderConnectionFactory){
                    DecoderConnectionFactory factory = (DecoderConnectionFactory)value;
                    setText(factory.getDecoderName());
                } 
                return component;
            }

           
        });
    }
    
    public Decoder createDecoder(){
        String sPort = portValue.getText();
        int port = Integer.valueOf(sPort);
        DecoderConnectionFactory factory = (DecoderConnectionFactory)factoryComboBox.getSelectedItem();
        return new Decoder(
                hostValue.getText(),
                port, 
                factory.getDecoderName(),
                Optional.of(factory),
                UUID.randomUUID().toString());
    } 

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        hostLabel = new javax.swing.JLabel();
        hostValue = new javax.swing.JTextField();
        portLabel = new javax.swing.JLabel();
        portValue = new javax.swing.JTextField();
        typeLabel = new javax.swing.JLabel();
        factoryComboBox = new javax.swing.JComboBox();

        org.openide.awt.Mnemonics.setLocalizedText(hostLabel, org.openide.util.NbBundle.getMessage(AddDecoderPanel.class, "AddDecoderPanel.hostLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(portLabel, org.openide.util.NbBundle.getMessage(AddDecoderPanel.class, "AddDecoderPanel.portLabel.text")); // NOI18N

        portValue.setText(org.openide.util.NbBundle.getMessage(AddDecoderPanel.class, "AddDecoderPanel.portValue.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(typeLabel, org.openide.util.NbBundle.getMessage(AddDecoderPanel.class, "AddDecoderPanel.typeLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(factoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(hostLabel)
                                    .addComponent(typeLabel))
                                .addGap(232, 232, 232)
                                .addComponent(portLabel)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(hostValue, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(portValue, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hostLabel)
                    .addComponent(portLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(portValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hostValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(typeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(factoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox factoryComboBox;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JTextField hostValue;
    private javax.swing.JLabel portLabel;
    private javax.swing.JTextField portValue;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables
}
