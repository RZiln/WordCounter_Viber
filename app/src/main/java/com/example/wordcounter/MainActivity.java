package com.example.wordcounter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wordcounter.analyzer.TestSentenceGenerator;
import com.example.wordcounter.analyzer.TextMetrics;
import com.example.wordcounter.analyzer.TextMetricsCalculator;
import com.example.wordcounter.model.CalculationRecord;
import com.example.wordcounter.ui.HistoryAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Main Activity providing text input, metrics visualization, sample generation,
 * validation, and calculation history.
 */
public class MainActivity extends AppCompatActivity {

    private final TextMetricsCalculator calculator = new TextMetricsCalculator();
    private final TestSentenceGenerator sentenceGenerator = new TestSentenceGenerator();
    private final HistoryAdapter historyAdapter = new HistoryAdapter();

    private TextInputLayout tilInputText;
    private TextInputEditText etInputText;
    private Button btnCalculate;
    private Button btnGenerateSample;
    private Button btnClear;

    private TextView tvSentencesValue;
    private TextView tvWordsValue;
    private TextView tvCharactersValue;
    private TextView tvNumbersValue;
    private TextView tvSummaryFormula;

    private RecyclerView rvHistory;
    private TextView tvEmptyHistory;
    private Button btnClearHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        setupListeners();
    }

    private void initViews() {
        tilInputText = findViewById(R.id.tilInputText);
        etInputText = findViewById(R.id.etInputText);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnGenerateSample = findViewById(R.id.btnGenerateSample);
        btnClear = findViewById(R.id.btnClear);

        tvSentencesValue = findViewById(R.id.tvSentencesValue);
        tvWordsValue = findViewById(R.id.tvWordsValue);
        tvCharactersValue = findViewById(R.id.tvCharactersValue);
        tvNumbersValue = findViewById(R.id.tvNumbersValue);
        tvSummaryFormula = findViewById(R.id.tvSummaryFormula);

        rvHistory = findViewById(R.id.rvHistory);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        btnClearHistory = findViewById(R.id.btnClearHistory);
    }

    private void setupRecyclerView() {
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(historyAdapter);
        updateHistoryVisibility();
    }

    private void setupListeners() {
        btnCalculate.setOnClickListener(v -> performCalculation());

        btnGenerateSample.setOnClickListener(v -> {
            tilInputText.setError(null);
            String sample = sentenceGenerator.getNextSentence();
            etInputText.setText(sample);
            etInputText.setSelection(sample.length());
            Toast.makeText(this, R.string.toast_sample_generated, Toast.LENGTH_SHORT).show();
        });

        btnClear.setOnClickListener(v -> {
            etInputText.setText("");
            tilInputText.setError(null);
            resetResults();
            Toast.makeText(this, R.string.toast_cleared, Toast.LENGTH_SHORT).show();
        });

        btnClearHistory.setOnClickListener(v -> {
            historyAdapter.clearRecords();
            updateHistoryVisibility();
            Toast.makeText(this, R.string.toast_history_cleared, Toast.LENGTH_SHORT).show();
        });
    }

    private void performCalculation() {
        String rawText = etInputText.getText() != null ? etInputText.getText().toString() : "";
        String trimmed = rawText.trim();

        if (trimmed.isEmpty()) {
            tilInputText.setError(getString(R.string.error_empty_input));
            Toast.makeText(this, R.string.error_empty_input, Toast.LENGTH_SHORT).show();
            return;
        }

        tilInputText.setError(null);

        TextMetrics metrics = calculator.calculate(rawText);

        tvSentencesValue.setText(String.valueOf(metrics.getSentenceCount()));
        tvWordsValue.setText(String.valueOf(metrics.getWordCount()));
        tvCharactersValue.setText(String.valueOf(metrics.getCharacterCount()));
        tvNumbersValue.setText(String.valueOf(metrics.getNumberCount()));

        String summaryText = "„" + trimmed + "“ => " + metrics.toSummaryString();
        tvSummaryFormula.setText(summaryText);

        CalculationRecord record = new CalculationRecord(trimmed, metrics);
        historyAdapter.addRecord(record);
        updateHistoryVisibility();
        rvHistory.scrollToPosition(0);
    }

    private void resetResults() {
        tvSentencesValue.setText("0");
        tvWordsValue.setText("0");
        tvCharactersValue.setText("0");
        tvNumbersValue.setText("0");
        tvSummaryFormula.setText("Formulė: 0, 0, 0, 0");
    }

    private void updateHistoryVisibility() {
        if (historyAdapter.isEmpty()) {
            tvEmptyHistory.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        } else {
            tvEmptyHistory.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
        }
    }
}
