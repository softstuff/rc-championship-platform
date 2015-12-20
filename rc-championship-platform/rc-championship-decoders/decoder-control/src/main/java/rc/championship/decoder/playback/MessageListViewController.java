package rc.championship.decoder.playback;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
import org.json.JSONObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import rc.championship.api.model.Decoder;
import rc.championship.api.services.decoder.DecoderManager;
import rc.championship.api.services.decoder.DecoderMessage;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class MessageListViewController implements Initializable, PropertyChangeListener{
    
    private static final String LAST_HISTORY_EXPORT_FOLDER = "LastHistoryExportFolder";

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
        
        String lastPath = NbPreferences.forModule(getClass()).get(LAST_HISTORY_EXPORT_FOLDER, System.getProperty("user.home"));
        if(Files.exists(Paths.get(lastPath))) {
            File lastFolder = Paths.get(lastPath).toFile();
            fileChooser.setInitialDirectory(lastFolder);
        }
        fileChooser.setInitialFileName("history.dmh");
        fileChooser.setTitle("Load decoder history");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Decoder history", "*.dmh"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            NbPreferences.forModule(getClass()).put(LAST_HISTORY_EXPORT_FOLDER, selectedFile.getParentFile().getAbsolutePath());
            loadHistoryFrom(selectedFile);
            
            
//            Optional<DecoderConnectionFactory> connectorFactory = decoder.getConnectorFactory();
//            if(connectorFactory.isPresent()){
//                connectorFactory.get().createEmulator().play(selectedFile);
//            }
        }
    }
    
    @FXML
    private void downloadButton(ActionEvent event){
        log.fine("downloadButton "+event);
        
        FileChooser fileChooser = new FileChooser();
        
        String lastPath = NbPreferences.forModule(getClass()).get(LAST_HISTORY_EXPORT_FOLDER, System.getProperty("user.home"));
        if(Files.exists(Paths.get(lastPath))) {
            File lastFolder = Paths.get(lastPath).toFile();
            fileChooser.setInitialDirectory(lastFolder);
        }
        fileChooser.setInitialFileName("history.dmh");
        fileChooser.setTitle("Save history to");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Decoder history", "*.dmh"));
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            NbPreferences.forModule(getClass()).put(LAST_HISTORY_EXPORT_FOLDER, selectedFile.getParentFile().getAbsolutePath());
            saveHistoryTo(selectedFile);
        }
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
        String time = Instant.now().toString();
        String command = decoderMessage.getCommand().name();
        String data = decoderMessage.getJson();
        rows.add(0, new DecoderMsgRowModel(time, command, data));
    }

    private void handleMessageTransmitted(DecoderMessage decoderMessage) {
        log.fine("handleMessageTransmitted "+decoderMessage);
        String time = Instant.now().toString();
        String command = decoderMessage.getCommand().name();
        String data = decoderMessage.getJson();
        rows.add(0, new DecoderMsgRowModel(time, command, data));
    }

    private void handleDecoderDisconnected(Decoder decoder) {   
        log.fine("handleDecoderDisconnected "+decoder);
        
    }

    private void handleDecoderConnected(Decoder decoder) {
        log.fine("handleDecoderConnected "+decoder);
    }

    private void saveHistoryTo(File selectedFile) {
        List<DecoderMsgRowModel> rowsToSave = new ArrayList<>(rows);
        List<String> dataToSave = rowsToSave.stream()
                .map(row -> String.format("{time:\"%s\", type:\"%s\", data:%s},", row.getTime(), row.getType(), row.getData()))
                .collect(Collectors.toList());
        dataToSave.add(0, "\"message_history\":[");
        dataToSave.add("]");
        
        try {
            Files.write(selectedFile.toPath(), dataToSave, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
    }

    private void loadHistoryFrom(File selectedFile) {
        
        try {
            List<String> lines = Files.readAllLines(selectedFile.toPath(), StandardCharsets.UTF_8);
            lines.remove(0);
            lines.remove(lines.size()-1);
            
            
            lines.forEach(row->{
                JSONObject json = new JSONObject(row);
                String time = json.getString("time");
                String command = json.getString("type");
                String data = json.getJSONObject("data").toString();
                rows.add(0, new DecoderMsgRowModel(time, command, data));
            });
            
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
