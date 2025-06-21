package oop.grp1.Control.DataFetcher;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import io.github.cdimascio.dotenv.Dotenv;
import oop.grp1.Control.DBManager.ChatbotManager;
import oop.grp1.Model.ChatResponse;
import oop.grp1.Model.Stock;

import java.util.*;
import java.util.stream.Collectors;

public class SmartChatbot {
    private final GoogleAiGeminiChatModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final ChatbotManager chatbotManager;
    private String session;
    private static final Dotenv dotenv = Dotenv.load();

    // Template cho RAG
    private static final PromptTemplate RAG_PROMPT_TEMPLATE = PromptTemplate.from("""
        Bạn là một chuyên gia tư vấn chứng khoán chuyên nghiệp. 
        Hãy trả lời câu hỏi dựa trên thông tin được cung cấp và kiến thức chuyên môn.
        
        Thông tin liên quan từ cơ sở dữ liệu:
        {{information}}
        
        Câu hỏi: {{question}}
        
        Hướng dẫn trả lời:
        - Đưa ra phân tích chính xác dựa trên dữ liệu thực
        - Giải thích các chỉ số và xu hướng một cách dễ hiểu
        - Cung cấp lời khuyên đầu tư thực tế
        - Luôn cảnh báo về rủi ro đầu tư
        - Sử dụng định dạng markdown để trình bày rõ ràng
        """);

    public SmartChatbot() {
        // Khởi tạo Gemini model
        this.chatModel = GoogleAiGeminiChatModel.builder()
                .apiKey(dotenv.get("GEMINI_API_KEY"))
                .modelName("gemini-1.5-flash")
                .build();

        // Khởi tạo embedding model
        this.embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        // Khởi tạo in-memory vector store
        this.embeddingStore = new InMemoryEmbeddingStore<>();

        this.chatbotManager = new ChatbotManager();
        this.session = UUID.randomUUID().toString();

        // Tải dữ liệu vào vector store
        loadStockDataIntoVectorStore();
    }

    /**
     * Tải tất cả dữ liệu cổ phiếu vào vector store để tìm kiếm semantic
     */
    private void loadStockDataIntoVectorStore() {
        List<String> tickers = Stock.getAllTickers();
        
        for (String ticker : tickers) {
            try {
                // Lấy thông tin chi tiết cho mỗi ticker
                Stock latestStock = Stock.getLatestStock(ticker);
                if (latestStock != null) {
                    // Tạo document cho mỗi cổ phiếu
                    String stockContent = buildStockDocument(ticker, latestStock);
                    Document document = Document.from(stockContent);
                    
                    // Chia nhỏ document thành segments
                    DocumentSplitter splitter = DocumentSplitters.recursive(300, 50);
                    List<TextSegment> segments = splitter.split(document);
                    
                    // Tạo embeddings và lưu vào store
                    for (TextSegment segment : segments) {
                        Embedding embedding = embeddingModel.embed(segment).content();
                        embeddingStore.add(embedding, segment);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading data for ticker " + ticker + ": " + e.getMessage());
            }
        }
    }

    /**
     * Xây dựng document cho mỗi cổ phiếu
     */
    private String buildStockDocument(String ticker, Stock stock) {
        StringBuilder content = new StringBuilder();
        
        content.append("Ticker: ").append(ticker).append("\n");
        content.append("Detailed Info: ").append(stock.getDetailedInfo()).append("\n");
        
        // Thêm thống kê
        Map<String, Object> stats = Stock.getSummaryStatistics(ticker);
        if (stats != null) {
            content.append("Statistics: ");
            for (Map.Entry<String, Object> entry : stats.entrySet()) {
                content.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
            }
            content.append("\n");
        }
        
        // Thêm thông tin biến động
        List<Stock> volatileStocks = Stock.getMostVolatileStocks(ticker, 3);
        if (volatileStocks != null && !volatileStocks.isEmpty()) {
            content.append("Volatile periods: ");
            for (Stock vs : volatileStocks) {
                content.append(vs.getTimestamp()).append(" (").append(String.format("%.2f", vs.getVolatility())).append("%), ");
            }
            content.append("\n");
        }
        
        return content.toString();
    }

    /**
     * Xử lý câu hỏi với RAG thông minh
     */
    public ChatResponse processQuery(String query) {
        long searchStartTime = System.currentTimeMillis();

        try {
            // Tìm kiếm semantic trong vector store
            Embedding queryEmbedding = embeddingModel.embed(query).content();
            List<EmbeddingMatch<TextSegment>> relevantSegments = embeddingStore.findRelevant(queryEmbedding, 5);
            
            // Tổng hợp thông tin liên quan
            String relevantInformation = relevantSegments.stream()
                    .map(match -> match.embedded().text())
                    .collect(Collectors.joining("\n\n"));
            
            // Nếu không tìm thấy thông tin liên quan, cung cấp thông tin chung
            if (relevantInformation.trim().isEmpty()) {
                relevantInformation = "Các mã cổ phiếu có sẵn: " + String.join(", ", Stock.getAllTickers());
            }
            
            // Tạo prompt với template
            Prompt prompt = RAG_PROMPT_TEMPLATE.apply(Map.of(
                    "information", relevantInformation,
                    "question", query
            ));
            
            long responseStartTime = System.currentTimeMillis();
            
            // Gửi đến Gemini để xử lý
            String response = chatModel.generate(prompt.text());
            
            ChatResponse chatResponse = new ChatResponse(
                    searchStartTime,
                    responseStartTime,
                    response,
                    session,
                    query
            );
            
            // Lưu vào database
            chatbotManager.saveToDB(chatResponse);
            
            return chatResponse;
            
        } catch (Exception e) {
            throw new RuntimeException("Error processing smart query: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật vector store khi có dữ liệu mới
     */
    public void updateVectorStore(String ticker) {
        Stock latestStock = Stock.getLatestStock(ticker);
        if (latestStock != null) {
            String stockContent = buildStockDocument(ticker, latestStock);
            Document document = Document.from(stockContent);
            
            DocumentSplitter splitter = DocumentSplitters.recursive(300, 50);
            List<TextSegment> segments = splitter.split(document);
            
            for (TextSegment segment : segments) {
                Embedding embedding = embeddingModel.embed(segment).content();
                embeddingStore.add(embedding, segment);
            }
        }
    }

    /**
     * Tìm kiếm tickers liên quan đến câu hỏi
     */
    public List<String> findRelevantTickers(String query) {
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.findRelevant(queryEmbedding, 10);
        
        return matches.stream()
                .map(match -> extractTickerFromSegment(match.embedded().text()))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Trích xuất ticker từ text segment
     */
    private String extractTickerFromSegment(String text) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.startsWith("Ticker: ")) {
                return line.substring("Ticker: ".length()).trim();
            }
        }
        return null;
    }

    // Getters and setters
    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
} 