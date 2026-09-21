package com.example.wordcounter.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wordcounter.analyzer.TestSentenceGenerator;
import com.example.wordcounter.analyzer.TextMetrics;
import com.example.wordcounter.analyzer.TextMetricsCalculator;
import com.example.wordcounter.model.CalculationRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel that owns all UI state for MainActivity.
 * Survives configuration changes (screen rotation, theme switch) automatically.
 * Offloads text metric computation to a background thread to prevent ANR.
 */
public class WordCounterViewModel extends ViewModel {

    private final TextMetricsCalculator calculator = new TextMetricsCalculator();
    private final TestSentenceGenerator sentenceGenerator = new TestSentenceGenerator();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /** Emits the latest calculation result, or null if none yet. */
    private final MutableLiveData<TextMetrics> _metrics = new MutableLiveData<>(null);
    public final LiveData<TextMetrics> metrics = _metrics;

    /** Emits the full (newest-first) history list every time it changes. */
    private final MutableLiveData<List<CalculationRecord>> _history =
            new MutableLiveData<>(Collections.emptyList());
    public final LiveData<List<CalculationRecord>> history = _history;

    /** True while a calculation is running in the background. */
    private final MutableLiveData<Boolean> _calculating = new MutableLiveData<>(false);
    public final LiveData<Boolean> calculating = _calculating;

    /**
     * Schedules a background calculation for the given text.
     * Posts the result back to LiveData on the main thread.
     *
     * @param rawText the text from the input field (may be empty/null)
     * @return true if the calculation was scheduled, false if text is blank
     */
    public boolean requestCalculation(String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            return false;
        }

        _calculating.setValue(true);
        executor.execute(() -> {
            TextMetrics result = calculator.calculate(rawText);
            CalculationRecord record = new CalculationRecord(rawText.trim(), result);

            // Build new history list (newest first, capped at 100 entries)
            List<CalculationRecord> currentHistory = _history.getValue();
            List<CalculationRecord> updatedHistory = new ArrayList<>();
            updatedHistory.add(record);
            if (currentHistory != null) {
                int remaining = Math.min(currentHistory.size(), 99);
                updatedHistory.addAll(currentHistory.subList(0, remaining));
            }

            _metrics.postValue(result);
            _history.postValue(Collections.unmodifiableList(updatedHistory));
            _calculating.postValue(false);
        });
        return true;
    }

    /**
     * Returns the next sample sentence in sequential order.
     */
    public String getNextSampleSentence() {
        return sentenceGenerator.getNextSentence();
    }

    /**
     * Clears the history list.
     */
    public void clearHistory() {
        _history.setValue(Collections.emptyList());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdownNow();
    }
}
