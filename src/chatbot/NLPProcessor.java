package chatbot;

import java.util.*;
import java.util.regex.Pattern;
import chatbot.ChatbotEngine.Intent;

/**
 * Natural Language Processing component for the chatbot
 * Handles text preprocessing, intent classification, and sentiment analysis
 */
public class NLPProcessor {

    // Stop words to filter out
    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
            "has", "he", "in", "is", "it", "its", "of", "on", "that", "the",
            "to", "was", "will", "with", "would", "could", "should", "can"
    );

    // Intent patterns
    private Map<Intent, List<Pattern>> intentPatterns;

    // Sentiment words
    private Set<String> positiveWords;
    private Set<String> negativeWords;

    public NLPProcessor() {
        initializeIntentPatterns();
        initializeSentimentWords();
    }

    /**
     * Preprocess text: lowercase, remove punctuation, tokenize
     */
    public String preprocess(String text) {
        if (text == null) return "";

        // Convert to lowercase
        text = text.toLowerCase();

        // Remove extra whitespace
        text = text.trim().replaceAll("\\s+", " ");

        // Remove some punctuation but keep important ones
        text = text.replaceAll("[^a-zA-Z0-9\\s?!.]", "");

        return text;
    }

    /**
     * Tokenize text into words
     */
    public List<String> tokenize(String text) {
        String[] words = preprocess(text).split("\\s+");
        List<String> tokens = new ArrayList<>();

        for (String word : words) {
            if (!word.isEmpty() && !STOP_WORDS.contains(word)) {
                tokens.add(word);
            }
        }

        return tokens;
    }

    /**
     * Classify user intent based on input
     */
    public Intent classifyIntent(String input) {
        input = preprocess(input);

        // Check each intent pattern
        for (Map.Entry<Intent, List<Pattern>> entry : intentPatterns.entrySet()) {
            for (Pattern pattern : entry.getValue()) {
                if (pattern.matcher(input).find()) {
                    return entry.getKey();
                }
            }
        }

        // Additional heuristic checks
        if (input.contains("?")) {
            return Intent.QUESTION;
        }

        if (input.split("\\s+").length > 10) {
            return Intent.PERSONAL; // Assume longer texts are personal
        }

        return Intent.UNKNOWN;
    }

    /**
     * Analyze sentiment of the input
     */
    public Map<String, Double> analyzeSentiment(String input) {
        List<String> tokens = tokenize(input);
        int positiveCount = 0;
        int negativeCount = 0;

        for (String token : tokens) {
            if (positiveWords.contains(token)) {
                positiveCount++;
            } else if (negativeWords.contains(token)) {
                negativeCount++;
            }
        }

        int totalSentimentWords = positiveCount + negativeCount;
        Map<String, Double> sentiment = new HashMap<>();

        if (totalSentimentWords == 0) {
            sentiment.put("positive", 0.5);
            sentiment.put("negative", 0.5);
            sentiment.put("neutral", 1.0);
        } else {
            double positive = (double) positiveCount / totalSentimentWords;
            double negative = (double) negativeCount / totalSentimentWords;

            sentiment.put("positive", positive);
            sentiment.put("negative", negative);
            sentiment.put("neutral", 1.0 - Math.max(positive, negative));
        }

        return sentiment;
    }

    /**
     * Extract keywords from input
     */
    public List<String> extractKeywords(String input) {
        List<String> tokens = tokenize(input);
        List<String> keywords = new ArrayList<>();

        // Simple keyword extraction - remove stop words and short words
        for (String token : tokens) {
            if (token.length() > 3 && !STOP_WORDS.contains(token)) {
                keywords.add(token);
            }
        }

        return keywords;
    }

    /**
     * Initialize intent recognition patterns
     */
    private void initializeIntentPatterns() {
        intentPatterns = new HashMap<>();

        // Greeting patterns
        intentPatterns.put(Intent.GREETING, Arrays.asList(
                Pattern.compile("\\b(hello|hi|hey|good morning|good afternoon|good evening|greetings)\\b"),
                Pattern.compile("\\bhow are you\\b"),
                Pattern.compile("\\bwhat's up\\b"),
                Pattern.compile("\\bnice to meet\\b")
        ));

        // Farewell patterns
        intentPatterns.put(Intent.FAREWELL, Arrays.asList(
                Pattern.compile("\\b(bye|goodbye|see you|farewell|take care|later)\\b"),
                Pattern.compile("\\bgood night\\b"),
                Pattern.compile("\\btalk to you later\\b"),
                Pattern.compile("\\bhave a good\\b")
        ));

        // Question patterns
        intentPatterns.put(Intent.QUESTION, Arrays.asList(
                Pattern.compile("\\b(what|who|when|where|why|how|which)\\b"),
                Pattern.compile("\\b(can you|could you|would you)\\b"),
                Pattern.compile("\\b(do you know|tell me about|explain)\\b"),
                Pattern.compile("\\bis it\\b|\\bare you\\b")
        ));

        // Help patterns
        intentPatterns.put(Intent.HELP, Arrays.asList(
                Pattern.compile("\\b(help|assist|support|guide)\\b"),
                Pattern.compile("\\bi need help\\b"),
                Pattern.compile("\\bcan you help\\b"),
                Pattern.compile("\\bwhat can you do\\b")
        ));

        // Time patterns
        intentPatterns.put(Intent.TIME, Arrays.asList(
                Pattern.compile("\\b(time|clock|hour|minute|date|today|now)\\b"),
                Pattern.compile("\\bwhat time\\b"),
                Pattern.compile("\\bcurrent time\\b")
        ));

        // Technology patterns
        intentPatterns.put(Intent.TECHNOLOGY, Arrays.asList(
                Pattern.compile("\\b(computer|software|program|code|java|python|ai|robot)\\b"),
                Pattern.compile("\\b(technology|tech|internet|web|app)\\b"),
                Pattern.compile("\\b(algorithm|machine learning|artificial intelligence)\\b")
        ));

        // Education patterns
        intentPatterns.put(Intent.EDUCATION, Arrays.asList(
                Pattern.compile("\\b(school|study|learn|education|teacher|student)\\b"),
                Pattern.compile("\\b(university|college|course|lesson|homework)\\b"),
                Pattern.compile("\\b(book|read|knowledge|subject)\\b")
        ));

        // Personal patterns
        intentPatterns.put(Intent.PERSONAL, Arrays.asList(
                Pattern.compile("\\b(i am|i'm|i feel|i think|i like|i love|i hate)\\b"),
                Pattern.compile("\\bmy name is\\b"),
                Pattern.compile("\\bi have\\b"),
                Pattern.compile("\\btell me about yourself\\b")
        ));
    }

    /**
     * Initialize sentiment analysis word lists
     */
    private void initializeSentimentWords() {
        positiveWords = Set.of(
                "good", "great", "excellent", "amazing", "wonderful", "fantastic", "awesome", "perfect",
                "happy", "glad", "pleased", "satisfied", "love", "like", "enjoy", "appreciate",
                "beautiful", "nice", "cool", "fun", "exciting", "interesting", "helpful", "useful",
                "thank", "thanks", "brilliant", "outstanding", "superb", "marvelous", "delighted",
                "positive", "optimistic", "confident", "successful", "win", "victory", "achieve"
        );

        negativeWords = Set.of(
                "bad", "terrible", "awful", "horrible", "disgusting", "hate", "dislike", "annoying",
                "sad", "angry", "mad", "upset", "disappointed", "frustrated", "worried", "concerned",
                "problem", "issue", "trouble", "difficult", "hard", "impossible", "wrong", "error",
                "fail", "failure", "lose", "lost", "broken", "damaged", "hurt", "pain", "suffer",
                "negative", "pessimistic", "depressed", "anxious", "fear", "scared", "boring", "dull"
        );
    }
}