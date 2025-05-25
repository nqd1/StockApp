package oop.grp1.Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import oop.grp1.Control.DBManager.NewsManager;

/**
 * Complete News class with data model, database operations, and analysis
 * Handles news data from 3 database tables: news_data, news_topics, news_tickersentiment
 * Designed for GUI applications - no console output
 */
public class News {
    // Main news properties
    private String title;
    private String url;
    private String timePublished;
    private String summary;
    private String source;
    private String sentimentLabel;
    private double sentimentScore;
    
    // Related data
    private List<Topic> topics;
    private List<TickerSentiment> tickerSentiments;
    
    // Database connection
    private static final String DB_URL = "jdbc:sqlite:stockAV.db";
    
    // Inner classes for related data
    public static class Topic {
        private String topic;
        private double relevanceScore;
        
        public Topic(String topic, double relevanceScore) {
            this.topic = topic;
            this.relevanceScore = relevanceScore;
        }
        
        // Getters and setters
        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public double getRelevanceScore() { return relevanceScore; }
        public void setRelevanceScore(double relevanceScore) { this.relevanceScore = relevanceScore; }
        
        @Override
        public String toString() {
            return String.format("%s (%.2f)", topic, relevanceScore);
        }
    }
    
    public static class TickerSentiment {
        private String ticker;
        private double relevanceScore;
        private double sentimentScore;
        private String sentimentLabel;
        
        public TickerSentiment(String ticker, double relevanceScore, double sentimentScore, String sentimentLabel) {
            this.ticker = ticker;
            this.relevanceScore = relevanceScore;
            this.sentimentScore = sentimentScore;
            this.sentimentLabel = sentimentLabel;
        }
        
        // Getters and setters
        public String getTicker() { return ticker; }
        public void setTicker(String ticker) { this.ticker = ticker; }
        public double getRelevanceScore() { return relevanceScore; }
        public void setRelevanceScore(double relevanceScore) { this.relevanceScore = relevanceScore; }
        public double getSentimentScore() { return sentimentScore; }
        public void setSentimentScore(double sentimentScore) { this.sentimentScore = sentimentScore; }
        public String getSentimentLabel() { return sentimentLabel; }
        public void setSentimentLabel(String sentimentLabel) { this.sentimentLabel = sentimentLabel; }
        
        public boolean isBullish() { return "Bullish".equalsIgnoreCase(sentimentLabel); }
        public boolean isBearish() { return "Bearish".equalsIgnoreCase(sentimentLabel); }
        public boolean isNeutral() { return "Neutral".equalsIgnoreCase(sentimentLabel); }
        
        @Override
        public String toString() {
            return String.format("%s: %s (%.2f) - Relevance: %.2f", 
                ticker, sentimentLabel, sentimentScore, relevanceScore);
        }
    }
    
    // ============ CONSTRUCTORS ============
    
    public News() {
        this.topics = new ArrayList<>();
        this.tickerSentiments = new ArrayList<>();
    }
    
    public News(String title, String url, String timePublished, String summary, 
                String source, String sentimentLabel, double sentimentScore) {
        this.title = title;
        this.url = url;
        this.timePublished = timePublished;
        this.summary = summary;
        this.source = source;
        this.sentimentLabel = sentimentLabel;
        this.sentimentScore = sentimentScore;
        this.topics = new ArrayList<>();
        this.tickerSentiments = new ArrayList<>();
    }
    
    // ============ GETTERS AND SETTERS ============
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getTimePublished() { return timePublished; }
    public void setTimePublished(String timePublished) { this.timePublished = timePublished; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getSentimentLabel() { return sentimentLabel; }
    public void setSentimentLabel(String sentimentLabel) { this.sentimentLabel = sentimentLabel; }
    
    public double getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(double sentimentScore) { this.sentimentScore = sentimentScore; }
    
    public List<Topic> getTopics() { return new ArrayList<>(topics); }
    public void setTopics(List<Topic> topics) { this.topics = new ArrayList<>(topics); }
    
    public List<TickerSentiment> getTickerSentiments() { return new ArrayList<>(tickerSentiments); }
    public void setTickerSentiments(List<TickerSentiment> tickerSentiments) { this.tickerSentiments = new ArrayList<>(tickerSentiments); }
    
    // ============ UTILITY METHODS ============
    
    /**
     * Check if the overall news sentiment is bullish
     */
    public boolean isBullish() {
        return "Bullish".equalsIgnoreCase(sentimentLabel);
    }
    
    /**
     * Check if the overall news sentiment is bearish
     */
    public boolean isBearish() {
        return "Bearish".equalsIgnoreCase(sentimentLabel);
    }
    
    /**
     * Check if the overall news sentiment is neutral
     */
    public boolean isNeutral() {
        return "Neutral".equalsIgnoreCase(sentimentLabel);
    }
    
    /**
     * Get formatted timestamp for display
     */
    public String getFormattedTimestamp() {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(timePublished, inputFormatter);
            return dateTime.format(outputFormatter);
        } catch (DateTimeParseException e) {
            return timePublished; // Return original if parsing fails
        }
    }
    
    /**
     * Get summary with word limit
     */
    public String getTruncatedSummary(int wordLimit) {
        if (summary == null) return "";
        String[] words = summary.split("\\s+");
        if (words.length <= wordLimit) return summary;
        
        StringBuilder truncated = new StringBuilder();
        for (int i = 0; i < wordLimit; i++) {
            truncated.append(words[i]).append(" ");
        }
        return truncated.toString().trim() + "...";
    }
    
    /**
     * Get sentiment score as percentage
     */
    public String getSentimentPercentage() {
        return String.format("%.1f%%", Math.abs(sentimentScore) * 100);
    }
    
    /**
     * Get sentiment score with sign
     */
    public String getFormattedSentimentScore() {
        String sign = sentimentScore >= 0 ? "+" : "";
        return sign + String.format("%.3f", sentimentScore);
    }
    
    /**
     * Check if this news is relevant to a specific ticker
     */
    public boolean isRelevantToTicker(String ticker) {
        return tickerSentiments.stream()
                .anyMatch(ts -> ts.getTicker().equalsIgnoreCase(ticker) && ts.getRelevanceScore() > 0.1);
    }
    
    /**
     * Get ticker sentiment for a specific ticker
     */
    public TickerSentiment getTickerSentiment(String ticker) {
        return tickerSentiments.stream()
                .filter(ts -> ts.getTicker().equalsIgnoreCase(ticker))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get most relevant topics (top N by relevance score)
     */
    public List<Topic> getMostRelevantTopics(int limit) {
        return topics.stream()
                .sorted((t1, t2) -> Double.compare(t2.getRelevanceScore(), t1.getRelevanceScore()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Get tickers with highest sentiment scores
     */
    public List<TickerSentiment> getMostPositiveTickers(int limit) {
        return tickerSentiments.stream()
                .sorted((ts1, ts2) -> Double.compare(ts2.getSentimentScore(), ts1.getSentimentScore()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Get average sentiment score for this news across all tickers
     */
    public double getAverageTickerSentiment() {
        if (tickerSentiments.isEmpty()) return 0.0;
        return tickerSentiments.stream()
                .mapToDouble(TickerSentiment::getSentimentScore)
                .average()
                .orElse(0.0);
    }
    
    /**
     * Get the highest relevance topic
     */
    public Topic getMostRelevantTopic() {
        return topics.stream()
                .max((t1, t2) -> Double.compare(t1.getRelevanceScore(), t2.getRelevanceScore()))
                .orElse(null);
    }
    
    // ============ DATABASE OPERATIONS ============
    
    /**
     * Get all news from database
     */
    public static List<News> getAllNews() {
        List<News> newsList = new ArrayList<>();
        String sql = "SELECT * FROM news_data ORDER BY time_published DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                News news = new News(
                    rs.getString("title"),
                    rs.getString("url"),
                    rs.getString("time_published"),
                    rs.getString("summary"),
                    rs.getString("source"),
                    rs.getString("sentiment_label"),
                    rs.getDouble("sentiment_score")
                );
                
                // Load related data
                loadTopics(news, conn);
                loadTickerSentiments(news, conn);
                
                newsList.add(news);
            }
        } catch (SQLException e) {
            System.err.println("Error loading all news: " + e.getMessage());
        }
        
        return newsList;
    }
    
    /**
     * Get news by URL (primary key)
     */
    public static News getNewsByUrl(String url) {
        String sql = "SELECT * FROM news_data WHERE url = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, url);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                News news = new News(
                    rs.getString("title"),
                    rs.getString("url"),
                    rs.getString("time_published"),
                    rs.getString("summary"),
                    rs.getString("source"),
                    rs.getString("sentiment_label"),
                    rs.getDouble("sentiment_score")
                );
                
                loadTopics(news, conn);
                loadTickerSentiments(news, conn);
                
                return news;
            }
        } catch (SQLException e) {
            System.err.println("Error loading news by URL: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get news related to a specific ticker
     */
    public static List<News> getNewsByTicker(String ticker) {
        List<News> newsList = new ArrayList<>();
        String sql = """
            SELECT DISTINCT nd.* FROM news_data nd
            JOIN news_tickersentiment nts ON nd.url = nts.url
            WHERE nts.ticker = ?
            ORDER BY nd.time_published DESC
        """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ticker.toUpperCase());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                News news = new News(
                    rs.getString("title"),
                    rs.getString("url"),
                    rs.getString("time_published"),
                    rs.getString("summary"),
                    rs.getString("source"),
                    rs.getString("sentiment_label"),
                    rs.getDouble("sentiment_score")
                );
                
                loadTopics(news, conn);
                loadTickerSentiments(news, conn);
                
                newsList.add(news);
            }
        } catch (SQLException e) {
            System.err.println("Error loading news by ticker: " + e.getMessage());
        }
        
        return newsList;
    }
    
    /**
     * Get news by sentiment
     */
    public static List<News> getNewsBySentiment(String sentimentLabel) {
        List<News> newsList = new ArrayList<>();
        String sql = "SELECT * FROM news_data WHERE sentiment_label = ? ORDER BY time_published DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sentimentLabel);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                News news = new News(
                    rs.getString("title"),
                    rs.getString("url"),
                    rs.getString("time_published"),
                    rs.getString("summary"),
                    rs.getString("source"),
                    rs.getString("sentiment_label"),
                    rs.getDouble("sentiment_score")
                );
                
                loadTopics(news, conn);
                loadTickerSentiments(news, conn);
                
                newsList.add(news);
            }
        } catch (SQLException e) {
            System.err.println("Error loading news by sentiment: " + e.getMessage());
        }
        
        return newsList;
    }
    
    /**
     * Get recent news (last N days)
     */
    public static List<News> getRecentNews(int days) {
        List<News> newsList = new ArrayList<>();
        String sql = """
            SELECT * FROM news_data 
            WHERE datetime(substr(time_published, 1, 4) || '-' || 
                          substr(time_published, 5, 2) || '-' || 
                          substr(time_published, 7, 2) || ' ' ||
                          substr(time_published, 10, 2) || ':' || 
                          substr(time_published, 12, 2) || ':' || 
                          substr(time_published, 14, 2)) > datetime('now', '-' || ? || ' days')
            ORDER BY time_published DESC
        """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, days);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                News news = new News(
                    rs.getString("title"),
                    rs.getString("url"),
                    rs.getString("time_published"),
                    rs.getString("summary"),
                    rs.getString("source"),
                    rs.getString("sentiment_label"),
                    rs.getDouble("sentiment_score")
                );
                
                loadTopics(news, conn);
                loadTickerSentiments(news, conn);
                
                newsList.add(news);
            }
        } catch (SQLException e) {
            System.err.println("Error loading recent news: " + e.getMessage());
        }
        
        return newsList;
    }
    
    /**
     * Search news by title or summary
     */
    public static List<News> searchNews(String keyword) {
        List<News> newsList = new ArrayList<>();
        String sql = """
            SELECT * FROM news_data 
            WHERE title LIKE ? OR summary LIKE ?
            ORDER BY time_published DESC
        """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                News news = new News(
                    rs.getString("title"),
                    rs.getString("url"),
                    rs.getString("time_published"),
                    rs.getString("summary"),
                    rs.getString("source"),
                    rs.getString("sentiment_label"),
                    rs.getDouble("sentiment_score")
                );
                
                loadTopics(news, conn);
                loadTickerSentiments(news, conn);
                
                newsList.add(news);
            }
        } catch (SQLException e) {
            System.err.println("Error searching news: " + e.getMessage());
        }
        
        return newsList;
    }
    
    /**
     * Get top news sources
     */
    public static Map<String, Integer> getTopSources(int limit) {
        Map<String, Integer> sources = new LinkedHashMap<>();
        String sql = """
            SELECT source, COUNT(*) as count 
            FROM news_data 
            GROUP BY source 
            ORDER BY count DESC 
            LIMIT ?
        """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                sources.put(rs.getString("source"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading top sources: " + e.getMessage());
        }
        
        return sources;
    }
    
    /**
     * Get sentiment distribution
     */
    public static Map<String, Integer> getSentimentDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        String sql = """
            SELECT sentiment_label, COUNT(*) as count 
            FROM news_data 
            GROUP BY sentiment_label
        """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                distribution.put(rs.getString("sentiment_label"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading sentiment distribution: " + e.getMessage());
        }
        
        return distribution;
    }
    
    /**
     * Get news count by ticker
     */
    public static Map<String, Integer> getNewsCountByTicker(int limit) {
        Map<String, Integer> tickerCounts = new LinkedHashMap<>();
        String sql = """
            SELECT ticker, COUNT(*) as count 
            FROM news_tickersentiment 
            GROUP BY ticker 
            ORDER BY count DESC 
            LIMIT ?
        """;
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tickerCounts.put(rs.getString("ticker"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading news count by ticker: " + e.getMessage());
        }
        
        return tickerCounts;
    }
    
    /**
     * Fetch and store new news data (silent operation for GUI)
     * @param ticker the stock symbol to fetch news for
     * @return true if successful, false otherwise
     */
    public static boolean fetchAndStoreNews(String ticker) {
        try {
            NewsManager newsManager = new NewsManager();
            newsManager.fetchAndStore(ticker);
            return true;
        } catch (Exception e) {
            // Silent operation for GUI - just return false on error
            return false;
        }
    }
    
    // ============ HELPER METHODS ============
    
    /**
     * Load topics for a news item
     */
    private static void loadTopics(News news, Connection conn) throws SQLException {
        String sql = "SELECT * FROM news_topics WHERE url = ? ORDER BY relevance_score DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, news.getUrl());
            ResultSet rs = stmt.executeQuery();
            
            List<Topic> topics = new ArrayList<>();
            while (rs.next()) {
                topics.add(new Topic(
                    rs.getString("topic"),
                    rs.getDouble("relevance_score")
                ));
            }
            news.setTopics(topics);
        }
    }
    
    /**
     * Load ticker sentiments for a news item
     */
    private static void loadTickerSentiments(News news, Connection conn) throws SQLException {
        String sql = "SELECT * FROM news_tickersentiment WHERE url = ? ORDER BY relevance_score DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, news.getUrl());
            ResultSet rs = stmt.executeQuery();
            
            List<TickerSentiment> tickerSentiments = new ArrayList<>();
            while (rs.next()) {
                tickerSentiments.add(new TickerSentiment(
                    rs.getString("ticker"),
                    rs.getDouble("relevance_score"),
                    rs.getDouble("sentiment_score"),
                    rs.getString("sentiment_label")
                ));
            }
            news.setTickerSentiments(tickerSentiments);
        }
    }
    
    // ============ ANALYSIS METHODS ============
    
    /**
     * Generate detailed analysis report for a ticker
     */
    public static String generateNewsAnalysisReport(String ticker) {
        List<News> tickerNews = getNewsByTicker(ticker);
        if (tickerNews.isEmpty()) {
            return "No news data available for " + ticker;
        }
        
        StringBuilder report = new StringBuilder();
        report.append("=== NEWS ANALYSIS REPORT FOR ").append(ticker).append(" ===\n\n");
        
        // Overall statistics
        int bullishCount = 0, bearishCount = 0, neutralCount = 0;
        double totalSentiment = 0;
        double totalRelevance = 0;
        
        for (News news : tickerNews) {
            TickerSentiment ts = news.getTickerSentiment(ticker);
            if (ts != null) {
                if (ts.isBullish()) bullishCount++;
                else if (ts.isBearish()) bearishCount++;
                else neutralCount++;
                totalSentiment += ts.getSentimentScore();
                totalRelevance += ts.getRelevanceScore();
            }
        }
        
        double avgSentiment = tickerNews.isEmpty() ? 0 : totalSentiment / tickerNews.size();
        double avgRelevance = tickerNews.isEmpty() ? 0 : totalRelevance / tickerNews.size();
        
        report.append("Total News Articles: ").append(tickerNews.size()).append("\n");
        report.append("Sentiment Distribution:\n");
        report.append("  • Bullish: ").append(bullishCount).append(" (")
              .append(String.format("%.1f%%", (bullishCount * 100.0) / tickerNews.size())).append(")\n");
        report.append("  • Bearish: ").append(bearishCount).append(" (")
              .append(String.format("%.1f%%", (bearishCount * 100.0) / tickerNews.size())).append(")\n");
        report.append("  • Neutral: ").append(neutralCount).append(" (")
              .append(String.format("%.1f%%", (neutralCount * 100.0) / tickerNews.size())).append(")\n");
        report.append("Average Sentiment Score: ").append(String.format("%.3f", avgSentiment)).append("\n");
        report.append("Average Relevance Score: ").append(String.format("%.3f", avgRelevance)).append("\n\n");
        
        // Market sentiment assessment
        String marketSentiment = "Neutral";
        if (avgSentiment > 0.1) marketSentiment = "Bullish";
        else if (avgSentiment < -0.1) marketSentiment = "Bearish";
        
        report.append("Overall Market Sentiment: ").append(marketSentiment).append("\n");
        report.append("Sentiment Confidence: ");
        if (avgRelevance > 0.7) report.append("High");
        else if (avgRelevance > 0.4) report.append("Medium");
        else report.append("Low");
        report.append("\n\n");
        
        // Recent news summary
        report.append("=== RECENT NEWS (Top 5) ===\n");
        tickerNews.stream().limit(5).forEach(news -> {
            TickerSentiment ts = news.getTickerSentiment(ticker);
            report.append("• ").append(news.getTitle()).append("\n");
            report.append("  Source: ").append(news.getSource());
            report.append(" | Time: ").append(news.getFormattedTimestamp());
            if (ts != null) {
                report.append(" | Sentiment: ").append(ts.getSentimentLabel())
                      .append(" (").append(String.format("%.3f", ts.getSentimentScore())).append(")");
                report.append(" | Relevance: ").append(String.format("%.2f", ts.getRelevanceScore()));
            }
            report.append("\n");
            report.append("  ").append(news.getTruncatedSummary(25)).append("\n\n");
        });
        
        return report.toString();
    }
    
    /**
     * Get sentiment trend for a ticker over time
     */
    public static Map<String, Double> getSentimentTrend(String ticker, int days) {
        Map<String, Double> trend = new LinkedHashMap<>();
        List<News> recentNews = getNewsByTicker(ticker).stream()
                .filter(news -> {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
                        LocalDateTime newsTime = LocalDateTime.parse(news.getTimePublished(), formatter);
                        return newsTime.isAfter(LocalDateTime.now().minusDays(days));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
        
        // Group by date and calculate average sentiment
        Map<String, List<Double>> dailySentiments = new HashMap<>();
        for (News news : recentNews) {
            TickerSentiment ts = news.getTickerSentiment(ticker);
            if (ts != null) {
                String date = news.getTimePublished().substring(0, 8); // YYYYMMDD
                dailySentiments.computeIfAbsent(date, k -> new ArrayList<>()).add(ts.getSentimentScore());
            }
        }
        
        for (Map.Entry<String, List<Double>> entry : dailySentiments.entrySet()) {
            double avgSentiment = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            trend.put(entry.getKey(), avgSentiment);
        }
        
        return trend;
    }
    
    // ============ OVERRIDES ============
    
    @Override
    public String toString() {
        return String.format("News{title='%s', source='%s', sentiment=%s (%.3f), time='%s', topics=%d, tickers=%d}", 
            title, source, sentimentLabel, sentimentScore, getFormattedTimestamp(), 
            topics.size(), tickerSentiments.size());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        News news = (News) obj;
        return Objects.equals(url, news.url);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
