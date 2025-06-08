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
import oop.grp1.Model.Stock;

public class WatchList extends VBox {
    private final TableView<Stock> watchListTable;
    static final ObservableList<Stock> watchListStocks = FXCollections.observableArrayList();

    public WatchList() {
        Label titleLabel = new Label("Danh Sách Theo Dõi");
        titleLabel.setFont(Font.font("Arial", 18));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        watchListTable = new TableView<>();
        watchListTable.setStyle("-fx-border-color: #dcdcdc; -fx-border-radius: 5px;");
        watchListTable.setPrefHeight(400);
        watchListTable.setEditable(true);
        watchListTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Stock, String> ticColumn = new TableColumn<>("tic");
        ticColumn.setCellValueFactory(new PropertyValueFactory<>("ticker"));

        TableColumn<Stock, Integer> volumeColumn = new TableColumn<>("volume");
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));

        TableColumn<Stock, Double> openColumn = new TableColumn<>("open");
        openColumn.setCellValueFactory(new PropertyValueFactory<>("open"));
        openColumn.setCellFactory(column -> new TableCell<Stock, Double>() {
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

        TableColumn<Stock, Double> closeColumn = new TableColumn<>("close");
        closeColumn.setCellValueFactory(new PropertyValueFactory<>("close"));
        closeColumn.setCellFactory(column -> new TableCell<Stock, Double>() {
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

        TableColumn<Stock, Double> highColumn = new TableColumn<>("high");
        highColumn.setCellValueFactory(new PropertyValueFactory<>("high"));
        highColumn.setCellFactory(column -> new TableCell<Stock, Double>() {
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

        TableColumn<Stock, Double> lowColumn = new TableColumn<>("low");
        lowColumn.setCellValueFactory(new PropertyValueFactory<>("low"));
        lowColumn.setCellFactory(column -> new TableCell<Stock, Double>() {
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

        TableColumn<Stock, String> changeColumn = new TableColumn<>("Change");
        changeColumn.setCellValueFactory(cellData -> new javafx.beans.property.ReadOnlyStringWrapper(cellData.getValue().getFormattedPercentageChange()));

        watchListTable.getColumns().addAll(ticColumn, volumeColumn, openColumn, closeColumn, highColumn, lowColumn, changeColumn);
        watchListTable.setItems(watchListStocks);

        this.getChildren().addAll(titleLabel, watchListTable);
        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 10px;");
        this.setEffect(new javafx.scene.effect.DropShadow(10, Color.GRAY));
        VBox.setVgrow(watchListTable, Priority.ALWAYS);

        watchListStocks.addListener((javafx.collections.ListChangeListener<Stock>) change -> {
            watchListTable.refresh();
            System.out.println("watchListStocks updated: " + watchListStocks);
        });
    }

    public TableView<Stock> getWatchListView() {
        return watchListTable;
    }

    public static void addToWatchList(Stock stock) {
        if (stock != null && !watchListStocks.contains(stock)) {
            watchListStocks.add(stock);
            System.out.println("Added to watchListStocks: " + stock);
        } else {
            System.out.println("Stock already in watchListStocks or null: " + stock);
        }
    }

    public static void removeFromWatchList(Stock stock) {
        watchListStocks.remove(stock);
        System.out.println("Removed from watchListStocks: " + stock);
    }

    public static ObservableList<Stock> getWatchListStocks() {
        return watchListStocks;
    }
}