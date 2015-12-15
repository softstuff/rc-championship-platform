package rc.championship.decoder.history;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import org.openide.util.Lookup;
import rc.championship.api.services.Storage;
import rc.championship.api.services.decoder.StoredMessage;

/**
 *
 * @author Stefan
 */
public class MessageHistoryViewController implements Initializable {

    private final Logger log = Logger.getLogger(getClass().getName());

    @FXML
    private Button filterButton;

    @FXML
    private TreeTableView<StoredMessage> treeTableView;

    @FXML
    private TreeTableColumn<StoredMessage, Long> columnId;

    @FXML
    private TreeTableColumn<StoredMessage, Date> columnTime;

    @FXML
    private TreeTableColumn<StoredMessage, String> columnType;

    private Storage storage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        columnId.setCellValueFactory((TreeTableColumn.CellDataFeatures<StoredMessage, Long> p) -> {
            StoredMessage msg = p.getValue().getValue();
            return new ReadOnlyObjectWrapper(msg == null ? null : msg.getId());
        });
        columnTime.setCellValueFactory((TreeTableColumn.CellDataFeatures<StoredMessage, Date> p) -> {
            StoredMessage msg = p.getValue().getValue();
            return new ReadOnlyObjectWrapper((msg == null ? null : new Date(msg.getTime())));
        });

        columnType.setCellValueFactory((TreeTableColumn.CellDataFeatures<StoredMessage, String> p) -> {
            StoredMessage msg = p.getValue().getValue();
            return new ReadOnlyObjectWrapper(msg == null ? null : msg.getType());
        });

        storage = Lookup.getDefault().lookup(Storage.class);
        if (storage == null) {
            log.severe("Can note find service Storage");
            Platform.exit();
            return;
        }

        treeTableView.setRoot(new TreeItem<>());

        doFilter();

    }

    @FXML
    void onFilterButton(ActionEvent event) {
        log.info("onFilterButton");

        doFilter();
    }

    private void doFilter() {
        log.info("doFilter");
        ObservableList<TreeItem<StoredMessage>> children = treeTableView.getRoot().getChildren();
        children.clear();
        List<StoredMessage> history = storage.getMessagesAfter(0l, true, 1000);
        history.forEach(msg -> children.add(new TreeItem<>(msg)));
    }

}
