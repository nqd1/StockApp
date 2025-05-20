package oop.grp1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// hẹ hẹ hẹ
// cái của khỉ này ở đây test javafx thôi
public class App extends Application {

    private int count = 0;

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Số: 0");
        Button button = new Button("Tăng");

        button.setOnAction(e -> {
            count++;
            label.setText("Số: " + count);
        });

        VBox root = new VBox(10, label, button);
        root.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        Scene scene = new Scene(root, 250, 150);
        primaryStage.setTitle("Đếm số");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // mvn clean javafx:run
    public static void main(String[] args) {
        launch(args);
    }
}
