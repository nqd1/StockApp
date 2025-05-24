package oop.grp1.GUI;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class MainLayout extends BorderPane {
    private final Sidebar sidebar;
    private final PageManager pageManager;

    public MainLayout() {
        pageManager = new PageManager();

        // Create stockDetailButton using JavaFX Button
        Button stockDetailButton = new Button("Stock Detail");

        // Initialize Sidebar with callbacks
        sidebar = new Sidebar(this::handleNavigation, stockDetailButton);
        this.setLeft(sidebar);

        // Set Initial Content
        setContent(pageManager.getDashboardPage());
    }

    private void handleNavigation(String page) {
        switch (page) {
            case "Dashboard":
                setContent(pageManager.getDashboardPage());
                break;
            case "Chatbot":
                setContent(pageManager.getChatbotPage());
                break;
            case "Watchlist":
                setContent(pageManager.getWatchListPage());
                break;
            case "StockDetail":
                setContent(pageManager.getStockDetail());
                break;
            default:
                System.out.println("Unknown page: " + page);
        }
    }

    private void setContent(javafx.scene.Node content) {
        this.setCenter(content);
    }
}