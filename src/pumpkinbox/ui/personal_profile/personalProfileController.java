package pumpkinbox.ui.personal_profile;

import com.jfoenix.controls.JFXButton;
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


    private ObservableList<EditFriendObject> editFriendsList = FXCollections.observableArrayList();

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
    ListView<EditFriendObject> edit_friends_list;

    @FXML
    HBox menuBar;

    @FXML
    Region draggableRegion;

    @FXML
    JFXButton cancelButton;

    @FXML
    Label closeIcon;

    @FXML
    Label minimizeIcon;


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
//        ResponseObject responseObject = Client.getEditFriendsList(userId, authenticationToken);
//
//        if(!responseObject.getStatusCode().equals(CODES.INVALID_REQUEST)){
//
//            if(responseObject.getResponse().equals("")){
//                AlertDialog alertDialog = new AlertDialog(stackPane, "No Friend Requests",
//                        "You have no new friend requests! Check again later.", "Okay");
//                alertDialog.showDialog();
//                return;
//            }
//
//            //Were okay, parse string
//            String[] response_array = responseObject.getResponse().split("\\|");
//            for(int i=0; i<response_array.length; i+=2){
//                editFriendsList.add(new RequestObject(Integer.parseInt(response_array[i]), userId, response_array[i+1], authenticationToken));
//            }
//        }
//
//        edit_friends_list.setItems(editFriendsList);
//        edit_friends_list.setCellFactory(studentListView -> new requestListViewCell());
    }

    public void getActivityList(){

//        ResponseObject responseObject = Client.getEditFriendsList(userId, authenticationToken);
//
//        if(!responseObject.getStatusCode().equals(CODES.INVALID_REQUEST)){
//
//            if(responseObject.getResponse().equals("")){
//                AlertDialog alertDialog = new AlertDialog(stackPane, "No Friend Requests",
//                        "You have no new friend requests! Check again later.", "Okay");
//                alertDialog.showDialog();
//                return;
//            }
//
//            //Were okay, parse string
//            String[] response_array = responseObject.getResponse().split("\\|");
//            for(int i=0; i<response_array.length; i+=2){
//                editFriendsList.add(new RequestObject(Integer.parseInt(response_array[i]), userId, response_array[i+1], authenticationToken));
//            }
//        }
//
//        edit_friends_list.setItems(editFriendsList);
//        edit_friends_list.setCellFactory(studentListView -> new requestListViewCell());
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
