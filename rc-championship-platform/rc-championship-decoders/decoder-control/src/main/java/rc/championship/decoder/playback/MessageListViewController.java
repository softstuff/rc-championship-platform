package rc.championship.decoder.playback;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import org.openide.util.Lookup;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderManager;
import rc.championship.api.services.decoder.DecoderMessage;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class MessageListViewController implements Initializable, PropertyChangeListener{

    private final Logger log = Logger.getLogger(getClass().getName());
    private DecoderManager decoderManager;
    private Collection<? extends Decoder> decoders;
    
    private ObservableList<DecoderMsgRowModel> rows;
    
    @FXML
    private Button loadButton;
        
    @FXML
    private Button downloadButton;

    @FXML
    private TableView<DecoderMsgRowModel> tableView;

    @FXML
    private TableColumn<DecoderMsgRowModel, SimpleStringProperty> timeColumn;

    @FXML
    private TableColumn<DecoderMsgRowModel, SimpleStringProperty> typeColumn;

    @FXML
    private TableColumn<DecoderMsgRowModel, SimpleStringProperty> dataColumn;

    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert loadButton != null : "fx:id=\"loadButton\" was not injected: check your FXML file 'MessageListView.fxml'.";
        assert downloadButton != null : "fx:id=\"downloadButton\" was not injected: check your FXML file 'MessageListView.fxml'.";
        assert tableView != null : "fx:id=\"tableView\" was not injected: check your FXML file 'MessageListView.fxml'.";
        
        
        rows = tableView.getItems();
        rows.add(new DecoderMsgRowModel("10", "test", "json"));        
        
        decoderManager = Lookup.getDefault().lookup(DecoderManager.class);
        if(decoderManager == null){
            log.severe("Cannot get a DecoderMessageManager object");
            Platform.exit();
        }
        decoderManager.addPropertyChangeListener(this);
        
    }  
        
    @FXML
    private void loadButton(ActionEvent event){
        log.fine("loadButton "+event);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load decoder history");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Decoder history", "*.dmh"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            
            
            
//            Optional<DecoderConnectionFactory> connectorFactory = decoder.getConnectorFactory();
//            if(connectorFactory.isPresent()){
//                connectorFactory.get().createEmulator().play(selectedFile);
//            }
        }
    }
    
    @FXML
    private void downloadButton(ActionEvent event){
        log.fine("downloadButton "+event);
    }

    
    void setDecoders(Collection<? extends Decoder> decoders) {
        this.decoders = decoders;
        log.fine("setDecoders "+decoders.size());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Platform.runLater(()->propertyChangeOnEDT(evt));
    }
    
    public void propertyChangeOnEDT(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(DecoderManager.PROP_DECODER_ATTACHED)) {
        
        } else if(evt.getPropertyName().equals(DecoderManager.PROP_DECODER_DETACHED)) {
        
        } else if(evt.getPropertyName().equals(DecoderManager.PROP_DECODER_CONNECTED)) {
            handleDecoderConnected((Decoder)evt.getNewValue());            
        } else if(evt.getPropertyName().equals(DecoderManager.PROP_DECODER_DISCONNECTED)) {
            handleDecoderDisconnected((Decoder)evt.getNewValue());
        } else if(evt.getPropertyName().equals(DecoderManager.PROP_MESSAGE_RECIVED)) {
            handleMessageRecived((DecoderMessage)evt.getNewValue());
        } else if(evt.getPropertyName().equals(DecoderManager.PROP_MESSAGE_TRANSMITTED)) {
            handleMessageTransmitted((DecoderMessage)evt.getNewValue());
        } else if(evt.getPropertyName().equals(DecoderManager.PROP_RECIVED_CORRUPT_DATA)) {
            
        }
    }

    private void handleMessageRecived(DecoderMessage decoderMessage) {
        log.fine("handleMessageRecived "+decoderMessage);
        String time = decoderMessage.getString("","");
        String command = decoderMessage.getCommand().name();
        String data = decoderMessage.getJson();
        rows.add(new DecoderMsgRowModel(time, command, data));
    }

    private void handleMessageTransmitted(DecoderMessage decoderMessage) {
        log.fine("handleMessageTransmitted "+decoderMessage);
        String time = decoderMessage.getString("","");
        String command = decoderMessage.getCommand().name();
        String data = decoderMessage.getJson();
        rows.add(new DecoderMsgRowModel(time, command, data));
    }

    private void handleDecoderDisconnected(Decoder decoder) {   
        log.fine("handleDecoderDisconnected "+decoder);
        
    }

    private void handleDecoderConnected(Decoder decoder) {
        log.fine("handleDecoderConnected "+decoder);
    }

}
