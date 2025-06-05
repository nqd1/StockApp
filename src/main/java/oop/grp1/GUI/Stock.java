package oop.grp1.GUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import oop.grp1.Control.DBManager.StockManager;





/**
 * Complete Stock class with data model, database operations, and analysis
 * Designed for GUI applications - no console output
 */
public class Stock {
    private String ticker;
    private int volume;
    private double open;
    private double close;
    private double high;
    private double low;
    private String timestamp;

    // Utility for formatting numbers
    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#,##0.00");
    private static final DecimalFormat VOLUME_FORMAT = new DecimalFormat("#,###");
    private static final String DB_URL = "jdbc:sqlite:stockAV.db";

    // ============ CONSTRUCTORS ============

    // Default constructor
    public Stock() {}

    // Full constructor
    public Stock(String ticker, int volume, double open, double close,
                 double high, double low, String timestamp) {
        this.ticker = ticker;
        this.volume = volume;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.timestamp = timestamp;
    }

    // ============ GETTERS AND SETTERS ============

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // ============ INFORMATIVE METHODS ============

    /**
     * Calculate the price change from open to close
     * @return price change (positive = gain, negative = loss)
     */
    public double getPriceChange() {
        return close - open;
    }

    /**
     * Calculate the percentage change from open to close
     * @return percentage change as decimal (0.05 = 5%)
     */
    public double getPercentageChange() {
        if (open == 0) return 0;
        return (close - open) / open;
    }

    /**
     * Get the trading range (high - low)
     * @return the price range for this time period
     */
    public double getTradingRange() {
        return high - low;
    }

    /**
     * Check if the stock closed higher than it opened (bullish)
     * @return true if close > open
     */
    public boolean isBullish() {
        return close > open;
    }

    /**
     * Check if the stock closed lower than it opened (bearish)
     * @return true if close < open
     */
    public boolean isBearish() {
        return close < open;
    }

    /**
     * Get the market sentiment as a string
     * @return "Bullish", "Bearish", or "Neutral"
     */
    public String getMarketSentiment() {
        if (isBullish()) return "Bullish";
        if (isBearish()) return "Bearish";
        return "Neutral";
    }

    /**
     * Calculate the volatility as a percentage of the average price
     * @return volatility percentage
     */
    public double getVolatility() {
        double avgPrice = (high + low) / 2;
        if (avgPrice == 0) return 0;
        return (getTradingRange() / avgPrice) * 100;
    }

    /**
     * Get formatted price change with + or - sign
     * @return formatted price change string
     */
    public String getFormattedPriceChange() {
        double change = getPriceChange();
        String sign = change >= 0 ? "+" : "";
        return sign + PRICE_FORMAT.format(change);
    }

    /**
     * Get formatted percentage change with + or - sign and % symbol
     * @return formatted percentage change string
     */
    public String getFormattedPercentageChange() {
        double pctChange = getPercentageChange() * 100;
        String sign = pctChange >= 0 ? "+" : "";
        return sign + String.format("%.2f", pctChange) + "%";
    }

    /**
     * Get formatted price for display
     * @param price the price to format
     * @return formatted price string
     */
    public static String formatPrice(double price) {
        return "$" + PRICE_FORMAT.format(price);
    }

    /**
     * Get formatted volume for display
     * @param volume the volume to format
     * @return formatted volume string
     */
    public static String formatVolume(int volume) {
        return VOLUME_FORMAT.format(volume) + " shares";
    }

    /**
     * Get a comprehensive summary of the stock information
     * @return detailed stock information as formatted string
     */
    public String getDetailedInfo() {
        StringBuilder info = new StringBuilder();
        info.append("=== STOCK INFORMATION ===\n");
        info.append("Ticker: ").append(ticker).append("\n");
        info.append("Timestamp: ").append(formatTimestamp()).append("\n");
        info.append("Open: ").append(formatPrice(open)).append("\n");
        info.append("Close: ").append(formatPrice(close)).append("\n");
        info.append("High: ").append(formatPrice(high)).append("\n");
        info.append("Low: ").append(formatPrice(low)).append("\n");
        info.append("Volume: ").append(formatVolume(volume)).append("\n");
        info.append("\n=== ANALYSIS ===\n");
        info.append("Price Change: ").append(getFormattedPriceChange()).append("\n");
        info.append("Percentage Change: ").append(getFormattedPercentageChange()).append("\n");
        info.append("Trading Range: ").append(formatPrice(getTradingRange())).append("\n");
        info.append("Market Sentiment: ").append(getMarketSentiment()).append("\n");
        info.append("Volatility: ").append(String.format("%.2f", getVolatility())).append("%\n");
        return info.toString();
    }

    /**
     * Get a concise one-line summary of the stock
     * @return brief stock summary
     */
    public String getQuickSummary() {
        return String.format("%s: $%.2f (%s) Vol: %s [%s]",
                ticker, close, getFormattedPercentageChange(),
                VOLUME_FORMAT.format(volume), getMarketSentiment());
    }

    /**
     * Format timestamp for better readability
     * @return formatted timestamp string
     */
    private String formatTimestamp() {
        try {
            // Assuming timestamp format from Alpha Vantage: "2023-12-08 16:00:00"
            LocalDateTime dateTime = LocalDateTime.parse(timestamp.replace(" ", "T"));
            return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
        } catch (Exception e) {
            return timestamp; // Return original if parsing fails
        }
    }

    /**
     * Get formatted timestamp for public access
     * @return formatted timestamp string
     */
    public String getFormattedTimestamp() {
        return formatTimestamp();
    }

    /**
     * Check if this is a high-volume trading period
     * @param averageVolume the average volume to compare against
     * @return true if volume is significantly above average
     */
    public boolean isHighVolumeTrading(int averageVolume) {
        return volume > (averageVolume * 1.5); // 50% above average
    }

    /**
     * Get price level relative to the day's range
     * @return percentage of where close price sits in the high-low range
     */
    public double getPricePositionInRange() {
        if (getTradingRange() == 0) return 0;
        return ((close - low) / getTradingRange()) * 100;
    }

    /**
     * Get a risk assessment based on volatility
     * @return risk level as string
     */
    public String getRiskLevel() {
        double volatility = getVolatility();
        if (volatility > 5.0) return "High Risk";
        if (volatility > 2.0) return "Medium Risk";
        return "Low Risk";
    }

    // ============ STATIC DATABASE & ANALYSIS METHODS ============

    /**
     * Retrieve all stock data for a specific ticker from database
     * @param ticker the stock symbol
     * @return list of Stock objects ordered by timestamp
     */
    public static List<Stock> getStockData(String ticker) {
        List<Stock> stocks = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT * FROM stock_data WHERE ticker = ? ORDER BY timestamp DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ticker);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Stock stock = new Stock(
                        rs.getString("ticker"),
                        rs.getInt("volume"),
                        rs.getDouble("open"),
                        rs.getDouble("close"),
                        rs.getDouble("high"),
                        rs.getDouble("low"),
                        rs.getString("timestamp")
                );
                stocks.add(stock);
            }
        } catch (SQLException e) {
            // Return empty list on error - GUI can handle this gracefully
        }

        return stocks;
    }

    /**
     * Get the latest stock data for a ticker
     * @param ticker the stock symbol
     * @return the most recent Stock object, or null if not found
     */
    public static Stock getLatestStock(String ticker) {
        List<Stock> stocks = getStockData(ticker);
        return stocks.isEmpty() ? null : stocks.get(0);
    }

    /**
     * Get all unique tickers from the database
     * @return list of all stock symbols in the database
     */
    public static List<String> getAllTickers() {
        List<String> tickers = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT DISTINCT ticker FROM stock_data ORDER BY ticker";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                tickers.add(rs.getString("ticker"));
            }
        } catch (SQLException e) {
            // Return empty list on error
        }

        return tickers;
    }

    /**
     * Calculate average volume for a ticker
     * @param ticker the stock symbol
     * @return average volume, or 0 if no data
     */
    public static int getAverageVolume(String ticker) {
        List<Stock> stocks = getStockData(ticker);
        if (stocks.isEmpty()) return 0;

        return (int) stocks.stream()
                .mapToInt(Stock::getVolume)
                .average()
                .orElse(0);
    }

    /**
     * Get the highest price recorded for a ticker
     * @param ticker the stock symbol
     * @return highest price, or 0 if no data
     */
    public static double getHighestPrice(String ticker) {
        List<Stock> stocks = getStockData(ticker);
        return stocks.stream()
                .mapToDouble(Stock::getHigh)
                .max()
                .orElse(0);
    }

    /**
     * Get the lowest price recorded for a ticker
     * @param ticker the stock symbol
     * @return lowest price, or 0 if no data
     */
    public static double getLowestPrice(String ticker) {
        List<Stock> stocks = getStockData(ticker);
        return stocks.stream()
                .mapToDouble(Stock::getLow)
                .min()
                .orElse(0);
    }

    /**
     * Calculate average price for a ticker
     * @param ticker the stock symbol
     * @return average close price, or 0 if no data
     */
    public static double getAveragePrice(String ticker) {
        List<Stock> stocks = getStockData(ticker);
        return stocks.stream()
                .mapToDouble(Stock::getClose)
                .average()
                .orElse(0);
    }

    /**
     * Get stocks with the highest trading volume
     * @param ticker the stock symbol
     * @param limit number of results to return
     * @return list of Stock objects with highest volume
     */
    public static List<Stock> getHighestVolumeStocks(String ticker, int limit) {
        List<Stock> stocks = getStockData(ticker);
        return stocks.stream()
                .sorted((a, b) -> Integer.compare(b.getVolume(), a.getVolume()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get most volatile trading periods
     * @param ticker the stock symbol
     * @param limit number of results to return
     * @return list of Stock objects with highest volatility
     */
    public static List<Stock> getMostVolatileStocks(String ticker, int limit) {
        List<Stock> stocks = getStockData(ticker);
        return stocks.stream()
                .sorted((a, b) -> Double.compare(b.getVolatility(), a.getVolatility()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get best performing periods (highest percentage gains)
     * @param ticker the stock symbol
     * @param limit number of results to return
     * @return list of Stock objects with highest percentage gains
     */
    public static List<Stock> getBestPerformingPeriods(String ticker, int limit) {
        List<Stock> stocks = getStockData(ticker);
        return stocks.stream()
                .filter(Stock::isBullish)
                .sorted((a, b) -> Double.compare(b.getPercentageChange(), a.getPercentageChange()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get worst performing periods (highest percentage losses)
     * @param ticker the stock symbol
     * @param limit number of results to return
     * @return list of Stock objects with highest percentage losses
     */
    public static List<Stock> getWorstPerformingPeriods(String ticker, int limit) {
        List<Stock> stocks = getStockData(ticker);
        return stocks.stream()
                .filter(Stock::isBearish)
                .sorted((a, b) -> Double.compare(a.getPercentageChange(), b.getPercentageChange()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Generate a comprehensive analysis report for a ticker
     * @param ticker the stock symbol
     * @return formatted analysis report
     */
    public static String generateAnalysisReport(String ticker) {
        List<Stock> stocks = getStockData(ticker);
        if (stocks.isEmpty()) {
            return "No data available for ticker: " + ticker;
        }

        Stock latest = stocks.get(0);
        int avgVolume = getAverageVolume(ticker);
        double avgPrice = getAveragePrice(ticker);
        double highestPrice = getHighestPrice(ticker);
        double lowestPrice = getLowestPrice(ticker);

        long bullishPeriods = stocks.stream().filter(Stock::isBullish).count();
        long bearishPeriods = stocks.stream().filter(Stock::isBearish).count();
        long neutralPeriods = stocks.size() - bullishPeriods - bearishPeriods;

        StringBuilder report = new StringBuilder();
        report.append("==========================================\n");
        report.append("           STOCK ANALYSIS REPORT\n");
        report.append("==========================================\n");
        report.append("Ticker: ").append(ticker).append("\n");
        report.append("Total Data Points: ").append(stocks.size()).append("\n");
        report.append("Analysis Period: ").append(stocks.get(stocks.size()-1).getTimestamp())
                .append(" to ").append(latest.getTimestamp()).append("\n\n");

        report.append("=== LATEST DATA ===\n");
        report.append(latest.getDetailedInfo()).append("\n");

        report.append("=== HISTORICAL SUMMARY ===\n");
        report.append("Average Price: ").append(formatPrice(avgPrice)).append("\n");
        report.append("Highest Price: ").append(formatPrice(highestPrice)).append("\n");
        report.append("Lowest Price: ").append(formatPrice(lowestPrice)).append("\n");
        report.append("Average Volume: ").append(formatVolume(avgVolume)).append("\n\n");

        report.append("=== MARKET SENTIMENT DISTRIBUTION ===\n");
        report.append("Bullish Periods: ").append(bullishPeriods)
                .append(" (").append(String.format("%.1f", (bullishPeriods * 100.0 / stocks.size()))).append("%)\n");
        report.append("Bearish Periods: ").append(bearishPeriods)
                .append(" (").append(String.format("%.1f", (bearishPeriods * 100.0 / stocks.size()))).append("%)\n");
        report.append("Neutral Periods: ").append(neutralPeriods)
                .append(" (").append(String.format("%.1f", (neutralPeriods * 100.0 / stocks.size()))).append("%)\n\n");

        // Top performing periods
        List<Stock> topGainers = getBestPerformingPeriods(ticker, 3);
        if (!topGainers.isEmpty()) {
            report.append("=== TOP 3 GAINING PERIODS ===\n");
            for (int i = 0; i < topGainers.size(); i++) {
                Stock stock = topGainers.get(i);
                report.append(i + 1).append(". ").append(stock.getFormattedTimestamp())
                        .append(": ").append(stock.getFormattedPercentageChange())
                        .append(" (").append(stock.getFormattedPriceChange()).append(")\n");
            }
            report.append("\n");
        }

        // Top losing periods
        List<Stock> topLosers = getWorstPerformingPeriods(ticker, 3);
        if (!topLosers.isEmpty()) {
            report.append("=== TOP 3 LOSING PERIODS ===\n");
            for (int i = 0; i < topLosers.size(); i++) {
                Stock stock = topLosers.get(i);
                report.append(i + 1).append(". ").append(stock.getFormattedTimestamp())
                        .append(": ").append(stock.getFormattedPercentageChange())
                        .append(" (").append(stock.getFormattedPriceChange()).append(")\n");
            }
            report.append("\n");
        }

        report.append("==========================================\n");

        return report.toString();
    }

    /**
     * Fetch and store new stock data for a ticker (silent operation for GUI)
     * @param ticker the stock symbol to fetch
     * @return true if successful, false otherwise
     */
    public static boolean fetchAndStoreStock(String ticker) {
        try {
            StockManager stockManager = new StockManager();
            stockManager.fetchAndStore(ticker);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get dashboard data for all stocks
     * @return map of ticker to latest stock data
     */
    public static Map<String, Stock> getDashboardData() {
        Map<String, Stock> dashboardData = new HashMap<>();
        List<String> tickers = getAllTickers();

        for (String ticker : tickers) {
            Stock latest = getLatestStock(ticker);
            if (latest != null) {
                dashboardData.put(ticker, latest);
            }
        }

        return dashboardData;
    }

    /**
     * Get summary statistics for a ticker
     * @param ticker the stock symbol
     * @return map containing various statistics
     */
    public static Map<String, Object> getSummaryStatistics(String ticker) {
        Map<String, Object> stats = new HashMap<>();
        List<Stock> stocks = getStockData(ticker);

        if (stocks.isEmpty()) {
            return stats;
        }

        stats.put("totalDataPoints", stocks.size());
        stats.put("averagePrice", getAveragePrice(ticker));
        stats.put("highestPrice", getHighestPrice(ticker));
        stats.put("lowestPrice", getLowestPrice(ticker));
        stats.put("averageVolume", getAverageVolume(ticker));
        stats.put("bullishPeriods", stocks.stream().filter(Stock::isBullish).count());
        stats.put("bearishPeriods", stocks.stream().filter(Stock::isBearish).count());
        stats.put("latestStock", stocks.get(0));

        return stats;
    }

    // ============ OBJECT OVERRIDES ============

    @Override
    public String toString() {
        return getQuickSummary();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Stock stock = (Stock) obj;
        return ticker.equals(stock.ticker) && timestamp.equals(stock.timestamp);
    }

    @Override
    public int hashCode() {
        return ticker.hashCode() + timestamp.hashCode();
    }
}