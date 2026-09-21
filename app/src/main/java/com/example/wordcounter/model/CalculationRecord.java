package com.example.wordcounter.model;

import com.example.wordcounter.analyzer.TextMetrics;

import java.util.Objects;

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
     * „Tekstas" => 1, 2, 11, 0
     * Truncates safely at Unicode code-point boundaries to avoid splitting emojis.
     */
    public String toHistoryDisplayString() {
        String cleanText = originalText.replace("\n", " ");
        // Count by code points so emojis (surrogate pairs) are not split in half
        int codePointLen = cleanText.codePointCount(0, cleanText.length());
        if (codePointLen > 40) {
            int cutOffset = cleanText.offsetByCodePoints(0, 37);
            cleanText = cleanText.substring(0, cutOffset) + "...";
        }
        return "\u201E" + cleanText + "\u201C => " + metrics.toSummaryString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalculationRecord that = (CalculationRecord) o;
        return timestamp == that.timestamp &&
                Objects.equals(originalText, that.originalText) &&
                Objects.equals(metrics, that.metrics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalText, metrics, timestamp);
    }
}
