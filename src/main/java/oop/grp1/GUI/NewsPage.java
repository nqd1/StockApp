package oop.grp1.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.application.Platform;
import javafx.scene.effect.DropShadow;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

import oop.grp1.Model.News;

public class NewsPage extends VBox {
    private ScrollPane newsScrollPane;
    private VBox newsContainer;
    private TextField searchField;
    private ComboBox<String> searchTypeComboBox;
    private ComboBox<String> sortTypeComboBox;
    private Button searchButton;
    private Button refreshButton;
    
    // Biến để theo dõi số tin tức đã hiển thị và danh sách toàn bộ tin tức
    private List<News> currentNewsList;
    private final int INITIAL_NEWS_COUNT = 10;
    private final int LOAD_MORE_COUNT = 10;
    private int displayedNewsCount = 0;
    
    public NewsPage() {
        this.getStyleClass().add("page-background");
        this.setPadding(new Insets(15));
        this.setSpacing(15);
        
        // Tạo phần header với tiêu đề
        Label titleLabel = new Label("Tin tức thị trường");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // Tạo thanh tìm kiếm
        HBox searchBar = createSearchBar();
        
        // Tạo container cho tin tức
        newsContainer = new VBox(15);
        newsContainer.setPadding(new Insets(10));
        
        // Tạo ScrollPane để cuộn tin tức
        newsScrollPane = new ScrollPane(newsContainer);
        newsScrollPane.setFitToWidth(true);
        newsScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(newsScrollPane, Priority.ALWAYS);
        
        // Thêm các phần tử vào trang
        this.getChildren().addAll(titleLabel, searchBar, newsScrollPane);
        
        // Tạo hiệu ứng đổ bóng cho trang
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.GRAY);
        shadow.setRadius(5);
        this.setEffect(shadow);
        
        // Load tin tức khi trang được tạo
        loadAllNews();
    }
    
        private HBox createSearchBar() {
        HBox searchBar = new HBox(10);
        searchBar.setPadding(new Insets(10));
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5px;");
          // TextField để nhập nội dung tìm kiếm
        searchField = new TextField();
        searchField.setPromptText("Nhập từ khóa tìm kiếm...");
        searchField.setPrefWidth(400);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        
        // Thêm xử lý phím Enter để tìm kiếm        
        searchField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                performSearch();
            }
        });
        
        // ComboBox để chọn loại tìm kiếm
        searchTypeComboBox = new ComboBox<>(FXCollections.observableArrayList("Theo tiêu đề", "Theo mã cổ phiếu"));
        searchTypeComboBox.setValue("Theo tiêu đề");
        searchTypeComboBox.setStyle("-fx-background-color: #f8f8f8;");
        
        // ComboBox để chọn loại sắp xếp
        sortTypeComboBox = new ComboBox<>(FXCollections.observableArrayList(
            "Theo thời gian", 
            "Sắp xếp theo điểm tích cực chung (cao đến thấp)",
            "Sắp xếp theo điểm tích cực chung (thấp đến cao)",
            "Sắp xếp theo điểm tích cực với mã cổ phiếu (cao đến thấp)"));
        sortTypeComboBox.setValue("Theo thời gian");
        sortTypeComboBox.setPrefWidth(300);
        sortTypeComboBox.setStyle("-fx-background-color: #f8f8f8;");
        
        searchButton = new Button("🔍");
        searchButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px;");
        searchButton.setTooltip(new Tooltip("Tìm kiếm"));
        searchButton.setOnAction(_ -> performSearch());
        
        refreshButton = new Button("🔄");
        refreshButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 16px;");
        refreshButton.setTooltip(new Tooltip("Làm mới"));
        refreshButton.setOnAction(_ -> refreshNews());
        
        // Thêm các phần tử vào thanh tìm kiếm
        searchBar.getChildren().addAll(searchField, searchTypeComboBox, sortTypeComboBox, searchButton, refreshButton);
        
        return searchBar;
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        String searchType = searchTypeComboBox.getValue();
        String sortType = sortTypeComboBox.getValue();
        
        if (keyword.isEmpty()) {
            loadAllNews();
            return;
        }
        
        // Xóa nội dung hiện tại
        newsContainer.getChildren().clear();
        
        List<News> searchResults;
        if ("Theo tiêu đề".equals(searchType)) {
            searchResults = News.searchNews(keyword);
        } else { // Theo mã cổ phiếu
            // Chuyển đổi keyword thành chữ hoa vì ticker thường được lưu ở dạng chữ hoa
            if ("Sắp xếp theo điểm tích cực với mã cổ phiếu (cao đến thấp)".equals(sortType)) {
                searchResults = News.getAllNewsSortedByTickerSentiment(keyword.toUpperCase());
            } else {
                searchResults = News.getNewsByTicker(keyword.toUpperCase());
            }
        }
        
        // Áp dụng sắp xếp theo overall sentiment score
        searchResults = applySortingByOverallSentiment(searchResults, sortType);
        
        if (searchResults.isEmpty()) {
            Label noResultsLabel;
            
            if ("Theo mã cổ phiếu".equals(searchType)) {
                // Hiển thị thông báo chi tiết hơn với gợi ý
                noResultsLabel = new Label("Không tìm thấy kết quả cho mã cổ phiếu: " + keyword.toUpperCase() + 
                                  "\nVui lòng nhập đúng mã giao dịch (VD: AAPL, MSFT, GOOGL)");
            } else {
                noResultsLabel = new Label("Không tìm thấy kết quả phù hợp");
            }
            
            noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            newsContainer.getChildren().add(noResultsLabel);        } else {
            currentNewsList = searchResults; // Lưu kết quả tìm kiếm
            displayedNewsCount = Math.min(INITIAL_NEWS_COUNT, currentNewsList.size());
            displayNews(currentNewsList.subList(0, displayedNewsCount));
            
            // Thêm nút "Tải thêm" nếu còn tin tức để tải
            if (displayedNewsCount < currentNewsList.size()) {
                addLoadMoreButton();
            }
        }
    }
    
    private void refreshNews() {
        // Hiển thị thông báo đang cập nhật
        newsContainer.getChildren().clear();
        Label updatingLabel = new Label("Đang cập nhật dữ liệu...");
        updatingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: blue;");
        newsContainer.getChildren().add(updatingLabel);
        
        // Thực hiện việc làm mới tin tức trong một thread riêng
        new Thread(() -> {
            String ticker = searchTypeComboBox.getValue().equals("Theo mã cổ phiếu") ? 
                       searchField.getText().trim() : "";
                       
            boolean success = News.fetchAndStoreNews(ticker.isEmpty() ? "MSFT" : ticker); // Mặc định lấy MSFT nếu không có ticker
            
            // Update UI trong thread JavaFX
            Platform.runLater(() -> {
                if (success) {
                    loadAllNews();
                } else {
                    newsContainer.getChildren().clear();
                    Label errorLabel = new Label("Không thể cập nhật dữ liệu. Vui lòng thử lại sau.");
                    errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
                    newsContainer.getChildren().add(errorLabel);
                }
            });
        }).start();
    }
    
    // Áp dụng sắp xếp theo điểm tích cực chung (overall sentiment score)
    private List<News> applySortingByOverallSentiment(List<News> newsList, String sortType) {
        if ("Sắp xếp theo điểm tích cực chung (cao đến thấp)".equals(sortType)) {
            return newsList.stream()
                    .sorted((n1, n2) -> Double.compare(n2.getSentimentScore(), n1.getSentimentScore()))
                    .collect(Collectors.toList());
        } else if ("Sắp xếp theo điểm tích cực chung (thấp đến cao)".equals(sortType)) {
            return newsList.stream()
                    .sorted((n1, n2) -> Double.compare(n1.getSentimentScore(), n2.getSentimentScore()))
                    .collect(Collectors.toList());
        } else {
            return newsList; // Theo thời gian (không thay đổi thứ tự)
        }
    }
    
    private void loadAllNews() {
        // Xóa tin tức hiện tại
        newsContainer.getChildren().clear();
        
        // Lấy tất cả tin tức từ cơ sở dữ liệu
        List<News> allNews = News.getAllNews();
        String sortType = sortTypeComboBox.getValue();
        
        // Áp dụng sắp xếp
        allNews = applySortingByOverallSentiment(allNews, sortType);
        
        if (allNews.isEmpty()) {
            Label noNewsLabel = new Label("Không có tin tức nào");
            noNewsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            newsContainer.getChildren().add(noNewsLabel);
        } else {
            currentNewsList = allNews; // Lưu danh sách tin tức hiện tại
            displayedNewsCount = Math.min(INITIAL_NEWS_COUNT, currentNewsList.size()); // Số tin tức sẽ hiển thị ban đầu
            displayNews(currentNewsList.subList(0, displayedNewsCount));
            
            // Thêm nút "Tải thêm" nếu còn tin tức để tải
            if (displayedNewsCount < currentNewsList.size()) {
                addLoadMoreButton();
            }
        }
    }
    
    private void displayNews(List<News> newsList) {
        for (News news : newsList) {
            // Tạo card cho mỗi tin tức
            VBox newsCard = createNewsCard(news);
            newsContainer.getChildren().add(newsCard);
        }
    }
    
    private VBox createNewsCard(News news) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #ddd;");
          // Tạo tiêu đề dưới dạng Hyperlink để click vào mở URL
        Hyperlink titleLink = new Hyperlink(news.getTitle());
        titleLink.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLink.setWrapText(true);
        // Đặt style mặc định cho hyperlink và đảm bảo nó luôn có màu xanh
        titleLink.setStyle("-fx-text-fill: #1a0dab;");
        titleLink.setVisited(false); // Đảm bảo luôn ở trạng thái chưa truy cập
          // Thêm CSS khi hover
        titleLink.setOnMouseEntered(_ -> titleLink.setStyle("-fx-text-fill: #1a0dab; -fx-underline: true;"));
        titleLink.setOnMouseExited(_ -> titleLink.setStyle("-fx-text-fill: #1a0dab; -fx-underline: false;"));
        
        titleLink.setOnAction(_ -> {
            openNewsUrl(news.getUrl());
            // Đặt lại style sau khi click để đảm bảo màu sắc không thay đổi
            titleLink.setStyle("-fx-text-fill: #1a0dab;");
            titleLink.setVisited(false);
        });
        
        // Tạo phần thông tin chung: nguồn, thời gian
        HBox infoBox = new HBox(15);
        Label sourceLabel = new Label("Nguồn: " + news.getSource());
        sourceLabel.setStyle("-fx-text-fill: #666;");
        Label timeLabel = new Label("Thời gian: " + news.getFormattedTimestamp());
        timeLabel.setStyle("-fx-text-fill: #666;");
        infoBox.getChildren().addAll(sourceLabel, timeLabel);
        
        // Tóm tắt tin tức
        Label summaryLabel = new Label(news.getTruncatedSummary(50));
        summaryLabel.setWrapText(true);
        
        // Phân tích cảm xúc
        HBox sentimentBox = new HBox(10);
        sentimentBox.setAlignment(Pos.CENTER_LEFT);
        
        Label sentimentLabel = new Label("Cảm xúc: " + news.getSentimentLabelTranslated());
        sentimentLabel.setStyle(getSentimentStyle(news.getSentimentLabel()));
        
        Label scoreLabel = new Label("Điểm tích cực: " + news.getFormattedSentimentScore());
        scoreLabel.setStyle("-fx-text-fill: black;");
        
        sentimentBox.getChildren().addAll(sentimentLabel, scoreLabel);
        
        // Chủ đề liên quan
        FlowPane topicsPane = new FlowPane(5, 5);
        topicsPane.setPadding(new Insets(5, 0, 0, 0));
        
        List<News.Topic> topTopics = news.getMostRelevantTopics(3);
        for (News.Topic topic : topTopics) {
    Label topicLabel = new Label(topic.getTopic());
    topicLabel.setPadding(new Insets(2, 5, 2, 5));
    topicLabel.setStyle("-fx-background-color: #d0eaff; -fx-background-radius: 3px; -fx-text-fill: #003366;");
    topicsPane.getChildren().add(topicLabel);
}
        
        // Thêm các phần tử vào card
        card.getChildren().addAll(titleLink, infoBox, summaryLabel, sentimentBox, topicsPane);
          // Thêm hiệu ứng hover
        card.setOnMouseEntered(_ -> {
            card.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #ddd;");
        });
        card.setOnMouseExited(_ -> {
            card.setStyle("-fx-background-color: white; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #ddd;");
        });
        
        return card;
    }
    

    private String getSentimentStyle(String sentiment) {
    if (sentiment != null && sentiment.toLowerCase().contains("bullish")) {
        return "-fx-text-fill: green; -fx-font-weight: bold;";
    } else if (sentiment != null && sentiment.toLowerCase().contains("bearish")) {
        return "-fx-text-fill: red; -fx-font-weight: bold;";
    } else {
        return "-fx-text-fill: gray; -fx-font-weight: bold;";
    }
}
    
    private void openNewsUrl(String url) {
        try {
            // Tạo cửa sổ popup mới để hiển thị nội dung URL
            Stage webViewStage = new Stage();
            webViewStage.setTitle("Tin tức");
            
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();
            webEngine.load(url);
            
            Scene scene = new Scene(webView, 1000, 600);
            webViewStage.setScene(scene);
            webViewStage.show();
        } catch (Exception e) {
            System.err.println("Không thể mở URL: " + e.getMessage());
            
            // Tạo thông báo lỗi
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể mở URL");
            alert.setContentText("Vui lòng kiểm tra kết nối internet hoặc sao chép URL để mở trong trình duyệt của bạn.");
            alert.showAndWait();
        }
    }
    
    private void addLoadMoreButton() {
        Button loadMoreButton = new Button("Tải thêm tin tức");
        loadMoreButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 14px;");        loadMoreButton.setOnAction(_ -> {
            loadMoreNews();
        });
        
        newsContainer.getChildren().add(loadMoreButton);
    }
      private void loadMoreNews() {
        if (currentNewsList == null || displayedNewsCount >= currentNewsList.size()) {
            return;
        }
        
        // Xóa nút "Tải thêm" hiện tại trước khi thêm tin tức mới
        newsContainer.getChildren().removeIf(node -> node instanceof Button);
        
        // Tính toán số tin tức sẽ được tải thêm
        int nextLoadCount = Math.min(LOAD_MORE_COUNT, currentNewsList.size() - displayedNewsCount);
        
        // Hiển thị các tin tức tiếp theo
        displayNews(currentNewsList.subList(displayedNewsCount, displayedNewsCount + nextLoadCount));
        displayedNewsCount += nextLoadCount;
        
        // Thêm lại nút "Tải thêm" ở cuối nếu còn tin tức để tải
        if (displayedNewsCount < currentNewsList.size()) {
            addLoadMoreButton();
        }
    }
}