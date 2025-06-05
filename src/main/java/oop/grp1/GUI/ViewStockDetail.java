package oop.grp1.GUI;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import oop.grp1.Model.Stock;
import javafx.concurrent.Task;

import java.util.List;
import java.util.Map;

public class ViewStockDetail extends VBox {
    private TextField searchField;
    private Button searchButton;
    private final Label stockInfoLabel;
    private final BarChart<String, Number> priceChart;
    private final BarChart<String, Number> volumeChart;
    private final VBox chartContainer;
    private final Label noDataLabel;
    private final ProgressIndicator loadingIndicator;
    private final VBox statisticsPanel;
    private final HBox chartButtonsBox;

    private String currentTicker = "";
    private List<Stock> currentStockData;

    public ViewStockDetail() {
        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 10px;");
        this.setEffect(new javafx.scene.effect.DropShadow(10, Color.GRAY));

        Label titleLabel = new Label("Chi Tiết Biến Động Cổ Phiếu");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        HBox searchBox = createSearchSection();

        stockInfoLabel = new Label("Nhập mã cổ phiếu để xem thông tin chi tiết");
        stockInfoLabel.setFont(Font.font("Arial", 14));
        stockInfoLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-padding: 10px;");
        stockInfoLabel.setWrapText(true);

        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);
        loadingIndicator.setPrefSize(50, 50);

        noDataLabel = new Label("Không tìm thấy dữ liệu cho mã cổ phiếu này");
        noDataLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        noDataLabel.setStyle("-fx-text-fill: #e74c3c;");
        noDataLabel.setVisible(false);

        statisticsPanel = createStatisticsPanel();
        statisticsPanel.setVisible(false);

        priceChart = createPriceChart();
        volumeChart = createVolumeChart();


        chartButtonsBox = createChartButtonsBox();
        chartButtonsBox.setVisible(false);

        chartContainer = new VBox(10);
        chartContainer.setVisible(false);

        this.getChildren().addAll(
                titleLabel,
                searchBox,
                stockInfoLabel,
                loadingIndicator,
                noDataLabel,
                statisticsPanel,
                chartButtonsBox,
                chartContainer
        );

        VBox.setVgrow(chartContainer, Priority.ALWAYS);
    }

    private HBox createChartButtonsBox() {
        Button priceChartButton = createChartButton("Biểu Đồ Biến Động Giá");
        Button volumeChartButton = createChartButton("Biểu Đồ Khối Lượng Giao Dịch");
        Button bothChartsButton = createChartButton("Hiển Thị Cả Hai Biểu Đồ");

        priceChartButton.setOnAction(e -> showPriceChartOnly());
        volumeChartButton.setOnAction(e -> showVolumeChartOnly());
        bothChartsButton.setOnAction(e -> showBothCharts());

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(5, 0, 5, 0));
        buttonsBox.getChildren().addAll(priceChartButton, volumeChartButton, bothChartsButton);

        return buttonsBox;
    }

    private Button createChartButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 12));
        button.setPadding(new Insets(8, 15, 8, 15));
        button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 5px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-border-radius: 5px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 5px;"));
        return button;
    }

    private void showPriceChartOnly() {
        chartContainer.getChildren().clear();
        updatePriceChart(currentStockData);
        chartContainer.getChildren().add(priceChart);
        priceChart.setPrefHeight(400);
    }

    private void showVolumeChartOnly() {
        chartContainer.getChildren().clear();
        updateVolumeChart(currentStockData);
        chartContainer.getChildren().add(volumeChart);
        volumeChart.setPrefHeight(400);
    }

    private void showBothCharts() {
        chartContainer.getChildren().clear();
        updatePriceChart(currentStockData);
        updateVolumeChart(currentStockData);

        priceChart.setPrefHeight(250);
        volumeChart.setPrefHeight(150);

        chartContainer.getChildren().addAll(priceChart, volumeChart);
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

        searchButton.setOnAction(e -> searchStock());
        searchField.setOnAction(e -> searchStock());

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.getChildren().addAll(new Label("Mã cổ phiếu:"), searchField, searchButton);

        return searchBox;
    }

    private VBox createStatisticsPanel() {
        VBox panel = new VBox(5);
        panel.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-radius: 8px; -fx-padding: 10px;");
        return panel;
    }

    private BarChart<String, Number> createPriceChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Thời gian (Điểm dữ liệu)");
        yAxis.setLabel("Giá ($)");

        yAxis.setAutoRanging(false);

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Biểu Đồ Biến Động Giá");
        chart.setPrefHeight(250);
        chart.setStyle("-fx-background-color: #ffffff;");

        chart.setCategoryGap(1);
        chart.setBarGap(0.5);
        chart.setStyle("-fx-background-color: #ffffff; -fx-bar-fill: #3498db;");

        return chart;
    }

    private BarChart<String, Number> createVolumeChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Thời gian (Điểm dữ liệu)");
        yAxis.setLabel("Khối lượng");


        yAxis.setAutoRanging(false);

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Biểu Đồ Khối Lượng Giao Dịch");
        chart.setPrefHeight(150);
        chart.setStyle("-fx-background-color: #ffffff;");

        chart.setCategoryGap(1);
        chart.setBarGap(0.5);
        chart.setStyle("-fx-background-color: #ffffff; -fx-bar-fill: #3498db;");

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
        if (ticker == null || ticker.trim().isEmpty()) return;

        currentTicker = ticker.trim().toUpperCase();
        searchField.setText(currentTicker);

        showLoading(true);
        hideComponents();

        Task<Void> task = new Task<>() {
            private List<Stock> stockData;
            private Map<String, Object> statistics;

            @Override
            protected Void call() throws Exception {
                Thread.sleep(500);
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
                        currentStockData = stockData;
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

        updateStockInfo(stockData.get(0));
        updateStatisticsPanel(statistics);

        showBothCharts();

        stockInfoLabel.setVisible(true);
        statisticsPanel.setVisible(true);
        chartButtonsBox.setVisible(true);
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
        info.append("📊 Thay Đổi: ").append(stock.getFormattedPercentageChange())
                .append(" (").append(stock.getFormattedPriceChange()).append(")\n");
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
        statsGrid.setHgap(15);
        statsGrid.setVgap(5);
        statsGrid.setPadding(new Insets(5, 0, 0, 0));

        addStatistic(statsGrid, 0, 0, "Tổng Điểm Dữ Liệu:", statistics.get("totalDataPoints").toString());
        addStatistic(statsGrid, 1, 0, "Giá Trung Bình:", Stock.formatPrice((Double) statistics.get("averagePrice")));
        addStatistic(statsGrid, 0, 1, "Giá Cao Nhất:", Stock.formatPrice((Double) statistics.get("highestPrice")));
        addStatistic(statsGrid, 1, 1, "Giá Thấp Nhất:", Stock.formatPrice((Double) statistics.get("lowestPrice")));
        addStatistic(statsGrid, 0, 2, "KL Trung Bình:", Stock.formatVolume((Integer) statistics.get("averageVolume")));
        addStatistic(statsGrid, 1, 2, "Phiên Tăng:", statistics.get("bullishPeriods").toString());
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


    private void setPriceChartYAxisRange(List<Stock> stockData) {
        if (stockData == null || stockData.isEmpty()) return;

        double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;

        int maxDataPoints = Math.min(12, stockData.size());
        int step = stockData.size() / maxDataPoints;
        if (step < 1) step = 1;

        for (int i = stockData.size() - 1; i >= 0; i -= step) {
            Stock stock = stockData.get(i);

            minPrice = Math.min(minPrice, stock.getOpen());
            minPrice = Math.min(minPrice, stock.getClose());
            minPrice = Math.min(minPrice, stock.getHigh());
            minPrice = Math.min(minPrice, stock.getLow());

            maxPrice = Math.max(maxPrice, stock.getOpen());
            maxPrice = Math.max(maxPrice, stock.getClose());
            maxPrice = Math.max(maxPrice, stock.getHigh());
            maxPrice = Math.max(maxPrice, stock.getLow());
        }

        double priceRange = maxPrice - minPrice;
        double buffer = priceRange * 0.05;

        double lowerBound = Math.max(0, minPrice - buffer);
        double upperBound = maxPrice + buffer;


        NumberAxis yAxis = (NumberAxis) priceChart.getYAxis();
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);


        double tickUnit = priceRange / 8;
        yAxis.setTickUnit(tickUnit);
    }


    private void setVolumeChartYAxisRange(List<Stock> stockData) {
        if (stockData == null || stockData.isEmpty()) return;

        long minVolume = Long.MAX_VALUE;
        long maxVolume = Long.MIN_VALUE;


        int maxDataPoints = Math.min(12, stockData.size());
        int step = stockData.size() / maxDataPoints;
        if (step < 1) step = 1;


        for (int i = stockData.size() - 1; i >= 0; i -= step) {
            Stock stock = stockData.get(i);
            minVolume = Math.min(minVolume, stock.getVolume());
            maxVolume = Math.max(maxVolume, stock.getVolume());
        }


        long volumeRange = maxVolume - minVolume;
        long buffer = (long) (volumeRange * 0.1);

        long lowerBound = Math.max(0, minVolume - buffer);
        long upperBound = maxVolume + buffer;

        NumberAxis yAxis = (NumberAxis) volumeChart.getYAxis();
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);


        double tickUnit = (double) volumeRange / 6;
        yAxis.setTickUnit(tickUnit);
    }

    private void updatePriceChart(List<Stock> stockData) {
        priceChart.getData().clear();


        setPriceChartYAxisRange(stockData);

        int maxDataPoints = Math.min(12, stockData.size());
        int step = stockData.size() / maxDataPoints;
        if (step < 1) step = 1;

        XYChart.Series<String, Number> openSeries = new XYChart.Series<>();
        openSeries.setName("Giá Mở Cửa");

        XYChart.Series<String, Number> closeSeries = new XYChart.Series<>();
        closeSeries.setName("Giá Đóng Cửa");

        XYChart.Series<String, Number> highSeries = new XYChart.Series<>();
        highSeries.setName("Giá Cao Nhất");

        XYChart.Series<String, Number> lowSeries = new XYChart.Series<>();
        lowSeries.setName("Giá Thấp Nhất");

        for (int i = stockData.size() - 1; i >= 0; i -= step) {
            Stock stock = stockData.get(i);
            String timePointCategory = String.valueOf((stockData.size() - i) / step);
            openSeries.getData().add(new XYChart.Data<>(timePointCategory, stock.getOpen()));
            closeSeries.getData().add(new XYChart.Data<>(timePointCategory, stock.getClose()));
            highSeries.getData().add(new XYChart.Data<>(timePointCategory, stock.getHigh()));
            lowSeries.getData().add(new XYChart.Data<>(timePointCategory, stock.getLow()));
        }

        priceChart.getData().addAll(openSeries, closeSeries, highSeries, lowSeries);
        priceChart.setTitle("Biểu Đồ Biến Động Giá - " + currentTicker);

        Platform.runLater(() -> {
            for (int i = 0; i < priceChart.getData().size(); i++) {
                XYChart.Series<String, Number> series = priceChart.getData().get(i);
                String color;

                switch (i) {
                    case 0: color = "#3498db"; break; // Open - xanh dương
                    case 1: color = "#2ecc71"; break; // Close - xanh lá
                    case 2: color = "#f39c12"; break; // High - cam
                    case 3: color = "#e74c3c"; break; // Low - đỏ
                    default: color = "#9b59b6"; break;
                }

                for (XYChart.Data<String, Number> item : series.getData()) {
                    if (item.getNode() != null) {
                        item.getNode().setStyle(
                                "-fx-bar-fill: " + color + ";" +
                                        "-fx-border-color: black;" +
                                        "-fx-border-width: 1;" +
                                        "-fx-background-color: " + color + ";"
                        );
                    }
                }
            }
        });
    }

    private void updateVolumeChart(List<Stock> stockData) {
        volumeChart.getData().clear();


        setVolumeChartYAxisRange(stockData);

        int maxDataPoints = Math.min(12, stockData.size());
        int step = stockData.size() / maxDataPoints;
        if (step < 1) step = 1;

        XYChart.Series<String, Number> volumeSeries = new XYChart.Series<>();
        volumeSeries.setName("Khối Lượng Giao Dịch");

        for (int i = stockData.size() - 1; i >= 0; i -= step) {
            Stock stock = stockData.get(i);
            String timePointCategory = String.valueOf((stockData.size() - i) / step);
            volumeSeries.getData().add(new XYChart.Data<>(timePointCategory, stock.getVolume()));
        }

        volumeChart.getData().add(volumeSeries);
        volumeChart.setTitle("Biểu Đồ Khối Lượng Giao Dịch - " + currentTicker);

        Platform.runLater(() -> {
            if (!volumeChart.getData().isEmpty()) {
                XYChart.Series<String, Number> series = volumeChart.getData().get(0);

                for (XYChart.Data<String, Number> item : series.getData()) {
                    if (item.getNode() != null) {
                        item.getNode().setStyle(
                                "-fx-bar-fill: #3498db;" +
                                        "-fx-border-color: black;" +
                                        "-fx-border-width: 1;" +
                                        "-fx-background-color: #3498db;"
                        );
                    }
                }
            }
        });
    }

    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
    }

    private void hideComponents() {
        stockInfoLabel.setVisible(false);
        statisticsPanel.setVisible(false);
        chartButtonsBox.setVisible(false);
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