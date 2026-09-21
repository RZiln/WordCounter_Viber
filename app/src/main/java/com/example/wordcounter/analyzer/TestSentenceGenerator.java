package com.example.wordcounter.analyzer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generator for sample sentences used for testing and demonstration.
 */
public class TestSentenceGenerator {

    private static final List<String> SAMPLE_SENTENCES = Collections.unmodifiableList(Arrays.asList(
            "Labas rytas",
            "Rugsėjo 1 prasidėjo karas. Liūdna",
            "Šiandien 2026 metais sukūrėme 3 puikias programėles! Ar viskas veikia gerai?",
            "Kaunas ir Vilnius – 2 didžiausi Lietuvos miestai. Juose gyvena apie 850000 gyventojų.",
            "Skaičiai: 10, 20 ir 30. Rezultatas yra 60!",
            "Testas be skyrybos ženklų su 1 skaičiumi",
            "Ar žinojote, kad Lietuvoje yra daugiau nei 6000 ežerų? Tai nuostabu!"
    ));

    private final Random random;
    private int currentIndex = 0;

    public TestSentenceGenerator() {
        this(new Random());
    }

    public TestSentenceGenerator(Random random) {
        this.random = random;
    }

    /**
     * Returns a random test sentence from the predefined list.
     */
    public String getRandomSentence() {
        return SAMPLE_SENTENCES.get(random.nextInt(SAMPLE_SENTENCES.size()));
    }

    /**
     * Returns the next test sentence in sequential order.
     */
    public String getNextSentence() {
        String sentence = SAMPLE_SENTENCES.get(currentIndex);
        currentIndex = (currentIndex + 1) % SAMPLE_SENTENCES.size();
        return sentence;
    }

    public List<String> getAllSampleSentences() {
        return SAMPLE_SENTENCES;
    }
}
