package com.example.wordcounter.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wordcounter.R;
import com.example.wordcounter.model.CalculationRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying calculation history records in a RecyclerView.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<CalculationRecord> items = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calculation_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        CalculationRecord item = items.get(position);
        holder.tvHistoryFormula.setText(item.toHistoryDisplayString());
        String timeStr = dateFormat.format(new Date(item.getTimestamp()));
        String details = holder.itemView.getContext().getString(
                R.string.label_results_title) + " (" + timeStr + ")";
        holder.tvHistoryDetails.setText(details);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Adds a new calculation record at the top of the list (newest first).
     */
    public void addRecord(CalculationRecord record) {
        items.add(0, record);
        notifyItemInserted(0);
    }

    /**
     * Clears all history records.
     */
    public void clearRecords() {
        int size = items.size();
        items.clear();
        notifyItemRangeRemoved(0, size);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        final TextView tvHistoryFormula;
        final TextView tvHistoryDetails;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistoryFormula = itemView.findViewById(R.id.tvHistoryFormula);
            tvHistoryDetails = itemView.findViewById(R.id.tvHistoryDetails);
        }
    }
}
