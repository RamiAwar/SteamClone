package pumpkinbox.ui.friend_profile;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import pumpkinbox.api.CODES;
import pumpkinbox.api.EditFriendObject;
import pumpkinbox.api.ResponseObject;
import pumpkinbox.client.Client;
import pumpkinbox.ui.images.Images;
import pumpkinbox.ui.notifications.Notification;

import java.io.IOException;

/**
 * Created by ramiawar on 4/30/17.
 */
public class editFriendListViewCell extends ListCell<EditFriendObject>{

        private ListView<EditFriendObject> listview;

        @FXML
        HBox root;

        @FXML
        ImageView friendImage;

        @FXML
        Label friendName;

        @FXML
        Label time;

        @FXML
        JFXButton removeFriendButton;

        private FXMLLoader mLLoader;
        private int friend_id;

        public editFriendListViewCell( ListView<EditFriendObject> listView ) {
            super();
            this.listview = listView; //Passing listview by reference to modify it
        }


        protected void updateItem(EditFriendObject editFriendObject, boolean empty) {

            super.updateItem(editFriendObject, empty);

            if(empty || editFriendObject == null) {

                setText(null);
                setGraphic(null);

            } else {

                if (mLLoader == null) {

                    mLLoader = new FXMLLoader(getClass().getResource("editFriendsCell.fxml"));
                    mLLoader.setController(this);

                    try {
                        mLLoader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                friendName.setText(String.valueOf(editFriendObject.getName()));
                friend_id = editFriendObject.getFriend_id();
                time.setText(String.valueOf(editFriendObject.getTime_added()));

                //FOR SETTING CUSTOM IMAGES:
                friendImage.setImage(Images.getImage(Images.tictactoe));

            removeFriendButton.setOnAction(event -> {

                ResponseObject r = Client.removeFriend(editFriendObject.getSenderId(), editFriendObject.getFriend_id(), editFriendObject.getAuth());

                if(r.getStatusCode().equals(CODES.OK)){
                    new Notification("Success", "Friend successfully removed.", 10, "SUCCESS");
                }else{
                    new Notification("Error", "An error occured. Please try again later or contact us at pumpkinbox@pumpkinbox.com if the problem persists.", 10, "ERROR");
                }
                listview.getItems().remove(editFriendObject);
            });

            setText(null);
            setGraphic(root);
            }

        }


    }

