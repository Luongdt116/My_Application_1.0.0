package huce.fit.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import huce.fit.myapplication.R;
import huce.fit.myapplication.objects.Venue;

public class FieldAdapter extends RecyclerView.Adapter<FieldAdapter.FieldViewHolder> {

    private List<Venue> fieldList = new ArrayList<>();
    private OnFieldClickListener listener;

    public interface OnFieldClickListener {
        void onBookClick(Venue venue);
        void onItemClick(Venue venue);
    }

    public void setOnFieldClickListener(OnFieldClickListener listener) {
        this.listener = listener;
    }

    public FieldAdapter() {
    }

    public void setFields(List<Venue> fields) {
        this.fieldList = fields;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_field, parent, false);
        return new FieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder holder, int position) {
        Venue venue = fieldList.get(position);
        holder.tvFieldName.setText(venue.getVenue_name());
        holder.tvFieldLocation.setText(venue.getAddress_detail());
        
        // Load image using helper or fallback
        int resId = holder.itemView.getContext().getResources().getIdentifier(
            venue.getLocalImageName(), "drawable", holder.itemView.getContext().getPackageName());
        if (resId != 0) {
            holder.imgField.setImageResource(resId);
        } else {
            holder.imgField.setImageResource(R.drawable.logo); 
        }

        holder.btnBook.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(venue);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(venue);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fieldList != null ? fieldList.size() : 0;
    }

    public static class FieldViewHolder extends RecyclerView.ViewHolder {
        ImageView imgField;
        TextView tvFieldName, tvFieldLocation;
        MaterialButton btnBook;

        public FieldViewHolder(@NonNull View itemView) {
            super(itemView);
            imgField = itemView.findViewById(R.id.imgField);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvFieldLocation = itemView.findViewById(R.id.tvFieldLocation);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }
}
