package rc.championship.decoder.playback;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class MessageListViewController implements Initializable {

    private ObservableList<DecoderMsgRowModel> rows;
    @FXML private TableView<DecoderMsgRowModel> tableView;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rows = tableView.getItems();
        rows.add(new DecoderMsgRowModel("10", "test", "json"));
    }    
    
}
