package huce.fit.myapplication.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import huce.fit.myapplication.R;
import huce.fit.myapplication.objects.Booking;
import huce.fit.myapplication.objects.Court;

public class CourtBookingAdapter extends RecyclerView.Adapter<CourtBookingAdapter.ViewHolder> {
    private List<Court> courtList = new ArrayList<>();
    private List<Booking> bookingList = new ArrayList<>();
    private OnSelectionChangedListener listener;
    
    // Lưu trữ các ô đang được chọn (Key: CourtName|Hour)
    private Set<String> selectedSlots = new HashSet<>();

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int selectedCount);
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.listener = listener;
    }

    public void setData(List<Court> courts, List<Booking> bookings) {
        this.courtList = courts;
        this.bookingList = bookings;
        // Giữ nguyên selection nếu data reload? Thường thì nên clear nếu ngày đổi, 
        // nhưng ở đây ta để Activity quản lý việc clear khi đổi ngày.
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedSlots.clear();
        notifyDataSetChanged();
    }

    public Set<String> getSelectedSlots() {
        return selectedSlots;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_court_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Court court = courtList.get(position);
        holder.tvCourtName.setText(court.getName());

        holder.layoutSlots.removeAllViews();
        // Hiển thị từ 5h sáng đến 23h tối (tổng cộng 18 ca nếu tính đến 23h đóng cửa)
        // Ca 1: 5-6, Ca 2: 6-7, ..., Ca 18: 22-23
        for (int hour = 5; hour <= 22; hour++) {
            final int h = hour;
            final int caNumber = hour - 4;
            
            TextView slotTv = new TextView(holder.itemView.getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(220, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.setMargins(4, 4, 4, 4);
            slotTv.setLayoutParams(lp);
            slotTv.setGravity(Gravity.CENTER);
            slotTv.setTextSize(10);
            slotTv.setText("Ca " + caNumber + "\n" + h + "h-" + (h+1) + "h");

            boolean isBooked = isTimeSlotBooked(court.getName(), hour);
            boolean isMaintenance = (court.getStatus() == 0);
            String selectionKey = court.getName() + "|" + hour;
            boolean isSelected = selectedSlots.contains(selectionKey);

            if (isBooked) {
                slotTv.setBackgroundColor(Color.parseColor("#E53935")); // Đỏ (Đã đặt)
                slotTv.setTextColor(Color.WHITE);
                slotTv.setEnabled(false);
            } else if (isMaintenance) {
                slotTv.setBackgroundColor(Color.parseColor("#757575")); // Xám (Bảo trì)
                slotTv.setTextColor(Color.WHITE);
                slotTv.setEnabled(false);
            } else if (isSelected) {
                slotTv.setBackgroundColor(Color.parseColor("#FF9800")); // Cam (Đang chọn)
                slotTv.setTextColor(Color.WHITE);
                slotTv.setOnClickListener(v -> {
                    selectedSlots.remove(selectionKey);
                    notifyDataSetChanged();
                    if (listener != null) listener.onSelectionChanged(selectedSlots.size());
                });
            } else {
                slotTv.setBackgroundColor(Color.WHITE); // Trắng (Trống)
                slotTv.setTextColor(Color.BLACK);
                slotTv.setOnClickListener(v -> {
                    selectedSlots.add(selectionKey);
                    notifyDataSetChanged();
                    if (listener != null) listener.onSelectionChanged(selectedSlots.size());
                });
            }
            holder.layoutSlots.addView(slotTv);
        }
    }

    private boolean isTimeSlotBooked(String courtName, int hour) {
        if (bookingList == null) return false;
        for (Booking b : bookingList) {
            if (b.getCourt_name() != null && b.getCourt_name().equals(courtName)) {
                try {
                    int startH = Integer.parseInt(b.getStart_time().split(":")[0]);
                    int endH = Integer.parseInt(b.getEnd_time().split(":")[0]);
                    if (hour >= startH && hour < endH) return true;
                } catch (Exception e) {}
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return courtList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourtName;
        LinearLayout layoutSlots;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourtName = itemView.findViewById(R.id.tvCourtNameRow);
            layoutSlots = itemView.findViewById(R.id.layoutTimeSlots);
        }
    }
}
