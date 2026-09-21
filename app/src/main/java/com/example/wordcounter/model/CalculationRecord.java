package com.example.wordcounter.model;

import com.example.wordcounter.analyzer.TextMetrics;

/**
 * Model representing a single calculation record in the runtime history.
 */
public class CalculationRecord {

    private final String originalText;
    private final TextMetrics metrics;
    private final long timestamp;

    public CalculationRecord(String originalText, TextMetrics metrics) {
        this(originalText, metrics, System.currentTimeMillis());
    }

    public CalculationRecord(String originalText, TextMetrics metrics, long timestamp) {
        this.originalText = originalText;
        this.metrics = metrics;
        this.timestamp = timestamp;
    }

    public String getOriginalText() {
        return originalText;
    }

    public TextMetrics getMetrics() {
        return metrics;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Formats history entry according to requirement:
     * „Tekstas“ => 1, 2, 11, 0
     */
    public String toHistoryDisplayString() {
        String cleanText = originalText.replace("\n", " ");
        if (cleanText.length() > 40) {
            cleanText = cleanText.substring(0, 37) + "...";
        }
        return "„" + cleanText + "“ => " + metrics.toSummaryString();
    }
}
