package oop.grp1.GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import oop.grp1.GUI.StockDetail.StockWithInterest;

public class PageManager {
    private final DashboardPage dashboardPage;
    private final ChatbotPage chatbotPage;
    private final WatchList watchListPage;
    private final StockDetail stockDetailPage;
    private final ViewStockDetail viewStockDetailPage;
    private final ObservableList<StockWithInterest> stockData;

    public PageManager() {
        dashboardPage = new DashboardPage();
        chatbotPage = new ChatbotPage();
        watchListPage = new WatchList();
        stockData = FXCollections.observableArrayList();

        stockDetailPage = new StockDetail();
        stockData.addAll(stockDetailPage.getStockData());
        stockDetailPage.getStockTable().setItems(stockData);

        viewStockDetailPage = new ViewStockDetail();
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
        stockDetailPage.updateStock(stock);
        if (stock != null) {
            StockWithInterest newStock = stockDetailPage.getStockTable().getItems().get(0);
            for (StockWithInterest existingStock : stockData) {
                if (existingStock.getStockCode().equals(newStock.getStockCode())) {
                    newStock.setInterested(existingStock.isInterested());
                    break;
                }
            }
        }
        return stockDetailPage;
    }

    public StockDetail getStockDetailPageByTicker(String ticker) {
        stockDetailPage.updateStockByTicker(ticker);
        if (!stockDetailPage.getStockData().isEmpty()) {
            StockWithInterest newStock = stockDetailPage.getStockTable().getItems().get(0);
            for (StockWithInterest existingStock : stockData) {
                if (existingStock.getStockCode().equals(newStock.getStockCode())) {
                    newStock.setInterested(existingStock.isInterested());
                    break;
                }
            }
        }
        return stockDetailPage;
    }

    public ViewStockDetail getViewStockDetailPage() {
        return viewStockDetailPage;
    }

    public ViewStockDetail getViewStockDetailPageByTicker(String ticker) {
        viewStockDetailPage.updateChartByTicker(ticker);
        return viewStockDetailPage;
    }
}