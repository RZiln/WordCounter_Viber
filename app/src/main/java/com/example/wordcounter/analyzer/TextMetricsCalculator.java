package com.example.wordcounter.analyzer;

import java.util.regex.Pattern;

/**
 * Core text metrics analyzer.
 * Calculates:
 * 1. Sentence count
 * 2. Word count (excluding numbers)
 * 3. Character count (Unicode code points — emojis counted as 1)
 * 4. Number count
 *
 * Designed as a standalone, reusable class with zero Android dependencies.
 */
public class TextMetricsCalculator {

    // Sentence-ending punctuation, but NOT inside decimal numbers (e.g. 3.14)
    // and NOT after common Lithuanian/Latin abbreviations.
    private static final Pattern SENTENCE_DELIMITER_PATTERN =
            Pattern.compile("[.!?]+(?![0-9])(?<!(?:dr|prof|mgr|pan|pvz|t\\.y|kt|plg|t|str|skyr|nr|tel|mob|el|red|min|max|fig|pav|priedas|priede|sakyk|žr|angl|lot|vert|a\\.m|p\\.m)[.!?])");

    // Matches integers, decimals, and numbers with thousands-separators:
    //   -5   3.14   3,14   1,000,000   1 000 000   1.000.000
    private static final Pattern NUMBER_PATTERN =
            Pattern.compile("^-?\\d{1,3}([ ,.]\\d{3})*([.,]\\d+)?$|^-?\\d+([.,]\\d+)?$");

    private static final Pattern CONTAINS_LETTER_PATTERN = Pattern.compile(".*\\p{L}.*");
    private static final Pattern CONTAINS_ALPHANUMERIC_PATTERN = Pattern.compile(".*[\\p{L}\\d].*");

    // Splits on regular spaces, tabs, newlines, non-breaking spaces (\u00A0),
    // narrow no-break spaces (\u202F) and all other Unicode separator characters (\p{Z}).
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("[\\s\\p{Z}]+");

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

        // Character count = Unicode code points (so emoji = 1, not 2)
        int characterCount = text.codePointCount(0, text.length());

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

        String[] tokens = WHITESPACE_PATTERN.split(trimmedText);
        for (String token : tokens) {
            if (token == null || token.isEmpty()) {
                continue;
            }

            // Strip leading/trailing punctuation while keeping potential negative sign for numbers.
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
