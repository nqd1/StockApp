package oop.grp1.Utils;

import com.google.gson.*;
import java.sql.*;
import java.util.*;

public class JsonToSQLite {

    private static final String DB_URL = "jdbc:sqlite:stock.db";

    public static void saveJsonToDatabase(String json) {
        try {
            // Parse JSON đầu vào
            JsonObject jsonObj = JsonParser.parseString(json).getAsJsonObject();
            JsonArray resultsArray = jsonObj.getAsJsonArray("results");

            if (resultsArray == null || resultsArray.size() == 0) {
                System.out.println("No data found in JSON.");
                return;
            }

            Connection conn = DriverManager.getConnection(DB_URL);

            String createTable = """
                CREATE TABLE IF NOT EXISTS stock_data (
                    ticker TEXT PRIMARY KEY,
                    volume TEXT,
                    open TEXT,
                    close TEXT,
                    high TEXT,
                    low TEXT,
                    timestamp TEXT,
                    trade_count TEXT
                );
            """;
            Statement stmt = conn.createStatement();
            stmt.execute(createTable);


            Map<String, JsonObject> newDataMap = new HashMap<>();
            for (JsonElement element : resultsArray) {
                JsonObject result = element.getAsJsonObject();
                String ticker = result.get("T").getAsString();

                JsonObject tickerData = newDataMap.getOrDefault(ticker, new JsonObject());
                appendToArray(tickerData, "volume", result.get("v").getAsDouble());
                appendToArray(tickerData, "open", result.get("o").getAsDouble());
                appendToArray(tickerData, "close", result.get("c").getAsDouble());
                appendToArray(tickerData, "high", result.get("h").getAsDouble());
                appendToArray(tickerData, "low", result.get("l").getAsDouble());
                appendToArray(tickerData, "timestamp", result.get("t").getAsLong());
                appendToArray(tickerData, "trade_count", result.get("n").getAsInt());

                newDataMap.put(ticker, tickerData);
            }

            Gson gson = new Gson();

            String selectSQL = "SELECT * FROM stock_data WHERE ticker = ?";
            String insertOrReplace = """
                INSERT OR REPLACE INTO stock_data
                (ticker, volume, open, close, high, low, timestamp, trade_count)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
            """;
            PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
            PreparedStatement insertStmt = conn.prepareStatement(insertOrReplace);

            for (Map.Entry<String, JsonObject> entry : newDataMap.entrySet()) {
                String ticker = entry.getKey();
                JsonObject newValues = entry.getValue();

                JsonObject mergedValues = new JsonObject();

                // Truy vấn dữ liệu cũ nếu có
                selectStmt.setString(1, ticker);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    mergedValues.add("volume", mergeArrays(rs.getString("volume"), newValues.getAsJsonArray("volume")));
                    mergedValues.add("open", mergeArrays(rs.getString("open"), newValues.getAsJsonArray("open")));
                    mergedValues.add("close", mergeArrays(rs.getString("close"), newValues.getAsJsonArray("close")));
                    mergedValues.add("high", mergeArrays(rs.getString("high"), newValues.getAsJsonArray("high")));
                    mergedValues.add("low", mergeArrays(rs.getString("low"), newValues.getAsJsonArray("low")));
                    mergedValues.add("timestamp", mergeArrays(rs.getString("timestamp"), newValues.getAsJsonArray("timestamp")));
                    mergedValues.add("trade_count", mergeArrays(rs.getString("trade_count"), newValues.getAsJsonArray("trade_count")));
                } else {
                    mergedValues = newValues;
                }

                // Thực hiện chèn/gộp
                insertStmt.setString(1, ticker);
                insertStmt.setString(2, gson.toJson(mergedValues.get("volume")));
                insertStmt.setString(3, gson.toJson(mergedValues.get("open")));
                insertStmt.setString(4, gson.toJson(mergedValues.get("close")));
                insertStmt.setString(5, gson.toJson(mergedValues.get("high")));
                insertStmt.setString(6, gson.toJson(mergedValues.get("low")));
                insertStmt.setString(7, gson.toJson(mergedValues.get("timestamp")));
                insertStmt.setString(8, gson.toJson(mergedValues.get("trade_count")));

                insertStmt.executeUpdate();
            }

            conn.close();
            System.out.println("Data successfully merged and saved to SQLite database.");
        } catch (Exception e) {
            System.err.println("Error saving JSON data to SQLite:");
            e.printStackTrace();
        }
    }

    private static void appendToArray(JsonObject obj, String key, Number value) {
        JsonArray array = obj.has(key) ? obj.getAsJsonArray(key) : new JsonArray();
        array.add(value);
        obj.add(key, array);
    }

    private static JsonArray mergeArrays(String oldJson, JsonArray newArray) {
        JsonArray oldArray = new JsonArray();
        try {
            if (oldJson != null && !oldJson.isEmpty()) {
                oldArray = JsonParser.parseString(oldJson).getAsJsonArray();
            }
        } catch (Exception e) {
            // Nếu lỗi parse, bỏ qua array cũ
        }

        for (JsonElement elem : newArray) {
            oldArray.add(elem);
        }
        return oldArray;
    }
}

