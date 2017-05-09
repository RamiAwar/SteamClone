package pumpkinbox.dialogs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * Created by ramiawar on 4/14/17.
 * @author Rami Awar
 * @version 1.0
 */

/**
 * Alert dialog is a dialog box with no action button, serving simply as an alert to the user.
 * <br><br>
 * <h1>Example usage of AlertDialog class:</h1>
 * <pre>
 * {@code
 * AlertDialog alert = new AlertDialog(stackPane, "Error", "You are required to fill all the fields", "Okay");
 * dialog.showDialog();
* }
 * </pre>
 */
public class AlertDialog {

    private JFXDialogLayout content;
    public JFXDialog dialog;
    public JFXButton actionButton;


    /**
     * Material dialog constructor with a cancel button text parameter.
     * @param stackPane StackPane to show dialog above
     * @param heading Text to show as dialog title
     * @param body Text to show as dialog content or body
     * @param actionDialogText Text to show on action button ( which closes the dialog )
     */
    public AlertDialog(StackPane stackPane, String heading, String body, String actionDialogText){

        this.content = new JFXDialogLayout();
        this.content.setHeading(new Text(heading));
        this.content.setBody(new Text(body));

        this.dialog = new JFXDialog(stackPane, this.content, JFXDialog.DialogTransition.BOTTOM);
        StackPane.setMargin(dialog, new Insets(10, 10, 10, 10));

        this.actionButton = new JFXButton(actionDialogText);

        this.actionButton.setOnAction(event -> this.dialog.close());
        this.actionButton.setStyle("-fx-text-fill:#4285f4;");

        this.content.setActions(this.actionButton);

    }

    /**
     * Material dialog constructor with cancel button text set to "Close".
     * @param stackPane StackPane to show dialog above
     * @param heading Text to show as dialog title
     * @param body Text to show as dialog content or body
     */
    public AlertDialog(StackPane stackPane, String heading, String body){

        this.content = new JFXDialogLayout();
        this.content.setHeading(new Text(heading));
        this.content.setBody(new Text(body));

        this.dialog = new JFXDialog(stackPane, this.content, JFXDialog.DialogTransition.BOTTOM);
        StackPane.setMargin(dialog, new Insets(10, 10, 10, 10));

        this.actionButton = new JFXButton("Close");


        this.actionButton.setOnAction(event -> this.dialog.close());
        this.actionButton.setStyle("-fx-text-fill:#4285f4;");

        this.content.setActions(this.actionButton);
    }


    /**
     * Shows the dialog created by the constructor.
     */
    public void showDialog(){
        this.dialog.show();
    }

    /**
     * Closes the dialog created by the constructor.
     */
    public void closeDialog(){
        this.dialog.close();
    }


}
