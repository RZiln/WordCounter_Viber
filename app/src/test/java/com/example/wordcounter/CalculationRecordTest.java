package com.example.wordcounter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.wordcounter.analyzer.TextMetrics;
import com.example.wordcounter.model.CalculationRecord;

import org.junit.Test;

/**
 * Unit tests for {@link CalculationRecord}.
 */
public class CalculationRecordTest {

    private static final TextMetrics SAMPLE_METRICS = new TextMetrics(1, 2, 11, 0);

    // -----------------------------------------------------------------------
    // Getters
    // -----------------------------------------------------------------------

    @Test
    public void testGetOriginalText() {
        CalculationRecord r = new CalculationRecord("Hello world", SAMPLE_METRICS);
        assertEquals("Hello world", r.getOriginalText());
    }

    @Test
    public void testGetMetrics() {
        CalculationRecord r = new CalculationRecord("text", SAMPLE_METRICS);
        assertEquals(SAMPLE_METRICS, r.getMetrics());
    }

    @Test
    public void testGetTimestampIsPositive() {
        CalculationRecord r = new CalculationRecord("text", SAMPLE_METRICS);
        assertTrue("timestamp should be positive", r.getTimestamp() > 0);
    }

    @Test
    public void testCustomTimestampConstructor() {
        long ts = 1_700_000_000_000L;
        CalculationRecord r = new CalculationRecord("text", SAMPLE_METRICS, ts);
        assertEquals(ts, r.getTimestamp());
    }

    // -----------------------------------------------------------------------
    // toHistoryDisplayString – short text
    // -----------------------------------------------------------------------

    @Test
    public void testHistoryDisplayShortText() {
        CalculationRecord r = new CalculationRecord("Labas rytas", SAMPLE_METRICS);
        String display = r.toHistoryDisplayString();
        assertNotNull(display);
        assertTrue("should contain the original text", display.contains("Labas rytas"));
        assertTrue("should contain summary", display.contains("1, 2, 11, 0"));
        assertTrue("should contain '=>' separator", display.contains("=>"));
    }

    @Test
    public void testHistoryDisplayFormat() {
        // Format: „text" => s, w, c, n
        CalculationRecord r = new CalculationRecord("Hi", new TextMetrics(1, 1, 2, 0));
        String display = r.toHistoryDisplayString();
        // Opening and closing Lithuanian quotation marks
        assertTrue(display.startsWith("\u201E"));
        assertTrue(display.contains("\u201C =>"));
    }

    // -----------------------------------------------------------------------
    // toHistoryDisplayString – long text truncation
    // -----------------------------------------------------------------------

    @Test
    public void testHistoryDisplayLongTextTruncated() {
        // Text longer than 40 code points should be truncated with "..."
        String longText = "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz";
        CalculationRecord r = new CalculationRecord(longText, SAMPLE_METRICS);
        String display = r.toHistoryDisplayString();
        assertTrue("truncated display should contain '...'", display.contains("..."));
    }

    @Test
    public void testHistoryDisplayExactly40CharsIsNotTruncated() {
        // Exactly 40 ASCII chars should NOT be truncated
        String text40 = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"; // 40 chars
        CalculationRecord r = new CalculationRecord(text40, SAMPLE_METRICS);
        String display = r.toHistoryDisplayString();
        assertFalse("40-char text should NOT be truncated", display.contains("..."));
    }

    @Test
    public void testHistoryDisplayNewlinesReplacedWithSpaces() {
        String textWithNewlines = "Line one\nLine two";
        CalculationRecord r = new CalculationRecord(textWithNewlines, SAMPLE_METRICS);
        String display = r.toHistoryDisplayString();
        assertFalse("newlines should be removed from display", display.contains("\n"));
        assertTrue("newline should be replaced by space", display.contains("Line one Line two"));
    }

    @Test
    public void testHistoryDisplayEmojiTruncationIsSafe() {
        // Build a string of 39 ASCII chars + 1 emoji (2 UTF-16 units) = > 40 code points at 41
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 39; i++) sb.append('A');
        sb.append("\uD83D\uDE0A"); // 1 code point, 2 char units
        // total = 40 code points => should NOT be truncated
        CalculationRecord r = new CalculationRecord(sb.toString(), SAMPLE_METRICS);
        String display = r.toHistoryDisplayString();
        assertFalse("40-code-point text with emoji should not be truncated", display.contains("..."));
    }

    // -----------------------------------------------------------------------
    // equals and hashCode
    // -----------------------------------------------------------------------

    @Test
    public void testEqualsSameFields() {
        long ts = 1_000L;
        CalculationRecord a = new CalculationRecord("text", SAMPLE_METRICS, ts);
        CalculationRecord b = new CalculationRecord("text", SAMPLE_METRICS, ts);
        assertEquals(a, b);
    }

    @Test
    public void testEqualsSelf() {
        CalculationRecord r = new CalculationRecord("text", SAMPLE_METRICS, 999L);
        assertEquals(r, r);
    }

    @Test
    public void testNotEqualsWhenTextDiffers() {
        long ts = 1_000L;
        CalculationRecord a = new CalculationRecord("text A", SAMPLE_METRICS, ts);
        CalculationRecord b = new CalculationRecord("text B", SAMPLE_METRICS, ts);
        assertNotEquals(a, b);
    }

    @Test
    public void testNotEqualsWhenMetricsDiffers() {
        long ts = 1_000L;
        CalculationRecord a = new CalculationRecord("text", SAMPLE_METRICS, ts);
        CalculationRecord b = new CalculationRecord("text", new TextMetrics(9, 9, 9, 9), ts);
        assertNotEquals(a, b);
    }

    @Test
    public void testNotEqualsWhenTimestampDiffers() {
        CalculationRecord a = new CalculationRecord("text", SAMPLE_METRICS, 1L);
        CalculationRecord b = new CalculationRecord("text", SAMPLE_METRICS, 2L);
        assertNotEquals(a, b);
    }

    @Test
    public void testHashCodeEqualObjects() {
        long ts = 5_000L;
        CalculationRecord a = new CalculationRecord("hello", SAMPLE_METRICS, ts);
        CalculationRecord b = new CalculationRecord("hello", SAMPLE_METRICS, ts);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testHashCodeConsistency() {
        CalculationRecord r = new CalculationRecord("hello", SAMPLE_METRICS, 1L);
        assertEquals(r.hashCode(), r.hashCode());
    }
}
