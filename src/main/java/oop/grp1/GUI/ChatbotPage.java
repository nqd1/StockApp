package oop.grp1.GUI;
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


public class ChatbotPage extends VBox {
    private final VBox messageContainer;
    private final TextField inputField;
    private final Button sendButton;
    private final ScrollPane scrollPane;

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
        this.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 10px;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.GRAY);
        shadow.setRadius(10);
        this.setEffect(shadow);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }

    private void handleSendMessage() {
        String userMessage = inputField.getText().trim();
        if (!userMessage.isEmpty()) {
            // Hiển thị tin nhắn người dùng
            addMessage(userMessage, true);
            inputField.clear();

            // Phản hồi từ chatbot
//            String botResponse = getChatbotResponse(userMessage);
//            addMessage(botResponse, false);


            String markdown = "# Tiêu đề\n\nĐây là đoạn **markdown** với *định dạng*.\n" +
                    "# Tiêu đề\n\nĐây là đoạn **markdown** với *định dạng*.ejfgaenwfkaewjfaewfaew hihihihihi con cho duc beo lol dcm me may\n" +
                    "# Tiêu đề\n\nĐây là đoạn **markdown** với *định dạng*.\n" +
                    "# Tiêu đề\n\nĐây là đoạn **markdown** với *định dạng*.\n" +
                    "# Tiêu đề\n\nĐây là đoạn **markdown** với *định dạng*.\n";
            String html = MarkdownUtils.convertToHtml(markdown);
//            addMessage(html, false);
            WebView webView = new WebView();
            webView.getEngine().loadContent(html);
            HBox messageBox = new HBox(webView);
            messageBox.setPadding(new Insets(5));
            messageBox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            messageContainer.getChildren().add(messageBox);

            // Tự động cuộn đến đầu của tin nhắn chatbot
            Platform.runLater(() -> {
                double messageBoxY = messageBox.getBoundsInParent().getMinY();
                double containerHeight = messageContainer.getBoundsInParent().getHeight();
                double scrollPaneHeight = scrollPane.getViewportBounds().getHeight();

                // Tính toán giá trị Vvalue
                double vValue = messageBoxY / (containerHeight - scrollPaneHeight);
                scrollPane.setVvalue(vValue);
            });

//            Label responseLabel = new Label(html);
//            responseLabel.setWrapText(true); // Ensures the content fits
//            responseLabel.setStyle("-fx-background-color: #eee; -fx-border-radius: 5px;");
//            messageContainer.getChildren().add(responseLabel);


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


    private String getChatbotResponse(String message) {
//         Giả lập phản hồi đơn giản
        return "I'm just a simple bot. You said: " + message;


    }
}
