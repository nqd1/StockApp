package oop.grp1.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class Sidebar extends VBox {
    public Sidebar() {
        this.setPrefWidth(250);
        this.setPadding(new Insets(20));
        this.setSpacing(10);
        this.setStyle("-fx-background-color: #2c3e50;");
        this.setAlignment(Pos.TOP_CENTER);

        // Create navigation buttons
        Button trendingButton = createNavButton("Cổ Phiếu Nổi Bật", FontAwesomeSolid.CHART_LINE, true); // Highlight as default
        Button newsButton = createNavButton("Tin Tức", FontAwesomeSolid.NEWSPAPER, false);
        Button stockDetailButton = createNavButton("Danh Sách Cổ Phiếu", FontAwesomeSolid.LIST, false);
        Button viewStockButton = createNavButton("Chi Tiết Cổ Phiếu", FontAwesomeSolid.CHART_BAR, false);
        Button watchListButton = createNavButton("Danh Sách Theo Dõi", FontAwesomeSolid.STAR, false);
        Button chatbotButton = createNavButton("Chatbot", FontAwesomeSolid.ROBOT, false);

        // Set button actions
        trendingButton.setOnAction(e -> PageManager.getInstance().showPage("TrendingstocksPage"));
        newsButton.setOnAction(e -> PageManager.getInstance().showPage("NewsPage"));
        stockDetailButton.setOnAction(e -> PageManager.getInstance().showPage("StockDetail"));
        viewStockButton.setOnAction(e -> PageManager.getInstance().showPage("ViewStockDetail"));
        watchListButton.setOnAction(e -> PageManager.getInstance().showPage("WatchList"));
        chatbotButton.setOnAction(e -> PageManager.getInstance().showPage("ChatbotPage"));

        // Add buttons to sidebar
        this.getChildren().addAll(
                trendingButton,
                newsButton,
                stockDetailButton,
                viewStockButton,
                watchListButton,
                chatbotButton
        );
    }

    private Button createNavButton(String text, FontAwesomeSolid icon, boolean isDefault) {
        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.setIconSize(20);
        fontIcon.setIconColor(Color.WHITE);

        Button button = new Button(text, fontIcon);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPrefWidth(220);
        String baseStyle = "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px; -fx-cursor: hand;";
        String defaultColor = isDefault ? "#34495e" : "transparent";
        button.setStyle(String.format(baseStyle, defaultColor));
        button.setOnMouseEntered(e -> button.setStyle(String.format(baseStyle, "#34495e")));
        button.setOnMouseExited(e -> button.setStyle(String.format(baseStyle, isDefault ? "#34495e" : "transparent")));
        return button;
    }
}