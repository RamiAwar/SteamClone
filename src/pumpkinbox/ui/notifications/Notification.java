package pumpkinbox.ui.notifications;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import pumpkinbox.ui.icons.Icons;

/**
 * Created by ramiawar on 4/4/17.
 */
public class Notification {

    public Notification (String title, String message, int seconds, String code){

        ImageView image;

        switch(code){
            case "SUCCESS":
                image  = new ImageView(new Image("images/check_green.png"));
                break;
            case "ERROR":
                image = new ImageView(new Image("images/cross_red.png"));
                break;
            default:
                image = new ImageView(new Image("images/warning_red.png"));
                break;
        }

        Notifications notifications = Notifications.create();
        notifications.title(title);
        notifications.text(message);
        notifications.graphic(image);
        notifications.darkStyle();
        notifications.hideAfter(Duration.seconds(seconds));
        notifications.position(Pos.BOTTOM_RIGHT);
        notifications.show();

    }


}
