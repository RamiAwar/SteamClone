package pumpkinbox.ui.home;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import pumpkinbox.api.NotificationObject;
import pumpkinbox.client.ChatClient;
import pumpkinbox.client.Client;
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
import pumpkinbox.ui.notifications.Notification;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ramiawar on 3/23/17.
 */
public class homeController implements Initializable{

    private String authenticationToken;
    private String online_status;
    private int userId;

    private BlockingQueue<String> messages_queue = new LinkedBlockingQueue<>();
    public BlockingQueue<NotificationObject> notification_queue = new LinkedBlockingQueue<>();


    public void setAuthenticationToken(String s){
        this.authenticationToken = s;
    }
    public void setUserId(int id){this.userId = id;}

    Icons icons = new Icons();

    ChatClient client = new ChatClient(messages_queue, notification_queue, userId, authenticationToken);

    @FXML
    Label closeIcon;
    @FXML
    HBox menuBar;
    @FXML
    Label minimizeIcon;
    @FXML
    Region draggableRegion;
    @FXML
    Label searchIcon;
    @FXML
    Label user_status;
    @FXML
    ContextMenu statusMenu;


    private Stage stage;

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

    @FXML
    void setStatusOnline(ActionEvent e){
        user_status.setText("Online");
        online_status = "online";
    }
    @FXML
    void setStatusOffline(ActionEvent e){
        user_status.setText("Offline");
        online_status = "offline";
    }
    @FXML
    void setStatusAway(ActionEvent e){
        user_status.setText("Away");
        online_status = "away";
    }

    void loadWindow(String location, String title){
        try {

            Parent parent = FXMLLoader.load(getClass().getResource(location));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.setAlwaysOnTop(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
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
        user_status.setOnMouseClicked(event -> {
            statusMenu.show(user_status, event.getScreenX(), event.getScreenY());
        });

        Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                updateGUI();
                System.out.println(authenticationToken);
                System.out.println(userId);
            }
        }));
        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();

    }

    private void minimize(){
        stage.setIconified(true);
    }

    public void loadIcons(){

        //Setting Icons on buttons using **ui.Icons.Icons** class
        icons.setSize("1em");

        closeIcon.setGraphic(icons.CLOSE_m);
        searchIcon.setGraphic(icons.SEARCH);
        user_status.setGraphic(icons.MINIMIZE_small);
        minimizeIcon.setGraphic(icons.MINIMIZE_m);

    }

    public void updateGUI(){
        if(!notification_queue.isEmpty()){
            NotificationObject notificationObject = new NotificationObject();
            try {
                notificationObject = notification_queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Notification notif = new Notification(notificationObject.getSenderUsername(), notificationObject.getSenderUsername(), 5, "MESSAGE");
        }

    }
}
