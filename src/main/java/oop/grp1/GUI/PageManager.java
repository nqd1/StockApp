package oop.grp1.GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import oop.grp1.GUI.StockDetail.StockWithInterest;

public class PageManager {
    private final DashboardPage dashboardPage;
    private final ChatbotPage chatbotPage;
    private final WatchList watchListPage;
    private final StockDetail stockDetailPage;
    private final ObservableList<StockWithInterest> stockData;

    public PageManager() {
        dashboardPage = new DashboardPage();
        chatbotPage = new ChatbotPage();
        watchListPage = new WatchList();
        stockData = FXCollections.observableArrayList();


        for (Stock stock : Stock.generateSampleStocks(5)) {
            stockData.add(new StockWithInterest(stock));
        }
        stockDetailPage = new StockDetail();
        stockDetailPage.getStockTable().setItems(stockData);
    }

    public DashboardPage getDashboardPage() {
        return dashboardPage;
    }

    public ChatbotPage getChatbotPage() {
        return chatbotPage;
    }

    public WatchList getWatchListPage() {
        return watchListPage;
    }

    public StockDetail getStockDetail() {
        return stockDetailPage;
    }

    public StockDetail getStockDetailPage(Stock stock) {
        if (stock != null) {
            stockDetailPage.updateStock(stock);
            StockWithInterest newStock = stockDetailPage.getStockTable().getItems().get(0);
            for (StockWithInterest existingStock : stockData) {
                if (existingStock.getStockCode().equals(newStock.getStockCode())) {
                    newStock.setInterested(existingStock.isInterested());
                    break;
                }
            }
        } else {
            stockDetailPage.updateStock(null); 
        }
        return stockDetailPage;
    }
}