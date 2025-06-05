 package oop.grp1.Control.DBManager;

import java.sql.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import oop.grp1.Model.ChatResponse;

public class ChatbotManager extends DBManager {
    private static final String dbUrl = "jdbc:sqlite:stockAV.db";

    public ChatbotManager() {
        super(dbUrl); 
        initializeDatabase();
    }

    private void initializeDatabase() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS chatbot_response (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                searchTime INTEGER NOT NULL,
                responseTime INTEGER NOT NULL,
                responseContent TEXT NOT NULL,
                session TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database: " + e.getMessage(), e);
        }
    }

    public void saveToDB(ChatResponse response) {
        String insertSQL = """
            INSERT INTO chatbot_response (searchTime, responseTime, responseContent, session)
            VALUES (?, ?, ?, ?)
        """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setLong(1, response.getSearchTime());
            pstmt.setLong(2, response.getResponseTime());
            pstmt.setString(3, response.getResponseContent());
            pstmt.setString(4, response.getSession());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving response to database: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveToDB(String json) {
        JsonObject jsonObj = JsonParser.parseString(json).getAsJsonObject();
        ChatResponse response = new ChatResponse(
            jsonObj.get("searchTime").getAsLong(),
            jsonObj.get("responseTime").getAsLong(),
            jsonObj.get("responseContent").getAsString(),
            jsonObj.get("session").getAsString()
        );
        saveToDB(response);
    }
}
