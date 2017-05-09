package pumpkinbox.ui.gamehubs.tictactoe;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.stage.StageStyle;
import pumpkinbox.api.*;
import pumpkinbox.client.Client;
import pumpkinbox.dialogs.AlertDialog;
import pumpkinbox.games.TicTacToe.TicTacToeClient;
import pumpkinbox.ui.draggable.EffectUtilities;
import pumpkinbox.ui.gamehubs.choose_friends_tictactoe.chooseFriendController;
import pumpkinbox.ui.icons.Icons;
import pumpkinbox.ui.images.Images;
import pumpkinbox.ui.requests.requestListViewCell;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ramiawar on 4/18/17.
 */

public class gamehubController implements Initializable{

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

    @FXML
    BarChart<?, ?> global_stats;

    @FXML
    CategoryAxis x;

    @FXML
    NumberAxis yAxis;

    @FXML
    BarChart<?,?> local_stats;

    @FXML
    ImageView game_image;


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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pumpkinbox/ui/gamehubs/choose_friends_tictactoe/choose_friend_screen.fxml"));
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
        new Thread(() -> {

            while (true) {

                try {
                    TicTacToeClient client = new TicTacToeClient(true, true);
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

        game_image.setImage(Images.getImage("tictactoe"));

        XYChart.Series set1 = new XYChart.Series<>();
        set1.getData().add(new XYChart.Data("WINS", 20));
        set1.getData().add(new XYChart.Data("LOSSES", 20));
        set1.getData().add(new XYChart.Data("TIES", 100));

        global_stats.getData().addAll(set1);

        XYChart.Series set2 = new XYChart.Series<>();
        set2.getData().add(new XYChart.Data("WINS", 2));
        set2.getData().add(new XYChart.Data("LOSSES", 2));
        set2.getData().add(new XYChart.Data("TIES", 5));

        local_stats.getData().addAll(set2);

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
