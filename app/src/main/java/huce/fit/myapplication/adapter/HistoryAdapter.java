package huce.fit.myapplication.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import huce.fit.myapplication.R;
import huce.fit.myapplication.objects.Booking;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<Booking> bookingList = new ArrayList<>();
    private OnBookingActionListener actionListener;

    public interface OnBookingActionListener {
        void onPayNow(Booking booking);
        void onDelete(Booking booking);
    }

    public void setOnBookingActionListener(OnBookingActionListener listener) {
        this.actionListener = listener;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookingList = bookings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_booking, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.tvVenueName.setText(booking.getVenue_name() + " - " + booking.getCourt_name()); 
        holder.tvCourtDetail.setText("Ca: " + booking.getStart_time() + " - " + booking.getEnd_time());
        holder.tvDate.setText("Ngày: " + booking.getBooking_date());
        
        long price = booking.getTotal_price_snapshot();
        if (price <= 0 && booking.getPayment_info() != null) {
            Object amt = booking.getPayment_info().get("amount");
            if (amt instanceof Number) price = ((Number) amt).longValue();
        }

        holder.tvTotalPrice.setText(price > 0 ? String.format(Locale.getDefault(), "%,dđ", price) : "Đã thanh toán");

        // Xử lý trạng thái và nút bấm
        holder.btnPayNow.setVisibility(View.GONE);
        holder.btnDeleteBooking.setVisibility(View.GONE);

        if (booking.getStatus() == 1) {
            holder.tvStatus.setText("Thành công");
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E8F5E9")));
        } else if (booking.getStatus() == 2) {
            holder.tvStatus.setText("Chờ thanh toán");
            holder.tvStatus.setTextColor(Color.parseColor("#FF9800"));
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFF3E0")));
            
            holder.btnPayNow.setVisibility(View.VISIBLE);
            holder.btnDeleteBooking.setVisibility(View.VISIBLE);
        } else {
            holder.tvStatus.setText("Đã hủy");
            holder.tvStatus.setTextColor(Color.RED);
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFEBEE")));
        }

        if (booking.getSelected_services() != null && !booking.getSelected_services().isEmpty()) {
            StringBuilder servicesStr = new StringBuilder("Dịch vụ: ");
            int count = 0;
            for (Map.Entry<String, Integer> entry : booking.getSelected_services().entrySet()) {
                servicesStr.append(entry.getKey()).append(" (x").append(entry.getValue()).append(")");
                if (count < booking.getSelected_services().size() - 1) servicesStr.append(", ");
                count++;
            }
            holder.tvServices.setText(servicesStr.toString());
            holder.tvServices.setVisibility(View.VISIBLE);
        } else {
            holder.tvServices.setVisibility(View.GONE);
        }

        holder.btnPayNow.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onPayNow(booking);
        });

        holder.btnDeleteBooking.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onDelete(booking);
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvVenueName, tvStatus, tvCourtDetail, tvDate, tvServices, tvTotalPrice;
        MaterialButton btnPayNow, btnDeleteBooking;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVenueName = itemView.findViewById(R.id.tvHistoryVenueName);
            tvStatus = itemView.findViewById(R.id.tvHistoryStatus);
            tvCourtDetail = itemView.findViewById(R.id.tvHistoryCourtDetail);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvServices = itemView.findViewById(R.id.tvHistoryServices);
            tvTotalPrice = itemView.findViewById(R.id.tvHistoryTotalPrice);
            btnPayNow = itemView.findViewById(R.id.btnPayNow);
            btnDeleteBooking = itemView.findViewById(R.id.btnDeleteBooking);
        }
    }
}
