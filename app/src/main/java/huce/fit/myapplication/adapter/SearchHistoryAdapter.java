package huce.fit.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import huce.fit.myapplication.R;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {
    private List<String> historyList = new ArrayList<>();
    private OnHistoryItemClickListener listener;

    public interface OnHistoryItemClickListener {
        void onItemClick(String query);
        void onDeleteClick(String query);
    }

    public void setOnHistoryItemClickListener(OnHistoryItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<String> data) {
        this.historyList = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String query = historyList.get(position);
        holder.tvQuery.setText(query);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(query);
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(query);
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuery;
        ImageView btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuery = itemView.findViewById(R.id.tvHistoryQuery);
            btnDelete = itemView.findViewById(R.id.btnDeleteHistory);
        }
    }
}
