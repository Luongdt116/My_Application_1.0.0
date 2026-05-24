package huce.fit.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
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
import huce.fit.myapplication.BookingActivity;
import huce.fit.myapplication.objects.Venue;

public class FieldAdapter extends RecyclerView.Adapter<FieldAdapter.FieldViewHolder> {
    private List<Venue> venueList = new ArrayList<>();
    private OnFieldClickListener listener;

    public interface OnFieldClickListener {
        void onBookClick(Venue venue);
        void onItemClick(Venue venue);
    }

    public void setOnFieldClickListener(OnFieldClickListener listener) {
        this.listener = listener;
    }

    public void setFields(List<Venue> venues) {
        this.venueList = venues;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_venue, parent, false);
        return new FieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder holder, int position) {
        Venue venue = venueList.get(position);
        
        holder.tvFieldName.setText(venue.getVenue_name());
        holder.tvFieldLocation.setText(venue.getAddress_detail());
        holder.tvFieldPrice.setText("Giá từ: " + venue.getDisplayPrice());

        Context context = holder.itemView.getContext();
        String imageName = venue.getLocalImageName();
        
        int resId = context.getResources().getIdentifier(
                imageName, 
                "drawable", 
                context.getPackageName()
        );
        
        if (resId != 0) {
            holder.imgField.setImageResource(resId);
        } else {
            holder.imgField.setImageResource(R.drawable.logo);
        }

        holder.btnBook.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(venue);
            } else {
                Intent intent = new Intent(context, BookingActivity.class);
                intent.putExtra("selected_venue", venue);
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(venue);
            } else {
                Intent intent = new Intent(context, BookingActivity.class);
                intent.putExtra("selected_venue", venue);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return venueList != null ? venueList.size() : 0;
    }

    class FieldViewHolder extends RecyclerView.ViewHolder {
        ImageView imgField;
        TextView tvFieldName, tvFieldLocation, tvFieldPrice;
        MaterialButton btnBook;

        public FieldViewHolder(@NonNull View itemView) {
            super(itemView);
            imgField = itemView.findViewById(R.id.imgField);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvFieldLocation = itemView.findViewById(R.id.tvFieldLocation);
            tvFieldPrice = itemView.findViewById(R.id.tvFieldPrice);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }
}
