package oop.grp1.GUI;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.util.List;

public class DashboardPage extends VBox {
    private final ListView<String> stockListView;
    private final ListView<String> newsListView;
    public DashboardPage() {
        Label label = new Label("Welcome to the Dashboard!");
        this.getChildren().add(label);
        Label stockLabel = new Label("Top 10 Chứng khoán:");
        stockListView = new ListView<>();
        Label newsLabel = new Label("Bài báo/Báo cáo về chứng khoán:");
        newsListView = new ListView<>();

        this.getChildren().addAll(stockLabel, stockListView, newsLabel, newsListView);

        // Gọi API để lấy dữ liệu
        loadStockData();
        loadNewsData();

    }
    private void loadStockData() {
        // Giả lập dữ liệu từ API
        List<String> stocks = List.of(
                "Stock A - Giá: 100",
                "Stock B - Giá: 200",
                "Stock C - Giá: 300",
                "Stock D - Giá: 400",
                "Stock E - Giá: 500",
                "Stock F - Giá: 600",
                "Stock G - Giá: 700",
                "Stock H - Giá: 800",
                "Stock I - Giá: 900",
                "Stock J - Giá: 1000"
        );
        stockListView.getItems().addAll(stocks);
    }

    private void loadNewsData() {
        // Giả lập dữ liệu từ API
        List<String> news = List.of(
                "Bài báo 1: Thị trường chứng khoán tăng mạnh",
                "Bài báo 2: Phân tích cổ phiếu tiềm năng",
                "Bài báo 3: Báo cáo tài chính quý 3",
                "Bài báo 4: Xu hướng đầu tư năm 2023",
                "Bài báo 5: Cảnh báo rủi ro thị trường"
        );
        newsListView.getItems().addAll(news);
    }
}
