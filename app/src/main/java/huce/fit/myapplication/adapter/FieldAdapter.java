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
import huce.fit.myapplication.objects.Field;

public class FieldAdapter extends RecyclerView.Adapter<FieldAdapter.FieldViewHolder> {

    private List<Field> fieldList = new ArrayList<>();
    private OnFieldClickListener listener;

    public interface OnFieldClickListener {
        void onBookClick(int position);
        void onItemClick(int position);
    }

    public void setOnFieldClickListener(OnFieldClickListener listener) {
        this.listener = listener;
    }

    public FieldAdapter() {
        // Constructor rỗng phù hợp khi dùng Room LiveData
    }

    // Phương thức quan trọng để Room cập nhật dữ liệu mới cho Adapter
    public void setFields(List<Field> fields) {
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
        Field field = fieldList.get(position);
        holder.tvFieldName.setText(field.getName());
        holder.tvFieldLocation.setText(field.getLocation());
        holder.imgField.setImageResource(field.getImageResId());

        holder.btnBook.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fieldList != null ? fieldList.size() : 0;
    }

    public Field getFieldAt(int position) {
        return fieldList.get(position);
    }

    class FieldViewHolder extends RecyclerView.ViewHolder {
        ImageView imgField;
        TextView tvFieldName, tvFieldLocation;
        MaterialButton btnBook;

        public FieldViewHolder(@NonNull View itemView) {
            super(itemView);
            imgField = itemView.findViewById(R.id.imgField);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvFieldLocation = itemView.findViewById(R.id.tvFieldLocation);
            btnBook = itemView.findViewById(R.id.btnBook);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            });
        }
    }
}
