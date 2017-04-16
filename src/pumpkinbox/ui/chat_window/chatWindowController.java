package pumpkinbox.ui.chat_window;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import pumpkinbox.api.MessageObject;
import pumpkinbox.api.User;
import pumpkinbox.client.ChatClient;
import pumpkinbox.dialogs.AlertDialog;
import pumpkinbox.ui.add_friend.addFriendController;
import pumpkinbox.ui.draggable.EffectUtilities;
import pumpkinbox.ui.icons.Icons;
import pumpkinbox.ui.images.Images;
import pumpkinbox.ui.notifications.Notification;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ramiawar on 4/16/17.
 */
public class chatWindowController implements Initializable {

    private String authenticationToken;
    private int userId;
    private String name;
    private Icons icons = new Icons();
    private Stage stage;
    private String friend_name;
    private int friendId;

    private BlockingQueue<MessageObject> sendMessageQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<MessageObject> receiveMessageQueue = new LinkedBlockingQueue<>();

    public void setSendMessageQueue(BlockingQueue<MessageObject> messages_queue){
        this.sendMessageQueue = messages_queue;
    }
    public void setReceiveMessageQueue(BlockingQueue<MessageObject> messageQueue){
        this.receiveMessageQueue = messageQueue;
    }

    public void setAuthenticationToken(String auth){
        this.authenticationToken = auth;
    }
    public void setUserID(int id){
        this.userId = id;
    }
    public void setUsername(String user){
        this.name = user;
    }
    public void setFriendName(String name){
        this.friend_name = name;
    }
    public void setFriendID(int id){
        this.friendId = id;
    }


    @FXML
    StackPane stackPane;
    @FXML
    ImageView friend_profile;
    @FXML
    Region draggableRegion;
    @FXML
    HBox menuBar;
    @FXML
    Label closeIcon;
    @FXML
    Label minimizeIcon;
    @FXML
    Label friendNameLabel;
    @FXML
    Label status_icon;
    @FXML
    JFXTextField message_box;
    @FXML
    TextArea text_area;


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
    void sendMessage(ActionEvent e){

        //Get message content from message_box
        String message_content = message_box.getText();

        //Clear message_box
        message_box.setText("");

        //Add message to send queue
        sendMessageQueue.offer(new MessageObject(userId, friendId, message_content));

        //Add message to text area
        text_area.appendText("\n" + name + ": " + message_content);
    }


    @FXML
    void loadFriendProfile(ActionEvent e){

        //TODO OPEN PROFILE PAGE WITH NEW INFO
        //TODO PASS ON AUTH TOKEN, ID, FRIEND_ID


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pumpkinbox/ui/add_friend/add_friend_screen.fxml"));
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

        stage.setTitle("Add a friend");
        Scene scene = new Scene(parent);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add("pumpkinbox/ui/add_friend/add_friend.css");

        stage.setScene(scene);
        stage.setAlwaysOnTop(false);
        stage.show();

        //Passing primaryStage to controller in order to make window draggable
        addFriendController controller =
                loader.<addFriendController>getController();

        controller.registerStage(stage);

        controller.setAuthenticationToken(authenticationToken);
        controller.setUserID(userId);
        controller.setName(name);
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



        //TODO CUSTOMIZE IMAGE
        friend_profile.setImage(Images.getImage("PRO2"));



        Timeline chat_updater = new Timeline(new KeyFrame(Duration.seconds(0.1), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                try {


                    updateGUI();

                } catch (InterruptedException e) {
                    System.out.println("Error taking from queue.");
                    e.printStackTrace();
                }
            }
        }));

        chat_updater.setCycleCount(Timeline.INDEFINITE);
        chat_updater.play();

    }

    private void minimize(){
        stage.setIconified(true);
    }

    public void loadIcons(){

        //Setting Icons on buttons using **ui.Icons.Icons** class
        icons.setSize("1em");

        closeIcon.setGraphic(icons.CLOSE_m);
        status_icon.setGraphic(icons.MINIMIZE_small);
        minimizeIcon.setGraphic(icons.MINIMIZE_m);
        status_icon.setGraphic(icons.GHOST_GREEN);

    }

    public void updateGUI() throws InterruptedException {

        //Initializing friend profile view
        if(friendNameLabel.getText().equals("")) friendNameLabel.setText(this.friend_name);

        //System.out.println("UPDATING MESSAGES");
        //TODO Get messages from receive queue

        ArrayList<MessageObject> messages = new ArrayList<>();

        if(!receiveMessageQueue.isEmpty()){

            System.out.println("TAKING MESSAGE FROM QUEUE TO DISPLAY");
            MessageObject message = receiveMessageQueue.take();

            int sender = message.getSender_id();
            if(sender == friendId){
                System.out.println("WE GOT A MATCH!");
                text_area.appendText("\n" + friend_name + ": " + message.getContent());
                text_area.setStyle("-fx-text-fill:#444;");
                text_area.appendText("\n\t\t\t" + message.getTime_sent());
            }else{
                System.out.println("THIS IS ANOTHER FRIEND :(((");
                messages.add(message);
            }

            //return unrelated messages to queue
            for (int i = 0; i < messages.size(); i++) {
                receiveMessageQueue.offer(messages.get(i));
            }
        }





    }


}
