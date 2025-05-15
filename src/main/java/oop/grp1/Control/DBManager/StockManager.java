package oop.grp1.Control.DBManager;

import com.google.gson.*;

import oop.grp1.Control.JsonBeautifier;
import oop.grp1.Control.DataFetcher.StockDF;

import java.sql.*;
import java.util.*;

public class StockManager extends DBManager {
    private static final String DB_URL = "jdbc:sqlite:stockAV.db";

    public StockManager() {
        super(DB_URL);
    }

    @Override
    public void fetchAndStore(String symbol) {
        StockDF dataFetcher = new StockDF();
        String data = dataFetcher.fetch(symbol);
        System.out.println(JsonBeautifier.beautify(data));
        saveToDB(data);
    }

    @Override
    public void saveToDB(String json) {
        try {
            JsonObject jsonObj = JsonParser.parseString(json).getAsJsonObject();

            String ticker = jsonObj
                .getAsJsonObject("Meta Data")
                .get("2. Symbol")
                .getAsString();

            JsonObject timeSeries = jsonObj.getAsJsonObject("Time Series (5min)");
            if (timeSeries == null || timeSeries.entrySet().isEmpty()) {
                System.out.println("No data found in JSON.");
                return;
            }

            Connection conn = getConnection();

            String createTable = """
                CREATE TABLE IF NOT EXISTS stock_data (
                    ticker TEXT,
                    volume INTEGER,
                    open REAL,
                    close REAL,
                    high REAL,
                    low REAL,
                    timestamp TEXT,
                    PRIMARY KEY (ticker, timestamp)
                );
            """;

            Statement stmt = conn.createStatement();
            stmt.execute(createTable);

            for (Map.Entry<String, JsonElement> entry : timeSeries.entrySet()) {
                String time = entry.getKey();
                JsonObject values = entry.getValue().getAsJsonObject();

                // Check if data for this timestamp already exists
                String checkSQL = "SELECT 1 FROM stock_data WHERE ticker = ? AND timestamp = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
                checkStmt.setString(1, ticker);
                checkStmt.setString(2, time);
                ResultSet rsCheck = checkStmt.executeQuery();

                if (rsCheck.next()) {
                    // Data already exists, skip insertion
                    continue;
                }

                // Prepare insertion for new data
                PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO stock_data (ticker, volume, open, close, high, low, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?);"
                );
                insertStmt.setString(1, ticker);
                insertStmt.setInt(2, Integer.parseInt(values.get("5. volume").getAsString()));
                insertStmt.setDouble(3, Double.parseDouble(values.get("1. open").getAsString()));
                insertStmt.setDouble(4, Double.parseDouble(values.get("4. close").getAsString()));
                insertStmt.setDouble(5, Double.parseDouble(values.get("2. high").getAsString()));
                insertStmt.setDouble(6, Double.parseDouble(values.get("3. low").getAsString()));
                insertStmt.setString(7, time);
                insertStmt.executeUpdate();
            }

            conn.close();
            System.out.println("Data saved to SQLite database successfully.");
        } catch (Exception e) {
            System.err.println("Error while saving JSON to SQLite:");
            e.printStackTrace();
        }
    }
}
