package oop.grp1.Control.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        String alterTableSQL = """
            ALTER TABLE chatbot_response ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
        """;

        String alterTableUserQuerySQL = """
            ALTER TABLE chatbot_response ADD COLUMN user_query TEXT;
        """;

        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS chatbot_response (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                searchTime INTEGER NOT NULL,
                responseTime INTEGER NOT NULL,
                responseContent TEXT NOT NULL,
                session TEXT NOT NULL,
                user_query TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """;
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {
            try {
                stmt.execute(alterTableSQL);
            } catch (SQLException e) {
                // Bỏ qua lỗi nếu cột đã tồn tại
            }
            
            try {
                stmt.execute(alterTableUserQuerySQL);
            } catch (SQLException e) {
                // Bỏ qua lỗi nếu cột đã tồn tại
            }
            
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database: " + e.getMessage(), e);
        }
    }

    public void saveToDB(ChatResponse response) {
        String insertSQL = """
            INSERT INTO chatbot_response (searchTime, responseTime, responseContent, session, user_query)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setLong(1, response.getSearchTime());
            pstmt.setLong(2, response.getResponseTime());
            pstmt.setString(3, response.getResponseContent());
            pstmt.setString(4, response.getSession());
            pstmt.setString(5, response.getUserQuery());

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
        
        if (jsonObj.has("userQuery")) {
            response.setUserQuery(jsonObj.get("userQuery").getAsString());
        }
        
        saveToDB(response);
    }
    
    public List<ChatResponse> getChatHistory(String session) {
        List<ChatResponse> history = new ArrayList<>();
        
        String sql = "SELECT * FROM chatbot_response WHERE session = ? ORDER BY created_at";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, session);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ChatResponse response = new ChatResponse(
                    rs.getLong("searchTime"),
                    rs.getLong("responseTime"),
                    rs.getString("responseContent"),
                    rs.getString("session")
                );
                response.setUserQuery(rs.getString("user_query"));
                history.add(response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return history;
    }
}