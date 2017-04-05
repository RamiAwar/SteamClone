package pumpkinbox.ui;/**
 * Created by ramiawar on 3/23/17.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static FXMLLoader loader;

    public static FXMLLoader getLoader(){
        return loader;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        loader = new FXMLLoader(
                getClass().getResource(
                        "login_screen.fxml"
                )
        );

        Scene scene = new Scene(loader.load());
        scene.setFill(Color.TRANSPARENT);


        primaryStage.setScene(scene);

        primaryStage.show();

        //Passing primaryStage to controller in order to make window draggable
        loginScreenController controller =
                loader.<loginScreenController>getController();
        controller.registerStage(primaryStage);

    }
}
