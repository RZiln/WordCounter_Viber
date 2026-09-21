package com.example.wordcounter.analyzer;

import java.util.Objects;

/**
 * Immutable model holding computed metrics for a given text.
 */
public final class TextMetrics {

    private final int sentenceCount;
    private final int wordCount;
    private final int characterCount;
    private final int numberCount;

    public TextMetrics(int sentenceCount, int wordCount, int characterCount, int numberCount) {
        this.sentenceCount = sentenceCount;
        this.wordCount = wordCount;
        this.characterCount = characterCount;
        this.numberCount = numberCount;
    }

    public int getSentenceCount() {
        return sentenceCount;
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getCharacterCount() {
        return characterCount;
    }

    public int getNumberCount() {
        return numberCount;
    }

    /**
     * Returns formatted metrics summary, e.g. "1, 2, 11, 0".
     */
    public String toSummaryString() {
        return sentenceCount + ", " + wordCount + ", " + characterCount + ", " + numberCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextMetrics that = (TextMetrics) o;
        return sentenceCount == that.sentenceCount &&
                wordCount == that.wordCount &&
                characterCount == that.characterCount &&
                numberCount == that.numberCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sentenceCount, wordCount, characterCount, numberCount);
    }

    @Override
    public String toString() {
        return "TextMetrics{" +
                "sentenceCount=" + sentenceCount +
                ", wordCount=" + wordCount +
                ", characterCount=" + characterCount +
                ", numberCount=" + numberCount +
                '}';
    }
}
