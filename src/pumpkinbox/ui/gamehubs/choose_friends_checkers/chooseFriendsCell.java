//package pumpkinbox.ui.gamehubs.choose_friends_tictactoe;
//
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.scene.control.ListCell;
//import javafx.scene.control.ListView;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.HBox;
//import javafx.scene.paint.Color;
//import javafx.stage.Stage;
//import javafx.stage.StageStyle;
//import pumpkinbox.api.EditFriendObject;
//import pumpkinbox.api.MessageObject;
//import pumpkinbox.ui.friend_profile.friendProfileController;
//import pumpkinbox.ui.images.Images;
//import pumpkinbox.ui.notifications.Notification;
//
//import java.io.IOException;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedBlockingQueue;
//
///**
// * Created by ramiawar on 4/30/17.
// */
//public class chooseFriendsCell extends ListCell<EditFriendObject>{
//
//        private ListView<EditFriendObject> listview;
//        private BlockingQueue<MessageObject> gameInvitationQueue = new LinkedBlockingQueue<>();
//
//        @FXML
//        HBox root;
//
//        @FXML
//        ImageView friendImage;
//
//        @FXML
//        Label friendName;
//
//        @FXML
//        Label time;
//
//        private FXMLLoader mLLoader;
//        private int friend_id;
//
//        public chooseFriendsCell(ListView<EditFriendObject> listView , BlockingQueue<MessageObject> gameInvitationQueue) {
//            super();
//            this.listview = listView; //Passing listview by reference to modify it
//            this.gameInvitationQueue = gameInvitationQueue;
//        }
//
//        protected void updateItem(EditFriendObject editFriendObject, BlockingQueue<MessageObject> gameInvitationQueue, boolean empty) {
//
//            super.updateItem(editFriendObject, empty);
//
//            if(empty || editFriendObject == null) {
//
//                setText(null);
//                setGraphic(null);
//
//            } else {
//
//                if (mLLoader == null) {
//
//                    mLLoader = new FXMLLoader(getClass().getResource("chooseFriendsCell.fxml"));
//                    mLLoader.setController(this);
//
//                    try {
//                        mLLoader.load();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//                friendName.setText(String.valueOf(editFriendObject.getName()));
//
//                //FOR SETTING CUSTOM IMAGES
//                friendImage.setImage(Images.getImage(Images.tictactoe));
//
//
//                root.setOnMouseClicked(event -> {
//
//                    //Get friend id
//                    int friend_id = editFriendObject.getFriend_id();
//
//                    //Send game invite to server
//                    gameInvitationQueue.offer(new MessageObject(editFriendObject.getSenderId(), editFriendObject.getFriend_id(),"tictactoe"));
//
//                    //display waiting window
//                    //in waiting window, wait for other user to confirm
//
//                    //close window
//
//                    //LOAD FRIEND PROFILE PAGE
////                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/pumpkinbox/ui/friend_profile/friend_profile_screen.fxml"));
////                    Parent parent = null;
////
////                    try {
////                        parent = loader.load();
////                    } catch (IOException e1) {
////                        Notification notification = new Notification("Error", "Unable to load window", 10, "ERROR");
////                        e1.printStackTrace();
////                        return;
////                    }
////
////                    Stage stage = new Stage(StageStyle.UNDECORATED);
////                    stage.initStyle(StageStyle.TRANSPARENT);
////
////                    stage.setTitle("Friend Profile");
////                    Scene scene = new Scene(parent);
////                    scene.setFill(Color.TRANSPARENT);
////
////                    stage.setScene(scene);
////                    stage.setAlwaysOnTop(false);
////                    stage.show();
////
////                    //Passing primaryStage to controller in order to make window draggable
////                    chooseFriends controller =
////                            loader.getController();
////
////                    controller.registerStage(stage);
////
////                    controller.setAuthenticationToken(editFriendObject.getAuth());
////                    controller.setUserID(editFriendObject.getSenderId());
////                    controller.setFriendId(editFriendObject.getFriend_id());
////                    controller.setName(editFriendObject.getName());
////
////                    controller.getFriendsList();
////                    controller.getFriendActivityList();
////                    controller.getFriendExperiencePoints();
//
//                });
//
//
//
//            setText(null);
//            setGraphic(root);
//
//            }
//
//        }
//
//
//    }
//

package pumpkinbox.ui.gamehubs.choose_friends_checkers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import pumpkinbox.api.CODES;
import pumpkinbox.api.EditFriendObject;
import pumpkinbox.api.MessageObject;
import pumpkinbox.api.ResponseObject;
import pumpkinbox.client.Client;
import pumpkinbox.games.TicTacToe.TicTacToeClient;
import pumpkinbox.games.checkers.Checkers;
import pumpkinbox.ui.images.Images;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by ramiawar on 4/30/17.
 */
public class chooseFriendsCell extends ListCell<EditFriendObject>{

    private ListView<EditFriendObject> listview;
    private BlockingQueue<MessageObject> gameInvitationQueue;
    private Stage callerStage;

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

    public chooseFriendsCell(ListView<EditFriendObject> listView, BlockingQueue<MessageObject> gameInvitationQueue, Stage callerStage) {
        super();
        this.listview = listView; //Passing listview by reference to modify it
        this.gameInvitationQueue = gameInvitationQueue;
        this.callerStage = callerStage;
    }


    protected void updateItem(EditFriendObject editFriendObject, boolean empty) {

        super.updateItem(editFriendObject, empty);

        if(empty || editFriendObject == null) {

            setText(null);
            setGraphic(null);

        } else {

            if (mLLoader == null) {

                mLLoader = new FXMLLoader(getClass().getResource("chooseFriendsCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            friendName.setText(String.valueOf(editFriendObject.getName()));
            friend_id = editFriendObject.getFriend_id();

            //FOR SETTING CUSTOM IMAGES:
            friendImage.setImage(Images.getImage(Images.tictactoe));

            root.setOnMouseClicked(event -> {

                //Add invitation to invitations queue
                int friend_id = editFriendObject.getFriend_id();
                MessageObject x = new MessageObject(editFriendObject.getSenderId(), friend_id, "checkers");

                ResponseObject r = Client.checkInvite(x);
                Client.sendInvite(x);


                if(r.getResponse().equals(CODES.OK)){

                    //LAUNCH GAME
                    Thread y = new Thread(() -> {

                        javafx.application.Application.launch(Checkers.class, "true", Integer.toString(editFriendObject.getSenderId()),
                                Integer.toString(editFriendObject.getFriend_id()));

                    });
                    y.setDaemon(false);
                    y.start();

                }

                callerStage.close();


            });



            setText(null);
            setGraphic(root);
        }

    }


}

