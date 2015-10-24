package rc.championship.decoder.player;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class PlayerViewController implements Initializable {

    @FXML
    private Button buttonConnect;

    @FXML
    private Label decoderTitle;

    @FXML
    private Label decoderStatus;

    @FXML
    private Button buttonPlayFile;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
