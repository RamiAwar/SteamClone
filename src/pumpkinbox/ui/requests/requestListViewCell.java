package pumpkinbox.ui.requests;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import pumpkinbox.api.CODES;
import pumpkinbox.api.RequestObject;
import pumpkinbox.api.ResponseObject;
import pumpkinbox.client.Client;
import pumpkinbox.dialogs.AlertDialog;
import pumpkinbox.ui.notifications.Notification;

import java.io.IOException;

/**
 * Created by ramiawar on 4/17/17.
 */
public class requestListViewCell extends ListCell<RequestObject> {

    private ListView<RequestObject> listview;

    @FXML
    HBox root;

    @FXML
    Label requester_email;

    @FXML
    JFXButton acceptButton;

    @FXML
    JFXButton rejectButton;

    private FXMLLoader mLLoader;

    public requestListViewCell( ListView<RequestObject> listView ) {
        super();
        this.listview = listView; // Here I pass the reference to my list. Is it a gd way to do that?
    }


    protected void updateItem(RequestObject requestObject, boolean empty) {

        super.updateItem(requestObject, empty);

        if(empty || requestObject == null) {

            setText(null);
            setGraphic(null);

        } else {

            if (mLLoader == null) {

                mLLoader = new FXMLLoader(getClass().getResource("requestCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            requester_email.setText(String.valueOf(requestObject.getRequester_username()));

            acceptButton.setOnAction(event -> {
                ResponseObject r = Client.acceptFriendRequest(requestObject.getRequest_id(), requestObject.getReceiver_id(), requestObject.getAuth());
                if(r.getStatusCode().equals(CODES.OK)){
                    new Notification("Success", "Request successfully accepted. You are now friends.", 10, "SUCCESS");
                }else{
                    new Notification("Error", "An error occured. Please try again later or contact us at pumpkinbox@pumpkinbox.com if the problem persists.", 10, "ERROR");
                }
                listview.getItems().remove(requestObject);
            });

            rejectButton.setOnAction(event -> {
                ResponseObject r = Client.rejectFriendRequest(requestObject.getRequest_id(), requestObject.getReceiver_id(), requestObject.getAuth());
                if(r.getStatusCode().equals(CODES.OK)) {
                    new Notification("Success", "Request successfully rejected.", 10, "SUCCESS");
                }else{
                        new Notification("Error", "An error occured. Please try again later or contact us at pumpkinbox@pumpkinbox.com if the problem persists.", 10, "ERROR");
                }
                listview.getItems().remove(requestObject);
            });

            setText(null);
            setGraphic(root);
        }

    }


}
