package chatbot;

import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Core chatbot engine implementing NLP and ML-like response generation
 * Uses rule-based system with learning capabilities
 */
public class ChatbotEngine {
    private NLPProcessor nlpProcessor;
    private ResponseGenerator responseGenerator;
    private Map<String, Integer> topicFrequency;
    private List<String> conversationHistory;
    private Map<String, String> learnedResponses;

    // Intent categories for classification
    public enum Intent {
        GREETING, FAREWELL, QUESTION, HELP, PERSONAL, TIME, WEATHER,
        TECHNOLOGY, EDUCATION, ENTERTAINMENT, UNKNOWN
    }

    public ChatbotEngine() {
        this.nlpProcessor = new NLPProcessor();
        this.responseGenerator = new ResponseGenerator();
        this.topicFrequency = new HashMap<>();
        this.conversationHistory = new ArrayList<>();
        this.learnedResponses = new HashMap<>();

        loadKnowledgeBase();
        initializeMLModel();
    }

    /**
     * Main method to process user input and generate response
     */
    public String processInput(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return "I didn't catch that. Could you please say something?";
        }

        // Add to conversation history
        conversationHistory.add("USER: " + userInput);

        // Process with NLP
        String processedInput = nlpProcessor.preprocess(userInput);
        Intent intent = nlpProcessor.classifyIntent(processedInput);
        Map<String, Double> sentiment = nlpProcessor.analyzeSentiment(processedInput);

        // Update topic frequency (simple ML)
        updateTopicFrequency(intent.toString());

        // Generate response
        String response = responseGenerator.generateResponse(intent, processedInput, sentiment);

        // Learn from interaction
        learnFromInteraction(processedInput, response);

        // Add response to history
        conversationHistory.add("BOT: " + response);

        return response;
    }

    /**
     * Initialize simple machine learning model (frequency-based learning)
     */
    private void initializeMLModel() {
        // Initialize topic frequencies
        for (Intent intent : Intent.values()) {
            topicFrequency.put(intent.toString(), 0);
        }
    }

    /**
     * Update topic frequency for simple learning
     */
    private void updateTopicFrequency(String topic) {
        topicFrequency.put(topic, topicFrequency.getOrDefault(topic, 0) + 1);
    }

    /**
     * Simple learning mechanism - stores patterns
     */
    private void learnFromInteraction(String input, String response) {
        // Extract key phrases and store successful interactions
        String[] words = input.toLowerCase().split("\\s+");
        if (words.length > 2) {
            String pattern = String.join(" ", Arrays.copyOfRange(words, 0, Math.min(3, words.length)));
            learnedResponses.put(pattern, response);
        }
    }

    /**
     * Load knowledge base from file
     */
    private void loadKnowledgeBase() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("knowledge_base.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is != null ? is :
                     new ByteArrayInputStream(getDefaultKnowledgeBase().getBytes())))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("|")) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 2) {
                        learnedResponses.put(parts[0].trim().toLowerCase(), parts[1].trim());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load knowledge base: " + e.getMessage());
        }
    }

    /**
     * Get default knowledge base if file not found
     */
    private String getDefaultKnowledgeBase() {
        return """
            hello|Hello! How can I help you today?
            hi|Hi there! What's on your mind?
            good morning|Good morning! Hope you're having a great day!
            how are you|I'm doing well, thank you for asking! How about you?
            what is your name|I'm an AI chatbot created for the CodeAlpha project. You can call me CodeBot!
            what can you do|I can chat with you, answer questions, and learn from our conversations!
            thank you|You're very welcome! Happy to help!
            bye|Goodbye! It was nice chatting with you!
            help|I'm here to chat and answer your questions. Try asking me about technology, general topics, or just have a conversation!
            what time is it|Let me check the current time for you.
            tell me a joke|Why don't scientists trust atoms? Because they make up everything!
            who created you|I was created as part of a CodeAlpha internship project using Java and NLP techniques.
            """;
    }

    /**
     * Get most discussed topics (simple analytics)
     */
    public Map<String, Integer> getTopicAnalytics() {
        return new HashMap<>(topicFrequency);
    }

    /**
     * Get conversation history
     */
    public List<String> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }

    /**
     * Save conversation to file
     */
    public void saveConversation(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=== Chatbot Conversation Log ===");
            writer.println("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println();

            for (String message : conversationHistory) {
                writer.println(message);
            }

            writer.println();
            writer.println("=== Topic Analytics ===");
            topicFrequency.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(entry -> writer.println(entry.getKey() + ": " + entry.getValue()));

        } catch (IOException e) {
            System.err.println("Error saving conversation: " + e.getMessage());
        }
    }
}