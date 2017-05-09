package pumpkinbox.ui.friend_profile;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pumpkinbox.api.EditFriendObject;
import pumpkinbox.ui.images.Images;
import pumpkinbox.ui.notifications.Notification;

import java.io.IOException;

/**
 * Created by ramiawar on 4/30/17.
 */
public class friendListViewCell extends ListCell<EditFriendObject>{

        private ListView<EditFriendObject> listview;

        @FXML
        HBox root;

        @FXML
        ImageView friendImage;

        @FXML
        Label friendName;

        @FXML
        Label time;

        private FXMLLoader mLLoader;
        private int friend_id;

        public friendListViewCell(ListView<EditFriendObject> listView ) {
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

                    mLLoader = new FXMLLoader(getClass().getResource("friendsCell.fxml"));
                    mLLoader.setController(this);

                    try {
                        mLLoader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                friendName.setText(String.valueOf(editFriendObject.getName()));
                time.setText(String.valueOf(editFriendObject.getTime_added()));

                //FOR SETTING CUSTOM IMAGES:
                friendImage.setImage(Images.getImage(Images.tictactoe));

            setText(null);
            setGraphic(root);
            }

        }


    }

