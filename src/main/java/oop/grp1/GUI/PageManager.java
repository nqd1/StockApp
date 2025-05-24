package oop.grp1.GUI;
public class PageManager {
    public TrendingstocksPage getDashboardPage() {
        return new TrendingstocksPage();
    }

    public ChatbotPage getChatbotPage() {
        return new ChatbotPage();
    }

    //    public OwnedStocksPage getOwnedStocksPage() {
//        return new OwnedStocksPage();
//    }
//
//    public StockDetailsPage getStockDetailsPage() {
//        return new StockDetailsPage();
//    }
//
//    public UserDetail getUserDetail() {
//        return new UserDetail();
//    }
//
//    public WatchList getWatchListPage() {
//        return new WatchList();
//    }
}