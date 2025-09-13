package chatbot;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Modern GUI for the AI Chatbot with real-time interaction
 * Features: Chat history, typing indicators, analytics, and modern design
 */
public class ChatbotGUI extends JFrame {

    private ChatbotEngine chatbotEngine;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton clearButton;
    private JButton saveButton;
    private JButton analyticsButton;
    private JLabel statusLabel;
    private JProgressBar thinkingBar;

    // UI Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color USER_MESSAGE_COLOR = new Color(155, 89, 182);
    private static final Color BOT_MESSAGE_COLOR = new Color(46, 204, 113);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    public ChatbotGUI() {
        this.chatbotEngine = new ChatbotEngine();
        initializeGUI();
        setupEventListeners();
        showWelcomeMessage();
    }

    /**
     * Initialize the GUI components
     */
    private void initializeGUI() {
        setTitle("CodeAlpha AI Chatbot - CodeBot Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(true);

        // Set application icon
        try {
            setIconImage(createBotIcon());
        } catch (Exception e) {
            System.err.println("Could not set application icon: " + e.getMessage());
        }

        // Main panel with modern layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Chat area (center)
        JPanel chatPanel = createChatPanel();
        mainPanel.add(chatPanel, BorderLayout.CENTER);

        // Input panel (south)
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // Status panel
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.EAST);

        add(mainPanel);

        // Set focus to input field
        SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());
    }

    /**
     * Create header panel with title and info
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        header.setPreferredSize(new Dimension(0, 80));

        // Title
        JLabel titleLabel = new JLabel("🤖 CodeBot - AI Assistant");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Powered by Java NLP & Machine Learning");
        subtitleLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(236, 240, 241));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);

        // Status indicator
        JLabel statusIndicator = new JLabel("🟢 Online");
        statusIndicator.setFont(new Font("Dialog", Font.BOLD, 14));
        statusIndicator.setForeground(Color.WHITE);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(statusIndicator, BorderLayout.EAST);

        return header;
    }

    /**
     * Create chat display panel
     */
    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());

        // Chat area with custom styling
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Dialog", Font.PLAIN, 14));
        chatArea.setBackground(Color.WHITE);
        chatArea.setForeground(TEXT_COLOR);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));

        chatPanel.add(scrollPane, BorderLayout.CENTER);

        return chatPanel;
    }

    /**
     * Create input panel with text field and buttons
     */
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setOpaque(false);

        // Thinking progress bar
        thinkingBar = new JProgressBar();
        thinkingBar.setIndeterminate(true);
        thinkingBar.setString("CodeBot is thinking...");
        thinkingBar.setStringPainted(true);
        thinkingBar.setVisible(false);
        thinkingBar.setForeground(SECONDARY_COLOR);

        // Input field
        inputField = new JTextField();
        inputField.setFont(new Font("Dialog", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                new EmptyBorder(10, 15, 10, 15)
        ));
        inputField.setPreferredSize(new Dimension(0, 45));

        // Send button
        sendButton = new JButton("Send 📤");
        sendButton.setFont(new Font("Dialog", Font.BOLD, 12));
        sendButton.setBackground(PRIMARY_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setPreferredSize(new Dimension(100, 45));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect for send button
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sendButton.setBackground(SECONDARY_COLOR);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                sendButton.setBackground(PRIMARY_COLOR);
            }
        });

        // Input container
        JPanel textInputPanel = new JPanel(new BorderLayout());
        textInputPanel.add(inputField, BorderLayout.CENTER);
        textInputPanel.add(sendButton, BorderLayout.EAST);

        // Main input panel
        JPanel mainInputPanel = new JPanel(new BorderLayout());
        mainInputPanel.add(thinkingBar, BorderLayout.NORTH);
        mainInputPanel.add(textInputPanel, BorderLayout.CENTER);

        inputPanel.add(mainInputPanel, BorderLayout.CENTER);

        return inputPanel;
    }

    /**
     * Create status panel with control buttons
     */
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(BACKGROUND_COLOR);
        statusPanel.setPreferredSize(new Dimension(150, 0));
        statusPanel.setBorder(new EmptyBorder(10, 10, 10, 0));

        // Control buttons
        clearButton = createStyledButton("🗑️ Clear Chat", new Color(231, 76, 60));
        saveButton = createStyledButton("💾 Save Chat", new Color(46, 204, 113));
        analyticsButton = createStyledButton("📊 Analytics", new Color(155, 89, 182));

        // Status label
        statusLabel = new JLabel("<html><div style='text-align: center;'>Ready to chat!<br/>Ask me anything!</div></html>");
        statusLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(new EmptyBorder(20, 10, 20, 10));

        statusPanel.add(Box.createVerticalStrut(20));
        statusPanel.add(clearButton);
        statusPanel.add(Box.createVerticalStrut(10));
        statusPanel.add(saveButton);
        statusPanel.add(Box.createVerticalStrut(10));
        statusPanel.add(analyticsButton);
        statusPanel.add(Box.createVerticalStrut(30));
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createVerticalGlue());

        return statusPanel;
    }

    /**
     * Create styled button
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Dialog", Font.BOLD, 11));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(130, 35));
        button.setMaximumSize(new Dimension(130, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        Color originalColor = color;
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    /**
     * Setup event listeners
     */
    private void setupEventListeners() {
        // Send button action
        sendButton.addActionListener(e -> sendMessage());

        // Enter key in input field
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        // Clear button
        clearButton.addActionListener(e -> clearChat());

        // Save button
        saveButton.addActionListener(e -> saveConversation());

        // Analytics button
        analyticsButton.addActionListener(e -> showAnalytics());
    }

    /**
     * Send message to chatbot
     */
    private void sendMessage() {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) return;

        // Disable input while processing
        inputField.setEnabled(false);
        sendButton.setEnabled(false);
        thinkingBar.setVisible(true);

        // Display user message
        appendMessage("You", userInput, USER_MESSAGE_COLOR);
        inputField.setText("");

        // Update status
        statusLabel.setText("<html><div style='text-align: center;'>Processing your message...</div></html>");

        // Process in background to keep UI responsive
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // Simulate thinking time for better UX
                Thread.sleep(500 + (int)(Math.random() * 1000));
                return chatbotEngine.processInput(userInput);
            }

            @Override
            protected void done() {
                try {
                    String response = get();
                    appendMessage("CodeBot", response, BOT_MESSAGE_COLOR);
                    statusLabel.setText("<html><div style='text-align: center;'>Ready for your next message!</div></html>");
                } catch (Exception e) {
                    appendMessage("CodeBot", "Sorry, I encountered an error processing your message. Please try again!",
                            Color.RED);
                    statusLabel.setText("<html><div style='text-align: center;'>Error occurred. Ready to try again!</div></html>");
                } finally {
                    // Re-enable input
                    inputField.setEnabled(true);
                    sendButton.setEnabled(true);
                    thinkingBar.setVisible(false);
                    SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());
                }
            }
        };

        worker.execute();
    }

    /**
     * Append message to chat area with styling
     */
    private void appendMessage(String sender, String message, Color color) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        String formattedMessage = String.format(
                "[%s] %s: %s\n\n",
                timestamp, sender, message
        );

        chatArea.append(formattedMessage);
        // Force scroll to bottom
        SwingUtilities.invokeLater(() -> {
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    /**
     * Clear chat history
     */
    private void clearChat() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear the chat history?",
                "Clear Chat",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            chatArea.setText("");
            showWelcomeMessage();
            statusLabel.setText("<html><div style='text-align: center;'>Chat cleared!<br/>Ready for a fresh start!</div></html>");
        }
    }

    /**
     * Save conversation to file
     */
    private void saveConversation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("chatbot_conversation_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                chatbotEngine.saveConversation(fileChooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Conversation saved successfully!",
                        "Save Complete", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("<html><div style='text-align: center;'>Conversation saved!</div></html>");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving conversation: " + e.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Show analytics dialog
     */
    private void showAnalytics() {
        Map<String, Integer> analytics = chatbotEngine.getTopicAnalytics();

        StringBuilder analyticsText = new StringBuilder();
        analyticsText.append("📊 CONVERSATION ANALYTICS\n\n");
        analyticsText.append("Topic Distribution:\n");
        analyticsText.append("─────────────────────\n");

        analytics.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    if (entry.getValue() > 0) {
                        analyticsText.append(String.format("• %s: %d messages\n",
                                entry.getKey(), entry.getValue()));
                    }
                });

        analyticsText.append("\n🔍 INSIGHTS:\n");
        analyticsText.append("─────────────────────\n");

        String mostDiscussed = analytics.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No topics yet");

        analyticsText.append("• Most discussed topic: ").append(mostDiscussed).append("\n");

        int totalMessages = analytics.values().stream().mapToInt(Integer::intValue).sum();
        analyticsText.append("• Total interactions: ").append(totalMessages).append("\n");

        if (totalMessages > 0) {
            analyticsText.append("• Average topic engagement: ")
                    .append(String.format("%.1f", totalMessages / (double) analytics.size()))
                    .append(" messages per topic\n");
        }

        JTextArea analyticsArea = new JTextArea(analyticsText.toString());
        analyticsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        analyticsArea.setEditable(false);
        analyticsArea.setBackground(BACKGROUND_COLOR);

        JScrollPane scrollPane = new JScrollPane(analyticsArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Conversation Analytics",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show welcome message
     */
    private void showWelcomeMessage() {
        String welcomeMessage = "🤖 Welcome to CodeBot - Your AI Assistant!\n\n" +
                "I'm powered by Java NLP and machine learning techniques. Here's what I can do:\n\n" +
                "✨ Chat naturally with you\n" +
                "🧠 Learn from our conversations\n" +
                "📊 Analyze sentiment and topics\n" +
                "🎯 Recognize your intents\n" +
                "💡 Answer questions on various topics\n" +
                "🤖 Tell jokes and have fun conversations\n\n" +
                "Try asking me about:\n" +
                "• Technology and programming\n" +
                "• Education and learning\n" +
                "• General questions\n" +
                "• Or just chat casually!\n\n" +
                "Type your message below and press Enter or click Send to start! 🚀";

        appendMessage("CodeBot", welcomeMessage, BOT_MESSAGE_COLOR);
    }

    /**
     * Create bot icon for the application
     */
    private Image createBotIcon() {
        // Create a simple bot icon programmatically
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = icon.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Bot head
        g2.setColor(PRIMARY_COLOR);
        g2.fillOval(4, 4, 24, 24);

        // Eyes
        g2.setColor(Color.WHITE);
        g2.fillOval(10, 12, 4, 4);
        g2.fillOval(18, 12, 4, 4);

        // Mouth
        g2.setColor(Color.WHITE);
        g2.fillRect(12, 20, 8, 2);

        g2.dispose();
        return icon;
    }

    // Main method for testing the GUI independently
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Try to set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            } catch (Exception e) {
                // Use default look and feel if system LAF fails
                System.err.println("Could not set system look and feel: " + e.getMessage());
            }
            new ChatbotGUI().setVisible(true);
        });
    }
}