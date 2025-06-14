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
        inputField.setPromptText("Type your message...");
        inputField.setFont(Font.font("Arial", 14));
        inputField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 5px;");
        HBox.setHgrow(inputField, Priority.ALWAYS);

        // Nút gửi
        sendButton = new Button("Send");
        sendButton.setFont(Font.font("Arial", 14));
        sendButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px;");
        sendButton.setOnMouseEntered(e -> sendButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-border-radius: 5px;"));
        sendButton.setOnMouseExited(e -> sendButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px;"));

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
        
        // Thêm tin nhắn chào mừng và giới thiệu
        addWelcomeMessage();
        
        // Tải lịch sử chat từ database
        loadChatHistory();
    }
    
    private void loadChatHistory() {
        // Lấy phiên hiện tại từ chatbot
        String currentSession = chatbot.getSession();
        
        // Lấy lịch sử chat từ database
        List<ChatResponse> history = chatbotManager.getChatHistory(currentSession);
        
        // Hiển thị lịch sử chat
        for (ChatResponse response : history) {
            // Hiển thị câu hỏi của người dùng
            if (response.getUserQuery() != null && !response.getUserQuery().isEmpty()) {
                addMessage(response.getUserQuery(), true);
            }
            
            // Hiển thị câu trả lời
            String html = MarkdownUtils.convertToHtml(response.getResponseContent());
            displayResponseAsWebView(html);
        }
    }
    
    private void displayResponseAsWebView(String html) {
        // Tạo WebView với nội dung HTML
        WebView webView = new WebView();
        
        // Ràng buộc chiều rộng WebView với chiều rộng của messageContainer (trừ padding)
        webView.prefWidthProperty().bind(messageContainer.widthProperty().subtract(30));
        
        // Thiết lập các thuộc tính cho WebView
        webView.setMinHeight(50);
        webView.setPrefHeight(Region.USE_COMPUTED_SIZE);
        webView.setMaxHeight(Double.MAX_VALUE);            
        
        // Tạo CSS để tự động điều chỉnh kích thước nội dung và loại bỏ thanh cuộn
        String customCSS = "<style>" +
                          "body { width: 95%; margin: 0; padding: 10px 25px 10px 10px; word-wrap: break-word; overflow: hidden; font-family: 'Segoe UI', 'Segoe UI Emoji', 'Apple Color Emoji', 'Noto Color Emoji', Arial, sans-serif;}" +
                          "pre { white-space: pre-wrap; }" +
                          "code { white-space: pre-wrap; }" +
                          "html, body { height: auto; overflow: visible; }" +
                          "</style>";
                          
        // Load nội dung HTML với CSS tùy chỉnh
        webView.getEngine().loadContent(customCSS + "<div id='content'>" + html + "</div>");
        
        // HBox chứa WebView
        HBox messageBox = new HBox(webView);
        messageBox.setPadding(new Insets(5));
        messageBox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        HBox.setHgrow(webView, Priority.ALWAYS);
        messageContainer.getChildren().add(messageBox);
        
        // Tự động điều chỉnh chiều cao của WebView dựa trên nội dung
        webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                // Sử dụng JavaScript để lấy chiều cao thực tế của nội dung
                Platform.runLater(() -> {
                    try {
                        // Sử dụng scrollHeight để đảm bảo lấy được toàn bộ chiều cao của nội dung
                        Object result = webView.getEngine().executeScript(
                            "Math.max(document.body.scrollHeight, document.documentElement.scrollHeight, " +
                            "document.body.offsetHeight, document.documentElement.offsetHeight, " +
                            "document.body.clientHeight, document.documentElement.clientHeight);"
                        );
                        if (result instanceof Number) {
                            double height = ((Number) result).doubleValue();
                            webView.setPrefHeight(height + 30); // thêm padding dư để đảm bảo không có scroll
                            
                            // Thêm đoạn code để vô hiệu hóa scrollbar trong WebView
                            webView.getEngine().executeScript(
                                "document.body.style.overflow='hidden';" +
                                "document.documentElement.style.overflow='hidden';"
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void addWelcomeMessage() {
        String welcomeMessage = """
            # Chào mừng bạn đến với Stock Market Assistant!

            **Tôi là trợ lý AI chuyên về thị trường chứng khoán.** Tôi có thể giúp gì cho bạn.
            """;
                
        // Tạo WebView để hiển thị tin nhắn markdown
        String html = MarkdownUtils.convertToHtml(welcomeMessage);
        
        WebView webView = new WebView();
        webView.prefWidthProperty().bind(messageContainer.widthProperty().subtract(30));
        webView.setMinHeight(50);
        webView.setPrefHeight(Region.USE_COMPUTED_SIZE);
        webView.setMaxHeight(Double.MAX_VALUE);
          String customCSS = "<style>" +
                           "body { width: 95%; margin: 0; padding: 20px; word-wrap: break-word; overflow: hidden; " +
                           "font-family: 'Segoe UI', 'Segoe UI Emoji', 'Apple Color Emoji', 'Noto Color Emoji', Arial, sans-serif; " +
                           "background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); " +
                           "color: #2c3e50; border-radius: 12px; border: 2px solid #dee2e6; " +
                           "box-shadow: 0 4px 12px rgba(0,0,0,0.1); }" +
                           "h1 { color: #2c3e50; margin-top: 0; font-size: 1.5em; }" +
                           "strong { color: #495057; font-weight: 600; }" +
                           "ul, li { color: #495057; margin: 8px 0; }" +
                           "hr { border: none; border-top: 2px solid #dee2e6; margin: 20px 0; }" +
                           "em { color: #6c757d; font-style: italic; }" +
                           "p { margin: 10px 0; line-height: 1.5; }" +
                           "pre { white-space: pre-wrap; background: #f8f9fa; padding: 10px; border-radius: 5px; }" +
                           "code { white-space: pre-wrap; background: #f8f9fa; padding: 2px 6px; border-radius: 4px; color: #e83e8c; }" +
                           "html, body { height: auto; overflow: visible; }" +
                           "</style>";
                           
        webView.getEngine().loadContent(customCSS + "<div id='content'>" + html + "</div>");
        
        HBox messageBox = new HBox(webView);
        messageBox.setPadding(new Insets(5));
        messageBox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        HBox.setHgrow(webView, Priority.ALWAYS);
        messageContainer.getChildren().add(messageBox);
        
        // Tự động điều chỉnh chiều cao của WebView
        webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                Platform.runLater(() -> {
                    try {
                        Object result = webView.getEngine().executeScript(
                            "Math.max(document.body.scrollHeight, document.documentElement.scrollHeight, " +
                            "document.body.offsetHeight, document.documentElement.offsetHeight, " +
                            "document.body.clientHeight, document.documentElement.clientHeight);"
                        );
                        if (result instanceof Number) {
                            double height = ((Number) result).doubleValue();
                            webView.setPrefHeight(height + 40);
                            
                            webView.getEngine().executeScript(
                                "document.body.style.overflow='hidden';" +
                                "document.documentElement.style.overflow='hidden';"
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void handleSendMessage() {
        String userMessage = inputField.getText().trim();
        if (!userMessage.isEmpty()) {
            // Hiển thị tin nhắn người dùng
            addMessage(userMessage, true);
            inputField.clear();

            ChatResponse response = chatbot.processQuery(userMessage);            
            String jsonResponse = gson.toJson(response);
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            String responseContent = jsonObject.get("responseContent").getAsString();

            // addMessage(responseContent, false);
            String html = MarkdownUtils.convertToHtml(responseContent);
            
            // Hiển thị phản hồi dưới dạng WebView
            displayResponseAsWebView(html);
        }
    }

    private void addMessage(String content, boolean isUser) {
        Node messageNode;

        if (content.trim().startsWith("<html")) {
            // Nếu là nội dung HTML, sử dụng WebView
            WebView webView = new WebView();
            webView.getEngine().loadContent(content);

            // Giới hạn kích thước WebView cho phù hợp với giao diện chat
            webView.setPrefSize(300, 100);
            messageNode = webView;
        } else
        {
            // Nếu là văn bản thuần túy, sử dụng Label
            Label messageLabel = new Label(content);
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(300);
            messageLabel.setPadding(new Insets(10));
            messageLabel.setFont(Font.font("Arial", 14));
            messageLabel.setStyle(isUser
                    ? "-fx-background-color: #dcf8c6; -fx-background-radius: 10 0 10 10; -fx-border-color: #c0c0c0;"
                    : "-fx-background-color: #eeeeee; -fx-background-radius: 0 10 10 10; -fx-border-color: #c0c0c0;");
            messageNode = messageLabel;
        }

        HBox messageBox = new HBox(messageNode);
        messageBox.setPadding(new Insets(5));

        if (isUser) {
            messageBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        } else {
            messageBox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        }

        messageContainer.getChildren().add(messageBox);
    }
}