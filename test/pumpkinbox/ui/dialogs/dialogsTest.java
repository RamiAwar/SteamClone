package pumpkinbox.ui.dialogs;

/**
 * Created by ramiawar on 4/14/17.
 */

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pumpkinbox.dialogs.AlertDialog;
import pumpkinbox.dialogs.DecisionDialog;
import pumpkinbox.ui.notifications.Notification;

import static javafx.application.Application.launch;

public class dialogsTest extends Application{


        public static void main(String[] args) {
            launch(args);
        }

        private static FXMLLoader loader;

        public static FXMLLoader getLoader(){
            return loader;
        }

        @Override
        public void start(Stage primaryStage) throws Exception{

            StackPane stackPane = new StackPane();

            JFXButton button = new JFXButton("Alert Dialog");
            button.setStyle("-fx-background-color:#444;-fx-text-fill:#eee;");

            JFXButton button2 = new JFXButton("Decision Dialog");
            button2.setStyle("-fx-background-color:#444;-fx-text-fill:#eee;");


            Scene scene = new Scene(stackPane, 300, 300);

            VBox buttons = new VBox(5);
            buttons.setSpacing(50);

            stackPane.getChildren().add(buttons);
            buttons.getChildren().add(button);
            buttons.getChildren().add(button2);


            stackPane.setAlignment(buttons, Pos.CENTER);

            primaryStage.setScene(scene);

            primaryStage.show();

            AlertDialog alert = new AlertDialog(stackPane, "Error", "Invalid input values please input again", "Okay");
            DecisionDialog dialog = new DecisionDialog(stackPane, "Error", "Invalid request would you like to retry?", "Yes", "No");

            dialog.confirmButton.setOnAction(event -> {
                Notification notif = new Notification("Friend Request", "You have received a friend request from rami@gmail.com", 20, "REQUEST");
            });

            button.setOnAction(event -> {
                alert.showDialog();
            });

            button2.setOnAction(event -> {
                dialog.showDialog();
            });


        }

}
