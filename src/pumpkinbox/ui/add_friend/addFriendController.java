package pumpkinbox.ui.add_friend;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pumpkinbox.api.CODES;
import pumpkinbox.api.ResponseObject;
import pumpkinbox.client.Client;
import pumpkinbox.dialogs.AlertDialog;
import pumpkinbox.ui.draggable.EffectUtilities;
import pumpkinbox.ui.icons.Icons;
import pumpkinbox.ui.images.Images;
import pumpkinbox.validation.Validator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;

/**
 * Created by ramiawar on 3/23/17.
 */
public class addFriendController implements Initializable{

    private String authenticationToken;

    private int userID;
    private String userName;

    public void setUserID(int userID) {
        this.userID = userID;
    }
    public void setName(String userName) {
        this.userName = userName;
    }

    private final String CRLF = "\r\n";

    //authentication token setter to set from outside controller
    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    Icons icons = new Icons();

    @FXML
    StackPane stackPane;

    @FXML
    Label closeIcon;

    @FXML
    Label minimizeIcon;

    @FXML
    HBox menuBar;

    @FXML
    JFXTextField friend_email;

    @FXML
    Label error_label;

    @FXML
    JFXButton searchButton;

    @FXML
    JFXButton cancelButton;

    @FXML
    Region draggableRegion;

    @FXML
    ImageView pumpkinbox_logo;

    private Stage stage;

    Validator emailValidator;

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


    /**
     * Validates email field then sends a request to find friend. If the friend is found, a friend request is sent to that user.
     * @param event
     */
    @FXML
    void addFriend(ActionEvent event){

        //Visible error label -> invalid email indirectly ( faster than checking email validity directly )
        if( friend_email.getText().isEmpty() || error_label.isVisible()){

            // Display ALERT: please fill all fields
            new AlertDialog(stackPane, "Invalid Request", "Please enter a valid friend's email.").showDialog();

            return;
        }

        String email = friend_email.getText();
        friend_email.clear();

        ResponseObject responseObject = Client.sendFriendRequestData(userID, userName, email, authenticationToken);

        switch(responseObject.getStatusCode()){
            case CODES.OK:
                new AlertDialog(stackPane, "Request Successful", "Your friend request has been sent.", "Okay").showDialog();
                break;
            case CODES.NOT_FOUND:
                new AlertDialog(stackPane, "Incorrect Email", "The username you requested does not exist. Please try again.", "Okay").showDialog();
                break;
            case CODES.SEND_ERROR:
                new AlertDialog(stackPane, "Unknown Error", "There was an error sending your request. Please try again later.", "Okay").showDialog();
                break;
            case CODES.ALREADY_EXISTS:
                new AlertDialog(stackPane, "Already Friends", "You are already friends. Try sending a request to another user.", "Okay").showDialog();
                break;
            default:
                break;
        }
    }



    void loadWindow(String location, String title){
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            Parent parent = loader.load();
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initStyle(StageStyle.TRANSPARENT);

            stage.setTitle(title);
            Scene scene = new Scene(parent);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setAlwaysOnTop(true);
            stage.show();
            stage.setOnCloseRequest(e -> {
                Platform.exit();
                System.exit(0);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadIcons();

        setupValidators();

        closeIcon.setOnMouseClicked((MouseEvent e) ->{
            close();
        });
        minimizeIcon.setOnMouseClicked((MouseEvent e) ->{
            minimize();
        });

        pumpkinbox_logo.setImage(Images.pumpkin_logo_gif);
    }

    private void minimize(){
        stage.setIconified(true);
    }

    public void loadIcons(){

        //Setting Icons on buttons using **ui.Icons.Icons** class
        icons.setSize("0.4em");

        closeIcon.setGraphic(icons.CLOSE_m);
        minimizeIcon.setGraphic(icons.MINIMIZE_m);

    }

    void setupValidators(){

        emailValidator = new Validator(error_label, friend_email, "Invalid email");
        emailValidator.createEmailValidator();

    }


}
