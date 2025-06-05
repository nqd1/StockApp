package oop.grp1.GUI;
public class PageManager {
    public TrendingstocksPage getDashboardPage() {
        return new TrendingstocksPage();
    }

    public ChatbotPage getChatbotPage() {
        return new ChatbotPage();
    }
    
    public NewsPage getNewsPage() {
        return new NewsPage();
    }
}