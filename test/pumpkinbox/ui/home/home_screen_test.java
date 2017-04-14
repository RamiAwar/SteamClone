package pumpkinbox.ui.home;

/**
 * Created by ramiawar on 4/14/17.
 */

import pumpkinbox.database.DatabaseHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class home_screen_test extends Application {

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
                        "home_screen.fxml"
                )
        );

        Scene scene = new Scene(loader.load());
        scene.setFill(Color.TRANSPARENT);


        primaryStage.setScene(scene);

        primaryStage.show();

        //Instantiate database handler instance to lower loading time
        DatabaseHandler.getInstance();

        //Passing primaryStage to controller in order to make window draggable
        homeController controller =
                loader.<homeController>getController();
        controller.registerStage(primaryStage);

        primaryStage.setOnCloseRequest(e -> Platform.exit());
    }
}
