package huce.fit.myapplication.adapter;

import android.content.Context;
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
        holder.tvFieldDescription.setText(venue.getDescription());
        holder.tvFieldLocation.setText(venue.getAddress_detail());
        holder.tvFieldPrice.setText("Giá từ: " + venue.getDisplayPrice());

        // HIỂN THỊ ƯU ĐÃI
        if (venue.getPromotionTitle() != null && !venue.getPromotionTitle().isEmpty()) {
            holder.tvPromoBadge.setVisibility(View.VISIBLE);
            holder.tvPromoTitle.setVisibility(View.VISIBLE);
            holder.tvPromoTitle.setText(venue.getPromotionTitle());
        } else {
            holder.tvPromoBadge.setVisibility(View.GONE);
            holder.tvPromoTitle.setVisibility(View.GONE);
        }

        // Xử lý ảnh
        Context context = holder.itemView.getContext();
        String imageName = venue.getLocalImageName();
        int resId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
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
    }

    @Override
    public int getItemCount() {
        return venueList != null ? venueList.size() : 0;
    }

    class FieldViewHolder extends RecyclerView.ViewHolder {
        ImageView imgField;
        TextView tvFieldName, tvFieldDescription, tvFieldLocation, tvFieldPrice, tvPromoBadge, tvPromoTitle;
        MaterialButton btnBook;

        public FieldViewHolder(@NonNull View itemView) {
            super(itemView);
            imgField = itemView.findViewById(R.id.imgField);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvFieldDescription = itemView.findViewById(R.id.tvFieldDescription);
            tvFieldLocation = itemView.findViewById(R.id.tvFieldLocation);
            tvFieldPrice = itemView.findViewById(R.id.tvFieldPrice);
            tvPromoBadge = itemView.findViewById(R.id.tvPromoBadge);
            tvPromoTitle = itemView.findViewById(R.id.tvPromoTitle);
            btnBook = itemView.findViewById(R.id.btnBook);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(venueList.get(position));
                }
            });
        }
    }
}
