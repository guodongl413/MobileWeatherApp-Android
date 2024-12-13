package com.example.mobileweatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AutocompleteAdapter extends RecyclerView.Adapter<AutocompleteAdapter.ViewHolder> {

    private List<String> suggestions;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String suggestion);
    }

    public AutocompleteAdapter(List<String> suggestions, OnItemClickListener onItemClickListener) {
        this.suggestions = suggestions;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String suggestion = suggestions.get(position);
        holder.textView.setText(suggestion);
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(suggestion));
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}

