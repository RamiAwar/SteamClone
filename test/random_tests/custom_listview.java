package random_tests;

/**
 * Created by ramiawar on 4/17/17.
 */
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import pumpkinbox.ui.notifications.Notification;

public class custom_listview extends Application {

        private ListView<String> lv;
        private static ObservableList<String> list;


        static class XCell extends ListCell<String> {
            HBox hbox = new HBox();
            Label label = new Label("(empty)");
            Pane pane = new Pane();
            Button accept = new Button("Accept");
            Button reject = new Button("Reject");
            String lastItem;

            public XCell() {
                super();
                hbox.getChildren().addAll(label, pane, accept, reject);
                HBox.setHgrow(pane, Priority.ALWAYS);
                accept.setOnAction(event -> {
                    new Notification("Accepted", "You have accepted " + lastItem + "'s request.", 5, "REQUEST");
                    list.remove(lastItem);
                });
                reject.setOnAction(event -> new Notification("Rejected", "You have rejected " + lastItem + "'s request.", 5, "REQUEST"));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);  // No text in label of super class
                if (empty) {
                    lastItem = null;
                    setGraphic(null);
                } else {
                    lastItem = item;
                    label.setText(item!=null ? item : "<null>");
                    setGraphic(hbox);
                }
            }
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            StackPane pane = new StackPane();
            Scene scene = new Scene(pane, 300, 150);
            primaryStage.setScene(scene);
             list = FXCollections.observableArrayList(
                    "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6");
            lv = new ListView<>(list);
            lv.setCellFactory(param -> new XCell());
            pane.getChildren().add(lv);
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }

}
