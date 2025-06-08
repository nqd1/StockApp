package oop.grp1.GUI;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;


public class PageManager {
    private static PageManager instance;
    private final Map<String, Node> pages;
    private StackPane contentArea; // Changed from Region to StackPane

    private PageManager() {
        pages = new HashMap<>();
    }

    public static PageManager getInstance() {
        if (instance == null) {
            instance = new PageManager();
        }
        return instance;
    }

    public void setContentArea(StackPane contentArea) { // Updated parameter type
        this.contentArea = contentArea;
    }

    public void showPage(String pageName) {
        if (contentArea == null) {
            System.err.println("Content area not set in PageManager");
            return;
        }

        Node page = pages.computeIfAbsent(pageName, this::createPage);
        if (page != null) {
            contentArea.getChildren().setAll(page); // Now works with StackPane
        } else {
            System.err.println("Page not found: " + pageName);
        }
    }

    private Node createPage(String pageName) {
        switch (pageName) {
            case "TrendingstocksPage":
                return new TrendingstocksPage();
            case "NewsPage":
                return new NewsPage();
            case "StockDetail":
                return new StockDetail();
            case "ViewStockDetail":
                return new ViewStockDetail();
            case "ChatbotPage":
                return new ChatbotPage();
            default:
                System.err.println("Unknown page: " + pageName);
                return null;
        }
    }

    public void updateStockDetail(String ticker) {
        StockDetail stockDetail = (StockDetail) pages.get("StockDetail");
        if (stockDetail != null) {
            stockDetail.updateStockByTicker(ticker);
        }
    }

    public void updateViewStockDetail(String ticker) {
        ViewStockDetail viewStockDetail = (ViewStockDetail) pages.get("ViewStockDetail");
        if (viewStockDetail != null) {
            viewStockDetail.updateChartByTicker(ticker);
        }
    }
}