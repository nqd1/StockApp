package oop.grp1.GUI;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
        }        
        // Top trending table
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
        gridPane.add(topThayDoiBox, 2, 0);        this.getChildren().add(gridPane);

        // Thêm spacer để đẩy footer xuống dưới cùng
        Region spacerToBottom = new Region();
        VBox.setVgrow(spacerToBottom, Priority.ALWAYS);
        this.getChildren().add(spacerToBottom);

        // Thêm footer
        VBox footer = createFooter();
        this.getChildren().add(footer);

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
        
        // Tùy chỉnh cell factory cho cột tên cổ phiếu để thêm hiệu ứng hover
        nameColumn.setCellFactory(column -> {
            return new TableCell<StockData, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setText(null);
                        setGraphic(null);
                        setStyle("");
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
            // Xử lý sự kiện hover
            row.setOnMouseEntered(event -> {
                if (!row.isEmpty() && !row.isSelected()) {
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
        
    @SuppressWarnings("unchecked")
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
                        setStyle("");
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
                        // Tạo icon dựa vào giá trị phần trăm thay đổi (tăng hay giảm)
                        FontIcon icon;
                        HBox hbox = new HBox(5);
                        String changeText;
                        
                        if (item > 0) {
                            // Tăng giá - mũi tên xanh lên
                            icon = new FontIcon(FontAwesomeSolid.ARROW_UP);
                            icon.setIconColor(Color.GREEN);
                            changeText = String.format("+%.2f%% (Tăng)", item);
                            setTextFill(Color.GREEN);
                        } else if (item < 0) {
                            // Giảm giá - mũi tên đỏ xuống
                            icon = new FontIcon(FontAwesomeSolid.ARROW_DOWN);
                            icon.setIconColor(Color.RED);
                            changeText = String.format("%.2f%% (Giảm)", item);
                            setTextFill(Color.RED);
                        } else {
                            // Không đổi - dấu bằng màu xám
                            icon = new FontIcon(FontAwesomeSolid.EQUALS);
                            icon.setIconColor(Color.GRAY);
                            changeText = "0.00% (Không đổi)";
                            setTextFill(Color.GRAY);
                        }
                        
                        Label label = new Label(changeText);
                        label.setTextFill((Color) getTextFill());
                        
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        hbox.getChildren().addAll(icon, label);
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
            // Xử lý sự kiện hover
            row.setOnMouseEntered(event -> {
                if (!row.isEmpty() && !row.isSelected()) {
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
    }      
    
    private void loadTopGiamGiaData() {
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
        List<Stock> allStocks = Stock.getAllTickers().stream()
            .map(Stock::getLatestStock)
            .filter(stock -> stock != null)
            .sorted((a, b) -> Double.compare(Math.abs(b.getPercentageChange()), Math.abs(a.getPercentageChange()))) // thay đổi % nhiều nhất (không phân biệt tăng hay giảm)
            .limit(10)
            .collect(Collectors.toList());

        List<StockChangeData> topThayDoiData = allStocks.stream()
            .map(stock -> new StockChangeData(stock.getTicker(), stock.getPercentageChange() * 100)) // Chuyển thành % nên nhân 100
            .collect(Collectors.toList());

        // Thiết lập đúng 10 hàng trong bảng, không có hàng trống        
        topThayDoiTable.setFixedCellSize(25); // Chiều cao cố định cho mỗi hàng
        topThayDoiTable.setPrefHeight(270); // Chiều cao cho 10 hàng + header + padding
        topThayDoiTable.getItems().clear();
        
        // Thêm dữ liệu giả nếu không đủ 10 kết quả
        while (topThayDoiData.size() < 10) {
            topThayDoiData.add(new StockChangeData("-", 0.0));
        }
        
        topThayDoiTable.getItems().addAll(topThayDoiData);
    }
    
    /**
     * Cập nhật thời gian làm mới dữ liệu
     */    
    private void updateLastUpdatedTime(Label label) {
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
            });        }).start();
    }    /**
     * Tạo footer cho trang
     */
    private VBox createFooter() {
        VBox footer = new VBox(15);
        footer.setPadding(new Insets(25, 20, 20, 20));
        footer.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1 0 0 0;");
        
        // Separator line
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #dee2e6;");
        
        // Header với logo
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        // Logo
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
            ImageView logoView = new ImageView(logoImage);
            logoView.setFitHeight(40);
            logoView.setFitWidth(40);
            logoView.setPreserveRatio(true);
            
            Label companyName = new Label("Stock Market Tracker");
            companyName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #495057;");
            
            headerBox.getChildren().addAll(logoView, companyName);
        } catch (Exception e) {
            Label companyName = new Label("Stock Market Tracker");
            companyName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #495057;");
            headerBox.getChildren().add(companyName);
        }
        
        // Main footer content với 5 cột
        GridPane footerGrid = new GridPane();
        footerGrid.setHgap(30);
        footerGrid.setVgap(10);
        
        // Set column constraints for footer (5 cột)
        for (int i = 0; i < 5; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS);
            footerGrid.getColumnConstraints().add(column);
        }
        
        // Column 1: App Info
        VBox appInfoBox = new VBox(8);
        Label appTitle = new Label("Về chúng tôi");
        appTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #495057;");
        
        FontIcon appIcon = new FontIcon(FontAwesomeSolid.INFO_CIRCLE);
        appIcon.setIconColor(Color.valueOf("#007bff"));
        appIcon.setIconSize(16);
        appTitle.setGraphic(appIcon);
        appTitle.setContentDisplay(ContentDisplay.LEFT);
        appTitle.setGraphicTextGap(8);
        
        Label appDescription = new Label("Theo dõi thị trường chứng khoán\nreal-time với dữ liệu cập nhật\nliên tục và phân tích chuyên sâu");
        appDescription.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        appInfoBox.getChildren().addAll(appTitle, appDescription);
        footerGrid.add(appInfoBox, 0, 0);
        
        // Column 2: Data Source
        VBox dataSourceBox = new VBox(8);
        Label dataSourceTitle = new Label("Nguồn dữ liệu");
        dataSourceTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #495057;");
        
        FontIcon dataIcon = new FontIcon(FontAwesomeSolid.DATABASE);
        dataIcon.setIconColor(Color.valueOf("#28a745"));
        dataIcon.setIconSize(16);
        dataSourceTitle.setGraphic(dataIcon);
        dataSourceTitle.setContentDisplay(ContentDisplay.LEFT);
        dataSourceTitle.setGraphicTextGap(8);
        
        Label dataSourceInfo = new Label("• Alpha Vantage API\n• Cập nhật mỗi 15 phút\n• Dữ liệu real-time\n• Độ chính xác cao");
        dataSourceInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        dataSourceBox.getChildren().addAll(dataSourceTitle, dataSourceInfo);
        footerGrid.add(dataSourceBox, 1, 0);
        
        // Column 3: Status & Stats
        VBox statusBox = new VBox(8);
        Label statusTitle = new Label("Trạng thái hệ thống");
        statusTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #495057;");
        
        FontIcon statusIcon = new FontIcon(FontAwesomeSolid.SIGNAL);
        statusIcon.setIconColor(Color.valueOf("#20c997"));
        statusIcon.setIconSize(16);
        statusTitle.setGraphic(statusIcon);
        statusTitle.setContentDisplay(ContentDisplay.LEFT);
        statusTitle.setGraphicTextGap(8);
        
        Label statusInfo = new Label("• Hoạt động bình thường\n• " + Stock.getAllTickers().size() + " mã cổ phiếu\n• Uptime: 99.9%\n• Phản hồi: <100ms");
        statusInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        statusBox.getChildren().addAll(statusTitle, statusInfo);
        footerGrid.add(statusBox, 2, 0);
        
        // Column 4: Chính sách bảo mật
        VBox policyBox = new VBox(8);
        Label policyTitle = new Label("Chính sách");
        policyTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #495057;");
        
        FontIcon policyIcon = new FontIcon(FontAwesomeSolid.SHIELD_ALT);
        policyIcon.setIconColor(Color.valueOf("#6f42c1"));
        policyIcon.setIconSize(16);
        policyTitle.setGraphic(policyIcon);
        policyTitle.setContentDisplay(ContentDisplay.LEFT);
        policyTitle.setGraphicTextGap(8);
        
        Label policyInfo = new Label("• Chính sách bảo mật\n• Điều khoản sử dụng\n• Quyền riêng tư\n• Hỗ trợ khách hàng");
        policyInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        policyBox.getChildren().addAll(policyTitle, policyInfo);
        footerGrid.add(policyBox, 3, 0);
        
        // Column 5: Thông tin nhóm
        VBox teamBox = new VBox(8);
        Label teamTitle = new Label("Liên hệ nhóm");
        teamTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #495057;");
        
        FontIcon teamIcon = new FontIcon(FontAwesomeSolid.USERS);
        teamIcon.setIconColor(Color.valueOf("#fd7e14"));
        teamIcon.setIconSize(16);
        teamTitle.setGraphic(teamIcon);
        teamTitle.setContentDisplay(ContentDisplay.LEFT);
        teamTitle.setGraphicTextGap(8);
        
        // Facebook
        HBox facebookBox = new HBox(5);
        FontIcon facebookIcon = new FontIcon(FontAwesomeSolid.THUMBS_UP);
        facebookIcon.setIconColor(Color.valueOf("#1877f2"));
        facebookIcon.setIconSize(12);
        Label facebookLabel = new Label("OOP Group 1");
        facebookLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6c757d;");
        facebookBox.getChildren().addAll(facebookIcon, facebookLabel);
        
        // Phone
        HBox phoneBox = new HBox(5);
        FontIcon phoneIcon = new FontIcon(FontAwesomeSolid.PHONE);
        phoneIcon.setIconColor(Color.valueOf("#198754"));
        phoneIcon.setIconSize(12);
        Label phoneLabel = new Label("0123-456-789");
        phoneLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6c757d;");
        phoneBox.getChildren().addAll(phoneIcon, phoneLabel);
        
        // Address
        HBox addressBox = new HBox(5);
        FontIcon addressIcon = new FontIcon(FontAwesomeSolid.MAP_MARKER_ALT);
        addressIcon.setIconColor(Color.valueOf("#dc3545"));
        addressIcon.setIconSize(12);
        Label addressLabel = new Label("Hà Nội, Việt Nam");
        addressLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6c757d;");
        addressBox.getChildren().addAll(addressIcon, addressLabel);
        
        teamBox.getChildren().addAll(teamTitle, facebookBox, phoneBox, addressBox);
        footerGrid.add(teamBox, 4, 0);
        
        // App Store và Google Play buttons
        HBox appStoreBox = new HBox(15);
        appStoreBox.setAlignment(Pos.CENTER);
        appStoreBox.setPadding(new Insets(15, 0, 10, 0));
        
        // App Store button
        Button appStoreBtn = new Button("Download on App Store");
        FontIcon appleIcon = new FontIcon(FontAwesomeSolid.STAR);
        appleIcon.setIconColor(Color.WHITE);
        appStoreBtn.setGraphic(appleIcon);
        appStoreBtn.setStyle("-fx-background-color: #000; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 8;");
          // Google Play button
        Button googlePlayBtn = new Button("Get it on Google Play");
        FontIcon androidIcon = new FontIcon(FontAwesomeSolid.MOBILE_ALT);
        androidIcon.setIconColor(Color.WHITE);
        googlePlayBtn.setGraphic(androidIcon);
        googlePlayBtn.setStyle("-fx-background-color: #414141; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 8;");
        
        appStoreBox.getChildren().addAll(appStoreBtn, googlePlayBtn);
        
        // Bottom row: Copyright and version
        HBox bottomRow = new HBox();
        bottomRow.setAlignment(Pos.CENTER);
        bottomRow.setPadding(new Insets(15, 0, 0, 0));
        
        Label copyright = new Label("© 2024 Stock Market Tracker v1.0 | Phát triển bởi OOP Group 1");
        copyright.setStyle("-fx-font-size: 11px; -fx-text-fill: #868e96;");
        
        FontIcon heartIcon = new FontIcon(FontAwesomeSolid.HEART);
        heartIcon.setIconColor(Color.valueOf("#dc3545"));
        heartIcon.setIconSize(12);
        
        Label madeWith = new Label("Made with");
        madeWith.setStyle("-fx-font-size: 11px; -fx-text-fill: #868e96;");
        
        Label javaLabel = new Label("JavaFX");
        javaLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #868e96;");
        
        HBox loveBox = new HBox(5);
        loveBox.setAlignment(Pos.CENTER);
        loveBox.getChildren().addAll(madeWith, heartIcon, javaLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        bottomRow.getChildren().addAll(copyright, spacer, loveBox);
        
        footer.getChildren().addAll(separator, headerBox, footerGrid, appStoreBox, bottomRow);
        return footer;
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
