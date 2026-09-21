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

    @Test
    public void testSpecificationExample1_LabasRytas() {
        // „Labas rytas“ => 1 (sakinys), 2 (žodžiai), 11 (rašybos ženklų), 0 (skaičių)
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
        // „Rugsėjo 1 prasidėjo karas. Liūdna“ => 2, 4, 33, 1
        String input = "Rugsėjo 1 prasidėjo karas. Liūdna";
        TextMetrics metrics = calculator.calculate(input);

        assertNotNull(metrics);
        assertEquals("Sentence count should be 2", 2, metrics.getSentenceCount());
        assertEquals("Word count should be 4", 4, metrics.getWordCount());
        assertEquals("Character count should be 33", 33, metrics.getCharacterCount());
        assertEquals("Number count should be 1", 1, metrics.getNumberCount());
        assertEquals("Summary string mismatch", "2, 4, 33, 1", metrics.toSummaryString());
    }

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
        assertEquals(7, metrics.getCharacterCount());
        assertEquals(0, metrics.getNumberCount());
    }

    @Test
    public void testMultiplePunctuationMarks() {
        String input = "Pirmas sakinys... Antras sakinys?! Trečias sakinys.";
        TextMetrics metrics = calculator.calculate(input);

        assertEquals(3, metrics.getSentenceCount());
        assertEquals(6, metrics.getWordCount());
        assertEquals(0, metrics.getNumberCount());
    }

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
    public void testLithuanianCharacters() {
        String input = "Ąžuolas, šaka, įlanka, žąsis.";
        TextMetrics metrics = calculator.calculate(input);

        assertEquals(1, metrics.getSentenceCount());
        assertEquals(4, metrics.getWordCount());
        assertEquals(0, metrics.getNumberCount());
    }
}
