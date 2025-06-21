package oop.grp1.GUI;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import oop.grp1.Control.DataFetcher.Chatbot;
import oop.grp1.Control.DBManager.ChatbotManager;
import oop.grp1.Model.ChatResponse;

import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;

import java.util.List;


public class ChatbotPage extends VBox {
    private final VBox messageContainer;
    private final TextField inputField;
    private final Button sendButton;
    private final ScrollPane scrollPane;

    Chatbot chatbot = new Chatbot();
    ChatbotManager chatbotManager = new ChatbotManager();
    Gson gson = new Gson();

    public ChatbotPage() {
        // Khung chứa tin nhắn (VBox)
        messageContainer = new VBox(10);
        messageContainer.setPadding(new Insets(10));

        // ScrollPane chứa VBox tin nhắn
        scrollPane = new ScrollPane(messageContainer);

        // Tự động cuộn xuống khi có tin nhắn mới
        messageContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            scrollPane.setVvalue(1.0);
        });
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Trường nhập liệu
        inputField = new TextField();
        inputField.setPromptText("Hỏi tôi về cổ phiếu (VD: 'Phân tích AAPL hôm nay', 'Xu hướng thị trường')...");
        inputField.setFont(Font.font("Arial", 14));
        inputField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 5px;");
        HBox.setHgrow(inputField, Priority.ALWAYS);

        // Nút gửi
        sendButton = new Button("🚀 Gửi");
        sendButton.setFont(Font.font("Arial", 14));
        sendButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 5px;");
        sendButton.setOnMouseEntered(e -> sendButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-border-radius: 5px;"));
        sendButton.setOnMouseExited(e -> sendButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 5px;"));

        // Gửi khi bấm Enter hoặc click Send
        sendButton.setOnAction(e -> handleSendMessage());
        inputField.setOnAction(e -> handleSendMessage());

        // Layout nhập liệu
        HBox inputLayout = new HBox(10, inputField, sendButton);
        inputLayout.setPadding(new Insets(10));
        inputLayout.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #dcdcdc; -fx-border-radius: 5px;");

        // Giao diện chính
        this.getChildren().addAll(scrollPane, inputLayout);
        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.getStyleClass().add("page-background");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.GRAY);
        shadow.setRadius(10);
        this.setEffect(shadow);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        addWelcomeMessage();
        
        loadChatHistory();
    }
    
    private void loadChatHistory() {
        String currentSession = chatbot.getSession();
        
        List<ChatResponse> history = chatbotManager.getChatHistory(currentSession);
        
        for (ChatResponse response : history) {
            if (response.getUserQuery() != null && !response.getUserQuery().isEmpty()) {
                addMessage(response.getUserQuery(), true);
            }
            
            String html = MarkdownUtils.convertToHtml(response.getResponseContent());
            displayMessageAsWebView(html, false);
        }
    }
    
    private void displayMessageAsWebView(String html, boolean isUser) {
        WebView webView = new WebView();
        webView.prefWidthProperty().bind(messageContainer.widthProperty().subtract(30));
        webView.setMinHeight(50);
        webView.setPrefHeight(Region.USE_COMPUTED_SIZE);
        webView.setMaxHeight(Double.MAX_VALUE);

        String customCSS = "<style>" +
                          "body { width: 95%; margin: 0; padding: 15px; word-wrap: break-word; overflow: hidden; " +
                          "font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; " +
                          (isUser ? "background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white;" :
                                   "background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); color: #2c3e50;") +
                          "border-radius: 12px; border: 1px solid #dee2e6; " +
                          "box-shadow: 0 2px 8px rgba(0,0,0,0.1); }" +
                          "h1, h2, h3 { margin-top: 0; } " +
                          "code { background: " + (isUser ? "rgba(255,255,255,0.2)" : "#f8f9fa") + "; " +
                          "padding: 2px 6px; border-radius: 4px; font-family: 'Courier New', monospace; } " +
                          "pre { background: " + (isUser ? "rgba(255,255,255,0.1)" : "#f8f9fa") + "; " +
                          "padding: 10px; border-radius: 5px; overflow-x: auto; } " +
                          "ul, ol { padding-left: 20px; } " +
                          "li { margin: 5px 0; } " +
                          "blockquote { border-left: 4px solid " + (isUser ? "#fff" : "#3498db") + "; " +
                          "margin: 10px 0; padding-left: 15px; font-style: italic; } " +
                          "hr { border: none; border-top: 1px solid " + (isUser ? "rgba(255,255,255,0.3)" : "#dee2e6") + "; margin: 15px 0; }" +
                          "</style>";

        String htmlContent = MarkdownUtils.convertToHtml(html);
        webView.getEngine().loadContent(customCSS + "<div id='content'>" + htmlContent + "</div>");

        HBox messageBox = new HBox(webView);
        messageBox.setPadding(new Insets(5));
        if (isUser) {
            messageBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        }
        HBox.setHgrow(webView, Priority.ALWAYS);
        messageContainer.getChildren().add(messageBox);

        webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                Platform.runLater(() -> {
                    try {
                        Object result = webView.getEngine().executeScript(
                            "Math.max(document.body.scrollHeight, document.documentElement.scrollHeight, " +
                            "document.body.offsetHeight, document.documentElement.offsetHeight);"
                        );
                        if (result instanceof Number) {
                            double height = ((Number) result).doubleValue();
                            webView.setPrefHeight(height + 20);
                            webView.getEngine().executeScript(
                                "document.body.style.overflow='hidden';" +
                                "document.documentElement.style.overflow='hidden';"
                            );
                        }
                    } catch (Exception e) {
                    }
                });
            }
        });
    }

    private void addWelcomeMessage() {
        String welcomeMessage = """
            # Chào mừng đến với Stock Assistant!
            
            **Tôi là trợ lý AI chuyên về thị trường chứng khoán** với khả năng:
            
            ## Tính năng chính:
            • **Tìm mã cổ phiếu** - Nhận diện ticker trong câu hỏi
            • **Phân tích dữ liệu** - Kết hợp thông tin từ database  
            • **Tư vấn thông minh** - Powered by Gemini AI
            
            ## Ví dụ câu hỏi:
            - *"Cho tôi thông tin về AAPL"*
            - *"Phân tích xu hướng MSFT"*
            - *"TSLA có nên đầu tư không?"*
            
            ---
            *Hãy hỏi tôi về bất kỳ mã cổ phiếu nào!*
            """;
                
        displayMessageAsWebView(welcomeMessage, false);
    }

    private void handleSendMessage() {
        String userMessage = inputField.getText().trim();
        if (!userMessage.isEmpty()) {
            // Hiển thị tin nhắn người dùng
            addMessage(userMessage, true);
            inputField.clear();

            // Thêm typing indicator
            addTypingIndicator();

            // Xử lý trong background thread
            new Thread(() -> {
                try {
                    ChatResponse response = chatbot.processQuery(userMessage);
                    String responseContent = response.getResponseContent();

                    Platform.runLater(() -> {
                        removeTypingIndicator();
                        displayMessageAsWebView(responseContent, false);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        removeTypingIndicator();
                        displayMessageAsWebView("❌ Lỗi xử lý: " + e.getMessage(), false);
                    });
                }
            }).start();
        }
    }

    private void addMessage(String content, boolean isUser) {
        Label messageLabel = new Label(content);
        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(10));
        messageLabel.setFont(Font.font("Arial", 14));

        if (isUser) {
            messageLabel.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 15px;");
            HBox messageBox = new HBox(messageLabel);
            messageBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            messageBox.setPadding(new Insets(5));
            messageContainer.getChildren().add(messageBox);
        } else {
            messageLabel.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; -fx-background-radius: 15px;");
            HBox messageBox = new HBox(messageLabel);
            messageBox.setPadding(new Insets(5));
            messageContainer.getChildren().add(messageBox);
        }
    }

    private void addTypingIndicator() {
        HBox typingBox = new HBox(10);
        typingBox.setPadding(new Insets(10));
        typingBox.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 15px;");
        
        Label typingLabel = new Label("🤖 AI đang suy nghĩ");
        typingLabel.setFont(Font.font("Arial", 12));
        typingLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(20, 20);
        
        typingBox.getChildren().addAll(spinner, typingLabel);
        typingBox.setId("typing-indicator");
        
        HBox container = new HBox(typingBox);
        container.setPadding(new Insets(5));
        messageContainer.getChildren().add(container);
    }

    private void removeTypingIndicator() {
        messageContainer.getChildren().removeIf(node -> {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                return hbox.getChildren().stream().anyMatch(child -> 
                    child instanceof HBox && "typing-indicator".equals(((HBox) child).getId()));
            }
            return false;
        });
    }
}