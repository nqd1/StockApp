package oop.grp1.GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import oop.grp1.Model.Stock;

import java.util.List;

public class StockDetail extends VBox {
    private final TableView<Stock> stockTable;
    private final ObservableList<Stock> stockData;

    // Default constructor: Load all stocks from the database
    public StockDetail() {
        stockTable = new TableView<>();
        stockData = FXCollections.observableArrayList();

        List<String> tickers = Stock.getAllTickers();
        for (String ticker : tickers) {
            Stock stock = Stock.getLatestStock(ticker);
            if (stock != null) {
                stockData.add(stock);
            }
        }

        setupTable();
    }

    // Constructor with specific ticker
    public StockDetail(String ticker) {
        stockTable = new TableView<>();
        stockData = FXCollections.observableArrayList();

        Stock stock = Stock.getLatestStock(ticker);
        if (stock != null) {
            stockData.add(stock);
        }

        setupTable();
    }

    // Constructor with Stock object
    public StockDetail(Stock stock) {
        stockTable = new TableView<>();
        stockData = FXCollections.observableArrayList();

        if (stock != null) {
            stockData.add(stock);
        }

        setupTable();
    }

    // Set up the table display
    private void setupTable() {
        TableColumn<Stock, String> ticColumn = new TableColumn<>("Mã CP");
        ticColumn.setCellValueFactory(new PropertyValueFactory<>("ticker"));

        // Fix: Change Long to Integer for volumeColumn
        TableColumn<Stock, Integer> volumeColumn = new TableColumn<>("Khối lượng");
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));
        volumeColumn.setCellFactory(column -> new TableCell<Stock, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("N/A");
                } else {
                    setText(Stock.formatVolume(item));
                }
            }
        });

        TableColumn<Stock, Double> openColumn = new TableColumn<>("Giá mở cửa");
        openColumn.setCellValueFactory(new PropertyValueFactory<>("open"));
        openColumn.setCellFactory(column -> new TableCell<Stock, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("N/A");
                } else {
                    setText(Stock.formatPrice(item));
                }
            }
        });

        TableColumn<Stock, Double> closeColumn = new TableColumn<>("Giá đóng cửa");
        closeColumn.setCellValueFactory(new PropertyValueFactory<>("close"));
        closeColumn.setCellFactory(column -> new TableCell<Stock, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("N/A");
                } else {
                    setText(Stock.formatPrice(item));
                }
            }
        });

        TableColumn<Stock, Double> highColumn = new TableColumn<>("Giá cao nhất");
        highColumn.setCellValueFactory(new PropertyValueFactory<>("high"));
        highColumn.setCellFactory(column -> new TableCell<Stock, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("N/A");
                } else {
                    setText(Stock.formatPrice(item));
                }
            }
        });

        TableColumn<Stock, Double> lowColumn = new TableColumn<>("Giá thấp nhất");
        lowColumn.setCellValueFactory(new PropertyValueFactory<>("low"));
        lowColumn.setCellFactory(column -> new TableCell<Stock, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("N/A");
                } else {
                    setText(Stock.formatPrice(item));
                }
            }
        });

        TableColumn<Stock, String> changeColumn = new TableColumn<>("Thay đổi");
        changeColumn.setCellValueFactory(cellData -> new javafx.beans.property.ReadOnlyStringWrapper(
                cellData.getValue().getFormattedPercentageChange()));

        TableColumn<Stock, String> timestampColumn = new TableColumn<>("Thời gian");
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        timestampColumn.setCellFactory(column -> new TableCell<Stock, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("N/A");
                } else {
                    setText(item);
                }
            }
        });

        stockTable.getColumns().addAll(ticColumn, volumeColumn, openColumn, closeColumn, highColumn, lowColumn, changeColumn, timestampColumn);
        stockTable.setItems(stockData);

        stockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Sử dụng CSS class
        this.setPadding(new Insets(15));
        this.getStyleClass().add("card");
        stockTable.getStyleClass().add("table-view");
        
        this.getChildren().add(stockTable);
        VBox.setVgrow(stockTable, javafx.scene.layout.Priority.ALWAYS);

        if (stockData.isEmpty()) {
            Label noDataLabel = new Label("Không có dữ liệu cổ phiếu để hiển thị.");
            noDataLabel.getStyleClass().add("title-label");
            this.getChildren().add(noDataLabel);
        }
    }

    public void updateStock(Stock stock) {
        stockData.clear();
        if (stock != null) {
            stockData.add(stock);
        }
        stockTable.refresh();
    }

    public void updateStockByTicker(String ticker) {
        Stock stock = Stock.getLatestStock(ticker);
        updateStock(stock);
    }

    public TableView<Stock> getStockTable() {
        return stockTable;
    }

    public ObservableList<Stock> getStockData() {
        return stockData;
    }
}