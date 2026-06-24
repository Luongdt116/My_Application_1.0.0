package huce.fit.myapplication.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import huce.fit.myapplication.R;
import huce.fit.myapplication.objects.Booking;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<Booking> bookingList = new ArrayList<>();

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

        // Hiển thị tạm ID sân hoặc Tên sân nếu bạn đã lưu trong Booking
        holder.tvVenueName.setText(booking.getCourt_name()); 
        holder.tvCourtDetail.setText("Ca: " + booking.getStart_time() + " - " + booking.getEnd_time());
        holder.tvDate.setText("Ngày: " + booking.getBooking_date());
        holder.tvTotalPrice.setText(String.format("%,dđ", booking.getTotal_price_snapshot()));

        // Trạng thái
        if (booking.getStatus() == 1) {
            holder.tvStatus.setText("Thành công");
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvStatus.setText("Đã hủy");
            holder.tvStatus.setTextColor(Color.RED);
        }

        // Dịch vụ đi kèm
        if (booking.getSelected_services() != null && !booking.getSelected_services().isEmpty()) {
            StringBuilder services = new StringBuilder("Dịch vụ: ");
            int count = 0;
            for (Map.Entry<String, Integer> entry : booking.getSelected_services().entrySet()) {
                services.append(entry.getKey()).append(" (x").append(entry.getValue()).append(")");
                if (count < booking.getSelected_services().size() - 1) services.append(", ");
                count++;
            }
            holder.tvServices.setText(services.toString());
            holder.tvServices.setVisibility(View.VISIBLE);
        } else {
            holder.tvServices.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvVenueName, tvStatus, tvCourtDetail, tvDate, tvServices, tvTotalPrice;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVenueName = itemView.findViewById(R.id.tvHistoryVenueName);
            tvStatus = itemView.findViewById(R.id.tvHistoryStatus);
            tvCourtDetail = itemView.findViewById(R.id.tvHistoryCourtDetail);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvServices = itemView.findViewById(R.id.tvHistoryServices);
            tvTotalPrice = itemView.findViewById(R.id.tvHistoryTotalPrice);
        }
    }
}
