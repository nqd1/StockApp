package oop.grp1.GUI;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainLayout extends BorderPane {
    private final Sidebar sidebar;
    private final StackPane contentArea;
    private final CustomTitleBar titleBar;

    public MainLayout(Stage stage) {
        // Tạo title bar tùy chỉnh
        titleBar = new CustomTitleBar(stage);
        this.setTop(titleBar);

        // Initialize sidebar
        sidebar = new Sidebar();
        this.setLeft(sidebar);

        // Initialize content area
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        this.setCenter(contentArea);

        // Set padding and growth - removed to avoid white borders
        this.setPadding(new Insets(0));
        BorderPane.setMargin(sidebar, new Insets(0));
        BorderPane.setMargin(contentArea, new Insets(0));

        // Initialize PageManager with content area and show default page
        PageManager.getInstance().setContentArea(contentArea);
        PageManager.getInstance().showPage("TrendingstocksPage");
        
        // Apply rounded corners style with transparency
        this.setStyle("-fx-background-radius: 10; -fx-background-color: transparent;");
    }

    public Sidebar getSidebar() {
        return sidebar;
    }

    public StackPane getContentArea() {
        return contentArea;
    }
    
    public CustomTitleBar getTitleBar() {
        return titleBar;
    }
}