package pumpkinbox.ui.gamehubs.choose_friends_checkers;

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
import pumpkinbox.api.EditFriendObject;
import pumpkinbox.api.MessageObject;
import pumpkinbox.api.ResponseObject;
import pumpkinbox.client.Client;
import pumpkinbox.ui.draggable.EffectUtilities;
import pumpkinbox.ui.icons.Icons;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ramiawar on 4/17/17.
 */

public class chooseFriendController implements Initializable{

    private String authenticationToken;
    private int userId;
    private String name;
    private Stage stage;
    private Icons icons = new Icons();


    private ObservableList<EditFriendObject> friendsList = FXCollections.observableArrayList();

    public void setGameInvitationQueue(BlockingQueue<MessageObject> gameInvitationQueue) {
        this.gameInvitationQueue = gameInvitationQueue;
    }

    /**
     * This queue stores incoming friend requests to be consumed when requests window is opened.
     */
    private BlockingQueue<MessageObject> gameInvitationQueue = new LinkedBlockingQueue<>();

    public void setAuthenticationToken(String s){this.authenticationToken = s;}
    public void setUsername(String name){
        this.name = name;
    }
    public void setUserID(int id){
        this.userId = id;
    }


    @FXML
    StackPane stackPane;

    @FXML
    ListView<EditFriendObject> listview;

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


    public void getEditFriendsList(){

        ResponseObject responseObject = Client.getEditFriendsList(userId, authenticationToken);

        if(!responseObject.getStatusCode().equals(CODES.INVALID_REQUEST)){

            System.out.println(responseObject.getResponse());

            try {
                //Were okay, parse string
                String[] response_array = responseObject.getResponse().split("\\|");

                //Return
                //FRIEND_ID |  FIRSTNAME LASTNAME   |      TIME ADDED

                for (int i = 0; i < response_array.length; i += 3) {
                    friendsList.add(new EditFriendObject(userId, Integer.parseInt(response_array[i]), response_array[i + 1], response_array[i + 2], authenticationToken));
                }

            }catch(Exception e){

                //Do something
                e.printStackTrace();

            }
        }

        listview.setItems(friendsList);
        listview.setCellFactory(studentListView -> new chooseFriendsCell(listview, gameInvitationQueue, stage));

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
