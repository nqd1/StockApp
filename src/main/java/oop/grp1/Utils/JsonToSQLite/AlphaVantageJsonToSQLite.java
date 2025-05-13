package oop.grp1.Utils.JsonToSQLite;

import com.google.gson.*;
import java.sql.*;
import java.util.*;

public class AlphaVantageJsonToSQLite {

    private static final String DB_URL = "jdbc:sqlite:stockAV.db";

    public static void saveJsonToDatabase(String json) {
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

            Connection conn = DriverManager.getConnection(DB_URL);

            String createTable = """
                CREATE TABLE IF NOT EXISTS stock_data (
                    ticker TEXT,
                    volume TEXT,
                    open TEXT,
                    close TEXT,
                    high TEXT,
                    low TEXT,
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
                insertStmt.setString(2, values.get("5. volume").getAsString());
                insertStmt.setString(3, values.get("1. open").getAsString());
                insertStmt.setString(4, values.get("4. close").getAsString());
                insertStmt.setString(5, values.get("2. high").getAsString());
                insertStmt.setString(6, values.get("3. low").getAsString());
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
