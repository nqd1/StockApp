package oop.grp1.GUI;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import oop.grp1.Model.Stock;
import java.util.List;

public class StockDetail extends VBox {
    private final TableView<StockWithInterest> stockTable;
    private final ObservableList<StockWithInterest> stockData;


    public static class StockWithInterest extends Stock {
        private final BooleanProperty interested;

        public StockWithInterest(Stock stock) {
            super(stock.getTicker(), stock.getVolume(), stock.getOpen(), stock.getClose(),
                    stock.getHigh(), stock.getLow(), stock.getTimestamp());
            this.interested = new SimpleBooleanProperty(WatchList.isInWatchList(stock.getTicker()));

            this.interested.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    WatchList.addToWatchList(this);
                } else {
                    WatchList.removeFromWatchList(this);
                }
            });
        }

        public boolean isInterested() {
            return interested.get();
        }

        public BooleanProperty interestedProperty() {
            return interested;
        }

        public void setInterested(boolean interested) {
            this.interested.set(interested);
        }

        public String getStockCode() {
            return getTicker();
        }

        public String getChange() {
            return getFormattedPercentageChange();
        }
    }


    public StockDetail() {
        stockTable = new TableView<>();
        stockData = FXCollections.observableArrayList();

        List<String> tickers = Stock.getAllTickers();
        for (String ticker : tickers) {
            Stock stock = Stock.getLatestStock(ticker);
            if (stock != null) {
                stockData.add(new StockWithInterest(stock));
            }
        }

        setupTable();
    }



    private void setupTable() {
        TableColumn<StockWithInterest, String> ticColumn = new TableColumn<>("Mã CP");
        ticColumn.setCellValueFactory(new PropertyValueFactory<>("ticker"));

        TableColumn<StockWithInterest, Integer> volumeColumn = new TableColumn<>("Khối lượng");
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));
        volumeColumn.setCellFactory(column -> new TableCell<>() {
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

        TableColumn<StockWithInterest, Double> openColumn = new TableColumn<>("Giá mở cửa");
        openColumn.setCellValueFactory(new PropertyValueFactory<>("open"));
        openColumn.setCellFactory(column -> new TableCell<>() {
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

        TableColumn<StockWithInterest, Double> closeColumn = new TableColumn<>("Giá đóng cửa");
        closeColumn.setCellValueFactory(new PropertyValueFactory<>("close"));
        closeColumn.setCellFactory(column -> new TableCell<>() {
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

        TableColumn<StockWithInterest, Double> highColumn = new TableColumn<>("Giá cao nhất");
        highColumn.setCellValueFactory(new PropertyValueFactory<>("high"));
        highColumn.setCellFactory(column -> new TableCell<>() {
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

        TableColumn<StockWithInterest, Double> lowColumn = new TableColumn<>("Giá thấp nhất");
        lowColumn.setCellValueFactory(new PropertyValueFactory<>("low"));
        lowColumn.setCellFactory(column -> new TableCell<>() {
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

        TableColumn<StockWithInterest, String> changeColumn = new TableColumn<>("Thay đổi");
        changeColumn.setCellValueFactory(cellData -> new javafx.beans.property.ReadOnlyStringWrapper(
                cellData.getValue().getFormattedPercentageChange()));

        TableColumn<StockWithInterest, String> timestampColumn = new TableColumn<>("Thời gian");
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        timestampColumn.setCellFactory(column -> new TableCell<>() {
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

        TableColumn<StockWithInterest, Boolean> interestedColumn = new TableColumn<>("Quan tâm");
        interestedColumn.setCellValueFactory(cellData -> cellData.getValue().interestedProperty());
        interestedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(interestedColumn));
        interestedColumn.setEditable(true);

        stockTable.getColumns().addAll(ticColumn, volumeColumn, openColumn, closeColumn, highColumn, lowColumn, changeColumn, timestampColumn, interestedColumn);
        stockTable.setItems(stockData);
        stockTable.setEditable(true);

        stockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        stockTable.setStyle("-fx-border-color: #dcdcdc; -fx-border-radius: 5px;");
        this.setPadding(new Insets(15));
        this.getStyleClass().add("page-background");
        this.getChildren().add(stockTable);
        this.setEffect(new javafx.scene.effect.DropShadow(10, Color.GRAY));
        VBox.setVgrow(stockTable, javafx.scene.layout.Priority.ALWAYS);

        if (stockData.isEmpty()) {
            Label noDataLabel = new Label("Không có dữ liệu cổ phiếu để hiển thị.");
            noDataLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px;");
            this.getChildren().add(noDataLabel);
        }
    }

    public void updateStock(Stock stock) {
        stockData.clear();
        if (stock != null) {
            stockData.add(new StockWithInterest(stock));
        }
        stockTable.refresh();
    }

    public void updateStockByTicker(String ticker) {
        Stock stock = Stock.getLatestStock(ticker);
        updateStock(stock);
    }

    public TableView<StockWithInterest> getStockTable() {
        return stockTable;
    }

    public ObservableList<StockWithInterest> getStockData() {
        return stockData;
    }
}