package com.example.wordcounter.analyzer;

import java.util.regex.Pattern;

/**
 * Core text metrics analyzer.
 * Calculates:
 * 1. Sentence count
 * 2. Word count (excluding numbers)
 * 3. Character count (including spaces and punctuation)
 * 4. Number count
 *
 * Designed as a standalone, reusable class with zero Android dependencies.
 */
public class TextMetricsCalculator {

    private static final Pattern SENTENCE_DELIMITER_PATTERN = Pattern.compile("[.!?]+(?![0-9])");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+([.,]\\d+)?$");
    private static final Pattern CONTAINS_LETTER_PATTERN = Pattern.compile(".*\\p{L}.*");
    private static final Pattern CONTAINS_ALPHANUMERIC_PATTERN = Pattern.compile(".*[\\p{L}\\d].*");

    /**
     * Calculates metrics for the given input text.
     *
     * @param text input text to analyze
     * @return TextMetrics object containing computed counts
     */
    public TextMetrics calculate(String text) {
        if (text == null || text.isEmpty()) {
            return new TextMetrics(0, 0, 0, 0);
        }

        int characterCount = text.length();
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return new TextMetrics(0, 0, characterCount, 0);
        }

        int sentenceCount = calculateSentenceCount(trimmed);
        int[] wordAndNumberCounts = calculateWordsAndNumbers(trimmed);
        int wordCount = wordAndNumberCounts[0];
        int numberCount = wordAndNumberCounts[1];

        return new TextMetrics(sentenceCount, wordCount, characterCount, numberCount);
    }

    private int calculateSentenceCount(String trimmedText) {
        String[] rawSentences = SENTENCE_DELIMITER_PATTERN.split(trimmedText);
        int count = 0;
        for (String sentence : rawSentences) {
            String trimmedSentence = sentence.trim();
            if (CONTAINS_ALPHANUMERIC_PATTERN.matcher(trimmedSentence).matches()) {
                count++;
            }
        }
        return count;
    }

    private int[] calculateWordsAndNumbers(String trimmedText) {
        int words = 0;
        int numbers = 0;

        String[] tokens = trimmedText.split("\\s+");
        for (String token : tokens) {
            if (token == null || token.isEmpty()) {
                continue;
            }

            // Strip leading/trailing punctuation, keeping potential negative sign for numbers
            String cleaned = token.replaceAll("^[^\\p{L}\\d\\-]+|[^\\p{L}\\d]+$", "");
            if (cleaned.isEmpty()) {
                continue;
            }

            if (NUMBER_PATTERN.matcher(cleaned).matches()) {
                numbers++;
            } else if (CONTAINS_LETTER_PATTERN.matcher(cleaned).matches()) {
                words++;
            }
        }

        return new int[]{words, numbers};
    }
}
