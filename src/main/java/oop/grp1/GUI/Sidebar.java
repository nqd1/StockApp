package oop.grp1.GUI;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Consumer;

public class Sidebar extends VBox {
    private final Button dashboardButton;
    private final Button chatbotButton;
    private final Button newsButton;
    private final double expandedWidth = 150;
    private final double collapsedWidth = 50;
    private Label logoLabel;  // Bỏ final để có thể gán trong try-catch

    public Sidebar(Consumer<String> onNavigate) {
        // Initial state: Collapsed
        this.setPrefWidth(collapsedWidth);
        this.setStyle("-fx-background-color: #000000;");
//        this.setSpacing(10);        // Logo
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
            ImageView logoView = new ImageView(logoImage);
            logoView.setFitHeight(45); // Tăng chiều cao
            logoView.setFitWidth(45);  // Tăng chiều rộng
            logoView.setPreserveRatio(true);
            
            logoLabel = new Label();
            logoLabel.setGraphic(logoView);
            logoLabel.setStyle("-fx-alignment: center; -fx-pref-width: " + collapsedWidth + "; -fx-pref-height: 60;");
            logoLabel.setMaxWidth(Double.MAX_VALUE); // Chiếm toàn bộ chiều rộng
            logoLabel.setPrefHeight(60); // Tăng chiều cao container
        } catch (Exception e) {
            // Fallback nếu không load được logo
            logoLabel = new Label("STOCK");
            logoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: center; -fx-font-weight: bold; " +
                             "-fx-pref-width: " + collapsedWidth + "; -fx-pref-height: 60;");
            logoLabel.setMaxWidth(Double.MAX_VALUE);
            logoLabel.setPrefHeight(60);
        }
        this.getChildren().add(logoLabel);
        
        // Thêm spacer sau logo
        Label spacer = new Label();
        spacer.setPrefHeight(10);
        this.getChildren().add(spacer);
        
        // Navigation Buttons with FontAwesome Icons
        dashboardButton = createButton("Dashboard", FontAwesomeSolid.CHART_LINE);
        chatbotButton = createButton("Chatbot", FontAwesomeSolid.ROBOT);
        newsButton = createButton("Tin tức", FontAwesomeSolid.NEWSPAPER);

        // Initial visibility for buttons
        setButtonsVisibility(false);

        dashboardButton.setOnAction(e -> onNavigate.accept("Dashboard"));
        chatbotButton.setOnAction(e -> onNavigate.accept("Chatbot"));
        newsButton.setOnAction(e -> onNavigate.accept("News"));

        this.getChildren().addAll(dashboardButton, chatbotButton, newsButton);

        // Mouse events for expand/collapse
        this.setOnMouseEntered(e -> expandSidebar());
        this.setOnMouseExited(e -> collapseSidebar());
    }    private Button createButton(String text, FontAwesomeSolid iconType) {
        FontIcon icon = new FontIcon(iconType);
        icon.setIconColor(Color.WHITE);
        icon.setIconSize(20);

        Button button = new Button(text, icon);
        button.setStyle(
        "-fx-pref-width: 150;" + 
        "-fx-text-fill: white;" + // Màu chữ trắng để tương phản với nền đen
        "-fx-background-color: #1a1a1a;" + // Nền xám đậm
        "-fx-background-radius: 5;" + // Bo góc cho nút
        "-fx-border-width: 1;" // Độ dày viền
    );
        button.setContentDisplay(ContentDisplay.LEFT); // Căn chỉnh icon và text theo chiều ngang
        button.setAlignment(Pos.CENTER_LEFT); // Căn chỉnh toàn bộ nội dung sang trái
        button.setPrefHeight(40); // Chiều cao cố định
        button.setMaxWidth(Double.MAX_VALUE); // Đảm bảo nút chiếm toàn bộ chiều rộng
        
        // Thêm hiệu ứng hover
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-pref-width: 150;" + 
            "-fx-text-fill: white;" + 
            "-fx-background-color: #2a2a2a;" + // Màu sáng hơn khi hover
            "-fx-background-radius: 5;" + 
            "-fx-border-width: 1;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-pref-width: 150;" + 
            "-fx-text-fill: white;" + 
            "-fx-background-color: #1a1a1a;" + // Màu gốc
            "-fx-background-radius: 5;" + 
            "-fx-border-width: 1;"
        ));
        
        return button;
    }

    private void expandSidebar() {
        Timeline expand = new Timeline(
                new KeyFrame(Duration.millis(300), new KeyValue(this.prefWidthProperty(), expandedWidth))
        );
        expand.setOnFinished(finishEvent -> {
            setButtonsVisibility(true); // Show text after expanding
            // Cập nhật style cho logo khi expand
            logoLabel.setStyle(logoLabel.getStyle().replace("-fx-pref-width: " + collapsedWidth, 
                                                           "-fx-pref-width: " + expandedWidth));
        });
        expand.play();
    }

    private void collapseSidebar() {
        setButtonsVisibility(false); // Hide text before collapsing
        // Cập nhật style cho logo khi collapse
        logoLabel.setStyle(logoLabel.getStyle().replace("-fx-pref-width: " + expandedWidth, 
                                                       "-fx-pref-width: " + collapsedWidth));
        Timeline collapse = new Timeline(
                new KeyFrame(Duration.millis(300), new KeyValue(this.prefWidthProperty(), collapsedWidth))
        );
        collapse.play();
    }

    private void setButtonsVisibility(boolean visible) {
        dashboardButton.setText(visible ? "Dashboard" : "");
        chatbotButton.setText(visible ? "Chatbot" : "");
        newsButton.setText(visible ? "Tin tức" : "");
    }
}