package oop.grp1.Control.DBManager;

import com.google.gson.*;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import oop.grp1.Control.DataFetcher.NewsDF;
import oop.grp1.Control.JsonBeautifier;

public class NewsManager extends DBManager {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String DB_URL = dotenv.get("DB_URL");
    public NewsManager() {
        super(DB_URL);
    }

    @Override
    public void fetchAndStore(String ticker) {
        NewsDF newsFetcher = new NewsDF();
        String data = newsFetcher.getNews(ticker);
        System.out.println(JsonBeautifier.beautify(data));
        saveToDB(data);
    }

    @Override
    public void saveToDB(String json) {
        try {
            JsonObject jsonObj = JsonParser.parseString(json).getAsJsonObject();
            JsonArray feeds = jsonObj.getAsJsonArray("feed");

            if (feeds == null || feeds.isEmpty()) {
                return;
            }

            Connection conn = getConnection();

            String createNewsTable = """
                CREATE TABLE IF NOT EXISTS news_data (
                    title TEXT,
                    url TEXT PRIMARY KEY,
                    time_published TEXT,
                    summary TEXT,
                    source TEXT,
                    sentiment_label TEXT,
                    sentiment_score REAL
                );
            """;

            String createTopicTable = """
                CREATE TABLE IF NOT EXISTS news_topics (
                    url TEXT,
                    topic TEXT,
                    relevance_score REAL,
                    FOREIGN KEY (url) REFERENCES news_data(url)
                );
            """;

            String createTickerTable = """
                CREATE TABLE IF NOT EXISTS news_tickersentiment (
                    url TEXT,
                    ticker TEXT,
                    relevance_score REAL,
                    sentiment_score REAL,
                    sentiment_label TEXT,
                    FOREIGN KEY (url) REFERENCES news_data(url)
                );
            """;

            Statement stmt = conn.createStatement();
            stmt.execute(createNewsTable);
            stmt.execute(createTopicTable);
            stmt.execute(createTickerTable);

            for (JsonElement item : feeds) {
                JsonObject newsItem = item.getAsJsonObject();
                String url = newsItem.get("url").getAsString();

                String checkSQL = "SELECT 1 FROM news_data WHERE url = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
                checkStmt.setString(1, url);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    continue;
                }

                PreparedStatement insertStmt = conn.prepareStatement("""
                    INSERT INTO news_data (title, url, time_published, summary, source, sentiment_label, sentiment_score)
                    VALUES (?, ?, ?, ?, ?, ?, ?);
                """);
                insertStmt.setString(1, newsItem.get("title").getAsString());
                insertStmt.setString(2, url);
                insertStmt.setString(3, newsItem.get("time_published").getAsString());
                insertStmt.setString(4, newsItem.get("summary").getAsString());
                insertStmt.setString(5, newsItem.get("source").getAsString());
                insertStmt.setString(6, newsItem.get("overall_sentiment_label").getAsString());
                insertStmt.setDouble(7, newsItem.get("overall_sentiment_score").getAsDouble());
                insertStmt.executeUpdate();

                JsonArray topics = newsItem.getAsJsonArray("topics");
                if (topics != null) {
                    for (JsonElement topicEl : topics) {
                        JsonObject topicObj = topicEl.getAsJsonObject();
                        PreparedStatement topicStmt = conn.prepareStatement("""
                            INSERT INTO news_topics (url, topic, relevance_score)
                            VALUES (?, ?, ?);
                        """);
                        topicStmt.setString(1, url);
                        topicStmt.setString(2, topicObj.get("topic").getAsString());
                        topicStmt.setDouble(3, topicObj.get("relevance_score").getAsDouble());
                        topicStmt.executeUpdate();
                    }
                }

                JsonArray tickers = newsItem.getAsJsonArray("ticker_sentiment");
                if (tickers != null) {
                    for (JsonElement tickerEl : tickers) {
                        JsonObject tickerObj = tickerEl.getAsJsonObject();
                        PreparedStatement tickerStmt = conn.prepareStatement("""
                            INSERT INTO news_tickersentiment (url, ticker, relevance_score, sentiment_score, sentiment_label)
                            VALUES (?, ?, ?, ?, ?);
                        """);
                        tickerStmt.setString(1, url);
                        tickerStmt.setString(2, tickerObj.get("ticker").getAsString());
                        tickerStmt.setDouble(3, tickerObj.get("relevance_score").getAsDouble());
                        tickerStmt.setDouble(4, tickerObj.get("ticker_sentiment_score").getAsDouble());
                        tickerStmt.setString(5, tickerObj.get("ticker_sentiment_label").getAsString());
                        tickerStmt.executeUpdate();
                    }
                }
            }
            conn.close();
            System.out.println("Data saved to SQLite database successfully.");
        } catch (Exception e) {
            System.err.println("Error while saving JSON to SQLite:");
            e.printStackTrace();
        }
    }
}
