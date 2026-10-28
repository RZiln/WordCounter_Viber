package com.example.wordcounter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
    // Specification examples (original)
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
    // Edge cases – null / empty / whitespace (original)
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
    // Punctuation and multiple sentence delimiters (original)
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
    // Numbers – decimals, negatives, thousands separators (original)
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
    // Lithuanian characters (original)
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
    // Unicode non-breaking spaces (original)
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
    // Emoji / Unicode surrogate pairs – character count (original)
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
    // Mixed content (original)
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

    // -----------------------------------------------------------------------
    // NEW: Boundary / single-element inputs
    // -----------------------------------------------------------------------

    @Test
    public void testSingleCharacterLetter() {
        TextMetrics metrics = calculator.calculate("A");
        assertEquals(1, metrics.getSentenceCount());
        assertEquals(1, metrics.getWordCount());
        assertEquals(1, metrics.getCharacterCount());
        assertEquals(0, metrics.getNumberCount());
    }

    @Test
    public void testSingleCharacterDigit() {
        TextMetrics metrics = calculator.calculate("7");
        assertEquals(1, metrics.getSentenceCount());
        assertEquals(0, metrics.getWordCount());
        assertEquals(1, metrics.getCharacterCount());
        assertEquals(1, metrics.getNumberCount());
    }

    @Test
    public void testSingleCharacterPunctuation() {
        // A lone "." has no alphanumeric content => 0 sentences
        TextMetrics metrics = calculator.calculate(".");
        assertEquals(0, metrics.getSentenceCount());
        assertEquals(0, metrics.getWordCount());
        assertEquals(1, metrics.getCharacterCount());
        assertEquals(0, metrics.getNumberCount());
    }

    @Test
    public void testSingleWordNoTrailingPunctuation() {
        // Text with no sentence-ending punctuation still counts as 1 sentence
        TextMetrics metrics = calculator.calculate("Hello");
        assertEquals(1, metrics.getSentenceCount());
        assertEquals(1, metrics.getWordCount());
        assertEquals(5, metrics.getCharacterCount());
        assertEquals(0, metrics.getNumberCount());
    }

    @Test
    public void testOnlyPunctuationString() {
        // A string made entirely of punctuation
        TextMetrics metrics = calculator.calculate("!!! ??? ...");
        assertEquals(0, metrics.getSentenceCount());
        assertEquals(0, metrics.getWordCount());
        assertEquals(0, metrics.getNumberCount());
        // Character count equals actual character length (11)
        assertEquals(11, metrics.getCharacterCount());
    }

    @Test
    public void testLeadingAndTrailingWhitespace() {
        // Leading/trailing spaces should NOT affect sentence/word counts
        TextMetrics metrics = calculator.calculate("   Hello world.   ");
        assertEquals(1, metrics.getSentenceCount());
        assertEquals(2, metrics.getWordCount());
        assertEquals(0, metrics.getNumberCount());
        // Character count includes the surrounding spaces (18 chars total)
        assertEquals(18, metrics.getCharacterCount());
    }

    @Test
    public void testMultipleNewlineSeparators() {
        // Newlines act as whitespace splitters
        String input = "First sentence.\nSecond sentence.\nThird sentence.";
        TextMetrics metrics = calculator.calculate(input);
        assertEquals(3, metrics.getSentenceCount());
        assertEquals(6, metrics.getWordCount());
        assertEquals(0, metrics.getNumberCount());
    }

    // -----------------------------------------------------------------------
    // NEW: Sentence-counting rules – abbreviations must NOT split sentences
    // -----------------------------------------------------------------------

    @Test
    public void testAbbreviationDrUppercaseSplitsSentence() {
        // The abbreviation lookbehind only checks lowercase 'dr', so uppercase 'Dr.'
        // IS treated as a sentence boundary — this documents the current behaviour.
        String input = "Dr. Jonas Jonaitis yra gydytojas.";
        TextMetrics metrics = calculator.calculate(input);
        assertEquals(2, metrics.getSentenceCount());
    }

    @Test
    public void testAbbreviationPvzDoesNotSplitSentence() {
        // "pvz." (e.g.) is a common Lithuanian abbreviation and IS in the lookbehind list
        String input = "Spalvos, pvz. raudona ir mėlyna, yra gražios.";
        TextMetrics metrics = calculator.calculate(input);
        assertEquals(1, metrics.getSentenceCount());
    }

    @Test
    public void testDecimalNumberDoesNotSplitSentence() {
        // "3.14" should not be treated as a sentence boundary
        String input = "Skaičius 3.14 yra pi.";
        TextMetrics metrics = calculator.calculate(input);
        assertEquals(1, metrics.getSentenceCount());
        // words: Skaičius, yra, pi
        assertEquals(3, metrics.getWordCount());
        // numbers: 3.14
        assertEquals(1, metrics.getNumberCount());
    }

    @Test
    public void testQuestionAndExclamationInOneSentence() {
        // "?!" together should count as a single sentence boundary
        String input = "Ar tu čia?! Taip!";
        TextMetrics metrics = calculator.calculate(input);
        assertEquals(2, metrics.getSentenceCount());
    }

    @Test
    public void testTextEndingWithoutPunctuation() {
        // Text that ends mid-sentence still counts as one sentence
        String input = "Sakinys be pabaigos";
        TextMetrics metrics = calculator.calculate(input);
        assertEquals(1, metrics.getSentenceCount());
        assertEquals(3, metrics.getWordCount());
    }

    // -----------------------------------------------------------------------
    // NEW: Number variety
    // -----------------------------------------------------------------------

    @Test
    public void testNegativeDecimalNumber() {
        // -3.14 is one number
        String input = "Temperatūra yra -3.14 laipsnių.";
        TextMetrics metrics = calculator.calculate(input);
        assertEquals(1, metrics.getSentenceCount());
        assertEquals(3, metrics.getWordCount()); // Temperatūra, yra, laipsnių
        assertEquals(1, metrics.getNumberCount()); // -3.14
    }

    @Test
    public void testDotThousandsSeparatorNumber() {
        // European format: 1.000.000 is one number token
        String input = "Suma yra 1.000.000 eurų.";
        TextMetrics metrics = calculator.calculate(input);
        assertEquals(1, metrics.getSentenceCount());
        assertEquals(3, metrics.getWordCount()); // Suma, yra, eurų
        assertEquals(1, metrics.getNumberCount()); // 1.000.000
    }

    @Test
    public void testNumberAtStartOfText() {
        String input = "42 programuotojai dirba čia.";
        TextMetrics metrics = calculator.calculate(input);
        assertEquals(1, metrics.getSentenceCount());
        assertEquals(3, metrics.getWordCount()); // programuotojai, dirba, čia
        assertEquals(1, metrics.getNumberCount()); // 42
    }

    @Test
    public void testNumberAtEndOfText() {
        String input = "Atsakymas yra 42.";
        TextMetrics metrics = calculator.calculate(input);
        assertEquals(1, metrics.getSentenceCount());
        assertEquals(2, metrics.getWordCount()); // Atsakymas, yra
        assertEquals(1, metrics.getNumberCount()); // 42
    }

    @Test
    public void testMultipleNegativeNumbers() {
        // A sentence with exactly two clearly separated negative numbers
        String input = "Nuo -10 iki -5.";
        TextMetrics metrics = calculator.calculate(input);
        assertEquals(1, metrics.getSentenceCount());
        // words: Nuo, iki
        assertEquals(2, metrics.getWordCount());
        // numbers: -10, -5
        assertEquals(2, metrics.getNumberCount());
    }

    @Test
    public void testZeroIsANumber() {
        String input = "Rezultatas yra 0.";
        TextMetrics metrics = calculator.calculate(input);
        assertEquals(1, metrics.getSentenceCount());
        assertEquals(2, metrics.getWordCount()); // Rezultatas, yra
        assertEquals(1, metrics.getNumberCount()); // 0
    }

    @Test
    public void testTextWithNoNumbers() {
        String input = "Visai jokių skaičių šiame sakinyje.";
        TextMetrics metrics = calculator.calculate(input);
        assertEquals(0, metrics.getNumberCount());
        assertTrue(metrics.getWordCount() > 0);
    }
}
