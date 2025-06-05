package oop.grp1.GUI;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.concurrent.Task;

import java.util.List;
import java.util.Map;

public class ViewStockDetail extends VBox {
    private TextField searchField;
    private Button searchButton;
    private final Label stockInfoLabel;
    private final LineChart<Number, Number> priceChart;
    private final LineChart<Number, Number> volumeChart;
    private final VBox chartContainer;
    private final Label noDataLabel;
    private final ProgressIndicator loadingIndicator;
    private final VBox statisticsPanel;

    private String currentTicker = "";

    public ViewStockDetail() {
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 10px;");
        this.setEffect(new javafx.scene.effect.DropShadow(10, Color.GRAY));

        // Title
        Label titleLabel = new Label("Chi Tiết Biến Động Cổ Phiếu");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        // Search section
        HBox searchBox = createSearchSection();

        // Stock info section
        stockInfoLabel = new Label("Nhập mã cổ phiếu để xem thông tin chi tiết");
        stockInfoLabel.setFont(Font.font("Arial", 14));
        stockInfoLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-padding: 10px;");
        stockInfoLabel.setWrapText(true);

        // Loading indicator
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);
        loadingIndicator.setPrefSize(50, 50);

        // No data label
        noDataLabel = new Label("Không tìm thấy dữ liệu cho mã cổ phiếu này");
        noDataLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        noDataLabel.setStyle("-fx-text-fill: #e74c3c;");
        noDataLabel.setVisible(false);

        // Statistics panel
        statisticsPanel = createStatisticsPanel();
        statisticsPanel.setVisible(false);

        // Charts
        priceChart = createPriceChart();
        volumeChart = createVolumeChart();

        chartContainer = new VBox(10);
        chartContainer.getChildren().addAll(priceChart, volumeChart);
        chartContainer.setVisible(false);

        // Add all components
        this.getChildren().addAll(
                titleLabel,
                searchBox,
                stockInfoLabel,
                loadingIndicator,
                noDataLabel,
                statisticsPanel,
                chartContainer
        );

        VBox.setVgrow(chartContainer, Priority.ALWAYS);
    }

    private HBox createSearchSection() {
        searchField = new TextField();
        searchField.setPromptText("Nhập mã cổ phiếu (VD: AAPL, GOOGL...)");
        searchField.setFont(Font.font("Arial", 14));
        searchField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-radius: 5px; -fx-padding: 8px;");
        searchField.setPrefWidth(300);

        searchButton = new Button("Tìm Kiếm");
        searchButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        searchButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 8px 16px;");
        searchButton.setOnMouseEntered(e -> searchButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 8px 16px;"));
        searchButton.setOnMouseExited(e -> searchButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 8px 16px;"));

        // Event handlers
        searchButton.setOnAction(e -> searchStock());
        searchField.setOnAction(e -> searchStock());

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.getChildren().addAll(new Label("Mã cổ phiếu:"), searchField, searchButton);

        return searchBox;
    }

    private VBox createStatisticsPanel() {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-radius: 8px; -fx-padding: 15px;");
        return panel;
    }

    private LineChart<Number, Number> createPriceChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Thời gian (Điểm dữ liệu)");
        yAxis.setLabel("Giá ($)");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Biểu Đồ Biến Động Giá");
        chart.setPrefHeight(300);
        chart.setStyle("-fx-background-color: #ffffff;");

        return chart;
    }

    private LineChart<Number, Number> createVolumeChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Thời gian (Điểm dữ liệu)");
        yAxis.setLabel("Khối lượng");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Biểu Đồ Khối Lượng Giao Dịch");
        chart.setPrefHeight(200);
        chart.setStyle("-fx-background-color: #ffffff;");

        return chart;
    }

    private void searchStock() {
        String ticker = searchField.getText().trim().toUpperCase();
        if (ticker.isEmpty()) {
            showAlert("Vui lòng nhập mã cổ phiếu");
            return;
        }

        updateChartByTicker(ticker);
    }

    public void updateChartByTicker(String ticker) {
        if (ticker == null || ticker.trim().isEmpty()) {
            return;
        }

        currentTicker = ticker.trim().toUpperCase();
        searchField.setText(currentTicker);

        // Show loading indicator
        showLoading(true);
        hideComponents();

        // Create background task
        Task<Void> task = new Task<Void>() {
            private List<Stock> stockData;
            private Map<String, Object> statistics;

            @Override
            protected Void call() throws Exception {
                // Simulate some processing time
                Thread.sleep(500);

                // Fetch data
                stockData = Stock.getStockData(currentTicker);
                if (!stockData.isEmpty()) {
                    statistics = Stock.getSummaryStatistics(currentTicker);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    showLoading(false);
                    if (stockData != null && !stockData.isEmpty()) {
                        displayStockData(stockData, statistics);
                    } else {
                        showNoDataMessage();
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showLoading(false);
                    showNoDataMessage();
                });
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void displayStockData(List<Stock> stockData, Map<String, Object> statistics) {
        if (stockData.isEmpty()) {
            showNoDataMessage();
            return;
        }

        // Update stock info
        Stock latestStock = stockData.get(0);
        updateStockInfo(latestStock);

        // Update statistics panel
        updateStatisticsPanel(statistics);

        // Update charts
        updatePriceChart(stockData);
        updateVolumeChart(stockData);

        // Show components
        stockInfoLabel.setVisible(true);
        statisticsPanel.setVisible(true);
        chartContainer.setVisible(true);
        noDataLabel.setVisible(false);
    }

    private void updateStockInfo(Stock stock) {
        StringBuilder info = new StringBuilder();
        info.append("📊 ").append(stock.getTicker()).append(" - Thông Tin Mới Nhất\n\n");
        info.append("💰 Giá Đóng Cửa: ").append(Stock.formatPrice(stock.getClose())).append("\n");
        info.append("📈 Giá Mở Cửa: ").append(Stock.formatPrice(stock.getOpen())).append("\n");
        info.append("⬆️ Giá Cao Nhất: ").append(Stock.formatPrice(stock.getHigh())).append("\n");
        info.append("⬇️ Giá Thấp Nhất: ").append(Stock.formatPrice(stock.getLow())).append("\n");
        info.append("📊 Khối Lượng: ").append(Stock.formatVolume(stock.getVolume())).append("\n");
        info.append("📊 Thay Đổi: ").append(stock.getFormattedPercentageChange());
        info.append(" (").append(stock.getFormattedPriceChange()).append(")\n");
        info.append("🎯 Xu Hướng: ").append(stock.getMarketSentiment()).append("\n");
        info.append("⚡ Độ Biến Động: ").append(String.format("%.2f%%", stock.getVolatility())).append("\n");
        info.append("🕒 Cập Nhật: ").append(stock.getFormattedTimestamp());

        stockInfoLabel.setText(info.toString());
        stockInfoLabel.setStyle("-fx-text-fill: #2c3e50; -fx-padding: 15px; -fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
    }

    private void updateStatisticsPanel(Map<String, Object> statistics) {
        statisticsPanel.getChildren().clear();

        Label statsTitle = new Label("📈 Thống Kê Tổng Quan");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        statsTitle.setStyle("-fx-text-fill: #2c3e50;");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(8);
        statsGrid.setPadding(new Insets(10, 0, 0, 0));

        // Row 1
        addStatistic(statsGrid, 0, 0, "Tổng Điểm Dữ Liệu:", statistics.get("totalDataPoints").toString());
        addStatistic(statsGrid, 1, 0, "Giá Trung Bình:", Stock.formatPrice((Double) statistics.get("averagePrice")));

        // Row 2
        addStatistic(statsGrid, 0, 1, "Giá Cao Nhất:", Stock.formatPrice((Double) statistics.get("highestPrice")));
        addStatistic(statsGrid, 1, 1, "Giá Thấp Nhất:", Stock.formatPrice((Double) statistics.get("lowestPrice")));

        // Row 3
        addStatistic(statsGrid, 0, 2, "KL Trung Bình:", Stock.formatVolume((Integer) statistics.get("averageVolume")));
        addStatistic(statsGrid, 1, 2, "Phiên Tăng:", statistics.get("bullishPeriods").toString());

        // Row 4
        addStatistic(statsGrid, 0, 3, "Phiên Giảm:", statistics.get("bearishPeriods").toString());

        statisticsPanel.getChildren().addAll(statsTitle, statsGrid);
    }

    private void addStatistic(GridPane grid, int col, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        labelNode.setStyle("-fx-text-fill: #34495e;");

        Label valueNode = new Label(value);
        valueNode.setFont(Font.font("Arial", 12));
        valueNode.setStyle("-fx-text-fill: #2980b9;");

        VBox container = new VBox(2);
        container.getChildren().addAll(labelNode, valueNode);

        grid.add(container, col, row);
    }

    private void updatePriceChart(List<Stock> stockData) {
        priceChart.getData().clear();

        XYChart.Series<Number, Number> openSeries = new XYChart.Series<>();
        openSeries.setName("Giá Mở Cửa");

        XYChart.Series<Number, Number> closeSeries = new XYChart.Series<>();
        closeSeries.setName("Giá Đóng Cửa");

        XYChart.Series<Number, Number> highSeries = new XYChart.Series<>();
        highSeries.setName("Giá Cao Nhất");

        XYChart.Series<Number, Number> lowSeries = new XYChart.Series<>();
        lowSeries.setName("Giá Thấp Nhất");

        // Reverse the order to show chronological progression
        for (int i = stockData.size() - 1; i >= 0; i--) {
            Stock stock = stockData.get(i);
            int timePoint = stockData.size() - i;

            openSeries.getData().add(new XYChart.Data<>(timePoint, stock.getOpen()));
            closeSeries.getData().add(new XYChart.Data<>(timePoint, stock.getClose()));
            highSeries.getData().add(new XYChart.Data<>(timePoint, stock.getHigh()));
            lowSeries.getData().add(new XYChart.Data<>(timePoint, stock.getLow()));
        }

        priceChart.getData().addAll(openSeries, closeSeries, highSeries, lowSeries);
        priceChart.setTitle("Biểu Đồ Biến Động Giá - " + currentTicker);
    }

    private void updateVolumeChart(List<Stock> stockData) {
        volumeChart.getData().clear();

        XYChart.Series<Number, Number> volumeSeries = new XYChart.Series<>();
        volumeSeries.setName("Khối Lượng Giao Dịch");

        // Reverse the order to show chronological progression
        for (int i = stockData.size() - 1; i >= 0; i--) {
            Stock stock = stockData.get(i);
            int timePoint = stockData.size() - i;
            volumeSeries.getData().add(new XYChart.Data<>(timePoint, stock.getVolume()));
        }

        volumeChart.getData().add(volumeSeries);
        volumeChart.setTitle("Biểu Đồ Khối Lượng Giao Dịch - " + currentTicker);
    }

    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
    }

    private void hideComponents() {
        stockInfoLabel.setVisible(false);
        statisticsPanel.setVisible(false);
        chartContainer.setVisible(false);
        noDataLabel.setVisible(false);
    }

    private void showNoDataMessage() {
        hideComponents();
        noDataLabel.setText("Không tìm thấy dữ liệu cho mã cổ phiếu: " + currentTicker);
        noDataLabel.setVisible(true);

        stockInfoLabel.setText("Vui lòng thử lại với mã cổ phiếu khác");
        stockInfoLabel.setStyle("-fx-text-fill: #e74c3c; -fx-padding: 10px;");
        stockInfoLabel.setVisible(true);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông Báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getter methods for integration
    public String getCurrentTicker() {
        return currentTicker;
    }

    public void clearData() {
        currentTicker = "";
        searchField.clear();
        priceChart.getData().clear();
        volumeChart.getData().clear();
        hideComponents();
        stockInfoLabel.setText("Nhập mã cổ phiếu để xem thông tin chi tiết");
        stockInfoLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-padding: 10px;");
    }
}