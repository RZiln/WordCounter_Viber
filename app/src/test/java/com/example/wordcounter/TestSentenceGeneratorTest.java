package com.example.wordcounter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.wordcounter.analyzer.TestSentenceGenerator;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Unit tests for {@link TestSentenceGenerator}.
 */
public class TestSentenceGeneratorTest {

    private TestSentenceGenerator generator;

    @Before
    public void setUp() {
        generator = new TestSentenceGenerator();
    }

    // -----------------------------------------------------------------------
    // getAllSampleSentences
    // -----------------------------------------------------------------------

    @Test
    public void testGetAllSampleSentencesIsNotEmpty() {
        List<String> sentences = generator.getAllSampleSentences();
        assertNotNull(sentences);
        assertFalse("sample list must not be empty", sentences.isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetAllSampleSentencesIsImmutable() {
        // Adding to the returned list should throw UnsupportedOperationException
        generator.getAllSampleSentences().add("should fail");
    }

    @Test
    public void testAllSentencesAreNonNull() {
        for (String s : generator.getAllSampleSentences()) {
            assertNotNull("each sentence must be non-null", s);
        }
    }

    @Test
    public void testAllSentencesAreNonEmpty() {
        for (String s : generator.getAllSampleSentences()) {
            assertFalse("each sentence must not be empty", s.trim().isEmpty());
        }
    }

    // -----------------------------------------------------------------------
    // getNextSentence – sequential cycling
    // -----------------------------------------------------------------------

    @Test
    public void testGetNextSentenceReturnsNonNull() {
        assertNotNull(generator.getNextSentence());
    }

    @Test
    public void testGetNextSentenceCyclesThroughAll() {
        List<String> all = generator.getAllSampleSentences();
        int size = all.size();

        // Collect one full cycle
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < size; i++) {
            seen.add(generator.getNextSentence());
        }
        // Every predefined sentence must have been returned
        assertTrue("every sentence should appear in one full cycle",
                seen.containsAll(all));
    }

    @Test
    public void testGetNextSentenceWrapsAround() {
        List<String> all = generator.getAllSampleSentences();
        int size = all.size();

        // Exhaust one full cycle
        for (int i = 0; i < size; i++) {
            generator.getNextSentence();
        }
        // The next call should return the first sentence again
        String first = all.get(0);
        String afterWrap = generator.getNextSentence();
        assertTrue("after full cycle, next sentence should be the first one again",
                first.equals(afterWrap));
    }

    @Test
    public void testGetNextSentenceMatchesPredefinedList() {
        List<String> all = generator.getAllSampleSentences();
        // The first call must return the first element
        String first = generator.getNextSentence();
        assertTrue("first getNextSentence() must match all.get(0)",
                all.get(0).equals(first));
    }

    // -----------------------------------------------------------------------
    // getRandomSentence
    // -----------------------------------------------------------------------

    @Test
    public void testGetRandomSentenceReturnsNonNull() {
        assertNotNull(generator.getRandomSentence());
    }

    @Test
    public void testGetRandomSentenceReturnsNonEmpty() {
        assertFalse(generator.getRandomSentence().trim().isEmpty());
    }

    @Test
    public void testGetRandomSentenceAlwaysFromKnownList() {
        List<String> all = generator.getAllSampleSentences();
        // Sample many times to get statistical confidence
        for (int i = 0; i < 100; i++) {
            String sentence = generator.getRandomSentence();
            assertTrue("random sentence must be from the predefined list",
                    all.contains(sentence));
        }
    }

    @Test
    public void testGetRandomSentenceWithFixedSeedIsDeterministic() {
        Random seed42a = new Random(42);
        Random seed42b = new Random(42);
        TestSentenceGenerator genA = new TestSentenceGenerator(seed42a);
        TestSentenceGenerator genB = new TestSentenceGenerator(seed42b);
        // Same seed => same sequence
        for (int i = 0; i < 20; i++) {
            assertTrue(genA.getRandomSentence().equals(genB.getRandomSentence()));
        }
    }

    @Test
    public void testGetRandomSentenceAndGetNextSentenceAreIndependent() {
        // Calling getRandomSentence should not affect the getNextSentence counter
        List<String> all = generator.getAllSampleSentences();
        // Consume several random sentences
        for (int i = 0; i < 10; i++) {
            generator.getRandomSentence();
        }
        // The first getNextSentence() should still return the first element
        String first = generator.getNextSentence();
        assertTrue("getRandomSentence should not affect the sequential index",
                all.get(0).equals(first));
    }
}
