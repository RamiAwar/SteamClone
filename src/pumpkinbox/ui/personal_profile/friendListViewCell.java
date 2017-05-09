package pumpkinbox.ui.personal_profile;

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
import pumpkinbox.api.CODES;
import pumpkinbox.api.EditFriendObject;
import pumpkinbox.api.ResponseObject;
import pumpkinbox.client.Client;
import pumpkinbox.dialogs.AlertDialog;
import pumpkinbox.ui.images.Images;
import pumpkinbox.ui.notifications.Notification;
import pumpkinbox.ui.friend_profile.friendProfileController;


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

        public boolean checkFriendPrivacy(int userId, String authenticationToken, int friend_id){

            //whether or not user is private
            ResponseObject response = Client.getFriendPrivacy(userId, authenticationToken, friend_id);

            if(response.getStatusCode().equals(CODES.OK)) {

                boolean canAccess;
                String result = response.getResponse();
                return Integer.parseInt(result) != 1;

            }else{
                new Notification("Error", "Could not retrieve friend information", 5, "ERROR");
                return false;
            }
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


                root.setOnMouseClicked(event -> {

                    //Check friend privacy
                    if(!checkFriendPrivacy(editFriendObject.getSenderId(), editFriendObject.getAuth(), editFriendObject.getFriend_id())){
                        return;
                    }

                    //LOAD FRIEND PROFILE PAGE
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/pumpkinbox/ui/friend_profile/friend_profile_screen.fxml"));
                    Parent parent = null;

                    try {
                        parent = loader.load();
                    } catch (IOException e1) {
                        Notification notification = new Notification("Error", "Unable to load window", 10, "ERROR");
                        e1.printStackTrace();
                        return;
                    }

                    Stage stage = new Stage(StageStyle.UNDECORATED);
                    stage.initStyle(StageStyle.TRANSPARENT);

                    stage.setTitle("Friend Profile");
                    Scene scene = new Scene(parent);
                    scene.setFill(Color.TRANSPARENT);

                    stage.setScene(scene);
                    stage.setAlwaysOnTop(false);
                    stage.show();

                    //Passing primaryStage to controller in order to make window draggable
                    friendProfileController controller =
                            loader.getController();

                    controller.registerStage(stage);

                    controller.setAuthenticationToken(editFriendObject.getAuth());
                    controller.setUserID(editFriendObject.getSenderId());
                    controller.setFriendId(editFriendObject.getFriend_id());
                    controller.setName(editFriendObject.getName());

                    controller.getFriendsList();
                    controller.getFriendActivityList();
                    controller.getFriendExperiencePoints();


                });

                friendName.setText(String.valueOf(editFriendObject.getName()));
                time.setText(String.valueOf(editFriendObject.getTime_added()));

                //FOR SETTING CUSTOM IMAGES:
                friendImage.setImage(Images.getImage(Images.tictactoe));

            setText(null);
            setGraphic(root);
            }

        }


    }

