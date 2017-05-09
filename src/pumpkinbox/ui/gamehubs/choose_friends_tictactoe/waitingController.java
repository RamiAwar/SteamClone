package pumpkinbox.ui.gamehubs.choose_friends_tictactoe;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import pumpkinbox.api.CODES;
import pumpkinbox.api.EditFriendObject;
import pumpkinbox.api.MessageObject;
import pumpkinbox.api.ResponseObject;
import pumpkinbox.client.Client;
import pumpkinbox.games.TicTacToe.TicTacToeClient;
import pumpkinbox.ui.draggable.EffectUtilities;
import pumpkinbox.ui.icons.Icons;
import sun.plugin2.message.Message;

import javax.swing.*;
import java.net.URL;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by ramiawar on 4/17/17.
 */

public class waitingController implements Initializable{

    private String authenticationToken;
    private int userId;
    private String name;
    private Stage stage;
    private Icons icons = new Icons();
    private MessageObject message;
    private Timer timer;
    private int checkInviteTime = 500;


    /**
     * This queue stores incoming friend requests to be consumed when requests window is opened.
     */

    public void setAuthenticationToken(String s){this.authenticationToken = s;}
    public void setUsername(String name){
        this.name = name;
    }
    public void setUserID(int id){
        this.userId = id;
    }
    public void setWaiting(MessageObject x){
        this.message = x;
    }

    @FXML
    StackPane stackPane;

    @FXML
    HBox menuBar;

    @FXML
    Region draggableRegion;

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
