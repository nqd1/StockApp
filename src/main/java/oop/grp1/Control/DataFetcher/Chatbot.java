package oop.grp1.Control.DataFetcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import oop.grp1.Control.DBManager.ChatbotManager;
import oop.grp1.Model.ChatResponse;
import oop.grp1.Model.Stock;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Chatbot {
    private final String apiKey;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final ChatbotManager chatbotManager;
    private String session;
    private static final Dotenv dotenv = Dotenv.load();
    private static final String DB_URL = dotenv.get("DB_URL");

    public Chatbot() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("GEMINI_API_KEY");
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.chatbotManager = new ChatbotManager();
        this.session = UUID.randomUUID().toString();
    }

    /**
     * Truy vấn thông tin cổ phiếu từ cơ sở dữ liệu
     * @param ticker Mã cổ phiếu cần truy vấn
     * @return Thông tin cổ phiếu dạng chuỗi
     */
    private String getStockInfo(String ticker) {
        StringBuilder stockInfo = new StringBuilder();
        
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Lấy thông tin cổ phiếu mới nhất
            Stock latestStock = Stock.getLatestStock(ticker);
            if (latestStock != null) {
                stockInfo.append("Thông tin cổ phiếu ").append(ticker).append(":\n");
                stockInfo.append(latestStock.getDetailedInfo()).append("\n\n");
            }
            
            // Thêm thống kê tóm tắt
            Map<String, Object> stats = Stock.getSummaryStatistics(ticker);
            if (stats != null && !stats.isEmpty()) {
                stockInfo.append("Thống kê tóm tắt:\n");
                for (Map.Entry<String, Object> entry : stats.entrySet()) {
                    stockInfo.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
                stockInfo.append("\n");
            }
            
            // Thêm các cổ phiếu có biến động lớn nhất
            List<Stock> volatileStocks = Stock.getMostVolatileStocks(ticker, 3);
            if (volatileStocks != null && !volatileStocks.isEmpty()) {
                stockInfo.append("Thời điểm biến động mạnh nhất:\n");
                for (Stock stock : volatileStocks) {
                    stockInfo.append("- ")
                            .append(stock.getTimestamp())
                            .append(": Biến động ")
                            .append(String.format("%.2f", stock.getVolatility()))
                            .append("%, Giá từ ")
                            .append(Stock.formatPrice(stock.getLow()))
                            .append(" đến ")
                            .append(Stock.formatPrice(stock.getHigh()))
                            .append("\n");
                }
                stockInfo.append("\n");
            }
            
        } catch (SQLException e) {
            stockInfo.append("Lỗi khi truy vấn dữ liệu: ").append(e.getMessage());
        }
        
        return stockInfo.toString();
    }
    
    /**
     * Truy vấn danh sách các mã cổ phiếu có trong database
     * @return Danh sách mã cổ phiếu
     */
    private List<String> getAvailableTickers() {
        List<String> tickers = Stock.getAllTickers();
        return tickers != null ? tickers : new ArrayList<>();
    }
    
    /**
     * Phân tích câu hỏi để tìm mã cổ phiếu
     * @param query Câu hỏi của người dùng
     * @return Mã cổ phiếu (nếu tìm thấy)
     */
    private String extractTickerFromQuery(String query) {
        // Chuẩn hóa câu hỏi: chuyển thành chữ hoa
        String normalizedQuery = query.toUpperCase();
        
        // Lấy danh sách mã cổ phiếu có trong hệ thống
        List<String> availableTickers = getAvailableTickers();
        
        // Tìm kiếm từng mã cổ phiếu trong câu hỏi
        for (String ticker : availableTickers) {
            if (normalizedQuery.contains(ticker)) {
                return ticker;
            }
        }
        
        return null;
    }

    private String buildStockQuery(String userQuery, String contextInfo) {
        return """
            Bạn là một chuyên gia tư vấn chứng khoán chuyên nghiệp.
            Hãy trả lời câu hỏi sau một cách chính xác, chi tiết, thân thiện và hữu ích.
            Nếu câu hỏi liên quan đến phân tích kỹ thuật, phân tích cơ bản,
            tin tức thị trường, hoặc chiến lược đầu tư, hãy cung cấp thông tin
            đầy đủ và lời khuyên thực tế. Luôn nhắc nhở về rủi ro đầu tư.
            
            Dưới đây là dữ liệu liên quan từ cơ sở dữ liệu của chúng tôi:
            %s
                
            Câu hỏi: %s
                """.formatted(contextInfo, userQuery);
    }

    public ChatResponse processQuery(String query) {
        long searchStartTime = System.currentTimeMillis();

        try {
            // Phân tích câu hỏi để tìm mã cổ phiếu
            String ticker = extractTickerFromQuery(query);
            String contextInfo = "";
            
            // Nếu tìm thấy mã cổ phiếu, lấy thông tin chi tiết
            if (ticker != null) {
                contextInfo = getStockInfo(ticker);
            } else {
                // Nếu không tìm thấy mã cụ thể, liệt kê các mã có sẵn
                List<String> availableTickers = getAvailableTickers();
                contextInfo = "Các mã cổ phiếu có sẵn trong hệ thống: " + String.join(", ", availableTickers);
            }
            
            // Xây dựng câu truy vấn RAG
            String ragQuery = buildStockQuery(query, contextInfo);
            
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

            String requestBody = String.format("""
                {
                    "contents": [{
                        "parts": [{
                            "text": "%s"
                        }]
                    }]
                }""", ragQuery.replace("\"", "\\\"").replace("\n", "\\n")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                    .build();

            long responseStartTime = System.currentTimeMillis();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            String responseContent = extractResponseContent(responseBody);

            ChatResponse chatResponse = new ChatResponse(
                    searchStartTime,
                    responseStartTime,
                    responseContent,
                    session,
                    query
            );

            // Lưu vào database
            chatbotManager.saveToDB(chatResponse);

            return chatResponse;
        } catch (IOException e) {
            throw new RuntimeException("Error processing query: " + e.getMessage(), e);
        }
    }

    public String extractResponseContent(String responseBody) throws JsonProcessingException {
        return objectMapper.readTree(responseBody)
                .path("candidates")
                .path(0)
                .path("content")
                .path("parts")
                .path(0)
                .path("text")
                .asText();
    }

    public void setSession(String session) {
        this.session = session;
    }
    
    public String getSession() {
        return this.session;
    }
}

