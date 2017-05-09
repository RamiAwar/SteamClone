package pumpkinbox.ui.create_user;

/**
 * Created by ramiawar on 4/14/17.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pumpkinbox.database.DatabaseHandler;


/**
 * Created by ramiawar on 3/23/17.
 */


public class signup_screen_test extends Application {

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
                        "signup_screen.fxml"
                )
        );

        Scene scene = new Scene(loader.load());
        scene.setFill(Color.TRANSPARENT);


        primaryStage.setScene(scene);

        primaryStage.show();

        //Instantiate database handler instance to lower loading time
        DatabaseHandler.getInstance();

        //Passing primaryStage to controller in order to make window draggable
        signupScreenController controller =
                loader.getController();
        controller.registerStage(primaryStage);

    }
}
