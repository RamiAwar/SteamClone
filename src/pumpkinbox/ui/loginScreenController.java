package pumpkinbox.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import pumpkinbox.api.ResponseObject;
import pumpkinbox.client.Client;
import pumpkinbox.ui.create_user.signupScreenController;
import pumpkinbox.ui.draggable.EffectUtilities;
import pumpkinbox.ui.home.homeController;
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
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pumpkinbox.api.CODES;
import pumpkinbox.ui.notifications.Notification;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ramiawar on 3/23/17.
 */
public class loginScreenController implements Initializable{

    private final String CRLF = "\r\n";
    private String authenticationToken;
    private int userId;
    private String name;


    Icons icons = new Icons();

    @FXML
    Label closeIcon;
    @FXML
    HBox menuBar;
    @FXML
    Region draggableRegion;
    @FXML
    Label minimizeIcon;
    @FXML
    JFXTextField email;
    @FXML
    JFXPasswordField password;
    @FXML
    JFXButton createAccountButton;
    @FXML
    Label errorLabel;


    private Stage stage;

    //Receiving stage from main class to make window draggable
    public void registerStage(Stage stage){

        this.stage = stage;
        EffectUtilities.makeDraggable(stage, menuBar);
        EffectUtilities.makeDraggable(stage, draggableRegion);

    }



    void close() {
        stage.close();
    }
    @FXML
    void close(ActionEvent e) {

        stage.close();
    }

    @FXML
    void signup(ActionEvent e){
        loadSignupWindow("/pumpkinbox/ui/create_user/signup_screen.fxml", "Create Account");
    }

    @FXML
    void login(ActionEvent event) {

        //loadSignupWindow("/com/librarymanager/ui/addMember/add_member.fxml", "New Member");

        //Making sure email and password fields are non-empty, or else we send an invalid request and try to receive objects that
        //will not be sent.
        if(email.getText().isEmpty() || password.getText().isEmpty()){
            errorLabel.setVisible(true);
            return;
        }

        ResponseObject response = Client.sendLoginData("LOGIN " + email.getText() + "|" + password.getText());

        switch(response.getStatusCode()) {
            case CODES.SEND_ERROR:

                System.out.println("Could not send data to server. Please try again.");

                //Do something, ALERT user
                Notification n = new Notification("Error",
                        "Could not contact server, check connection.",
                        4,
                        "ERROR");

                break;

            case CODES.OK:
                System.out.println("Sending successful.");
                System.out.println("Login succeeded.");

                //Receive authentication token
                authenticationToken = response.getToken();
                name = response.getUserName();
                System.out.println(authenticationToken);
                errorLabel.setVisible(false);
                userId = response.getUserId();

                //Transition to next scene
                loadHomeWindow("/pumpkinbox/ui/home/home_screen.fxml", "PumpkinBox");
                close();
                break;

            case CODES.NOT_FOUND:
                System.out.println("Sending successful.");
                System.out.println("User not found.");
                password.clear();
                errorLabel.setVisible(true);
                break;

            default:
                break;
        }
    }



    void loadSignupWindow(String location, String title){
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            Parent parent = loader.load();

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initStyle(StageStyle.TRANSPARENT);

            stage.setTitle(title);
            Scene scene = new Scene(parent);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add("pumpkinbox/ui/create_user/signup.css");

            stage.setScene(scene);
            stage.setAlwaysOnTop(true);
            stage.show();

            //Passing primaryStage to controller in order to make window draggable
            signupScreenController controller =
                    loader.<signupScreenController>getController();
            controller.registerStage(stage);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void loadHomeWindow(String location, String title){
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            Parent parent = loader.load();

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initStyle(StageStyle.TRANSPARENT);

            stage.setTitle(title);
            Scene scene = new Scene(parent);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add("pumpkinbox/ui/home/home.css");

            stage.setScene(scene);
            stage.setAlwaysOnTop(false);
            stage.show();

            //Passing primaryStage to controller in order to make window draggable
            homeController controller =
                    loader.<homeController>getController();

            controller.registerStage(stage);

            System.out.println("Writing token to home: " + authenticationToken);
            controller.setAuthenticationToken(authenticationToken);
            controller.setUserId(userId);
            controller.setName(name);
            controller.initClient();

            System.out.println("LOGIN - USER NAME: " + name);

            stage.setOnCloseRequest(e -> Platform.exit());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        loadIcons();

        errorLabel.managedProperty().bind(errorLabel.visibleProperty());

        errorLabel.setVisible(false);
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
