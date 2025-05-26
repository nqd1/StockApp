package oop.grp1.GUI;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import oop.grp1.Model.Stock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TrendingstocksPage extends VBox {
    private final TableView<StockData> topTrendingTable;
    private final TableView<StockData> topGiamGiaTable;
    private final TableView<StockChangeData> topThayDoiTable;

    public TrendingstocksPage() {
        Label titleLabel = new Label("Diễn biến thị trường");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        this.getChildren().add(titleLabel);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setPadding(new Insets(20));

        // Set column constraints to make tables expand with the window
        for (int i = 0; i < 3; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(column);
        }

        // Top trending table
        VBox topTrendingBox = new VBox();
        Label topTrendingLabel = new Label("Top trending");
        topTrendingTable = createStockTable();
        topTrendingBox.getChildren().addAll(topTrendingLabel, topTrendingTable);
        gridPane.add(topTrendingBox, 0, 0);

        // Top giảm giá table
        VBox topGiamGiaBox = new VBox();
        Label topGiamGiaLabel = new Label("Top giảm giá");
        topGiamGiaTable = createStockTable();
        topGiamGiaBox.getChildren().addAll(topGiamGiaLabel, topGiamGiaTable);
        gridPane.add(topGiamGiaBox, 1, 0);

        // Top thay đổi nhiều nhất table
        VBox topThayDoiBox = new VBox();
        Label topThayDoiLabel = new Label("Top thay đổi nhiều nhất");
        topThayDoiTable = createStockChangeTable();
        topThayDoiBox.getChildren().addAll(topThayDoiLabel, topThayDoiTable);
        gridPane.add(topThayDoiBox, 2, 0);

        this.getChildren().add(gridPane);

        // Load data
        loadTopTrendingData();
        loadTopGiamGiaData();
        loadTopThayDoiData();
    }

    @SuppressWarnings("unchecked")
    private TableView<StockData> createStockTable() {
        TableView<StockData> table = new TableView<>();

        TableColumn<StockData, String> nameColumn = new TableColumn<>("Tên cổ phiếu");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));        
        TableColumn<StockData, Double> priceColumn = new TableColumn<>("Giá");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        table.getColumns().addAll(nameColumn, priceColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Đặt độ cao cố định để hiển thị chính xác 10 hàng
        table.setFixedCellSize(25);
        table.setPrefHeight(270); // Chiều cao cho 10 hàng + header + padding
        
        // Tắt thanh cuộn
        table.setStyle("-fx-background-color: white;");
        table.getStyleClass().add("no-scroll-bar");
        
        return table;
    }    @SuppressWarnings("unchecked")
    private TableView<StockChangeData> createStockChangeTable() {
        TableView<StockChangeData> table = new TableView<>();

        TableColumn<StockChangeData, String> nameColumn = new TableColumn<>("Tên cổ phiếu");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<StockChangeData, Double> changeColumn = new TableColumn<>("Thay đổi");
        changeColumn.setCellValueFactory(new PropertyValueFactory<>("change"));

        table.getColumns().addAll(nameColumn, changeColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Đặt độ cao cố định để hiển thị chính xác 10 hàng
        table.setFixedCellSize(25);
        table.setPrefHeight(270); // Chiều cao cho 10 hàng + header + padding
        
        // Tắt thanh cuộn
        table.setStyle("-fx-background-color: white;");
        table.getStyleClass().add("no-scroll-bar");
        
        return table;
    }
       private void loadTopTrendingData() {
        // Danh sách các ticker mặc định theo thứ tự
        List<String> defaultTickers = List.of("AAPL", "MSFT", "AMZN", "GOOGL", "GOOG", "META", "TSLA", "NVDA", "COMP", "V");
        
        List<StockData> topTrendingData = new ArrayList<>();
        
        // Tạo danh sách cổ phiếu với thứ tự đã định trước
        for (String ticker : defaultTickers) {
            Stock stock = Stock.getLatestStock(ticker);
            if (stock != null) {
                topTrendingData.add(new StockData(ticker, stock.getClose()));
            }
        }
        
        // Nếu không đủ 10 thì bổ sung thêm từ danh sách tất cả các ticker
        if (topTrendingData.size() < 10) {
            List<Stock> additionalStocks = Stock.getAllTickers().stream()
                .filter(ticker -> !defaultTickers.contains(ticker))
                .map(Stock::getLatestStock)
                .filter(stock -> stock != null)
                .limit(10 - topTrendingData.size())
                .collect(Collectors.toList());
                
            for (Stock stock : additionalStocks) {
                topTrendingData.add(new StockData(stock.getTicker(), stock.getClose()));
            }
        }
        
        // Thiết lập đúng 10 hàng trong bảng, không có hàng trống
        topTrendingTable.setFixedCellSize(25); // Chiều cao cố định cho mỗi hàng
        topTrendingTable.setPrefHeight(270); // Chiều cao cho 10 hàng + header + padding
        topTrendingTable.getItems().clear();
        topTrendingTable.getItems().addAll(topTrendingData.subList(0, Math.min(10, topTrendingData.size())));
    }    private void loadTopGiamGiaData() {
        List<Stock> allBearishStocks = Stock.getAllTickers().stream()
            .map(Stock::getLatestStock)
            .filter(stock -> stock != null && stock.isBearish())
            .sorted((a, b) -> Double.compare(a.getPercentageChange(), b.getPercentageChange())) // giảm giá nhiều nhất lên trên
            .limit(10)
            .collect(Collectors.toList());

        List<StockData> topGiamGiaData = allBearishStocks.stream()
            .map(stock -> new StockData(stock.getTicker(), stock.getClose()))
            .collect(Collectors.toList());

        // Thiết lập đúng 10 hàng trong bảng, không có hàng trống
        topGiamGiaTable.setFixedCellSize(25); // Chiều cao cố định cho mỗi hàng
        topGiamGiaTable.setPrefHeight(270); // Chiều cao cho 10 hàng + header + padding
        topGiamGiaTable.getItems().clear();
        topGiamGiaTable.getItems().addAll(topGiamGiaData);
    }    private void loadTopThayDoiData() {
        List<Stock> allVolatileStocks = Stock.getAllTickers().stream()
            .map(Stock::getLatestStock)
            .filter(stock -> stock != null)
            .sorted((a, b) -> Double.compare(b.getVolatility(), a.getVolatility())) // biến động nhiều nhất lên trên
            .limit(10)
            .collect(Collectors.toList());

        List<StockChangeData> topThayDoiData = allVolatileStocks.stream()
            .map(stock -> new StockChangeData(stock.getTicker(), stock.getVolatility()))
            .collect(Collectors.toList());

        // Thiết lập đúng 10 hàng trong bảng, không có hàng trống
        topThayDoiTable.setFixedCellSize(25); // Chiều cao cố định cho mỗi hàng
        topThayDoiTable.setPrefHeight(270); // Chiều cao cho 10 hàng + header + padding
        topThayDoiTable.getItems().clear();
        topThayDoiTable.getItems().addAll(topThayDoiData);
    }

    public static class StockData {
        private final String name;
        private final double price;

        public StockData(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }

    public static class StockChangeData {
        private final String name;
        private final double change;

        public StockChangeData(String name, double change) {
            this.name = name;
            this.change = change;
        }

        public String getName() {
            return name;
        }

        public double getChange() {
            return change;
        }
    }
}
