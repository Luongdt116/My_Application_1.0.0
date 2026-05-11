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
        void onBookClick(int position);
        void onItemClick(int position);
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
        // Sử dụng cái khung item_venue.xml mà bạn đã thiết kế
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_venue, parent, false);
        return new FieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder holder, int position) {
        Venue venue = venueList.get(position);
        
        // ĐÂY LÀ ĐOẠN BIẾN TĨNH THÀNH ĐỘNG:
        // Lấy dữ liệu từ đối tượng Venue (Firebase) và điền vào các ô trống trong XML
        holder.tvFieldName.setText(venue.getVenue_name());
        holder.tvFieldLocation.setText(venue.getAddress_detail());
        holder.tvFieldPrice.setText("Giá từ: " + venue.getDisplayPrice());

        // XỬ LÝ ẢNH ĐỘNG TỪ DRAWABLE:
        Context context = holder.itemView.getContext();
        String imageName = venue.getLocalImageName(); // Lấy chữ "football" hoặc "badminton"
        
        int resId = context.getResources().getIdentifier(
                imageName, 
                "drawable", 
                context.getPackageName()
        );
        
        if (resId != 0) {
            holder.imgField.setImageResource(resId);
        } else {
            holder.imgField.setImageResource(R.drawable.logo); // Hiện logo nếu sai tên ảnh
        }

        holder.btnBook.setOnClickListener(v -> {
            if (listener != null) listener.onBookClick(position);
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
            // Ánh xạ đúng các ID trong file item_venue.xml của bạn
            imgField = itemView.findViewById(R.id.imgField);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvFieldLocation = itemView.findViewById(R.id.tvFieldLocation);
            tvFieldPrice = itemView.findViewById(R.id.tvFieldPrice);
            btnBook = itemView.findViewById(R.id.btnBook);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
