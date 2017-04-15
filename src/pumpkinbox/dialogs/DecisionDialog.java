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
 * Decision dialog is a dialog box with two action buttons, one of which can be programmed from the caller class, and the other is a cancel button.
 * This is done through referencing the public variable confirmButton and overriding its setOnAction method.
 * <br><br>
 * <h1>Example usage of DecisionDialog class:</h1>
 * <pre>
 * {@code
 * DecisionDialog dialog = new DecisionDialog(stackPane, "GAME INVITE", "John Doe has invited you to a game of TicTacToe.", "JOIN GAME");
 * dialog.confirmButton.setOnAction(event -> dialog.closeDialog());
 * dialog.showDialog();
 * }
 * </pre>
 */
public class DecisionDialog {

    public JFXButton confirmButton;

    private JFXDialogLayout content;
    private JFXDialog dialog;
    private JFXButton cancelButton;


    /**
     * Material dialog constructor with a cancel button text parameter.
     * @param stackPane
     * @param heading
     * @param body
     * @param confirmButtonText
     * @param cancelButtonText
     */
    public DecisionDialog(StackPane stackPane, String heading, String body, String confirmButtonText, String cancelButtonText){

        this.content = new JFXDialogLayout();
        this.content.setHeading(new Text(heading));
        this.content.setBody(new Text(body));

        this.dialog = new JFXDialog(stackPane, this.content, JFXDialog.DialogTransition.BOTTOM);
        stackPane.setMargin(dialog, new Insets(10, 10, 10, 10));

        this.confirmButton = new JFXButton(confirmButtonText);
        this.cancelButton = new JFXButton(cancelButtonText);

        this.confirmButton.setStyle("-fx-text-fill:#4285f4;");
        this.cancelButton.setStyle("-fx-text-fill:#4285f4;");


        this.cancelButton.setOnAction(event -> this.dialog.close());

        this.content.setActions(this.confirmButton, this.cancelButton);
    }

    /**
     * Material dialog constructor with cancel button text set to "Cancel".
     * @param stackPane
     * @param heading
     * @param body
     * @param confirmButtonText
     */
    public DecisionDialog(StackPane stackPane, String heading, String body, String confirmButtonText){

        this.content = new JFXDialogLayout();
        this.content.setHeading(new Text(heading));
        this.content.setBody(new Text(body));

        this.dialog = new JFXDialog(stackPane, this.content, JFXDialog.DialogTransition.BOTTOM);
        stackPane.setMargin(dialog, new Insets(10, 10, 10, 10));

        this.confirmButton = new JFXButton(confirmButtonText);
        this.cancelButton = new JFXButton("Cancel");

        this.confirmButton.setStyle("-fx-text-fill:#4285f4;");
        this.cancelButton.setStyle("-fx-text-fill:#4285f4;");


        this.cancelButton.setOnAction(event -> this.dialog.close());

        this.content.setActions(this.confirmButton, this.cancelButton);
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
