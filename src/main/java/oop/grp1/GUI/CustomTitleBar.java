package oop.grp1.GUI;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class CustomTitleBar extends HBox {
    private final Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;
    private boolean maximized = false;
    private double restoreX, restoreY, restoreWidth, restoreHeight;

    public CustomTitleBar(Stage stage) {
        this.stage = stage;
        
        setupTitleBar();
        setupDragHandlers();
    }

    private void setupTitleBar() {
        this.setPrefHeight(40);
        this.setStyle(
            "-fx-background-color: linear-gradient(to right, #2c3e50, #34495e);" +
            "-fx-border-color: #1a252f;" +
            "-fx-border-width: 0 0 1 0;"
        );
        this.setPadding(new Insets(5, 10, 5, 10));
        this.setAlignment(Pos.CENTER_LEFT);

        // Logo và tiêu đề
        HBox leftSection = createLeftSection();
        
        // Spacer để đẩy nút sang phải
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Nút điều khiển
        HBox controlButtons = createControlButtons();
        
        this.getChildren().addAll(leftSection, spacer, controlButtons);
    }

    private HBox createLeftSection() {
        HBox leftSection = new HBox(10);
        leftSection.setAlignment(Pos.CENTER_LEFT);

        // Logo
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
            ImageView logoView = new ImageView(logoImage);
            logoView.setFitHeight(25);
            logoView.setFitWidth(25);
            logoView.setPreserveRatio(true);
            leftSection.getChildren().add(logoView);
        } catch (Exception e) {
            // Fallback icon nếu không load được logo
            FontIcon fallbackIcon = new FontIcon(FontAwesomeSolid.CHART_LINE);
            fallbackIcon.setIconSize(20);
            fallbackIcon.setIconColor(javafx.scene.paint.Color.WHITE);
            leftSection.getChildren().add(fallbackIcon);
        }

        // Tiêu đề
        Label titleLabel = new Label("Stock Management App");
        titleLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;"
        );
        leftSection.getChildren().add(titleLabel);

        return leftSection;
    }

    private HBox createControlButtons() {
        HBox controlButtons = new HBox(2);
        controlButtons.setAlignment(Pos.CENTER_RIGHT);

        // Nút thu nhỏ
        Button minimizeButton = createControlButton(FontAwesomeSolid.WINDOW_MINIMIZE, "#f39c12");
        minimizeButton.setOnAction(e -> stage.setIconified(true));

        // Nút phóng to/khôi phục
        Button maximizeButton = createControlButton(FontAwesomeSolid.WINDOW_MAXIMIZE, "#27ae60");
        maximizeButton.setOnAction(e -> toggleMaximize());

        // Nút đóng
        Button closeButton = createControlButton(FontAwesomeSolid.TIMES, "#e74c3c");
        closeButton.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });

        controlButtons.getChildren().addAll(minimizeButton, maximizeButton, closeButton);
        return controlButtons;
    }

    private Button createControlButton(FontAwesomeSolid icon, String hoverColor) {
        Button button = new Button();
        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.setIconSize(12);
        fontIcon.setIconColor(javafx.scene.paint.Color.WHITE);
        button.setGraphic(fontIcon);
        
        button.setPrefSize(35, 30);
        button.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-cursor: hand;"
        );

        // Hiệu ứng hover
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: " + hoverColor + ";" +
                "-fx-border-color: transparent;" +
                "-fx-cursor: hand;"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" +
                "-fx-cursor: hand;"
            );
        });

        return button;
    }

    private void setupDragHandlers() {
        // Xử lý kéo cửa sổ
        this.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        this.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown() && !maximized) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        // Double click để phóng to/thu nhỏ
        this.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                toggleMaximize();
            }
        });
    }

    private void toggleMaximize() {
        if (maximized) {
            // Khôi phục kích thước
            stage.setX(restoreX);
            stage.setY(restoreY);
            stage.setWidth(restoreWidth);
            stage.setHeight(restoreHeight);
            maximized = false;
        } else {
            // Lưu vị trí và kích thước hiện tại
            restoreX = stage.getX();
            restoreY = stage.getY();
            restoreWidth = stage.getWidth();
            restoreHeight = stage.getHeight();
            
            // Phóng to toàn màn hình
            javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
            javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            maximized = true;
        }
    }
} 