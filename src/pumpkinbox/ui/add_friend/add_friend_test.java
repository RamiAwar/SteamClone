package pumpkinbox.ui.add_friend;

/**
 * Created by ramiawar on 4/3/17.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pumpkinbox.database.DatabaseHandler;

public class add_friend_test extends Application {

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
                        "add_friend_screen.fxml"
                )
        );

        Scene scene = new Scene(loader.load());
        scene.setFill(Color.TRANSPARENT);


        primaryStage.setScene(scene);

        primaryStage.show();

        //Instantiate database handler instance to lower loading time
        DatabaseHandler.getInstance();

        //Passing primaryStage to controller in order to make window draggable
        addFriendController controller =
                loader.<addFriendController>getController();
        controller.registerStage(primaryStage);

    }
}
