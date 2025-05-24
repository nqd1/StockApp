package oop.grp1.GUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
// cai nay de test thoi nhe
// dung quan tam
public class Stock {
    private String stockCode;
    private long volume;         // Khối lượng giao dịch
    private double openPrice;    // Giá mở cửa
    private double highPrice;    // Giá cao nhất
    private double lowPrice;     // Giá thấp nhất
    private double closePrice;   // Giá đóng cửa

    // Random để tạo dữ liệu giả lập
    private static final Random random = new Random();

    // Constructor với dữ liệu giả lập
    public Stock(String stockCode) {
        this.stockCode = stockCode;
        this.volume = 1000 + random.nextInt(9000); // Khối lượng từ 1000 đến 10000
        this.openPrice = 200.0 + (random.nextDouble() * 50.0); // Giá từ 200 đến 250
        this.highPrice = openPrice + (random.nextDouble() * 5.0); // Cao hơn giá mở tối đa 5
        this.lowPrice = openPrice - (random.nextDouble() * 5.0); // Thấp hơn giá mở tối đa 5
        this.closePrice = lowPrice + (random.nextDouble() * (highPrice - lowPrice)); // Giá đóng dao động giữa low và high
    }

    // Constructor với tất cả các tham số
    public Stock(String stockCode, long volume, double openPrice, double highPrice, double lowPrice, double closePrice) {
        this.stockCode = stockCode;
        this.volume = volume;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
    }

    public String getStockCode() {
        return stockCode;
    }

    public long getVolume() {
        return volume;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public double getClosePrice() {
        return closePrice;
    }

    // Tính toán thay đổi giá (dựa trên open và close)
    public String getChange() {
        double changePercent = ((closePrice - openPrice) / openPrice) * 100;
        return String.format("%.2f%%", changePercent);
    }

    // Giả định tên công ty (có thể thay bằng dữ liệu thực tế)
    public String getCompanyName() {
        return "Công ty " + stockCode; // Có thể thay bằng API hoặc dữ liệu thực tế
    }

    @Override
    public String toString() {
        return stockCode + " - " + getCompanyName() + " - Giá: " + String.format("%.2f", closePrice)
                + " - Thay đổi: " + getChange() + " - Khối lượng: " + volume;
    }

    // Phương thức tạo danh sách giả lập để test
    public static List<Stock> generateSampleStocks(int count) {
        List<Stock> stocks = new ArrayList<>();
        String[] stockCodes = {"AAPL", "GOOGL", "TSLA", "MSFT", "AMZN"};
        for (int i = 0; i < count; i++) {
            stocks.add(new Stock(stockCodes[random.nextInt(stockCodes.length)]));
        }
        return stocks;
    }
}