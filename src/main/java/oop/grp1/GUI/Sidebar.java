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

public class Sidebar extends VBox {
    private final Button trendingButton;
    private final Button newsButton;
    private final Button stockDetailButton;
    private final Button viewStockButton;
    private final Button chatbotButton;
    private final Button smartChatbotButton;
    
    private final double expandedWidth = 210;
    private final double collapsedWidth = 50;
    private Label logoLabel;  // Bỏ final để có thể gán trong try-catch

    public Sidebar() {
        // Initial state: Collapsed
        this.setPrefWidth(collapsedWidth);
        this.getStyleClass().add("sidebar");
        
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
        trendingButton = createButton("Cổ Phiếu Nổi Bật", FontAwesomeSolid.CHART_LINE);
        newsButton = createButton("Tin Tức", FontAwesomeSolid.NEWSPAPER);
        stockDetailButton = createButton("Danh Sách Cổ Phiếu", FontAwesomeSolid.LIST);
        viewStockButton = createButton("Chi Tiết Cổ Phiếu", FontAwesomeSolid.CHART_BAR);
        chatbotButton = createButton("Chatbot", FontAwesomeSolid.ROBOT);
        smartChatbotButton = createButton("Smart AI", FontAwesomeSolid.BRAIN);

        // Initial visibility for buttons
        setButtonsVisibility(false);

        // Set button actions
        trendingButton.setOnAction(e -> PageManager.getInstance().showPage("TrendingstocksPage"));
        newsButton.setOnAction(e -> PageManager.getInstance().showPage("NewsPage"));
        stockDetailButton.setOnAction(e -> PageManager.getInstance().showPage("StockDetail"));
        viewStockButton.setOnAction(e -> PageManager.getInstance().showPage("ViewStockDetail"));
        chatbotButton.setOnAction(e -> PageManager.getInstance().showPage("ChatbotPage"));
        smartChatbotButton.setOnAction(e -> PageManager.getInstance().showPage("SmartChatbotPage"));

        this.getChildren().addAll(
            trendingButton,
            newsButton,
            stockDetailButton,
            viewStockButton,
            chatbotButton,
            smartChatbotButton
        );

        // Mouse events for expand/collapse
        this.setOnMouseEntered(e -> expandSidebar());
        this.setOnMouseExited(e -> collapseSidebar());
    }    
    
    private Button createButton(String text, FontAwesomeSolid iconType) {
        FontIcon icon = new FontIcon(iconType);
        icon.setIconColor(Color.WHITE);
        icon.setIconSize(20);

        Button button = new Button(text, icon);
        button.getStyleClass().add("sidebar-button");
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPrefWidth(expandedWidth);
        button.setMaxWidth(Double.MAX_VALUE);
        
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
        trendingButton.setText(visible ? "Cổ Phiếu Nổi Bật" : "");
        newsButton.setText(visible ? "Tin Tức" : "");
        stockDetailButton.setText(visible ? "Danh Sách Cổ Phiếu" : "");
        viewStockButton.setText(visible ? "Chi Tiết Cổ Phiếu" : "");
        chatbotButton.setText(visible ? "Chatbot" : "");
        smartChatbotButton.setText(visible ? "Smart AI" : "");
    }
}