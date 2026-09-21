package com.example.wordcounter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.wordcounter.analyzer.TextMetrics;
import com.example.wordcounter.analyzer.TextMetricsCalculator;

import org.junit.Before;
import org.junit.Test;

public class TextMetricsCalculatorTest {

    private TextMetricsCalculator calculator;

    @Before
    public void setUp() {
        calculator = new TextMetricsCalculator();
    }

    // -----------------------------------------------------------------------
    // Specification examples
    // -----------------------------------------------------------------------

    @Test
    public void testSpecificationExample1_LabasRytas() {
        // „Labas rytas" => 1 (sakinys), 2 (žodžiai), 11 (ženklų), 0 (skaičių)
        String input = "Labas rytas";
        TextMetrics metrics = calculator.calculate(input);

        assertNotNull(metrics);
        assertEquals("Sentence count should be 1", 1, metrics.getSentenceCount());
        assertEquals("Word count should be 2", 2, metrics.getWordCount());
        assertEquals("Character count should be 11", 11, metrics.getCharacterCount());
        assertEquals("Number count should be 0", 0, metrics.getNumberCount());
        assertEquals("Summary string mismatch", "1, 2, 11, 0", metrics.toSummaryString());
    }

    @Test
    public void testSpecificationExample2_Rugsejo1() {
        // „Rugsėjo 1 prasidėjo karas. Liūdna" => 2, 4, 33, 1
        String input = "Rugsėjo 1 prasidėjo karas. Liūdna";
        TextMetrics metrics = calculator.calculate(input);

        assertNotNull(metrics);
        assertEquals("Sentence count should be 2", 2, metrics.getSentenceCount());
        assertEquals("Word count should be 4", 4, metrics.getWordCount());
        assertEquals("Character count should be 33", 33, metrics.getCharacterCount());
        assertEquals("Number count should be 1", 1, metrics.getNumberCount());
        assertEquals("Summary string mismatch", "2, 4, 33, 1", metrics.toSummaryString());
    }

    // -----------------------------------------------------------------------
    // Edge cases – null / empty / whitespace
    // -----------------------------------------------------------------------

    @Test
    public void testEmptyAndNullString() {
        TextMetrics nullMetrics = calculator.calculate(null);
        assertEquals(0, nullMetrics.getSentenceCount());
        assertEquals(0, nullMetrics.getWordCount());
        assertEquals(0, nullMetrics.getCharacterCount());
        assertEquals(0, nullMetrics.getNumberCount());

        TextMetrics emptyMetrics = calculator.calculate("");
        assertEquals(0, emptyMetrics.getSentenceCount());
        assertEquals(0, emptyMetrics.getWordCount());
        assertEquals(0, emptyMetrics.getCharacterCount());
        assertEquals(0, emptyMetrics.getNumberCount());
    }

    @Test
    public void testWhitespaceOnly() {
        TextMetrics metrics = calculator.calculate("   \t\n  ");
        assertEquals(0, metrics.getSentenceCount());
        assertEquals(0, metrics.getWordCount());
        // 7 characters total (spaces + tab + newline)
        assertEquals(7, metrics.getCharacterCount());
        assertEquals(0, metrics.getNumberCount());
    }

    // -----------------------------------------------------------------------
    // Punctuation and multiple sentence delimiters
    // -----------------------------------------------------------------------

    @Test
    public void testMultiplePunctuationMarks() {
        String input = "Pirmas sakinys... Antras sakinys?! Trečias sakinys.";
        TextMetrics metrics = calculator.calculate(input);

        assertEquals(3, metrics.getSentenceCount());
        assertEquals(6, metrics.getWordCount());
        assertEquals(0, metrics.getNumberCount());
    }

    // -----------------------------------------------------------------------
    // Numbers – decimals, negatives, thousands separators
    // -----------------------------------------------------------------------

    @Test
    public void testDecimalsAndNegativeNumbers() {
        String input = "Pi reikšmė yra 3.14. Šiandien šalna: -5 laipsniai!";
        TextMetrics metrics = calculator.calculate(input);

        // Sentences: "Pi reikšmė yra 3.14" and "Šiandien šalna: -5 laipsniai!" => 2
        assertEquals(2, metrics.getSentenceCount());
        // Words: "Pi", "reikšmė", "yra", "Šiandien", "šalna", "laipsniai" => 6
        assertEquals(6, metrics.getWordCount());
        // Numbers: "3.14", "-5" => 2
        assertEquals(2, metrics.getNumberCount());
    }

    @Test
    public void testThousandsSeparatorNumbers() {
        // 1,000,000 is a single token and matches NUMBER_PATTERN => 1 number
        // "2 500 000" splits by space into 3 tokens ("2", "500", "000"), each a number => 3 numbers
        // Total numbers = 4
        String input = "Populiacija siekia 1,000,000 žmonių. Biudžetas yra 2 500 000 eurų.";
        TextMetrics metrics = calculator.calculate(input);

        assertEquals(2, metrics.getSentenceCount());
        // words: Populiacija, siekia, žmonių, Biudžetas, yra, eurų
        assertEquals(6, metrics.getWordCount());
        // numbers: 1,000,000 | 2 | 500 | 000
        assertEquals(4, metrics.getNumberCount());
    }

    @Test
    public void testCommaDecimalNumber() {
        // Lithuanian convention uses comma as decimal separator: 3,14
        String input = "Reikšmė 3,14 yra pi.";
        TextMetrics metrics = calculator.calculate(input);

        assertEquals(1, metrics.getSentenceCount());
        // words: Reikšmė, yra, pi (pi is a word with letters)
        assertEquals(3, metrics.getWordCount());
        assertEquals(1, metrics.getNumberCount()); // 3,14
    }

    // -----------------------------------------------------------------------
    // Lithuanian characters
    // -----------------------------------------------------------------------

    @Test
    public void testLithuanianCharacters() {
        String input = "Ąžuolas, šaka, įlanka, žąsis.";
        TextMetrics metrics = calculator.calculate(input);

        assertEquals(1, metrics.getSentenceCount());
        assertEquals(4, metrics.getWordCount());
        assertEquals(0, metrics.getNumberCount());
    }

    // -----------------------------------------------------------------------
    // Unicode non-breaking spaces
    // -----------------------------------------------------------------------

    @Test
    public void testNonBreakingSpaces() {
        // Words separated by non-breaking space \u00A0 (common in copy-paste from browsers/PDFs)
        String input = "Labas\u00A0rytas";
        TextMetrics metrics = calculator.calculate(input);

        assertEquals(1, metrics.getSentenceCount());
        assertEquals(2, metrics.getWordCount()); // must split correctly
        assertEquals(0, metrics.getNumberCount());
    }

    // -----------------------------------------------------------------------
    // Emoji / Unicode surrogate pairs – character count
    // -----------------------------------------------------------------------

    @Test
    public void testEmojiCharacterCount() {
        // "Hi 😊" – 4 code points: H(1), i(1), space(1), 😊(1 code point, 2 UTF-16 units)
        String input = "Hi \uD83D\uDE0A";
        TextMetrics metrics = calculator.calculate(input);

        // codePointCount should return 4, NOT 5 (emoji is 1 code point but 2 char units)
        assertEquals(4, metrics.getCharacterCount());
        assertEquals(1, metrics.getWordCount()); // "Hi"
    }

    // -----------------------------------------------------------------------
    // Mixed content
    // -----------------------------------------------------------------------

    @Test
    public void testMixedTextFromSpecSamples() {
        // From TestSentenceGenerator sample
        String input = "Skaičiai: 10, 20 ir 30. Rezultatas yra 60!";
        TextMetrics metrics = calculator.calculate(input);

        assertEquals(2, metrics.getSentenceCount());
        // words: Skaičiai, ir, Rezultatas, yra
        assertEquals(4, metrics.getWordCount());
        // numbers: 10, 20, 30, 60
        assertEquals(4, metrics.getNumberCount());
    }
}
