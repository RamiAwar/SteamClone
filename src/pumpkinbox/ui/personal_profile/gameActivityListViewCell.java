package pumpkinbox.ui.personal_profile;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import pumpkinbox.api.CODES;
import pumpkinbox.api.GameActivityObject;
import pumpkinbox.api.RequestObject;
import pumpkinbox.api.ResponseObject;
import pumpkinbox.client.Client;
import pumpkinbox.ui.images.Images;
import pumpkinbox.ui.notifications.Notification;

import java.io.IOException;

/**
 * Created by ramiawar on 4/30/17.
 */
public class gameActivityListViewCell extends ListCell<GameActivityObject> {

    private ListView<GameActivityObject> listview;

    @FXML
    HBox root;

    @FXML
    ImageView gameImage;

    @FXML
    Label gameTitle;

    @FXML
    Label gameStatus;

    @FXML
    Label gameExperience;

    @FXML
    Label friend_username;

    private FXMLLoader mLLoader;

    public gameActivityListViewCell( ListView<GameActivityObject> listView ) {
        super();
        this.listview = listView; //Passing listview by reference to modify it
    }


    protected void updateItem(GameActivityObject gameActivityObject, boolean empty) {

        super.updateItem(gameActivityObject, empty);

        if(empty || gameActivityObject == null) {

            setText(null);
            setGraphic(null);

        } else {

            if (mLLoader == null) {

                mLLoader = new FXMLLoader(getClass().getResource("gameActivityCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            gameTitle.setText(String.valueOf(gameActivityObject.getGameName()));
            gameExperience.setText(String.valueOf(gameActivityObject.getExperience()));
            gameStatus.setText(String.valueOf(gameActivityObject.getStatus()));
            friend_username.setText(String.valueOf(gameActivityObject.getOpponentName()));

            if(gameActivityObject.getStatus().equals("WIN")) {
                gameStatus.setTextFill(Paint.valueOf("#2ed352"));//WIN
            }else if(gameActivityObject.getStatus().equals("LOSE")) {
                gameStatus.setTextFill(Paint.valueOf("#ff4545"));//LOSE
            }

            gameImage.setImage(Images.getImage(gameActivityObject.getGameName()));


            setText(null);
            setGraphic(root);
        }

    }


}
