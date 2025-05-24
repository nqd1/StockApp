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

import java.util.List;

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
        return table;
    }

    @SuppressWarnings("unchecked")
    private TableView<StockChangeData> createStockChangeTable() {
        TableView<StockChangeData> table = new TableView<>();

        TableColumn<StockChangeData, String> nameColumn = new TableColumn<>("Tên cổ phiếu");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<StockChangeData, Double> changeColumn = new TableColumn<>("Thay đổi");
        changeColumn.setCellValueFactory(new PropertyValueFactory<>("change"));

        table.getColumns().addAll(nameColumn, changeColumn);
        return table;
    }

    private void loadTopTrendingData() {
        List<StockData> topTrending = List.of(
            new StockData("Stock A", 100),
            new StockData("Stock B", 200),
            new StockData("Stock C", 300),
            new StockData("Stock D", 400),
            new StockData("Stock E", 500)
        );
        topTrendingTable.getItems().addAll(topTrending);
    }

    private void loadTopGiamGiaData() {
        List<StockData> topGiamGia = List.of(
            new StockData("Stock F", 90),
            new StockData("Stock G", 80),
            new StockData("Stock H", 70),
            new StockData("Stock I", 60),
            new StockData("Stock J", 50)
        );
        topGiamGiaTable.getItems().addAll(topGiamGia);
    }

    private void loadTopThayDoiData() {
        List<StockChangeData> topThayDoi = List.of(
            new StockChangeData("Stock K", 15),
            new StockChangeData("Stock L", 12),
            new StockChangeData("Stock M", 10),
            new StockChangeData("Stock N", 8),
            new StockChangeData("Stock O", 5)
        );
        topThayDoiTable.getItems().addAll(topThayDoi);
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
