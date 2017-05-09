package pumpkinbox.ui.friend_profile;

import com.jfoenix.controls.JFXProgressBar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pumpkinbox.api.CODES;
import pumpkinbox.api.EditFriendObject;
import pumpkinbox.api.GameActivityObject;
import pumpkinbox.api.ResponseObject;
import pumpkinbox.client.Client;
import pumpkinbox.dialogs.AlertDialog;
import pumpkinbox.ui.draggable.EffectUtilities;
import pumpkinbox.ui.icons.Icons;
import pumpkinbox.ui.images.Images;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ramiawar on 4/18/17.
 */

public class friendProfileController implements Initializable{

    private String authenticationToken;
    private int userId;
    private String name;
    private Stage stage;
    private String user_name;
    private Icons icons = new Icons();
    private int friendId;

    private ObservableList<EditFriendObject> friendsList = FXCollections.observableArrayList();
    private ObservableList<GameActivityObject> gameActivityList = FXCollections.observableArrayList();

    public void setAuthenticationToken(String s){
        this.authenticationToken = s;
    }
    public void setUsername(String name){
        this.user_name = name;
    }
    public void setUserID(int id){
        this.userId = id;
    }
    public void setFriendId(int id){
        this.friendId = id;
    }
    public void setName(String name){
        this.name = name;
    }

    @FXML
    StackPane stackPane;

    @FXML
    ListView<GameActivityObject> activity_list;

    @FXML
    ListView<EditFriendObject> friends_list;

    @FXML
    HBox menuBar;

    @FXML
    Region draggableRegion;

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
    Label username;

    @FXML
    Label email;

    @FXML
    ImageView profile_photo;


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


    public void getFriendsList(){

        updateGUI();

        ResponseObject responseObject = Client.getEditFriendsList(userId, authenticationToken);

        if(!responseObject.getStatusCode().equals(CODES.INVALID_REQUEST)){

            System.out.println(responseObject.getResponse());
            //Were okay, parse string
            String[] response_array = responseObject.getResponse().split("\\|");

            //Return
            //FRIEND_ID |  FIRSTNAME LASTNAME   |      TIME ADDED
            try {

                for (int i = 0; i < response_array.length; i += 3) {
                    friendsList.add(new EditFriendObject(userId, Integer.parseInt(response_array[i]), response_array[i + 1], response_array[i + 2], authenticationToken));
                }
            }catch(Exception e){
                //Do something
            }
        }

        friends_list.setItems(friendsList);
        friends_list.setCellFactory(studentListView -> new friendListViewCell(friends_list));
    }

    public void getFriendActivityList(){

        ResponseObject responseObject = Client.getFriendActivityList(userId, authenticationToken, friendId);

        if(!responseObject.getStatusCode().equals(CODES.INVALID_REQUEST)){

            //Were okay, parse string
            //RESPONSE FORMAT
            // FRIEND NAME | GAME NAME | GAME STATUS | EXPERIENCE

            try {

                String[] response_array = responseObject.getResponse().split("\\|");
                for (int i = 0; i < response_array.length; i += 4) {
                    gameActivityList.add(new GameActivityObject(response_array[i], response_array[i + 1], response_array[i + 3], response_array[i + 2]));
                }
            }catch(Exception e){
                //Do something
            }

        }

        activity_list.setItems(gameActivityList);
        activity_list.setCellFactory(studentListView -> new gameActivityListViewCell(activity_list));

    }

    public void getFriendExperiencePoints(){

        //get sum of experience points
        ResponseObject response = Client.getFriendExperience(userId, authenticationToken, friendId);
        System.out.println("EXP:" + response.getResponse());
        if(response.getStatusCode().equals(CODES.OK)) {

            int experience = 0 + Integer.parseInt(response.getResponse());
            int level = 0 + (experience / 20);
            double progress = 0 + (((double) experience % 20)/20);


            experience_points.setText(Integer.toString(experience) + "xp");
            level_label.setText("Level " + Integer.toString(level));
            level_progress_bar.setProgress(progress);

        }else{
            AlertDialog alert = new AlertDialog(stackPane, "Error", "Could not get experience points", "Okay");
        }

    }

    public void updateGUI(){
        username.setText(name);
        email.setText(user_name);
        profile_photo.setImage(Images.pumpkin_logo);
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
