package chatbot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import chatbot.ChatbotEngine.Intent;

/**
 * Response generation component using rule-based AI and context awareness
 * Generates contextually appropriate responses based on intent and sentiment
 */
public class ResponseGenerator {

    private Map<Intent, List<String>> responseTemplates;
    private Random random;
    private String botName = "CodeBot";
    private int conversationTurn = 0;

    // Context tracking
    private Intent lastIntent = Intent.UNKNOWN;
    private String lastTopic = "";

    public ResponseGenerator() {
        this.random = new Random();
        initializeResponseTemplates();
    }

    /**
     * Generate response based on intent, input, and sentiment
     */
    public String generateResponse(Intent intent, String processedInput, Map<String, Double> sentiment) {
        conversationTurn++;

        // Check for specific patterns first
        String specificResponse = getSpecificResponse(processedInput);
        if (specificResponse != null) {
            lastIntent = intent;
            return specificResponse;
        }

        // Generate contextual response
        String response = generateContextualResponse(intent, processedInput, sentiment);

        // Add personality based on sentiment
        response = addPersonality(response, sentiment);

        // Add context awareness
        response = addContextAwareness(response, intent);

        lastIntent = intent;
        return response;
    }

    /**
     * Generate contextual response based on intent
     */
    private String generateContextualResponse(Intent intent, String input, Map<String, Double> sentiment) {
        List<String> templates = responseTemplates.getOrDefault(intent, responseTemplates.get(Intent.UNKNOWN));
        String baseResponse = templates.get(random.nextInt(templates.size()));

        // Replace placeholders
        baseResponse = baseResponse.replace("{name}", botName);
        baseResponse = baseResponse.replace("{time}", getCurrentTime());
        baseResponse = baseResponse.replace("{turn}", String.valueOf(conversationTurn));

        // Handle specific intents with dynamic content
        switch (intent) {
            case TIME:
                return "The current time is " + getCurrentTime() + ". Is there anything else I can help you with?";

            case TECHNOLOGY:
                return generateTechResponse(input);

            case EDUCATION:
                return generateEducationResponse(input);

            case PERSONAL:
                return generatePersonalResponse(input, sentiment);

            case QUESTION:
                return generateQuestionResponse(input);

            default:
                return baseResponse;
        }
    }

    /**
     * Check for specific patterns and return custom responses
     */
    private String getSpecificResponse(String input) {
        // Name detection
        if (input.contains("my name is") || input.contains("i am") || input.contains("i'm")) {
            String[] words = input.split("\\s+");
            for (int i = 0; i < words.length - 1; i++) {
                if ((words[i].equals("my") && words[i+1].equals("name")) ||
                        words[i].equals("am") || words[i].equals("i'm")) {
                    if (i + 2 < words.length) {
                        String name = words[i + 2];
                        return "Nice to meet you, " + capitalize(name) + "! I'm " + botName + ", your AI assistant.";
                    }
                }
            }
            return "Nice to meet you! I'm " + botName + ", your AI assistant.";
        }

        // Joke request
        if (input.contains("joke") || input.contains("funny")) {
            return getRandomJoke();
        }

        // About bot
        if (input.contains("who are you") || input.contains("what are you")) {
            return "I'm " + botName + ", an AI chatbot created as part of a CodeAlpha internship project. " +
                    "I use Java and NLP techniques to understand and respond to your messages!";
        }

        // Creator info
        if (input.contains("who created") || input.contains("who made")) {
            return "I was created by a talented intern as part of the CodeAlpha Java programming internship. " +
                    "The project showcases NLP, machine learning concepts, and GUI development!";
        }

        // Capabilities
        if (input.contains("what can you do") || input.contains("your capabilities")) {
            return "I can chat with you, answer questions, tell jokes, provide information about various topics, " +
                    "analyze the sentiment of our conversation, and learn from our interactions. Try asking me about " +
                    "technology, education, or just have a casual conversation!";
        }

        return null;
    }

    /**
     * Generate technology-related responses
     */
    private String generateTechResponse(String input) {
        if (input.contains("java")) {
            return "Java is a fantastic programming language! It's object-oriented, platform-independent, and " +
                    "widely used for enterprise applications. Are you learning Java programming?";
        } else if (input.contains("ai") || input.contains("artificial intelligence")) {
            return "Artificial Intelligence is fascinating! I'm a simple example of AI using NLP and rule-based " +
                    "responses. AI can be used for many things like chatbots, recommendation systems, and automation.";
        } else if (input.contains("programming") || input.contains("code")) {
            return "Programming is an amazing skill! It allows you to create software, solve problems, and bring " +
                    "ideas to life. What programming languages are you interested in?";
        }

        return "Technology is constantly evolving! Whether it's programming, AI, web development, or mobile apps, " +
                "there's always something new to learn. What aspect of technology interests you most?";
    }

    /**
     * Generate education-related responses
     */
    private String generateEducationResponse(String input) {
        if (input.contains("study") || input.contains("learning")) {
            return "Learning is a lifelong journey! Whether you're studying programming, mathematics, science, or " +
                    "any other subject, consistency and practice are key. What are you currently studying?";
        } else if (input.contains("school") || input.contains("university")) {
            return "Education opens doors to new opportunities! It's great that you're focused on learning. " +
                    "Remember, the most important thing is to stay curious and keep asking questions.";
        }

        return "Education is the foundation of personal growth. Whether formal or self-directed learning, " +
                "every bit of knowledge you gain makes you more capable. What would you like to learn about?";
    }

    /**
     * Generate personal responses based on sentiment
     */
    private String generatePersonalResponse(String input, Map<String, Double> sentiment) {
        double positiveScore = sentiment.get("positive");
        double negativeScore = sentiment.get("negative");

        if (positiveScore > 0.6) {
            return "That's wonderful to hear! I'm glad you're feeling positive. " +
                    "It's always great when people share good news or positive thoughts.";
        } else if (negativeScore > 0.6) {
            return "I'm sorry to hear that you're going through a tough time. " +
                    "Remember that challenges are temporary, and talking about them can help. " +
                    "Is there anything specific I can help you with?";
        }

        return "I appreciate you sharing that with me. Everyone has their own unique experiences and perspectives. " +
                "Feel free to tell me more if you'd like to chat about it!";
    }

    /**
     * Generate question responses
     */
    private String generateQuestionResponse(String input) {
        if (input.contains("what") && input.contains("time")) {
            return "The current time is " + getCurrentTime() + ".";
        } else if (input.contains("how") && input.contains("are you")) {
            return "I'm doing well, thank you for asking! I'm here and ready to chat. How are you doing today?";
        } else if (input.contains("why")) {
            return "That's a thoughtful question! The 'why' behind things often reveals deeper understanding. " +
                    "Could you provide more context so I can give you a better answer?";
        } else if (input.contains("how")) {
            return "Great question! The 'how' of things is often just as important as the 'what'. " +
                    "Let me know more details and I'll do my best to help explain!";
        }

        return "That's an interesting question! I'll do my best to help. Could you provide a bit more " +
                "context or be more specific about what you'd like to know?";
    }

    /**
     * Add personality based on sentiment analysis
     */
    private String addPersonality(String response, Map<String, Double> sentiment) {
        double positiveScore = sentiment.get("positive");
        double negativeScore = sentiment.get("negative");

        // Add enthusiasm for positive sentiment
        if (positiveScore > 0.7) {
            String[] enthusiasticEndings = {" 😊", " That's great!", " I love your enthusiasm!", " Awesome!"};
            response += enthusiasticEndings[random.nextInt(enthusiasticEndings.length)];
        }

        // Add empathy for negative sentiment
        else if (negativeScore > 0.7) {
            String[] empatheticEndings = {" I understand.", " I'm here if you need to talk.",
                    " Things will get better.", " Take care."};
            response += empatheticEndings[random.nextInt(empatheticEndings.length)];
        }

        return response;
    }

    /**
     * Add context awareness to responses
     */
    private String addContextAwareness(String response, Intent currentIntent) {
        // If repeating same intent, acknowledge continuation
        if (lastIntent == currentIntent && currentIntent != Intent.UNKNOWN) {
            if (random.nextDouble() < 0.3) { // 30% chance to acknowledge continuation
                String[] continuations = {"Also, ", "Additionally, ", "By the way, ", "Furthermore, "};
                response = continuations[random.nextInt(continuations.length)] + response.toLowerCase();
            }
        }

        return response;
    }

    /**
     * Get current time formatted
     */
    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    /**
     * Get random joke
     */
    private String getRandomJoke() {
        String[] jokes = {
                "Why don't scientists trust atoms? Because they make up everything!",
                "Why did the programmer quit his job? He didn't get arrays!",
                "How do you comfort a JavaScript bug? You console it!",
                "Why do Java developers wear glasses? Because they don't see sharp!",
                "What's a computer's favorite snack? Chips!",
                "Why was the computer cold? It left its Windows open!",
                "What do you call a programmer from Finland? Nerdic!",
                "Why don't programmers like nature? It has too many bugs!"
        };
        return jokes[random.nextInt(jokes.length)];
    }

    /**
     * Capitalize first letter of string
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Initialize response templates for different intents
     */
    private void initializeResponseTemplates() {
        responseTemplates = new HashMap<>();

        responseTemplates.put(Intent.GREETING, Arrays.asList(
                "Hello! How can I help you today?",
                "Hi there! Nice to meet you!",
                "Greetings! I'm {name}, your AI assistant.",
                "Hey! What's on your mind?",
                "Good to see you! How are you doing?",
                "Welcome! I'm here to chat and help."
        ));

        responseTemplates.put(Intent.FAREWELL, Arrays.asList(
                "Goodbye! It was great chatting with you!",
                "See you later! Have a wonderful day!",
                "Take care! Feel free to come back anytime!",
                "Farewell! Hope to chat with you again soon!",
                "Bye! Thanks for the conversation!",
                "Until next time! Stay awesome!"
        ));

        responseTemplates.put(Intent.QUESTION, Arrays.asList(
                "That's a great question! Let me think about that.",
                "Interesting question! I'll do my best to help.",
                "Good question! Could you be more specific?",
                "I'd be happy to help answer that!",
                "Let me see what I can tell you about that.",
                "That's something worth exploring!"
        ));

        responseTemplates.put(Intent.HELP, Arrays.asList(
                "I'm here to help! What do you need assistance with?",
                "Of course! I'd be glad to help you out.",
                "No problem! Tell me what you need help with.",
                "I'm ready to assist you! What's the issue?",
                "Help is on the way! What can I do for you?",
                "Absolutely! I'm here to support you."
        ));

        responseTemplates.put(Intent.PERSONAL, Arrays.asList(
                "Thank you for sharing that with me!",
                "I appreciate you telling me about yourself.",
                "That's interesting! Tell me more.",
                "I enjoy getting to know you better.",
                "Thanks for opening up! I'm here to listen.",
                "It's nice to learn more about you!"
        ));

        responseTemplates.put(Intent.TECHNOLOGY, Arrays.asList(
                "Technology is fascinating! What aspect interests you?",
                "I love discussing tech topics! Tell me more.",
                "Technology is constantly evolving. What's your focus?",
                "Great topic! I enjoy talking about technology.",
                "Tech is amazing! What would you like to explore?",
                "Technology opens up so many possibilities!"
        ));

        responseTemplates.put(Intent.EDUCATION, Arrays.asList(
                "Learning is wonderful! What are you studying?",
                "Education is so important! Tell me more about your studies.",
                "I love discussing educational topics!",
                "Knowledge is power! What subject interests you?",
                "Learning never stops! What would you like to explore?",
                "Education opens doors to amazing opportunities!"
        ));

        responseTemplates.put(Intent.TIME, Arrays.asList(
                "Let me check the current time for you.",
                "Time flies! Let me get that information.",
                "Sure! I can tell you the current time.",
                "Of course! Here's the time information you requested."
        ));

        responseTemplates.put(Intent.WEATHER, Arrays.asList(
                "I'd love to help with weather info, but I don't have access to current weather data.",
                "For accurate weather information, I'd recommend checking a weather app or website.",
                "Weather can change quickly! Check your local weather service for updates.",
                "I wish I could give you weather updates, but I don't have that capability yet."
        ));

        responseTemplates.put(Intent.ENTERTAINMENT, Arrays.asList(
                "Entertainment is great for relaxation! What do you enjoy?",
                "I love talking about fun stuff! What entertains you?",
                "Entertainment comes in so many forms! Tell me your favorites.",
                "Fun topic! What kind of entertainment do you prefer?",
                "Everyone needs some entertainment! What's your go-to?"
        ));

        responseTemplates.put(Intent.UNKNOWN, Arrays.asList(
                "I'm not quite sure I understand. Could you rephrase that?",
                "That's interesting! Could you tell me more about what you mean?",
                "I'd like to help, but I need more context. Can you explain further?",
                "Hmm, I'm not following. Could you be more specific?",
                "I want to give you a good response, but I need more information.",
                "Could you help me understand what you're looking for?",
                "I'm here to chat! What would you like to talk about?",
                "Let's explore that topic together! Tell me more."
        ));
    }
}