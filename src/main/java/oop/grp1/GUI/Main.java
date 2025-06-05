package oop.grp1.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new MainLayout(), 1000, 700);
        
        // Add CSS for hiding scrollbars
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        // Add logo to title bar
        try {
            Image logoIcon = new Image(getClass().getResourceAsStream("/images/logo.png"));
            primaryStage.getIcons().add(logoIcon);
        } catch (Exception e) {
            System.err.println("Could not load logo image for title bar: " + e.getMessage());
        }
        
        primaryStage.setTitle("Stock Management App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}