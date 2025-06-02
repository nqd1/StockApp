package oop.grp1.GUI;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class MainLayout extends BorderPane {
    private final Sidebar sidebar;
    private final StackPane contentArea; // Changed from Region to StackPane

    public MainLayout() {
        // Initialize sidebar
        sidebar = new Sidebar();
        this.setLeft(sidebar);

        // Initialize content area
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: #f4f4f4;");
        this.setCenter(contentArea);

        // Set padding and growth
        this.setPadding(new Insets(10));
        BorderPane.setMargin(sidebar, new Insets(0, 10, 0, 0));
        BorderPane.setMargin(contentArea, new Insets(0, 0, 0, 10));

        // Initialize PageManager with content area and show TrendingstocksPage by default
        PageManager.getInstance().setContentArea(contentArea);
        PageManager.getInstance().showPage("TrendingstocksPage");
    }

    public Sidebar getSidebar() {
        return sidebar;
    }

    public StackPane getContentArea() { // Updated return type
        return contentArea;
    }
}