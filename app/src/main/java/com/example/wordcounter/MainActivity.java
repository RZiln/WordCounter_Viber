package com.example.wordcounter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wordcounter.analyzer.TextMetrics;
import com.example.wordcounter.databinding.ActivityMainBinding;
import com.example.wordcounter.ui.HistoryAdapter;
import com.example.wordcounter.ui.viewmodel.WordCounterViewModel;

import java.util.List;

/**
 * Main Activity providing text input, metrics visualization, sample generation,
 * validation, and calculation history.
 *
 * All mutable state lives in {@link WordCounterViewModel}, which survives
 * configuration changes (rotation, theme switch). Heavy computations are
 * offloaded to a background thread inside the ViewModel.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private WordCounterViewModel viewModel;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(WordCounterViewModel.class);

        setupRecyclerView();
        setupListeners();
        observeViewModel();
    }

    private void setupRecyclerView() {
        historyAdapter = new HistoryAdapter();
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvHistory.setAdapter(historyAdapter);
    }

    private void setupListeners() {
        // Clear error as soon as the user starts typing
        binding.etInputText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.tilInputText.getError() != null) {
                    binding.tilInputText.setError(null);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.btnCalculate.setOnClickListener(v -> performCalculation());

        binding.btnGenerateSample.setOnClickListener(v -> {
            binding.tilInputText.setError(null);
            String sample = viewModel.getNextSampleSentence();
            binding.etInputText.setText(sample);
            binding.etInputText.setSelection(sample.length());
            Toast.makeText(this, R.string.toast_sample_generated, Toast.LENGTH_SHORT).show();
        });

        binding.btnClear.setOnClickListener(v -> {
            binding.etInputText.setText("");
            binding.tilInputText.setError(null);
            resetResults();
            Toast.makeText(this, R.string.toast_cleared, Toast.LENGTH_SHORT).show();
        });

        binding.btnClearHistory.setOnClickListener(v -> {
            viewModel.clearHistory();
            Toast.makeText(this, R.string.toast_history_cleared, Toast.LENGTH_SHORT).show();
        });
    }

    private void observeViewModel() {
        viewModel.metrics.observe(this, this::displayMetrics);

        viewModel.history.observe(this, records -> {
            historyAdapter.submitList(records);
            updateHistoryVisibility(records);
            if (records != null && !records.isEmpty()) {
                binding.rvHistory.scrollToPosition(0);
            }
        });

        viewModel.calculating.observe(this, isCalculating -> {
            binding.btnCalculate.setEnabled(!isCalculating);
        });
    }

    private void performCalculation() {
        String rawText = binding.etInputText.getText() != null
                ? binding.etInputText.getText().toString() : "";

        if (rawText.trim().isEmpty()) {
            binding.tilInputText.setError(getString(R.string.error_empty_input));
            return;
        }

        binding.tilInputText.setError(null);
        viewModel.requestCalculation(rawText);
    }

    private void displayMetrics(TextMetrics metrics) {
        if (metrics == null) return;

        binding.tvSentencesValue.setText(String.valueOf(metrics.getSentenceCount()));
        binding.tvWordsValue.setText(String.valueOf(metrics.getWordCount()));
        binding.tvCharactersValue.setText(String.valueOf(metrics.getCharacterCount()));
        binding.tvNumbersValue.setText(String.valueOf(metrics.getNumberCount()));

        // Trim summary text safely (ViewModel history stores trimmed text already)
        String summaryText = getString(R.string.formula_format,
                truncateSafely(binding.etInputText.getText() != null
                        ? binding.etInputText.getText().toString().trim() : "", 40),
                metrics.toSummaryString());
        binding.tvSummaryFormula.setText(summaryText);
    }

    private void resetResults() {
        binding.tvSentencesValue.setText("0");
        binding.tvWordsValue.setText("0");
        binding.tvCharactersValue.setText("0");
        binding.tvNumbersValue.setText("0");
        binding.tvSummaryFormula.setText(R.string.default_summary_formula);
    }

    private void updateHistoryVisibility(List<?> records) {
        boolean empty = records == null || records.isEmpty();
        binding.tvEmptyHistory.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.rvHistory.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    /**
     * Truncates a string safely at Unicode code-point boundaries so emojis are never split.
     */
    private String truncateSafely(String text, int maxCodePoints) {
        int codePointLen = text.codePointCount(0, text.length());
        if (codePointLen <= maxCodePoints) return text;
        int cutOffset = text.offsetByCodePoints(0, maxCodePoints - 3);
        return text.substring(0, cutOffset) + "...";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
