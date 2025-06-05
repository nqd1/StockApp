package oop.grp1.Control.DataFetcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import oop.grp1.Control.DBManager.ChatbotManager;
import oop.grp1.Model.ChatResponse;

import java.io.IOException;
import java.util.UUID;

public class Chatbot {
    private final String apiKey;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final ChatbotManager chatbotManager;
    private String session;

    public Chatbot() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("GEMINI_API_KEY");
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.chatbotManager = new ChatbotManager();
        this.session = UUID.randomUUID().toString();
    }

    private String buildStockQuery(String userQuery) {
        return """
            Bạn là một chuyên gia tư vấn chứng khoán chuyên nghiệp.
            Hãy trả lời câu hỏi sau một cách chính xác, chi tiết, thân thiện và hữu ích.
            Nếu câu hỏi liên quan đến phân tích kỹ thuật, phân tích cơ bản,
            tin tức thị trường, hoặc chiến lược đầu tư, hãy cung cấp thông tin
            đầy đủ và lời khuyên thực tế. Luôn nhắc nhở về rủi ro đầu tư.
                
            Câu hỏi: %s
                """.formatted(userQuery);
    }

    public ChatResponse processQuery(String query) {
        long searchStartTime = System.currentTimeMillis();

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

            String requestBody = String.format("""
                {
                    "contents": [{
                        "parts": [{
                            "text": "%s"
                        }]
                    }]
                }""", query
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
                    session
            );

            chatbotManager.saveResponse(chatResponse);

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
}

