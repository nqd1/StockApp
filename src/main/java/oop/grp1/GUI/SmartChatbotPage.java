package oop.grp1.GUI;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import oop.grp1.Control.DataFetcher.SmartChatbot;
import oop.grp1.Model.ChatResponse;

import java.util.List;

public class SmartChatbotPage extends VBox {
    private final VBox messageContainer;
    private final TextField inputField;
    private final Button sendButton;
    private final ScrollPane scrollPane;
    private final Label statusLabel;
    private final ProgressIndicator loadingIndicator;

    private SmartChatbot smartChatbot;

    public SmartChatbotPage() {
        // Khởi tạo SmartChatbot (có thể mất thời gian do load embeddings)
        initializeSmartChatbot();

        // Khung chứa tin nhắn (VBox)
        messageContainer = new VBox(10);
        messageContainer.setPadding(new Insets(10));

        // ScrollPane chứa VBox tin nhắn
        scrollPane = new ScrollPane(messageContainer);
        scrollPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            scrollPane.setVvalue(1.0);
        });
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Status label và loading indicator
        HBox statusBox = new HBox(10);
        statusBox.setPadding(new Insets(5, 10, 5, 10));
        statusBox.setStyle("-fx-background-color: #f0f8ff; -fx-border-color: #dcdcdc; -fx-border-radius: 3px;");
        
        statusLabel = new Label("Đang khởi tạo AI thông minh...");
        statusLabel.setFont(Font.font("Arial", 12));
        statusLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(20, 20);
        
        statusBox.getChildren().addAll(loadingIndicator, statusLabel);

        // Trường nhập liệu
        inputField = new TextField();
        inputField.setPromptText("Hỏi tôi về cổ phiếu (VD: 'Phân tích AAPL hôm nay', 'So sánh MSFT và GOOGL')...");
        inputField.setFont(Font.font("Arial", 14));
        inputField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 5px;");
        inputField.setDisable(true); // Disable cho đến khi khởi tạo xong
        HBox.setHgrow(inputField, Priority.ALWAYS);

        // Nút gửi
        sendButton = new Button("🚀 Gửi");
        sendButton.setFont(Font.font("Arial", 14));
        sendButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-border-radius: 5px;");
        sendButton.setOnMouseEntered(e -> sendButton.setStyle("-fx-background-color: #229954; -fx-text-fill: white; -fx-border-radius: 5px;"));
        sendButton.setOnMouseExited(e -> sendButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-border-radius: 5px;"));
        sendButton.setDisable(true); // Disable cho đến khi khởi tạo xong

        // Event handlers
        sendButton.setOnAction(e -> handleSendMessage());
        inputField.setOnAction(e -> handleSendMessage());

        // Layout nhập liệu
        HBox inputLayout = new HBox(10, inputField, sendButton);
        inputLayout.setPadding(new Insets(10));
        inputLayout.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #dcdcdc; -fx-border-radius: 5px;");

        // Giao diện chính
        this.getChildren().addAll(statusBox, scrollPane, inputLayout);
        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.getStyleClass().add("page-background");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.GRAY);
        shadow.setRadius(10);
        this.setEffect(shadow);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Thêm tin nhắn chào mừng
        addWelcomeMessage();
    }

    private void initializeSmartChatbot() {
        // Khởi tạo trong background thread
        new Thread(() -> {
            try {
                smartChatbot = new SmartChatbot();
                
                Platform.runLater(() -> {
                    statusLabel.setText("✅ AI thông minh đã sẵn sàng!");
                    loadingIndicator.setVisible(false);
                    inputField.setDisable(false);
                    sendButton.setDisable(false);
                    
                    // Ẩn status box sau 3 giây
                    new Thread(() -> {
                        try {
                            Thread.sleep(3000);
                            Platform.runLater(() -> {
                                if (this.getChildren().size() > 0) {
                                    this.getChildren().remove(0); // Remove status box
                                }
                            });
                        } catch (InterruptedException ignored) {}
                    }).start();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("❌ Lỗi khởi tạo AI: " + e.getMessage());
                    loadingIndicator.setVisible(false);
                });
            }
        }).start();
    }

    private void addWelcomeMessage() {
        String welcomeMessage = """
            # Chào mừng đến với Smart AI Assistant!
            
            **Tôi là trợ lý AI thông minh sử dụng công nghệ RAG (Retrieval-Augmented Generation)**
            
            ## Khả năng nâng cao:
            • **Tìm kiếm semantic** - Hiểu ý nghĩa thực sự của câu hỏi
            • **Phân tích đa chiều** - Kết hợp nhiều nguồn dữ liệu liên quan  
            • **Trả lời thông minh** - Dựa trên vector embeddings và AI
            
            ## Ví dụ câu hỏi:
            - *"Cổ phiếu nào đang có xu hướng tăng mạnh?"*
            - *"So sánh hiệu suất AAPL và MSFT trong tuần qua"*
            - *"Phân tích rủi ro đầu tư vào TSLA"*
            - *"Tìm cổ phiếu có volatility thấp để đầu tư dài hạn"*
            
            ---
            *Hãy hỏi tôi bất cứ điều gì về thị trường chứng khoán! *
            """;

        displayMessageAsWebView(welcomeMessage, false);
    }

    private void handleSendMessage() {
        String userMessage = inputField.getText().trim();
        if (userMessage.isEmpty() || smartChatbot == null) {
            return;
        }

        // Clear input
        inputField.clear();

        // Hiển thị tin nhắn user
        addMessage(userMessage, true);

        // Hiển thị typing indicator
        addTypingIndicator();

        // Xử lý trong background thread
        new Thread(() -> {
            try {
                ChatResponse response = smartChatbot.processQuery(userMessage);
                
                Platform.runLater(() -> {
                    removeTypingIndicator();
                    displayMessageAsWebView(response.getResponseContent(), false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    removeTypingIndicator();
                    addMessage("❌ Lỗi xử lý: " + e.getMessage(), false);
                });
            }
        }).start();
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

        // Auto-resize WebView
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
                        // Ignore errors
                    }
                });
            }
        });
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

    // Getters
    public SmartChatbot getSmartChatbot() {
        return smartChatbot;
    }
} 