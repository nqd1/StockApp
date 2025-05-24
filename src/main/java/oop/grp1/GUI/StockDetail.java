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

import java.util.List;

public class StockDetail extends VBox {
    private final TableView<StockWithInterest> stockTable;
    private final ObservableList<StockWithInterest> stockData;

    public static class StockWithInterest extends Stock {
        private final BooleanProperty interested;

        public StockWithInterest(Stock stock) {
            super(stock.getStockCode(), stock.getVolume(), stock.getOpenPrice(), stock.getHighPrice(), stock.getLowPrice(), stock.getClosePrice());
            this.interested = new SimpleBooleanProperty(false);
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
    }

    public StockDetail() {
        stockTable = new TableView<>();
        stockData = FXCollections.observableArrayList();

        List<Stock> sampleStocks = Stock.generateSampleStocks(5);
        for (Stock stock : sampleStocks) {
            stockData.add(new StockWithInterest(stock));
        }

        TableColumn<StockWithInterest, String> ticColumn = new TableColumn<>("tic");
        ticColumn.setCellValueFactory(new PropertyValueFactory<>("stockCode"));

        TableColumn<StockWithInterest, Long> volumeColumn = new TableColumn<>("volume");
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));

        TableColumn<StockWithInterest, Double> openColumn = new TableColumn<>("open");
        openColumn.setCellValueFactory(new PropertyValueFactory<>("openPrice"));

        TableColumn<StockWithInterest, Double> closeColumn = new TableColumn<>("close");
        closeColumn.setCellValueFactory(new PropertyValueFactory<>("closePrice"));

        TableColumn<StockWithInterest, Double> highColumn = new TableColumn<>("high");
        highColumn.setCellValueFactory(new PropertyValueFactory<>("highPrice"));

        TableColumn<StockWithInterest, Double> lowColumn = new TableColumn<>("low");
        lowColumn.setCellValueFactory(new PropertyValueFactory<>("lowPrice"));

        TableColumn<StockWithInterest, Boolean> interestedColumn = new TableColumn<>("Quan tâm");
        interestedColumn.setCellValueFactory(cellData -> cellData.getValue().interestedProperty());
        interestedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(interestedColumn));
        interestedColumn.setEditable(true);
        interestedColumn.setOnEditCommit(event -> {
            StockWithInterest selectedStock = event.getRowValue();
            if (selectedStock.isInterested()) {
                WatchList.addToWatchList(selectedStock);
                System.out.println("Đã thêm " + selectedStock.getStockCode() + " vào danh sách quan tâm. Current watchList: " + WatchList.watchListStocks);
            } else {
                WatchList.removeFromWatchList(selectedStock);
                System.out.println("Đã xóa " + selectedStock.getStockCode() + " khỏi danh sách quan tâm. Current watchList: " + WatchList.watchListStocks);
            }
        });

        openColumn.setCellFactory(column -> new TableCell<StockWithInterest, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("N/A");
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        closeColumn.setCellFactory(column -> new TableCell<StockWithInterest, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("N/A");
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        highColumn.setCellFactory(column -> new TableCell<StockWithInterest, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("N/A");
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        lowColumn.setCellFactory(column -> new TableCell<StockWithInterest, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("N/A");
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        stockTable.getColumns().addAll(ticColumn, volumeColumn, openColumn, closeColumn, highColumn, lowColumn, interestedColumn);
        stockTable.setItems(stockData);
        stockTable.setEditable(true);

        stockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        stockTable.setStyle("-fx-border-color: #dcdcdc; -fx-border-radius: 5px;");
        this.setPadding(new Insets(15));
        this.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 10px;");
        this.getChildren().add(stockTable);
        this.setEffect(new javafx.scene.effect.DropShadow(10, Color.GRAY));
        VBox.setVgrow(stockTable, javafx.scene.layout.Priority.ALWAYS);
    }

    public StockDetail(Stock stock) {
        this();
        if (stock != null) {
            stockData.clear();
            stockData.add(new StockWithInterest(stock));
        }
    }

    public void updateStock(Stock stock) {
        stockData.clear();
        StockWithInterest stockWithInterest = (stock != null) ? new StockWithInterest(stock) : null;
        if (stockWithInterest != null) {
            stockData.add(stockWithInterest);
        }
    }

    public TableView<StockWithInterest> getStockTable() {
        return stockTable;
    }
}