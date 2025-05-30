package oop.grp1.GUI;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import oop.grp1.Model.Stock;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class TrendingstocksPage extends VBox {
    private final TableView<StockData> topTrendingTable;
    private final TableView<StockData> topGiamGiaTable;
    private final TableView<StockChangeData> topThayDoiTable;

    public TrendingstocksPage() {
        // Header với tiêu đề và nút làm mới
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 20, 5, 20));
        
        Label titleLabel = new Label("Diễn biến thị trường");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Tạo nút làm mới với icon
        Button refreshButton = createRefreshButton();
        
        // Thêm label hiển thị thời gian cập nhật
        Label lastUpdatedLabel = new Label();
        lastUpdatedLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
        updateLastUpdatedTime(lastUpdatedLabel);
        
        // Tạo vùng chứa tiêu đề và thời gian cập nhật
        VBox titleBox = new VBox(5);
        titleBox.getChildren().addAll(titleLabel, lastUpdatedLabel);
        
        // Đẩy nút làm mới sang bên phải
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(titleBox, spacer, refreshButton);
        this.getChildren().add(header);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setPadding(new Insets(10, 20, 20, 20));

        // Set column constraints to make tables expand with the window
        for (int i = 0; i < 3; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(column);
        }        // Top trending table
        VBox topTrendingBox = new VBox(5);
        Label topTrendingLabel = createHeaderWithIcon("Top trending", FontAwesomeSolid.FIRE, Color.ORANGERED);
        topTrendingTable = createStockTable();
        topTrendingBox.getChildren().addAll(topTrendingLabel, topTrendingTable);
        gridPane.add(topTrendingBox, 0, 0);

        // Top giảm giá table
        VBox topGiamGiaBox = new VBox(5);
        Label topGiamGiaLabel = createHeaderWithIcon("Top giảm giá", FontAwesomeSolid.ARROW_DOWN, Color.RED);
        topGiamGiaTable = createStockTable();
        topGiamGiaBox.getChildren().addAll(topGiamGiaLabel, topGiamGiaTable);
        gridPane.add(topGiamGiaBox, 1, 0);

        // Top thay đổi nhiều nhất table
        VBox topThayDoiBox = new VBox(5);
        Label topThayDoiLabel = createHeaderWithIcon("Top thay đổi nhiều nhất", FontAwesomeSolid.CHART_LINE, Color.BLUE);
        topThayDoiTable = createStockChangeTable();
        topThayDoiBox.getChildren().addAll(topThayDoiLabel, topThayDoiTable);
        gridPane.add(topThayDoiBox, 2, 0);

        this.getChildren().add(gridPane);

        // Load data
        loadTopTrendingData();
        loadTopGiamGiaData();
        loadTopThayDoiData();
    }    @SuppressWarnings("unchecked")
    private TableView<StockData> createStockTable() {
        TableView<StockData> table = new TableView<>();

        TableColumn<StockData, String> nameColumn = new TableColumn<>("Tên cổ phiếu");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        // Tùy chỉnh cell factory cho cột tên cổ phiếu để thêm hiệu ứng hover
        nameColumn.setCellFactory(column -> {
            return new TableCell<StockData, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        
                        // Thêm hiệu ứng hover
                        setOnMouseEntered(event -> setStyle("-fx-background-color: #f0f8ff;"));
                        setOnMouseExited(event -> setStyle(""));
                    }
                }
            };
        });
        
        TableColumn<StockData, Double> priceColumn = new TableColumn<>("Giá");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        // Tùy chỉnh cell factory cho cột giá để định dạng số
        priceColumn.setCellFactory(column -> {
            return new TableCell<StockData, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        // Định dạng số
                        setText(String.format("$%.2f", item));
                        
                        // Thêm hiệu ứng hover
                        setOnMouseEntered(event -> setStyle("-fx-background-color: #f0f8ff;"));
                        setOnMouseExited(event -> setStyle(""));
                    }
                }
            };
        });

        table.getColumns().addAll(nameColumn, priceColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Đặt độ cao cố định để hiển thị chính xác 10 hàng
        table.setFixedCellSize(25);
        table.setPrefHeight(270); // Chiều cao cho 10 hàng + header + padding
        
        // Tắt thanh cuộn và thêm style cho bảng
        table.setStyle("-fx-background-color: white; -fx-border-radius: 5px; -fx-border-color: #ddd;");
        table.getStyleClass().add("no-scroll-bar");
        
        // Thêm CSS cho bảng - zebra striping
        table.setRowFactory(tv -> {
            TableRow<StockData> row = new TableRow<>();
            row.setOnMouseEntered(event -> {
                if (!row.isEmpty()) {
                    row.setStyle("-fx-background-color: #f0f8ff;");
                }
            });
            row.setOnMouseExited(event -> {
                if (!row.isEmpty()) {
                    if (row.getIndex() % 2 == 0) {
                        row.setStyle("-fx-background-color: white;");
                    } else {
                        row.setStyle("-fx-background-color: #f5f5f5;");
                    }
                }
            });
            
            // Thêm zebra striping
            row.setPrefHeight(25);
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (row.getIndex() % 2 == 0) {
                    row.setStyle("-fx-background-color: white;");
                } else {
                    row.setStyle("-fx-background-color: #f5f5f5;");
                }
            });
            return row;
        });
        
        return table;
    }    @SuppressWarnings("unchecked")
    private TableView<StockChangeData> createStockChangeTable() {
        TableView<StockChangeData> table = new TableView<>();

        TableColumn<StockChangeData, String> nameColumn = new TableColumn<>("Tên cổ phiếu");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        // Tùy chỉnh cell factory cho cột tên cổ phiếu
        nameColumn.setCellFactory(column -> {
            return new TableCell<StockChangeData, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        
                        // Thêm hiệu ứng hover
                        setOnMouseEntered(event -> setStyle("-fx-background-color: #f0f8ff;"));
                        setOnMouseExited(event -> setStyle(""));
                    }
                }
            };
        });

        TableColumn<StockChangeData, Double> changeColumn = new TableColumn<>("Thay đổi");
        changeColumn.setCellValueFactory(new PropertyValueFactory<>("change"));
        
        // Tùy chỉnh cell factory cho cột thay đổi để thêm icon và định dạng số
        changeColumn.setCellFactory(column -> {
            return new TableCell<StockChangeData, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setText(null);
                        setGraphic(null);
                        setStyle("");
                    } else {
                        // Định dạng số với 2 chữ số thập phân
                        setText(String.format("%.2f%%", item));
                        
                        // Tạo icon dựa vào giá trị volatility (giả định > 4% là cao)
                        FontIcon icon;
                        HBox hbox = new HBox(5);
                        if (item > 5.0) {
                            // Biến động cao - vòng tròn đỏ có dấu chấm than
                            icon = new FontIcon(FontAwesomeSolid.EXCLAMATION_CIRCLE);
                            icon.setIconColor(Color.RED);
                            setText(String.format("%.2f%% (Cao)", item));
                            setTextFill(Color.RED);
                        } else if (item > 2.0) {
                            // Biến động trung bình - vòng tròn vàng
                            icon = new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE);
                            icon.setIconColor(Color.ORANGE);
                            setText(String.format("%.2f%% (TB)", item));
                            setTextFill(Color.ORANGE);
                        } else {
                            // Biến động thấp - vòng tròn xanh lá
                            icon = new FontIcon(FontAwesomeSolid.INFO_CIRCLE);
                            icon.setIconColor(Color.GREEN);
                            setText(String.format("%.2f%% (Thấp)", item));
                            setTextFill(Color.GREEN);
                        }
                        
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        hbox.getChildren().addAll(icon, new Label(getText()));
                        setGraphic(hbox);
                        setText(null);
                        
                        // Thêm hiệu ứng hover
                        setOnMouseEntered(event -> setStyle("-fx-background-color: #f0f8ff;"));
                        setOnMouseExited(event -> setStyle(""));
                    }
                }
            };
        });

        table.getColumns().addAll(nameColumn, changeColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Đặt độ cao cố định để hiển thị chính xác 10 hàng
        table.setFixedCellSize(25);
        table.setPrefHeight(270); // Chiều cao cho 10 hàng + header + padding
        
        // Tắt thanh cuộn
        table.setStyle("-fx-background-color: white; -fx-border-radius: 5px; -fx-border-color: #ddd;");
        table.getStyleClass().add("no-scroll-bar");
        
        // Thêm CSS cho bảng - zebra striping
        table.setRowFactory(tv -> {
            TableRow<StockChangeData> row = new TableRow<>();
            row.setOnMouseEntered(event -> {
                if (!row.isEmpty()) {
                    row.setStyle("-fx-background-color: #f0f8ff;");
                }
            });
            row.setOnMouseExited(event -> {
                if (!row.isEmpty()) {
                    if (row.getIndex() % 2 == 0) {
                        row.setStyle("-fx-background-color: white;");
                    } else {
                        row.setStyle("-fx-background-color: #f5f5f5;");
                    }
                }
            });
            
            // Thêm zebra striping
            row.setPrefHeight(25);
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (row.getIndex() % 2 == 0) {
                    row.setStyle("-fx-background-color: white;");
                } else {
                    row.setStyle("-fx-background-color: #f5f5f5;");
                }
            });
            return row;
        });
        
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
    }      private void loadTopGiamGiaData() {
        List<Stock> allBearishStocks = Stock.getAllTickers().stream()
            .map(Stock::getLatestStock)
            .filter(stock -> stock != null && stock.isBearish())
            .sorted((a, b) -> Double.compare(a.getPercentageChange(), b.getPercentageChange())) // giảm giá nhiều nhất lên trên
            .limit(10)
            .collect(Collectors.toList());

        List<StockData> topGiamGiaData = allBearishStocks.stream()
            .map(stock -> new StockData(stock.getTicker(), stock.getClose()))
            .collect(Collectors.toList());

        // Thêm dữ liệu giả nếu không đủ 10 kết quả
        while (topGiamGiaData.size() < 10) {
            topGiamGiaData.add(new StockData("-", 0.0));
        }

        // Thiết lập đúng 10 hàng trong bảng
        topGiamGiaTable.getItems().clear();
        topGiamGiaTable.getItems().addAll(topGiamGiaData);
    }
    private void loadTopThayDoiData() {
        List<Stock> allVolatileStocks = Stock.getAllTickers().stream()
            .map(Stock::getLatestStock)
            .filter(stock -> stock != null)
            .sorted((a, b) -> Double.compare(b.getVolatility(), a.getVolatility())) // biến động nhiều nhất lên trên
            .limit(10)
            .collect(Collectors.toList());

        List<StockChangeData> topThayDoiData = allVolatileStocks.stream()
            .map(stock -> new StockChangeData(stock.getTicker(), stock.getVolatility()))
            .collect(Collectors.toList());

        // Thiết lập đúng 10 hàng trong bảng, không có hàng trống        topThayDoiTable.setFixedCellSize(25); // Chiều cao cố định cho mỗi hàng
        topThayDoiTable.setPrefHeight(270); // Chiều cao cho 10 hàng + header + padding
        topThayDoiTable.getItems().clear();
        
        // Thêm dữ liệu giả nếu không đủ 10 kết quả
        while (topThayDoiData.size() < 10) {
            topThayDoiData.add(new StockChangeData("-", 0.0));
        }
        
        topThayDoiTable.getItems().addAll(topThayDoiData);}
    
    /**
     * Cập nhật thời gian làm mới dữ liệu
     */    private void updateLastUpdatedTime(Label label) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        label.setText("Cập nhật lần cuối: " + now.format(formatter));
    }
    
    /**
     * Tạo tiêu đề có icon
     */
    private Label createHeaderWithIcon(String text, FontAwesomeSolid iconType, Color iconColor) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        FontIcon icon = new FontIcon(iconType);
        icon.setIconColor(iconColor);
        icon.setIconSize(18);
        
        label.setGraphic(icon);
        label.setGraphicTextGap(8);
        label.setContentDisplay(ContentDisplay.LEFT);
        
        return label;
    }
    
    /**
     * Tạo nút làm mới với icon
     */
    private Button createRefreshButton() {
        Button refreshButton = new Button("Làm mới dữ liệu");
        
        // Tạo icon cho nút làm mới
        FontIcon refreshIcon = new FontIcon(FontAwesomeSolid.SYNC_ALT);
        refreshIcon.setIconColor(Color.WHITE);
        refreshButton.setGraphic(refreshIcon);
        refreshButton.setContentDisplay(ContentDisplay.LEFT);
        
        // Thiết lập style cho nút
        refreshButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 15;");
        
        // Thêm hiệu ứng hover
        refreshButton.setOnMouseEntered(e -> refreshButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-padding: 8 15;"));
        refreshButton.setOnMouseExited(e -> refreshButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 15;"));
        
        // Xử lý sự kiện khi nhấn nút làm mới
        refreshButton.setOnAction(event -> refreshAllData());
        
        return refreshButton;
    }
    
    /**
     * Làm mới tất cả dữ liệu
     */
    private void refreshAllData() {
        // Hiển thị thông báo đang cập nhật
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Đang cập nhật");
        alert.setHeaderText(null);
        alert.setContentText("Đang cập nhật dữ liệu, vui lòng đợi...");
        alert.show();
        
        // Tạo thread mới để tải dữ liệu
        new Thread(() -> {
            // Cờ để kiểm tra thành công
            AtomicBoolean success = new AtomicBoolean(true);
            
            // Lấy tất cả ticker và cập nhật
            List<String> allTickers = Stock.getAllTickers();
            
            for (String ticker : allTickers) {
                boolean result = Stock.fetchAndStoreStock(ticker);
                if (!result) {
                    success.set(false);
                }
            }
            
            // Cập nhật UI trong thread JavaFX
            Platform.runLater(() -> {
                // Đóng thông báo đang cập nhật
                alert.close();
                
                if (success.get()) {
                    // Tải lại dữ liệu
                    loadTopTrendingData();
                    loadTopGiamGiaData();
                    loadTopThayDoiData();
                    
                    // Cập nhật thời gian làm mới
                    for (javafx.scene.Node node : this.getChildren()) {
                        if (node instanceof HBox) {
                            for (javafx.scene.Node child : ((HBox) node).getChildren()) {
                                if (child instanceof VBox) {
                                    for (javafx.scene.Node grandChild : ((VBox) child).getChildren()) {
                                        if (grandChild instanceof Label && ((Label) grandChild).getText().startsWith("Cập nhật")) {
                                            updateLastUpdatedTime((Label) grandChild);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // Hiển thị thông báo thành công
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Thành công");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Đã cập nhật dữ liệu thành công!");
                    successAlert.show();
                } else {
                    // Hiển thị thông báo lỗi
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Lỗi");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Có lỗi xảy ra khi cập nhật dữ liệu. Vui lòng thử lại sau.");
                    errorAlert.show();
                }
            });
        }).start();
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
