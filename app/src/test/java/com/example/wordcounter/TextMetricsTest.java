package com.example.wordcounter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import com.example.wordcounter.analyzer.TextMetrics;

import org.junit.Test;

/**
 * Unit tests for the {@link TextMetrics} immutable data model.
 */
public class TextMetricsTest {

    // -----------------------------------------------------------------------
    // Constructor and getters
    // -----------------------------------------------------------------------

    @Test
    public void testGettersReturnConstructorValues() {
        TextMetrics m = new TextMetrics(2, 5, 30, 1);
        assertEquals(2, m.getSentenceCount());
        assertEquals(5, m.getWordCount());
        assertEquals(30, m.getCharacterCount());
        assertEquals(1, m.getNumberCount());
    }

    @Test
    public void testAllZeroValues() {
        TextMetrics m = new TextMetrics(0, 0, 0, 0);
        assertEquals(0, m.getSentenceCount());
        assertEquals(0, m.getWordCount());
        assertEquals(0, m.getCharacterCount());
        assertEquals(0, m.getNumberCount());
    }

    @Test
    public void testLargeValues() {
        TextMetrics m = new TextMetrics(Integer.MAX_VALUE, Integer.MAX_VALUE,
                Integer.MAX_VALUE, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, m.getSentenceCount());
        assertEquals(Integer.MAX_VALUE, m.getWordCount());
        assertEquals(Integer.MAX_VALUE, m.getCharacterCount());
        assertEquals(Integer.MAX_VALUE, m.getNumberCount());
    }

    // -----------------------------------------------------------------------
    // toSummaryString
    // -----------------------------------------------------------------------

    @Test
    public void testToSummaryStringAllZeros() {
        assertEquals("0, 0, 0, 0", new TextMetrics(0, 0, 0, 0).toSummaryString());
    }

    @Test
    public void testToSummaryStringTypicalValues() {
        assertEquals("2, 4, 33, 1", new TextMetrics(2, 4, 33, 1).toSummaryString());
    }

    @Test
    public void testToSummaryStringFormat() {
        // Format must be exactly "s, w, c, n"
        TextMetrics m = new TextMetrics(1, 2, 11, 0);
        assertEquals("1, 2, 11, 0", m.toSummaryString());
    }

    // -----------------------------------------------------------------------
    // equals
    // -----------------------------------------------------------------------

    @Test
    public void testEqualsIdentical() {
        TextMetrics a = new TextMetrics(1, 2, 11, 0);
        TextMetrics b = new TextMetrics(1, 2, 11, 0);
        assertEquals(a, b);
    }

    @Test
    public void testEqualsSelf() {
        TextMetrics m = new TextMetrics(3, 5, 20, 2);
        assertEquals(m, m);
    }

    @Test
    public void testNotEqualsWhenSentenceDiffers() {
        assertNotEquals(new TextMetrics(1, 2, 11, 0), new TextMetrics(2, 2, 11, 0));
    }

    @Test
    public void testNotEqualsWhenWordDiffers() {
        assertNotEquals(new TextMetrics(1, 2, 11, 0), new TextMetrics(1, 3, 11, 0));
    }

    @Test
    public void testNotEqualsWhenCharDiffers() {
        assertNotEquals(new TextMetrics(1, 2, 11, 0), new TextMetrics(1, 2, 12, 0));
    }

    @Test
    public void testNotEqualsWhenNumberDiffers() {
        assertNotEquals(new TextMetrics(1, 2, 11, 0), new TextMetrics(1, 2, 11, 1));
    }

    @Test
    public void testNotEqualsNull() {
        assertNotEquals(null, new TextMetrics(1, 2, 11, 0));
    }

    @Test
    public void testNotEqualsOtherType() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals("string", new TextMetrics(1, 2, 11, 0));
    }

    // -----------------------------------------------------------------------
    // hashCode
    // -----------------------------------------------------------------------

    @Test
    public void testHashCodeConsistency() {
        TextMetrics m = new TextMetrics(1, 2, 11, 0);
        assertEquals(m.hashCode(), m.hashCode());
    }

    @Test
    public void testHashCodeEqualObjects() {
        TextMetrics a = new TextMetrics(2, 4, 33, 1);
        TextMetrics b = new TextMetrics(2, 4, 33, 1);
        assertEquals(a.hashCode(), b.hashCode());
    }

    // -----------------------------------------------------------------------
    // toString
    // -----------------------------------------------------------------------

    @Test
    public void testToStringContainsAllFields() {
        TextMetrics m = new TextMetrics(1, 2, 11, 0);
        String s = m.toString();
        assertNotNull(s);
        // toString should mention all four metric names
        assertEquals(true, s.contains("sentenceCount"));
        assertEquals(true, s.contains("wordCount"));
        assertEquals(true, s.contains("characterCount"));
        assertEquals(true, s.contains("numberCount"));
    }

    @Test
    public void testToStringContainsValues() {
        TextMetrics m = new TextMetrics(3, 7, 42, 5);
        String s = m.toString();
        assertEquals(true, s.contains("3"));
        assertEquals(true, s.contains("7"));
        assertEquals(true, s.contains("42"));
        assertEquals(true, s.contains("5"));
    }
}
