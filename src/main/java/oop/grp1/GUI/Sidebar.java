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

public class Sidebar extends VBox {
    private final Button dashboardButton;
    private final Button chatbotButton;
    private final Button stockDetailButton;
    private final Button userdetailButton;
    private final Button watchlistButton;
    private final Button viewStockDetailButton;
    private final double expandedWidth = 150;
    private final double collapsedWidth = 50;
    private final Label logoLabel;

    public Sidebar(Consumer<String> onNavigate, Button stockDetailButton) {
        this.stockDetailButton = stockDetailButton;

        this.setPrefWidth(collapsedWidth);
        this.setStyle("-fx-background-color: #000000;");

        logoLabel = new Label("Logo");
        logoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-alignment: center;");
        this.getChildren().add(logoLabel);

        dashboardButton = createButton("Dashboard", "/images/conlonduc.png");
        chatbotButton = createButton("Chatbot", "/images/conlonduc.png");
        userdetailButton = createButton("User Detail", "/images/conlonduc.png");
        watchlistButton = createButton("Watchlist", "/images/conlonduc.png");
        viewStockDetailButton = createButton("View Stock Chart", "/images/conlonduc.png");

        this.stockDetailButton.setStyle(
                "-fx-pref-width: 150;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: #1a1a1a;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-width: 1;"
        );
        this.stockDetailButton.setContentDisplay(ContentDisplay.LEFT);
        this.stockDetailButton.setAlignment(Pos.CENTER_LEFT);
        this.stockDetailButton.setPrefHeight(40);
        this.stockDetailButton.setMaxWidth(Double.MAX_VALUE);

        setButtonsVisibility(false);

        dashboardButton.setOnAction(e -> onNavigate.accept("Dashboard"));
        chatbotButton.setOnAction(e -> onNavigate.accept("Chatbot"));
        userdetailButton.setOnAction(e -> onNavigate.accept("UserDetail"));
        watchlistButton.setOnAction(e -> onNavigate.accept("Watchlist"));
        stockDetailButton.setOnAction(e -> onNavigate.accept("StockDetail"));
        viewStockDetailButton.setOnAction(e -> onNavigate.accept("ViewStockDetail"));

        this.getChildren().addAll(dashboardButton, chatbotButton, userdetailButton, watchlistButton, stockDetailButton, viewStockDetailButton);

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
                        "-fx-text-fill: white;" +
                        "-fx-background-color: #1a1a1a;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-width: 1;"
        );
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPrefHeight(40);
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private void expandSidebar() {
        Timeline expand = new Timeline(
                new KeyFrame(Duration.millis(300), new KeyValue(this.prefWidthProperty(), expandedWidth))
        );
        expand.setOnFinished(finishEvent -> setButtonsVisibility(true));
        expand.play();
    }

    private void collapseSidebar() {
        setButtonsVisibility(false);
        Timeline collapse = new Timeline(
                new KeyFrame(Duration.millis(300), new KeyValue(this.prefWidthProperty(), collapsedWidth))
        );
        collapse.play();
    }

    private void setButtonsVisibility(boolean visible) {
        dashboardButton.setText(visible ? "Dashboard" : "");
        chatbotButton.setText(visible ? "Chatbot" : "");
        userdetailButton.setText(visible ? "User Detail" : "");
        watchlistButton.setText(visible ? "Watchlist" : "");
        viewStockDetailButton.setText(visible ? "View Stock Chart" : "");
    }
}