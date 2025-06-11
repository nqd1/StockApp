package oop.grp1.GUI;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.concurrent.Task;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import oop.grp1.Model.Stock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewStockDetail extends VBox {
    private TextField searchField;
    private Button searchButton;
    private final Label stockInfoLabel;
    private final LineChart<String, Number> priceChart;
    private final LineChart<String, Number> volumeChart;
    private final VBox chartContainer;
    private final Label noDataLabel;
    private final ProgressIndicator loadingIndicator;
    private final VBox statisticsPanel;
    private final HBox chartButtonsBox;
    
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Button applyDateFilterButton;
    private CheckBox showAllDataCheckBox;

    private String currentTicker = "";
    private List<Stock> currentStockData;
    private List<Stock> allStockData;

    private Map<String, Boolean> seriesVisibility = new HashMap<>();

    public ViewStockDetail() {
        this.getStyleClass().add("page-background");
        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.setEffect(new DropShadow(10, Color.GRAY));

        Label titleLabel = new Label("Chi Tiết Biến Động Cổ Phiếu");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        HBox searchBox = createSearchSection();
        HBox dateFilterBox = createDateFilterSection();

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

        initSeriesVisibility();

        this.getChildren().addAll(
                titleLabel,
                searchBox,
                dateFilterBox,
                stockInfoLabel,
                loadingIndicator,
                noDataLabel,
                statisticsPanel,
                chartButtonsBox,
                chartContainer
        );

        VBox.setVgrow(chartContainer, Priority.ALWAYS);
    }

    private HBox createDateFilterSection() {
        HBox dateFilterBox = new HBox(10);
        dateFilterBox.setAlignment(Pos.CENTER_LEFT);
        dateFilterBox.setPadding(new Insets(5, 0, 5, 0));

        Label fromLabel = new Label("Từ ngày:");
        fromLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        startDatePicker = new DatePicker();
        startDatePicker.setValue(LocalDate.now().minusDays(30));
        startDatePicker.setStyle("-fx-font-size: 12px;");

        Label toLabel = new Label("Đến ngày:");
        toLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        endDatePicker = new DatePicker();
        endDatePicker.setValue(LocalDate.now());
        endDatePicker.setStyle("-fx-font-size: 12px;");

        applyDateFilterButton = new Button("Áp dụng");
        applyDateFilterButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        applyDateFilterButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 6px 12px;");
        applyDateFilterButton.setOnMouseEntered(e -> applyDateFilterButton.setStyle("-fx-background-color: #229954; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 6px 12px;"));
        applyDateFilterButton.setOnMouseExited(e -> applyDateFilterButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 6px 12px;"));
        applyDateFilterButton.setOnAction(e -> applyDateFilter());

        showAllDataCheckBox = new CheckBox("Hiển thị tất cả dữ liệu");
        showAllDataCheckBox.setFont(Font.font("Arial", 12));
        showAllDataCheckBox.setOnAction(e -> {
            if (showAllDataCheckBox.isSelected()) {
                displayAllData();
            } else {
                applyDateFilter();
            }
        });

        Button resetDateButton = new Button("Đặt lại");
        resetDateButton.setFont(Font.font("Arial", 12));
        resetDateButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 6px 12px;");
        resetDateButton.setOnMouseEntered(e -> resetDateButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 6px 12px;"));
        resetDateButton.setOnMouseExited(e -> resetDateButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 6px 12px;"));
        resetDateButton.setOnAction(e -> resetDateFilter());

        dateFilterBox.getChildren().addAll(fromLabel, startDatePicker, toLabel, endDatePicker, 
                                         applyDateFilterButton, showAllDataCheckBox, resetDateButton);
        dateFilterBox.setVisible(false);

        return dateFilterBox;
    }

    private void applyDateFilter() {
        if (allStockData == null || allStockData.isEmpty()) return;

        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            showAlert("Vui lòng chọn cả ngày bắt đầu và ngày kết thúc");
            return;
        }

        if (startDate.isAfter(endDate)) {
            showAlert("Ngày bắt đầu không thể sau ngày kết thúc");
            return;
        }

        currentStockData = allStockData.stream()
                .filter(stock -> {
                    try {
                        LocalDateTime stockDateTime = LocalDateTime.parse(stock.getTimestamp().replace(" ", "T"));
                        LocalDate stockDate = stockDateTime.toLocalDate();
                        return !stockDate.isBefore(startDate) && !stockDate.isAfter(endDate);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        if (currentStockData.isEmpty()) {
            showAlert("Không có dữ liệu trong khoảng thời gian đã chọn");
            return;
        }

        showAllDataCheckBox.setSelected(false);
        displayStockData(currentStockData, Stock.getSummaryStatistics(currentTicker));
    }

    private void displayAllData() {
        if (allStockData == null || allStockData.isEmpty()) return;
        
        currentStockData = allStockData;
        displayStockData(currentStockData, Stock.getSummaryStatistics(currentTicker));
    }

    private void resetDateFilter() {
        startDatePicker.setValue(LocalDate.now().minusDays(30));
        endDatePicker.setValue(LocalDate.now());
        showAllDataCheckBox.setSelected(false);
        if (allStockData != null && !allStockData.isEmpty()) {
            applyDateFilter();
        }
    }

    private void initSeriesVisibility() {
        seriesVisibility.put("Giá Mở Cửa", true);
        seriesVisibility.put("Giá Đóng Cửa", true);
        seriesVisibility.put("Giá Cao Nhất", true);
        seriesVisibility.put("Giá Thấp Nhất", true);
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

    private VBox createStatisticsPanel() {
        VBox panel = new VBox(5);
        panel.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-radius: 8px; -fx-padding: 10px;");
        return panel;
    }

    private LineChart<String, Number> createPriceChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Thời gian");
        yAxis.setLabel("Giá ($)");
        yAxis.setAutoRanging(false);

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Biểu Đồ Biến Động Giá");
        chart.setPrefHeight(250);
        chart.setStyle("-fx-background-color: #ffffff;");
        chart.setCreateSymbols(true);
        chart.setLegendVisible(true);

        return chart;
    }

    private LineChart<String, Number> createVolumeChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Thời gian");
        yAxis.setLabel("Khối lượng");
        yAxis.setAutoRanging(false);

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Biểu Đồ Khối Lượng Giao Dịch");
        chart.setPrefHeight(150);
        chart.setStyle("-fx-background-color: #ffffff;");
        chart.setCreateSymbols(true);
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
                        allStockData = stockData;
                        
                        ((HBox) getChildren().get(2)).setVisible(true);
                        
                        applyDateFilter();
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
        initSeriesVisibility();
        showBothCharts();

        stockInfoLabel.setVisible(true);
        statisticsPanel.setVisible(true);
        chartButtonsBox.setVisible(true);
        chartContainer.setVisible(true);
        noDataLabel.setVisible(false);
    }

    private void updateStockInfo(Stock stock) {
        StringBuilder info = new StringBuilder();
        info.append(" ").append(stock.getTicker()).append(" - Thông Tin Mới Nhất\n\n");
        info.append(" Giá Đóng Cửa: ").append(Stock.formatPrice(stock.getClose())).append("\n");
        info.append(" Giá Mở Cửa: ").append(Stock.formatPrice(stock.getOpen())).append("\n");
        info.append("⬆️ Giá Cao Nhất: ").append(Stock.formatPrice(stock.getHigh())).append("\n");
        info.append("⬇️ Giá Thấp Nhất: ").append(Stock.formatPrice(stock.getLow())).append("\n");
        info.append(" Khối Lượng: ").append(Stock.formatVolume(stock.getVolume())).append("\n");
        info.append(" Thay Đổi: ").append(stock.getFormattedPercentageChange())
                .append(" (").append(stock.getFormattedPriceChange()).append(")\n");
        info.append(" Xu Hướng: ").append(stock.getMarketSentiment()).append("\n");
        info.append("⚡ Độ Biến Động: ").append(String.format("%.2f%%", stock.getVolatility())).append("\n");
        info.append(" Cập Nhật: ").append(stock.getFormattedTimestamp());

        stockInfoLabel.setText(info.toString());
        stockInfoLabel.setStyle("-fx-text-fill: #2c3e50; -fx-padding: 15px; -fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
    }

    private void updateStatisticsPanel(Map<String, Object> statistics) {
        statisticsPanel.getChildren().clear();

        Label statsTitle = new Label(" Thống Kê Tổng Quan");
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

        double minPrice = stockData.stream()
                .mapToDouble(stock -> Math.min(Math.min(stock.getOpen(), stock.getClose()), 
                                             Math.min(stock.getHigh(), stock.getLow())))
                .min().orElse(0);

        double maxPrice = stockData.stream()
                .mapToDouble(stock -> Math.max(Math.max(stock.getOpen(), stock.getClose()), 
                                             Math.max(stock.getHigh(), stock.getLow())))
                .max().orElse(100);

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

        long minVolume = stockData.stream()
                .mapToLong(Stock::getVolume)
                .min().orElse(0);

        long maxVolume = stockData.stream()
                .mapToLong(Stock::getVolume)
                .max().orElse(1000);

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

        if (stockData == null || stockData.isEmpty()) return;

        List<Stock> sortedData = stockData.stream()
                .sorted((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                .collect(Collectors.toList());

        int maxDataPoints;
        if (sortedData.size() <= 50) {
            maxDataPoints = sortedData.size();
        } else if (sortedData.size() <= 200) {
            maxDataPoints = Math.min(50, sortedData.size());
        } else {
            maxDataPoints = Math.min(100, sortedData.size());
        }

        int step = Math.max(1, sortedData.size() / maxDataPoints);

        XYChart.Series<String, Number> openSeries = new XYChart.Series<>();
        openSeries.setName("Giá Mở Cửa");

        XYChart.Series<String, Number> closeSeries = new XYChart.Series<>();
        closeSeries.setName("Giá Đóng Cửa");

        XYChart.Series<String, Number> highSeries = new XYChart.Series<>();
        highSeries.setName("Giá Cao Nhất");

        XYChart.Series<String, Number> lowSeries = new XYChart.Series<>();
        lowSeries.setName("Giá Thấp Nhất");

        for (int i = 0; i < sortedData.size(); i += step) {
            Stock stock = sortedData.get(i);
            String timePointCategory = stock.getShortTimestamp();

            XYChart.Data<String, Number> openData = new XYChart.Data<>(timePointCategory, stock.getOpen());
            openData.setExtraValue(stock);
            openSeries.getData().add(openData);

            XYChart.Data<String, Number> closeData = new XYChart.Data<>(timePointCategory, stock.getClose());
            closeData.setExtraValue(stock);
            closeSeries.getData().add(closeData);

            XYChart.Data<String, Number> highData = new XYChart.Data<>(timePointCategory, stock.getHigh());
            highData.setExtraValue(stock);
            highSeries.getData().add(highData);

            XYChart.Data<String, Number> lowData = new XYChart.Data<>(timePointCategory, stock.getLow());
            lowData.setExtraValue(stock);
            lowSeries.getData().add(lowData);
        }

        if (seriesVisibility.get("Giá Mở Cửa")) {
            priceChart.getData().add(openSeries);
        }
        if (seriesVisibility.get("Giá Đóng Cửa")) {
            priceChart.getData().add(closeSeries);
        }
        if (seriesVisibility.get("Giá Cao Nhất")) {
            priceChart.getData().add(highSeries);
        }
        if (seriesVisibility.get("Giá Thấp Nhất")) {
            priceChart.getData().add(lowSeries);
        }

        priceChart.setTitle("Biểu Đồ Biến Động Giá - " + currentTicker + 
                           " (" + maxDataPoints + "/" + sortedData.size() + " điểm)");

        Platform.runLater(() -> {
            setupLineChartDataEvents(sortedData);
            setupLegendClickEvent();
        });
    }

    private void setupLegendClickEvent() {
        for (Node node : priceChart.lookupAll(".chart-legend-item")) {
            if (node instanceof Label) {
                Label legendItem = (Label) node;
                String seriesName = legendItem.getText();

                Tooltip.install(legendItem, new Tooltip("Nhấp để xem riêng " + seriesName + ", nhấp lại để xem tất cả"));

                updateLegendStyle(legendItem, seriesVisibility.getOrDefault(seriesName, false));

                legendItem.setOnMouseClicked(event -> {
                    boolean allVisible = seriesVisibility.get("Giá Mở Cửa") &&
                            seriesVisibility.get("Giá Đóng Cửa") &&
                            seriesVisibility.get("Giá Cao Nhất") &&
                            seriesVisibility.get("Giá Thấp Nhất");

                    if (allVisible) {
                        seriesVisibility.keySet().forEach(key -> seriesVisibility.put(key, false));
                        seriesVisibility.put(seriesName, true);
                    } else if (seriesVisibility.get(seriesName)) {
                        initSeriesVisibility();
                    } else {
                        seriesVisibility.keySet().forEach(key -> seriesVisibility.put(key, false));
                        seriesVisibility.put(seriesName, true);
                    }

                    updatePriceChart(currentStockData);
                });

                legendItem.setOnMouseEntered(e -> {
                    legendItem.setCursor(javafx.scene.Cursor.HAND);
                    legendItem.setStyle(legendItem.getStyle() + "; -fx-underline: true; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 2, 0, 0, 0)");
                });

                legendItem.setOnMouseExited(e -> {
                    legendItem.setCursor(javafx.scene.Cursor.DEFAULT);
                    updateLegendStyle(legendItem, seriesVisibility.getOrDefault(seriesName, false));
                });
            }
        }
    }

    private void updateLegendStyle(Label legendItem, boolean visible) {
        String seriesName = legendItem.getText();
        String color;

        switch (seriesName) {
            case "Giá Mở Cửa":
                color = "#e74c3c"; // Đỏ
                break;
            case "Giá Đóng Cửa":
                color = "#f39c12"; // Cam
                break;
            case "Giá Cao Nhất":
                color = "#2ecc71"; // Xanh lá
                break;
            case "Giá Thấp Nhất":
                color = "#3498db"; // Xanh dương
                break;
            default:
                color = "#9b59b6";
                break;
        }

        if (visible) {
            legendItem.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
        } else {
            legendItem.setStyle("-fx-text-fill: #a0a0a0; -fx-font-weight: normal;");
        }
    }

    private void setupLineChartDataEvents(List<Stock> stockData) {
        for (int i = 0; i < priceChart.getData().size(); i++) {
            XYChart.Series<String, Number> series = priceChart.getData().get(i);
            String color;
            String priceType;

            switch (series.getName()) {
                case "Giá Mở Cửa":
                    color = "#e74c3c"; // Đỏ
                    priceType = "Giá Mở Cửa";
                    break;
                case "Giá Đóng Cửa":
                    color = "#f39c12"; // Cam
                    priceType = "Giá Đóng Cửa";
                    break;
                case "Giá Cao Nhất":
                    color = "#2ecc71"; // Xanh lá
                    priceType = "Giá Cao Nhất";
                    break;
                case "Giá Thấp Nhất":
                    color = "#3498db"; // Xanh dương
                    priceType = "Giá Thấp Nhất";
                    break;
                default:
                    color = "#9b59b6";
                    priceType = "Giá";
                    break;
            }

            final String finalPriceType = priceType;
            final String finalColor = color;

            for (XYChart.Data<String, Number> item : series.getData()) {
                if (item.getNode() != null) {
                    item.getNode().setStyle(
                            "-fx-background-color: " + color + ", white;" +
                            "-fx-background-insets: 0, 2;" +
                            "-fx-background-radius: 5px;" +
                            "-fx-padding: 5px;"
                    );

                    Tooltip tooltip = new Tooltip(
                            finalPriceType + ": " + Stock.formatPrice(item.getYValue().doubleValue())
                    );
                    Tooltip.install(item.getNode(), tooltip);

                    item.getNode().setOnMouseClicked(event -> {
                        Stock clickedStock = (Stock) item.getExtraValue();
                        showPriceDetailDialog(clickedStock, finalPriceType, item.getYValue().doubleValue());
                    });

                    item.getNode().setOnMouseEntered(e -> {
                        item.getNode().setStyle(
                                "-fx-background-color: " + finalColor + ", white;" +
                                "-fx-background-insets: 0, 2;" +
                                "-fx-background-radius: 8px;" +
                                "-fx-padding: 8px;" +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 0);" +
                                "-fx-cursor: hand;"
                        );
                    });

                    item.getNode().setOnMouseExited(e -> {
                        item.getNode().setStyle(
                                "-fx-background-color: " + finalColor + ", white;" +
                                "-fx-background-insets: 0, 2;" +
                                "-fx-background-radius: 5px;" +
                                "-fx-padding: 5px;"
                        );
                    });
                }
            }
        }
    }

    private void showPriceDetailDialog(Stock stock, String priceType, double priceValue) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi Tiết " + priceType);
        alert.setHeaderText(currentTicker + " - " + priceType);

        StringBuilder content = new StringBuilder();
        content.append(priceType).append(": ").append(Stock.formatPrice(priceValue)).append("\n\n");
        content.append("Ngày: ").append(stock.getFormattedTimestamp()).append("\n\n");

        switch (priceType) {
            case "Giá Mở Cửa":
                content.append("So với Giá Đóng Cửa: ").append(compareValues(priceValue, stock.getClose())).append("\n");
                content.append("So với Giá Cao Nhất: ").append(compareValues(priceValue, stock.getHigh())).append("\n");
                content.append("So với Giá Thấp Nhất: ").append(compareValues(priceValue, stock.getLow())).append("\n");
                break;
            case "Giá Đóng Cửa":
                content.append("So với Giá Mở Cửa: ").append(compareValues(priceValue, stock.getOpen())).append("\n");
                content.append("So với Giá Cao Nhất: ").append(compareValues(priceValue, stock.getHigh())).append("\n");
                content.append("So với Giá Thấp Nhất: ").append(compareValues(priceValue, stock.getLow())).append("\n");
                break;
            case "Giá Cao Nhất":
                content.append("So với Giá Mở Cửa: ").append(compareValues(priceValue, stock.getOpen())).append("\n");
                content.append("So với Giá Đóng Cửa: ").append(compareValues(priceValue, stock.getClose())).append("\n");
                content.append("So với Giá Thấp Nhất: ").append(compareValues(priceValue, stock.getLow())).append("\n");
                break;
            case "Giá Thấp Nhất":
                content.append("So với Giá Mở Cửa: ").append(compareValues(priceValue, stock.getOpen())).append("\n");
                content.append("So với Giá Đóng Cửa: ").append(compareValues(priceValue, stock.getClose())).append("\n");
                content.append("So với Giá Cao Nhất: ").append(compareValues(priceValue, stock.getHigh())).append("\n");
                break;
        }

        if (priceType.equals("Giá Mở Cửa") || priceType.equals("Giá Đóng Cửa")) {
            content.append("\nPhân tích phiên giao dịch: ");
            if (stock.getOpen() < stock.getClose()) {
                content.append("Phiên tăng điểm ").append(Stock.formatPrice(stock.getClose() - stock.getOpen()));
                content.append(" (").append(String.format("%.2f%%", (stock.getClose() - stock.getOpen()) / stock.getOpen() * 100)).append(")");
            } else if (stock.getOpen() > stock.getClose()) {
                content.append("Phiên giảm điểm ").append(Stock.formatPrice(stock.getOpen() - stock.getClose()));
                content.append(" (").append(String.format("%.2f%%", (stock.getOpen() - stock.getClose()) / stock.getOpen() * 100)).append(")");
            } else {
                content.append("Phiên giao dịch đi ngang");
            }
        }

        content.append("\n\nKhối lượng giao dịch: ").append(Stock.formatVolume(stock.getVolume()));

        if (priceType.equals("Giá Cao Nhất") || priceType.equals("Giá Thấp Nhất")) {
            double tradingRange = stock.getHigh() - stock.getLow();
            double percentRange = (stock.getLow() != 0) ? (tradingRange / stock.getLow() * 100) : 0;
            content.append("\nBiên độ giao dịch: ").append(Stock.formatPrice(tradingRange));
            content.append(" (").append(String.format("%.2f%%", percentRange)).append(")");
        }

        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    private String compareValues(double value1, double value2) {
        double difference = value1 - value2;
        double percentageDiff = (value2 != 0) ? (difference / value2) * 100 : 0;

        String result = Stock.formatPrice(Math.abs(difference));
        if (difference > 0) {
            result += String.format(" cao hơn (%.2f%%)", percentageDiff);
        } else if (difference < 0) {
            result += String.format(" thấp hơn (%.2f%%)", Math.abs(percentageDiff));
        } else {
            result += " (bằng nhau)";
        }

        return result;
    }

    private void updateVolumeChart(List<Stock> stockData) {
        volumeChart.getData().clear();
        setVolumeChartYAxisRange(stockData);

        if (stockData == null || stockData.isEmpty()) return;

        List<Stock> sortedData = stockData.stream()
                .sorted((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                .collect(Collectors.toList());

        int maxDataPoints;
        if (sortedData.size() <= 50) {
            maxDataPoints = sortedData.size();
        } else if (sortedData.size() <= 200) {
            maxDataPoints = Math.min(50, sortedData.size());
        } else {
            maxDataPoints = Math.min(100, sortedData.size());
        }

        int step = Math.max(1, sortedData.size() / maxDataPoints);

        XYChart.Series<String, Number> volumeSeries = new XYChart.Series<>();
        volumeSeries.setName("Khối Lượng Giao Dịch");

        for (int i = 0; i < sortedData.size(); i += step) {
            Stock stock = sortedData.get(i);
            String timePointCategory = stock.getShortTimestamp();
            XYChart.Data<String, Number> volumeData = new XYChart.Data<>(timePointCategory, stock.getVolume());
            volumeData.setExtraValue(stock);
            volumeSeries.getData().add(volumeData);
        }

        volumeChart.getData().add(volumeSeries);
        volumeChart.setTitle("Biểu Đồ Khối Lượng Giao Dịch - " + currentTicker + 
                           " (" + maxDataPoints + "/" + sortedData.size() + " điểm)");

        Platform.runLater(() -> {
            if (!volumeChart.getData().isEmpty()) {
                XYChart.Series<String, Number> series = volumeChart.getData().get(0);
                for (XYChart.Data<String, Number> item : series.getData()) {
                    if (item.getNode() != null) {
                        item.getNode().setStyle(
                                "-fx-background-color: #3498db, white;" +
                                "-fx-background-insets: 0, 2;" +
                                "-fx-background-radius: 5px;" +
                                "-fx-padding: 5px;"
                        );
                        Tooltip tooltip = new Tooltip(
                                "Khối lượng: " + Stock.formatVolume(item.getYValue().intValue()) +
                                "\nThời gian: " + ((Stock) item.getExtraValue()).getFormattedTimestamp()
                        );
                        Tooltip.install(item.getNode(), tooltip);
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
        ((HBox) getChildren().get(2)).setVisible(false);
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
        allStockData = null;
        currentStockData = null;
        hideComponents();
        stockInfoLabel.setText("Nhập mã cổ phiếu để xem thông tin chi tiết");
        stockInfoLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-padding: 10px;");
        stockInfoLabel.setVisible(true);
        
        resetDateFilter();
    }
}