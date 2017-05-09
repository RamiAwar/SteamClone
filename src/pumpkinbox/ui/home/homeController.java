package pumpkinbox.ui.home;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import pumpkinbox.api.*;
import pumpkinbox.client.ChatClient;
import pumpkinbox.client.Client;
import pumpkinbox.dialogs.AlertDialog;
import pumpkinbox.dialogs.DecisionDialog;
import pumpkinbox.games.TicTacToe.TicTacToeClient;
import pumpkinbox.ui.add_friend.addFriendController;
import pumpkinbox.ui.chat_window.chatWindowController;
import pumpkinbox.ui.draggable.EffectUtilities;
import pumpkinbox.ui.gamehubs.checkers.checkersGamehubController;
import pumpkinbox.ui.gamehubs.tictactoe.gamehubController;
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
import pumpkinbox.ui.personal_profile.personalProfileController;
import pumpkinbox.ui.requests.friendRequestsScreenController;


import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by ramiawar on 3/23/17.
 */
public class homeController implements Initializable{

    private String authenticationToken;
    private String online_status;
    private int userId;
    private String name;
    private Icons icons = new Icons();
    private ChatClient client;
    private Stage stage;


    private ObservableList<String> friendsList = FXCollections.observableArrayList();

    /**
     * This queue stores messages to be sent. The client empties this queue and sends these messages to the server.
     */
    private BlockingQueue<MessageObject> sendMessageQueue = new LinkedBlockingQueue<>();

    /**
     * This queue stores incoming messages to be displayed or consumed when the user opens the chat with the sender of the message.
     */
    private BlockingQueue<MessageObject> receiveMessageQueue = new LinkedBlockingQueue<>();

    /**
     * This queue stores incoming messages for notification purposes
     */
    private BlockingQueue<MessageObject> messageNotificationQueue = new LinkedBlockingQueue<>();

    /**
     * This queue stores incoming friend requests to be consumed when requests window is opened.
     */
    private BlockingQueue<RequestObject> friendRequestsQueue = new LinkedBlockingQueue<>();

    /**
     * This queue stores incoming friend requests in order to be consumed as notifications
     */
    private BlockingQueue<RequestObject> friendRequestsNotificationQueue = new LinkedBlockingQueue<>();

    /**
     * This queue stores incoming friend requests to be displayed as dismissable alerts.
     */
    private BlockingQueue<MessageObject> gameInvitationNotificationQueue = new LinkedBlockingQueue<>();


    private final BlockingQueue<User> onlineFriends = new LinkedBlockingQueue<>();


    private ArrayList<String> onlineFriendsNames = new ArrayList<>();
    private ArrayList<User> onlineFriendsDetails = new ArrayList<>();


    public void setAuthenticationToken(String s){
        this.authenticationToken = s;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setUserId(int id){
        this.userId = id;
    }


    /**
     * This function creates a new client, passing the numerous BlockingQueues to the thread. ( BlockingQueues are used since they are
     * thread safe objects and fit the need )
     */
    public void initClient(){

        //System.out.println("Initializing client with : " + authenticationToken + name);
        client = new ChatClient(onlineFriends, sendMessageQueue, receiveMessageQueue, messageNotificationQueue,
                friendRequestsQueue, friendRequestsNotificationQueue, gameInvitationNotificationQueue, userId, name, authenticationToken);
        client.connect();
    }

    @FXML
    StackPane stackPane;
    @FXML
    Label closeIcon;
    @FXML
    HBox menuBar;
    @FXML
    Label minimizeIcon;
    @FXML
    Region draggableRegion;
    @FXML
    Label user_status;
    @FXML
    ContextMenu statusMenu;
    @FXML
    ListView friends_list;
    @FXML
    Label username;
    @FXML
    ImageView profile_photo;
    @FXML
    JFXButton add_friend;
    @FXML
    Label status_icon;
    @FXML
    JFXButton request_button;
    @FXML
    ImageView tictactoe_image;
    @FXML
    ImageView checkers_image;




    //Receiving stage from main class to make window draggable

    public void registerStage(Stage stage){

        this.stage = stage;
        EffectUtilities.makeDraggable(this.stage, this.menuBar);
        EffectUtilities.makeDraggable(this.stage, this.draggableRegion);

    }

    void close() {
        stage.close();
        Platform.exit();
    }

    @FXML
    void close(ActionEvent e) {
        stage.close();
        Platform.exit();
    }

    @FXML
    void setStatusOnline(ActionEvent e){
        user_status.setText("Online");
        status_icon.setGraphic(icons.GHOST_GREEN);
        online_status = "online";
    }

    @FXML
    void setStatusOffline(ActionEvent e){
        user_status.setText("Offline");
        status_icon.setGraphic(icons.GHOST_RED);
        online_status = "offline";
    }
    @FXML
    void setStatusAway(ActionEvent e){
        user_status.setText("Away");
        status_icon.setGraphic(icons.GHOST_ORANGE);
        online_status = "away";
    }


    @FXML
    void loadMyProfile(ActionEvent e){


        //LOAD PROFILE PAGE
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pumpkinbox/ui/personal_profile/personal_profile_screen.fxml"));
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

        stage.setTitle("My Profile");
        Scene scene = new Scene(parent);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add("pumpkinbox/ui/requests/requests.css");

        stage.setScene(scene);
        stage.setAlwaysOnTop(false);
        stage.show();

        //Passing primaryStage to controller in order to make window draggable
        personalProfileController controller =
                loader.getController();

        controller.registerStage(stage);

        controller.setAuthenticationToken(authenticationToken);
        controller.setUserID(userId);
        controller.setUsername(name);

        controller.getEditFriendsList();
        controller.getActivityList();
        controller.getExperiencePoints();
    }

    @FXML
    void loadAddFriend(ActionEvent e){
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
                loader.getController();

        controller.registerStage(stage);

        controller.setAuthenticationToken(authenticationToken);
        controller.setUserID(userId);
        controller.setName(name);

    }

    @FXML
    void loadRequests(ActionEvent eventi){

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pumpkinbox/ui/requests/friend_requests_screen.fxml"));
            Parent parent = loader.load();

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initStyle(StageStyle.TRANSPARENT);

            stage.setTitle("Requests");

            Scene scene = new Scene(parent);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add("pumpkinbox/ui/requests/requests.css");

            stage.setScene(scene);
            stage.setAlwaysOnTop(false);
            stage.show();

            //Passing primaryStage to controller in order to make window draggable
            friendRequestsScreenController controller =
                    loader.getController();

            controller.registerStage(stage);

            controller.setAuthenticationToken(authenticationToken);
            controller.setUserID(userId);
            controller.setUsername(name);
            controller.setFriendRequestsNotificationQueue(friendRequestsNotificationQueue);
            controller.setFriendRequestsQueue(friendRequestsQueue);
            controller.setupList();

        } catch (IOException e) {
            e.printStackTrace();
        }

//        //RESET UNREAD REQUESTS
//        request_counter = 0;

    }

    void loadChatWindow(int friend_id, String friend_name){


        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pumpkinbox/ui/chat_window/chat_screen.fxml"));
            Parent parent = loader.load();

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initStyle(StageStyle.TRANSPARENT);

            stage.setTitle("Chat with " + Integer.toString(friend_id));
            Scene scene = new Scene(parent);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add("pumpkinbox/ui/chat_window/chat.css");

            stage.setScene(scene);
            stage.setAlwaysOnTop(false);
            stage.show();

            //Passing primaryStage to controller in order to make window draggable
            chatWindowController controller =
                    loader.getController();

            controller.registerStage(stage);

            controller.setAuthenticationToken(authenticationToken);
            controller.setUserID(userId);
            controller.setUsername(name);
            controller.setFriendID(friend_id);
            controller.setFriendName(friend_name);
            controller.setSendMessageQueue(sendMessageQueue);
            controller.setReceiveMessageQueue(receiveMessageQueue);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @FXML
    public void launch_tictactoe(MouseEvent e){

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pumpkinbox/ui/gamehubs/tictactoe/gamehub_screen.fxml"));
            Parent parent = loader.load();

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initStyle(StageStyle.TRANSPARENT);

            stage.setTitle("Gamehub");
            Scene scene = new Scene(parent);
            scene.setFill(Color.TRANSPARENT);

            stage.setScene(scene);
            stage.setAlwaysOnTop(false);
            stage.show();

            //Passing primaryStage to controller in order to make window draggable
            gamehubController controller =
                    loader.getController();

            controller.registerStage(stage);

            controller.setAuthenticationToken(authenticationToken);
            controller.setUserID(userId);
            controller.setUsername(name);
            controller.setGameInvitationQueue(gameInvitationNotificationQueue);
//            controller.getData();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    @FXML
    public void launch_checkers(MouseEvent e){

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pumpkinbox/ui/gamehubs/checkers/checkers_gamehub_screen.fxml"));
            Parent parent = loader.load();

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initStyle(StageStyle.TRANSPARENT);

            stage.setTitle("Gamehub");
            Scene scene = new Scene(parent);
            scene.setFill(Color.TRANSPARENT);

            stage.setScene(scene);
            stage.setAlwaysOnTop(false);
            stage.show();

            //Passing primaryStage to controller in order to make window draggable
            checkersGamehubController controller =
                    loader.getController();

            controller.registerStage(stage);

            controller.setAuthenticationToken(authenticationToken);
            controller.setUserID(userId);
            controller.setUsername(name);
            controller.setGameInvitationQueue(gameInvitationNotificationQueue);

        } catch (IOException e1) {
            e1.printStackTrace();
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

        tictactoe_image.setImage(Images.getImage("tictactoe"));
        checkers_image.setImage(Images.profile2);

        username.setText(name);

        profile_photo.setImage(Images.pumpkin_logo);

        Timeline gui_updater = new Timeline(new KeyFrame(Duration.seconds(1), event -> {

            try {

                updateGUI();

            } catch (InterruptedException e) {
                System.out.println("Error taking from queue.");
                e.printStackTrace();
            }
        }));

        gui_updater.setCycleCount(Timeline.INDEFINITE);
        gui_updater.play();

        Timeline tictactoechecker = new Timeline(new KeyFrame(Duration.seconds(5), event -> {

            try {

                while(!gameInvitationNotificationQueue.isEmpty()){

                    MessageObject x = gameInvitationNotificationQueue.take();

                    System.out.println(x);

                    if(x.getReceiver_id() == userId && x.getContent().equals("tictactoe")){

                        System.out.println("FOUND A GAME INVITE");
                        gameInvitationNotificationQueue.offer(new MessageObject(userId, x.getSender_id(), "OK"));

                        //launch tictactoe
                        new Thread(() -> {

                            while (true) {

                                try {
                                    TicTacToeClient client = new TicTacToeClient(true, false);
                                    client.setSender_id(x.getSender_id());
                                    client.setReceiver_id(x.getReceiver_id());
                                    client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                    client.frame.setSize(500, 500);
                                    client.frame.setVisible(true);
                                    client.frame.setResizable(false);
                                    client.play();
                                    if (!client.wantsToPlayAgain()) {
                                        break;
                                    }

                                }catch(Exception e1){
                                    e1.printStackTrace();
                                }
                            }
                        }).start();

                    }else{
                        gameInvitationNotificationQueue.offer(x);
                    }
                }

            } catch (Exception e) {
                System.out.println("Error taking from queue.");
                e.printStackTrace();
            }
        }));

        tictactoechecker.setCycleCount(Timeline.INDEFINITE);
        tictactoechecker.play();

    }

    private void minimize(){
        stage.setIconified(true);
    }

    public void loadIcons(){

        //Setting Icons on buttons using **ui.Icons.Icons** class
        icons.setSize("1em");

        closeIcon.setGraphic(icons.CLOSE_m);
        user_status.setGraphic(icons.MINIMIZE_small);
        minimizeIcon.setGraphic(icons.MINIMIZE_m);
        add_friend.setGraphic(icons.ADD);
        status_icon.setGraphic(icons.GHOST_GREEN);

    }

    public void displayDialog(boolean x) {

        Platform.runLater(() -> {

            DecisionDialog dialog = new DecisionDialog(stackPane, "Error", "Invalid request would you like to retry?", "Yes", "No");

        });
    }



    public void updateGUI() throws InterruptedException {


        //Update request button text
//        request_button.setText("Requests(" + request_counter + ")");

        if(!name.equals(null)) username.setText(name);

        checkAndAdd(onlineFriends, onlineFriendsNames);

        friendsList.setAll(onlineFriendsNames);
        friends_list.getItems().setAll(onlineFriendsNames);


        friends_list.setOnMouseClicked(event -> {

            String friend_firstname = friends_list.getSelectionModel().getSelectedItem().toString();
            int friend_id = -1;

            //getting friend id
            for (int i = 0; i < onlineFriendsDetails.size(); i++) {
                if(onlineFriendsDetails.get(i).getFirstName().equals(friend_firstname)){
                    friend_id = onlineFriendsDetails.get(i).getUserId();
                    break;
                }
            }

            if(friend_id == -1){
                System.out.println("FRIEND ID NEGATIVE");
                AlertDialog alert = new AlertDialog(stackPane, "Unknown Error", "An unknown error has occurred. Please try again later.");
                alert.showDialog();
                return;
            }else {

                //opening new chat window
                loadChatWindow(friend_id, friend_firstname);
            }

        });


        //----------NOTIFICATIONS FOR MESSAGES
        if(!messageNotificationQueue.isEmpty()){

            MessageObject message = messageNotificationQueue.take();

            Platform.runLater(() -> new Notification(
                    "New message from " + getFirstnameById(message.getSender_id()),
                    message.getContent(),
                    10,
                    "MESSAGE"
            ));
        }

        //----------NOTIFICATIONS FOR FRIEND REQUESTS
        if(!friendRequestsNotificationQueue.isEmpty()){

            RequestObject request = friendRequestsNotificationQueue.take();
            String requester_username = request.getRequester_username();

            Platform.runLater(() -> new Notification(
                    "Friend Request",
                    "You have received a friend request from " + requester_username,
                    10,
                    "REQUEST"
            ));

//            request_counter++;
        }

        //----------NOTIFICATIONS FOR GAME INVITATIONS
        if(!gameInvitationNotificationQueue.isEmpty()){

            System.out.println("GOT IT!");

            MessageObject m = gameInvitationNotificationQueue.take();

                //LAUNCH GAME AS STARTER
                Thread y = new Thread(() -> {

                    while (true) {

                        try {
                            TicTacToeClient client = new TicTacToeClient(true, false);
                            client.setSender_id(m.getReceiver_id());
                            client.setReceiver_id(m.getSender_id());
                            client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            client.frame.setSize(500, 500);
                            client.frame.setVisible(true);
                            client.frame.setResizable(false);
                            client.play();
                            if (!client.wantsToPlayAgain()) {
                                break;
                            }
                        }catch(Exception e1){
                            e1.printStackTrace();
                        }
                    }
                });
                y.setDaemon(false);
                y.start();
            }
    }

    private void checkAndAdd(BlockingQueue<User> friends, ArrayList<String> array) throws InterruptedException {

        while(!friends.isEmpty()){

            User friend = friends.take();

            //TODO remove users that go offline SOMEHOW

            if(!onlineFriendsDetails.contains(friend)){
                onlineFriendsDetails.add(friend);
//                System.out.println("FRIEND ID:" + friend.getUserId());
            }

            if(!array.contains(friend.getFirstName())){
                array.add(friend.getFirstName());
            }
        }
    }

    private String getFirstnameById(int id){
        for (int i = 0; i < onlineFriendsDetails.size(); i++) {
            if(onlineFriendsDetails.get(i).getUserId() == id){
                return onlineFriendsDetails.get(i).getFirstName();
            }
        }
        return "ERROR";
    }
}
