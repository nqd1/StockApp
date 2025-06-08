package oop.grp1.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Sử dụng transparent để có thể bo góc
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        
        MainLayout mainLayout = new MainLayout(primaryStage);
        mainLayout.getStyleClass().add("main-layout");
        
        Scene scene = new Scene(mainLayout, 1000, 700);
        scene.setFill(Color.TRANSPARENT);
        
        // Add CSS for hiding scrollbars and custom styling
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
        
        // Center the stage on screen
        primaryStage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}