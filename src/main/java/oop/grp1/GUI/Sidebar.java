package oop.grp1.GUI;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.function.Consumer;

public class Sidebar extends VBox {    private final Button dashboardButton;
    private final Button chatbotButton;
    private final Button newsButton;
    private final Button userdetailButton;
    private final double expandedWidth = 150;
    private final double collapsedWidth = 50;
    private final Label logoLabel;

    public Sidebar(Consumer<String> onNavigate) {
        // Initial state: Collapsed
        this.setPrefWidth(collapsedWidth);
        this.setStyle("-fx-background-color: #000000;");
//        this.setSpacing(10);

        // Logo
        logoLabel = new Label("Logo");
        logoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-alignment: center;");
        this.getChildren().add(logoLabel);        // Navigation Buttons with Icons
        dashboardButton = createButton("Dashboard", "/images/conlonduc.png");
        chatbotButton = createButton("Chatbot", "/images/conlonduc.png");
        newsButton = createButton("Tin tức", "/images/conlonduc.png");
        userdetailButton = createButton("User Detail", "/images/conlonduc.png");

        // Initial visibility for buttons
        setButtonsVisibility(false);        dashboardButton.setOnAction(e -> onNavigate.accept("Dashboard"));
        chatbotButton.setOnAction(e -> onNavigate.accept("Chatbot"));
        newsButton.setOnAction(e -> onNavigate.accept("News"));
        userdetailButton.setOnAction(e -> onNavigate.accept("UserDetail"));

        this.getChildren().addAll(dashboardButton, chatbotButton, newsButton, userdetailButton);

        // Mouse events for expand/collapse
        this.setOnMouseEntered(e -> expandSidebar());
        this.setOnMouseExited(e -> collapseSidebar());
    }

    private Button createButton(String text, String iconPath) {
        ImageView icon = new ImageView(getClass().getResource(iconPath).toExternalForm());
        icon.setFitWidth(24);
        icon.setFitHeight(35);

        Button button = new Button(text, icon);
        button.setStyle(
        "-fx-pref-width: 150;" + 
        "-fx-text-fill: white;" + // Màu chữ đen để tương phản với nền trắng
        "-fx-background-color: #1a1a1a;" + // Nền trắng
        "-fx-background-radius: 5;" + // Bo góc cho nút
        // "-fx-border-color: #cccccc;" + // Viền xám nhạt
        "-fx-border-width: 1;" // Độ dày viền
    );
        button.setContentDisplay(ContentDisplay.LEFT); // Căn chỉnh icon và text theo chiều ngang
        button.setAlignment(Pos.CENTER_LEFT); // Căn chỉnh toàn bộ nội dung sang trái
        button.setPrefHeight(40); // Chiều cao cố định
        button.setMaxWidth(Double.MAX_VALUE); // Đảm bảo nút chiếm toàn bộ chiều rộng
        return button;
    }

    private void expandSidebar() {
        Timeline expand = new Timeline(
                new KeyFrame(Duration.millis(300), new KeyValue(this.prefWidthProperty(), expandedWidth))
        );
        expand.setOnFinished(finishEvent -> setButtonsVisibility(true)); // Show text after expanding
        expand.play();
    }

    private void collapseSidebar() {
        setButtonsVisibility(false); // Hide text before collapsing
        Timeline collapse = new Timeline(
                new KeyFrame(Duration.millis(300), new KeyValue(this.prefWidthProperty(), collapsedWidth))
        );
        collapse.play();
    }    private void setButtonsVisibility(boolean visible) {
        dashboardButton.setText(visible ? "Dashboard" : "");
        chatbotButton.setText(visible ? "Chatbot" : "");
        newsButton.setText(visible ? "Tin tức" : "");
        userdetailButton.setText(visible ? "User Detail" : "");
    }
}