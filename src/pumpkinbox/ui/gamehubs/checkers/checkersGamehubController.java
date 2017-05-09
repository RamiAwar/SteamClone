package pumpkinbox.ui.gamehubs.checkers;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pumpkinbox.api.EditFriendObject;
import pumpkinbox.api.MessageObject;
import pumpkinbox.games.checkers.Checkers;
import pumpkinbox.ui.draggable.EffectUtilities;
import pumpkinbox.ui.gamehubs.choose_friends_tictactoe.chooseFriendController;
import pumpkinbox.ui.icons.Icons;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ramiawar on 4/18/17.
 */

public class checkersGamehubController implements Initializable{

    private String authenticationToken;
    private int userId;
    private String name;
    private Stage stage;

    private ObservableList<EditFriendObject> online_users = FXCollections.observableArrayList();

    private BlockingQueue<MessageObject> gameInvitationQueue = new LinkedBlockingQueue<>();

    private Icons icons = new Icons();

    public void setAuthenticationToken(String s){
        this.authenticationToken = s;
    }
    public void setUsername(String name){
        this.name = name;
    }
    public void setUserID(int id){
        this.userId = id;
    }
    public void setGameInvitationQueue(BlockingQueue<MessageObject> game){this.gameInvitationQueue = game;}

    @FXML
    StackPane stackPane;

    @FXML
    ListView<EditFriendObject> online_users_list;

    @FXML
    HBox menuBar;

    @FXML
    Region draggableRegion;

    @FXML
    Label closeIcon;

    @FXML
    Label minimizeIcon;

    @FXML
    JFXButton play_offline_button;

    @FXML
    JFXButton play_online_button;


    //Receiving stage from main class to make window draggable

    public void registerStage(Stage stage){

        this.stage = stage;
        EffectUtilities.makeDraggable(this.stage, this.menuBar);
        EffectUtilities.makeDraggable(this.stage, this.draggableRegion);

    }


    @FXML
    public void launch_online(ActionEvent e){

        //Open choose friends window
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pumpkinbox/ui/gamehubs/choose_friends_checkers/choose_friends_checkers.fxml"));
            Parent parent = loader.load();

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initStyle(StageStyle.TRANSPARENT);

            stage.setTitle("Choose Friend");
            Scene scene = new Scene(parent);
            scene.setFill(Color.TRANSPARENT);

            stage.setScene(scene);
            stage.setAlwaysOnTop(false);
            stage.show();

            //Passing primaryStage to controller in order to make window draggable
            chooseFriendController controller = loader.getController();

            controller.registerStage(stage);
            controller.setAuthenticationToken(authenticationToken);
            controller.setUserID(userId);
            controller.setUsername(name);
            controller.setGameInvitationQueue(gameInvitationQueue);
            controller.getEditFriendsList();

        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    @FXML
    public void launch_offline(ActionEvent e){

        //Launch tictactoe offline

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                javafx.application.Application.launch(Checkers.class, "true");
            }
        });



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
