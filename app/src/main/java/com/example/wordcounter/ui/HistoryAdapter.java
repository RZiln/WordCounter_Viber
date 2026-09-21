package com.example.wordcounter.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wordcounter.R;
import com.example.wordcounter.model.CalculationRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Adapter for displaying calculation history records in a RecyclerView.
 * Uses ListAdapter + DiffUtil for efficient, animated updates.
 */
public class HistoryAdapter extends ListAdapter<CalculationRecord, HistoryAdapter.HistoryViewHolder> {

    private static final DiffUtil.ItemCallback<CalculationRecord> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CalculationRecord>() {
                @Override
                public boolean areItemsTheSame(@NonNull CalculationRecord oldItem,
                                               @NonNull CalculationRecord newItem) {
                    return oldItem.getTimestamp() == newItem.getTimestamp();
                }

                @Override
                public boolean areContentsTheSame(@NonNull CalculationRecord oldItem,
                                                  @NonNull CalculationRecord newItem) {
                    return oldItem.equals(newItem);
                }
            };

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public HistoryAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calculation_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        CalculationRecord item = getItem(position);
        holder.tvHistoryFormula.setText(item.toHistoryDisplayString());
        String timeStr = dateFormat.format(new Date(item.getTimestamp()));
        String details = holder.itemView.getContext().getString(
                R.string.history_item_details_format,
                holder.itemView.getContext().getString(R.string.label_results_title),
                timeStr);
        holder.tvHistoryDetails.setText(details);
    }

    public boolean isEmpty() {
        return getCurrentList().isEmpty();
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
