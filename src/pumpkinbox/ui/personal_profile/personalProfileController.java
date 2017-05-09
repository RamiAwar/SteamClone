package pumpkinbox.ui.personal_profile;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pumpkinbox.api.CODES;
import pumpkinbox.api.RequestObject;
import pumpkinbox.api.ResponseObject;
import pumpkinbox.client.Client;
import pumpkinbox.dialogs.AlertDialog;
import pumpkinbox.ui.draggable.EffectUtilities;
import pumpkinbox.ui.icons.Icons;
import pumpkinbox.ui.requests.requestListViewCell;

import java.net.URL;
import java.rmi.server.ExportException;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import pumpkinbox.api.*;
import pumpkinbox.client.ChatClient;
import pumpkinbox.client.Client;
import pumpkinbox.dialogs.AlertDialog;
import pumpkinbox.ui.add_friend.addFriendController;
import pumpkinbox.ui.chat_window.chatWindowController;
import pumpkinbox.ui.create_user.signupScreenController;
import pumpkinbox.ui.draggable.EffectUtilities;
import pumpkinbox.ui.icons.Icons;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pumpkinbox.ui.images.Images;
import pumpkinbox.ui.notifications.Notification;

import javax.xml.ws.Response;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by ramiawar on 4/18/17.
 */

public class personalProfileController implements Initializable{

    private String authenticationToken;
    private int userId;
    private String name;
    private Stage stage;
    private Icons icons = new Icons();

    private ObservableList<EditFriendObject> friendsList = FXCollections.observableArrayList();
    private ObservableList<EditFriendObject> editFriendsList = FXCollections.observableArrayList();
    private ObservableList<GameActivityObject> gameActivityList = FXCollections.observableArrayList();

    public void setAuthenticationToken(String s){
        this.authenticationToken = s;
    }
    public void setUsername(String name){
        this.name = name;
    }
    public void setUserID(int id){
        this.userId = id;
    }

    @FXML
    StackPane stackPane;
    @FXML
    ListView<GameActivityObject> activity_list;
    @FXML
    ListView<EditFriendObject> friends_list;
    @FXML
    ListView<EditFriendObject> edit_friends_list;
    @FXML
    HBox menuBar;
    @FXML
    Region draggableRegion;
    @FXML
    ImageView profile_photo;
    @FXML
    Label closeIcon;
    @FXML
    Label minimizeIcon;
    @FXML
    Label experience_points;
    @FXML
    JFXProgressBar level_progress_bar;
    @FXML
    Label level_label;
    @FXML
    Label experience_points1;
    @FXML
    JFXProgressBar level_progress_bar1;
    @FXML
    Label level_label1;
    @FXML
    Label username;
    @FXML
    Label email;


    //Receiving stage from main class to make window draggable

    public void registerStage(Stage stage){

        this.stage = stage;
        EffectUtilities.makeDraggable(this.stage, this.menuBar);
        EffectUtilities.makeDraggable(this.stage, this.draggableRegion);

    }

    void close() {
        stage.close();
    }

    @FXML
    void close(ActionEvent e) {
        stage.close();
    }

    public void getEditFriendsList(){

        username.setText(name);
        email.setText("");

        ResponseObject responseObject = Client.getEditFriendsList(userId, authenticationToken);

        if(!responseObject.getStatusCode().equals(CODES.INVALID_REQUEST)){

            System.out.println(responseObject.getResponse());

            try {
                //Were okay, parse string
                String[] response_array = responseObject.getResponse().split("\\|");

                //Return
                //FRIEND_ID |  FIRSTNAME LASTNAME   |   TIME ADDED

                for (int i = 0; i < response_array.length; i += 3) {
                    friendsList.add(new EditFriendObject(userId, Integer.parseInt(response_array[i]), response_array[i + 1], response_array[i + 2], authenticationToken));
                    editFriendsList.add(new EditFriendObject(userId, Integer.parseInt(response_array[i]), response_array[i + 1], response_array[i + 2], authenticationToken));
                }

            }catch(Exception e){
                //Do something
            }
        }

        edit_friends_list.setItems(editFriendsList);
        edit_friends_list.setCellFactory(studentListView -> new editFriendListViewCell(edit_friends_list));

        friends_list.setItems(friendsList);
        friends_list.setCellFactory(studentListView -> new friendListViewCell(friends_list));
    }

    public void getActivityList(){

        ResponseObject responseObject = Client.getActivityList(userId, authenticationToken);

        if(!responseObject.getStatusCode().equals(CODES.INVALID_REQUEST)){

            //Were okay, parse string
            //RESPONSE FORMAT
            // FRIEND NAME | GAME NAME | GAME STATUS | EXPERIENCE

            try {
                String[] response_array = responseObject.getResponse().split("\\|");
                for (int i = 0; i < response_array.length; i += 4) {
                    gameActivityList.add(new GameActivityObject(response_array[i], response_array[i + 1], response_array[i + 3], response_array[i + 2]));
                }
            }catch (Exception e){
                //Do something
            }
        }

        activity_list.setItems(gameActivityList);
        activity_list.setCellFactory(studentListView -> new gameActivityListViewCell(activity_list));

    }

    public void getExperiencePoints(){

        //get sum of experience points
        ResponseObject response = Client.getExperience(userId, authenticationToken);

        if(response.getStatusCode().equals(CODES.OK)) {

            int experience = Integer.parseInt(response.getResponse());
            int level = experience / 20;
            double progress = ((double) experience % 20)/20;

            experience_points.setText(Integer.toString(experience) + "xp");
            level_label.setText("Level " + Integer.toString(level));
            level_progress_bar.setProgress(progress);

            experience_points1.setText(Integer.toString(experience) + "xp");
            level_label1.setText("Level " + Integer.toString(level));
            level_progress_bar1.setProgress(progress);

        }else{
            AlertDialog alert = new AlertDialog(stackPane, "Error", "Could not get experience points", "Okay");
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadIcons();

        closeIcon.setOnMouseClicked((MouseEvent e) ->{
            close();
        });
        minimizeIcon.setOnMouseClicked((MouseEvent e) ->{
            minimize();
        });

        profile_photo.setImage(Images.pumpkin_logo);
    }

    private void minimize(){
        stage.setIconified(true);
    }

    public void loadIcons(){

        //Setting Icons on buttons using **ui.Icons.Icons** class
        icons.setSize("1em");

        closeIcon.setGraphic(icons.CLOSE_m);
        minimizeIcon.setGraphic(icons.MINIMIZE_m);


    }



}
