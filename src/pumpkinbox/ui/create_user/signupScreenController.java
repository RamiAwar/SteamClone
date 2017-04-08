package pumpkinbox.ui.create_user;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pumpkinbox.api.CODES;
import pumpkinbox.client.Client;
import pumpkinbox.api.ResponseObject;
import pumpkinbox.ui.draggable.EffectUtilities;
import pumpkinbox.ui.icons.Icons;
import pumpkinbox.ui.images.Images;
import pumpkinbox.validation.Validator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ramiawar on 3/23/17.
 */
public class signupScreenController implements Initializable{

    private final String CRLF = "\r\n";

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
    JFXTextField firstname;
    @FXML
    JFXTextField lastname;
    @FXML
    Label firstnameLabel;
    @FXML
    Label lastnameLabel;
    @FXML
    Label emailLabel;
    @FXML
    Label passwordLabel;
    @FXML
    StackPane root;
    @FXML
    ImageView pumpkinbox_logo;

    Validator firstnameValidator;
    Validator lastnameValidator;
    Validator emailValidator;
    Validator passwordValidator;

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
    void signup(ActionEvent event){

        if(email.getText().isEmpty() || password.getText().isEmpty() || firstname.getText().isEmpty() || lastname.getText().isEmpty()){

            //TODO: Display ALERT: please fill all fields
            //
            //
            //
            //TODO: CHECK IF ANY LABEL IS VISIBLE -> INCORRECT INFO
            //
            //
            //

            return;
        }

        ResponseObject response = Client.sendSignupData("SIGNUP " + email.getText() + "|" + password.getText() + " " + firstname.getText() + "|" + lastname.getText());

        switch(response.getStatusCode()){
            case CODES.ALREADY_EXISTS:
                System.out.println("User already exists. Try logging in...");
                email.clear();
                password.clear();
                firstname.clear();
                lastname.clear();
                //TODO: alert user that they already exists


                break;
            case CODES.OK:
                System.out.println("New user successfully created.");
                System.out.println("Please login.");
                //TODO: Display alert, success, please log in
                close(); //close window
                break;

            case CODES.INSERTION_ERROR:
                System.out.println("There was an error creating your account. Please try again later.");
                //TODO: Display message
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

        EffectUtilities.makeDraggable(stage, menuBar);
        EffectUtilities.makeDraggable(stage, draggableRegion);

        closeIcon.setOnMouseClicked((MouseEvent e) ->{
            close();
        });
        minimizeIcon.setOnMouseClicked((MouseEvent e) ->{
            minimize();
        });

        pumpkinbox_logo.setImage(Images.pumpkin);
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
        firstnameValidator = new Validator(firstnameLabel, firstname, "Required");
        firstnameValidator.createNonEmptyValidator();

        lastnameValidator = new Validator(lastnameLabel, lastname, "Required");
        lastnameValidator.createNonEmptyValidator();

        emailValidator = new Validator(emailLabel, email, "Required");
        emailValidator.createEmailValidator();

        passwordValidator = new Validator(passwordLabel, password, "Required");
        passwordValidator.createPasswordValidator();

    }


}
