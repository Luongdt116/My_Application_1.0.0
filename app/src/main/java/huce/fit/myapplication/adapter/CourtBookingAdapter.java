package huce.fit.myapplication.adapter;

import android.graphics.Color;
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
    private Set<String> selectedSlots = new HashSet<>();

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int selectedCount);
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.listener = listener;
    }

    public void setData(List<Court> courts, List<Booking> bookings) {
        this.courtList = (courts != null) ? courts : new ArrayList<>();
        this.bookingList = (bookings != null) ? bookings : new ArrayList<>();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_court_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (courtList == null || courtList.isEmpty()) return;
        
        Court court = courtList.get(position);
        holder.tvCourtName.setText(court.getName());
        
        // Quản lý 17 Ca (Từ 5h đến 21h bắt đầu)
        for (int i = 0; i < 17; i++) {
            final int hour = 5 + i;
            TextView slotTv = holder.slots[i];
            
            if (slotTv == null) continue;

            boolean isBooked = isTimeSlotBooked(court.getName(), hour);
            boolean isMaintenance = (court.getStatus() == 0);
            String selectionKey = court.getName() + "|" + hour;
            boolean isSelected = selectedSlots.contains(selectionKey);

            if (isBooked) {
                slotTv.setBackgroundColor(Color.parseColor("#E53935")); // Đỏ: Đã đặt
                slotTv.setTextColor(Color.WHITE);
                slotTv.setOnClickListener(null);
            } else if (isMaintenance) {
                slotTv.setBackgroundColor(Color.parseColor("#757575")); // Xám: Bảo trì
                slotTv.setTextColor(Color.WHITE);
                slotTv.setOnClickListener(null);
            } else if (isSelected) {
                slotTv.setBackgroundColor(Color.parseColor("#FF9800")); // Cam: Đang chọn
                slotTv.setTextColor(Color.WHITE);
                slotTv.setOnClickListener(v -> {
                    selectedSlots.remove(selectionKey);
                    notifyDataSetChanged();
                    if (listener != null) listener.onSelectionChanged(selectedSlots.size());
                });
            } else {
                slotTv.setBackgroundColor(Color.parseColor("#F5F5F5")); // Trống
                slotTv.setTextColor(Color.BLACK);
                slotTv.setOnClickListener(v -> {
                    selectedSlots.add(selectionKey);
                    notifyDataSetChanged();
                    if (listener != null) listener.onSelectionChanged(selectedSlots.size());
                });
            }
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
        return courtList != null ? courtList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourtName;
        TextView[] slots = new TextView[17];
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourtName = itemView.findViewById(R.id.tvCourtNameRow);
            // Ánh xạ 17 slot từ XML
            slots[0] = itemView.findViewById(R.id.slot5);
            slots[1] = itemView.findViewById(R.id.slot6);
            slots[2] = itemView.findViewById(R.id.slot7);
            slots[3] = itemView.findViewById(R.id.slot8);
            slots[4] = itemView.findViewById(R.id.slot9);
            slots[5] = itemView.findViewById(R.id.slot10);
            slots[6] = itemView.findViewById(R.id.slot11);
            slots[7] = itemView.findViewById(R.id.slot12);
            slots[8] = itemView.findViewById(R.id.slot13);
            slots[9] = itemView.findViewById(R.id.slot14);
            slots[10] = itemView.findViewById(R.id.slot15);
            slots[11] = itemView.findViewById(R.id.slot16);
            slots[12] = itemView.findViewById(R.id.slot17);
            slots[13] = itemView.findViewById(R.id.slot18);
            slots[14] = itemView.findViewById(R.id.slot19);
            slots[15] = itemView.findViewById(R.id.slot20);
            slots[16] = itemView.findViewById(R.id.slot21);
        }
    }
}
