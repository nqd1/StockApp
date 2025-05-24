package oop.grp1.GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.Priority;

public class WatchList extends VBox {
    private final TableView<StockDetail.StockWithInterest> watchListTable;
    static final ObservableList<StockDetail.StockWithInterest> watchListStocks = FXCollections.observableArrayList();

    public WatchList() {
        Label titleLabel = new Label("Danh Sách Theo Dõi");
        titleLabel.setFont(Font.font("Arial", 18));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        watchListTable = new TableView<>();
        watchListTable.setStyle("-fx-border-color: #dcdcdc; -fx-border-radius: 5px;");
        watchListTable.setPrefHeight(400);
        watchListTable.setEditable(true);
        watchListTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Định nghĩa các cột tương tự như StockDetail
        TableColumn<StockDetail.StockWithInterest, String> ticColumn = new TableColumn<>("tic");
        ticColumn.setCellValueFactory(new PropertyValueFactory<>("stockCode"));

        TableColumn<StockDetail.StockWithInterest, Long> volumeColumn = new TableColumn<>("volume");
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));

        TableColumn<StockDetail.StockWithInterest, Double> openColumn = new TableColumn<>("open");
        openColumn.setCellValueFactory(new PropertyValueFactory<>("openPrice"));
        openColumn.setCellFactory(column -> new TableCell<StockDetail.StockWithInterest, Double>() {
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

        TableColumn<StockDetail.StockWithInterest, Double> closeColumn = new TableColumn<>("close");
        closeColumn.setCellValueFactory(new PropertyValueFactory<>("closePrice"));
        closeColumn.setCellFactory(column -> new TableCell<StockDetail.StockWithInterest, Double>() {
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

        TableColumn<StockDetail.StockWithInterest, Double> highColumn = new TableColumn<>("high");
        highColumn.setCellValueFactory(new PropertyValueFactory<>("highPrice"));
        highColumn.setCellFactory(column -> new TableCell<StockDetail.StockWithInterest, Double>() {
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

        TableColumn<StockDetail.StockWithInterest, Double> lowColumn = new TableColumn<>("low");
        lowColumn.setCellValueFactory(new PropertyValueFactory<>("lowPrice"));
        lowColumn.setCellFactory(column -> new TableCell<StockDetail.StockWithInterest, Double>() {
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

        TableColumn<StockDetail.StockWithInterest, String> changeColumn = new TableColumn<>("Change");
        changeColumn.setCellValueFactory(cellData -> new javafx.beans.property.ReadOnlyStringWrapper(cellData.getValue().getChange()));

        watchListTable.getColumns().addAll(ticColumn, volumeColumn, openColumn, closeColumn, highColumn, lowColumn, changeColumn);
        watchListTable.setItems(watchListStocks);

        this.getChildren().addAll(titleLabel, watchListTable);
        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 10px;");
        this.setEffect(new javafx.scene.effect.DropShadow(10, Color.GRAY));
        VBox.setVgrow(watchListTable, Priority.ALWAYS);

        // Lắng nghe thay đổi để làm mới bảng
        watchListStocks.addListener((javafx.collections.ListChangeListener<StockDetail.StockWithInterest>) change -> {
            watchListTable.refresh();
            System.out.println("watchListStocks updated: " + watchListStocks);
        });
    }

    public TableView<StockDetail.StockWithInterest> getWatchListView() {
        return watchListTable;
    }

    public static void addToWatchList(StockDetail.StockWithInterest stock) {
        if (stock != null && !watchListStocks.contains(stock)) {
            watchListStocks.add(stock);
            System.out.println("Added to watchListStocks: " + stock);
        } else {
            System.out.println("Stock already in watchListStocks or null: " + stock);
        }
    }

    public static void removeFromWatchList(StockDetail.StockWithInterest stock) {
        watchListStocks.remove(stock);
        System.out.println("Removed from watchListStocks: " + stock);
    }

    public static ObservableList<StockDetail.StockWithInterest> getWatchListStocks() {
        return watchListStocks;
    }
}