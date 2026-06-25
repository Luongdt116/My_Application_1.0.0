package huce.fit.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import huce.fit.myapplication.adapter.CourtBookingAdapter;
import huce.fit.myapplication.adapter.ServiceAdapter;
import huce.fit.myapplication.objects.Booking;
import huce.fit.myapplication.objects.Court;
import huce.fit.myapplication.objects.Service;
import huce.fit.myapplication.objects.Venue;

public class BookingActivity extends AppCompatActivity {

    private TextView tvSelectedDate, tvVenueName;
    private ImageView btnBack;
    private MaterialButton btnNext;
    private RecyclerView rvBookingCourts;
    private HorizontalScrollView hsvBookingTable;
    private SeekBar zoomSlider;
    private CourtBookingAdapter adapter;
    private Venue selectedVenue;
    private DatabaseReference mDatabase;
    private List<Booking> dayBookings = new ArrayList<>();
    private List<Court> courtList = new ArrayList<>();
    
    private long unitPrice = 0;
    // Đảm bảo URL này khớp 100% với Database của bạn
    private String dbUrl = "https://app-moblie-131d8-default-rtdb.firebaseio.com/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        selectedVenue = (Venue) getIntent().getSerializableExtra("selected_venue");
        if (selectedVenue == null) {
            Toast.makeText(this, "Không tìm thấy thông tin sân!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo Database với URL chính xác
        mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference();

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvVenueName = findViewById(R.id.tvVenueNameBooking);
        btnBack = findViewById(R.id.btnBackBooking);
        btnNext = findViewById(R.id.btnNext);
        rvBookingCourts = findViewById(R.id.rvBookingCourts);
        hsvBookingTable = findViewById(R.id.hsvBookingTable);
        zoomSlider = findViewById(R.id.zoomSlider);

        tvVenueName.setText(selectedVenue.getVenue_name());

        adapter = new CourtBookingAdapter();
        rvBookingCourts.setLayoutManager(new LinearLayoutManager(this));
        rvBookingCourts.setAdapter(adapter);

        if (selectedVenue.getVenue_prices() != null && !selectedVenue.getVenue_prices().isEmpty()) {
            unitPrice = selectedVenue.getVenue_prices().values().iterator().next().fixed_price;
        }

        adapter.setOnSelectionChangedListener(selectedCount -> {
            if (selectedCount > 0) {
                btnNext.setText("TIẾP THEO (" + selectedCount + " ca - " + String.format("%,d", selectedCount * unitPrice) + "đ)");
            } else {
                btnNext.setText("TIẾP THEO");
            }
        });

        zoomSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && hsvBookingTable.getChildAt(0) != null) {
                    int maxScrollX = hsvBookingTable.getChildAt(0).getWidth() - hsvBookingTable.getWidth();
                    if (maxScrollX > 0) hsvBookingTable.scrollTo((progress * maxScrollX) / 100, 0);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        Calendar c = Calendar.getInstance();
        updateDateDisplay(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));

        tvSelectedDate.setOnClickListener(v -> showDatePicker());
        btnBack.setOnClickListener(v -> finish());
        btnNext.setOnClickListener(v -> handleNextStep());

        loadData();
    }

    private void loadData() {
        Log.d("FIREBASE_LOG", "Đang tải sân cho Venue: " + selectedVenue.getVenueId());
        
        // Tải danh sách sân (courts) trực tiếp từ Venues/{venue_id}/courts như trong JSON của bạn
        mDatabase.child("Venues").child(selectedVenue.getVenueId()).child("courts")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    courtList.clear();
                    if (snapshot.exists()) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Court court = data.getValue(Court.class);
                            if (court != null) courtList.add(court);
                        }
                        Log.d("FIREBASE_LOG", "Đã nạp " + courtList.size() + " sân");
                        fetchBookingsFromFirebase();
                    } else {
                        Log.e("FIREBASE_LOG", "Không thấy node 'courts' tại ID: " + selectedVenue.getVenueId());
                        Toast.makeText(BookingActivity.this, "Sân này chưa cập nhật danh sách ca tập!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FIREBASE_LOG", "Lỗi nạp sân: " + error.getMessage());
                }
            });
    }

    private void fetchBookingsFromFirebase() {
        String dateStr = tvSelectedDate.getText().toString();
        String[] parts = dateStr.split("/");
        String queryDate = parts[2] + "-" + parts[1] + "-" + parts[0];

        mDatabase.child("Bookings")
                .orderByChild("venue_id").equalTo(selectedVenue.getVenueId())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dayBookings.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Booking b = data.getValue(Booking.class);
                    if (b != null && queryDate.equals(b.getBooking_date())) {
                        dayBookings.add(b);
                    }
                }
                adapter.setData(courtList, dayBookings);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE_LOG", "Lỗi nạp lịch đặt: " + error.getMessage());
            }
        });
    }

    private void handleNextStep() {
        if (adapter.getSelectedSlots().isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một ca tập!", Toast.LENGTH_SHORT).show();
            return;
        }
        showServicesBottomSheet();
    }

    private void showServicesBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_services, null);
        dialog.setContentView(view);

        RecyclerView rvServices = view.findViewById(R.id.rvServices);
        TextView tvTotal = view.findViewById(R.id.tvServiceTotal);
        MaterialButton btnConfirm = view.findViewById(R.id.btnConfirmServices);

        final long[] serviceFee = {0};
        if (selectedVenue.getServices() != null && !selectedVenue.getServices().isEmpty()) {
            ServiceAdapter sAdapter = new ServiceAdapter(selectedVenue.getServices(), selectedServices -> {
                serviceFee[0] = 0;
                for (Map.Entry<String, Integer> entry : selectedServices.entrySet()) {
                    Service s = selectedVenue.getServices().get(entry.getKey());
                    if (s != null) serviceFee[0] += (long) s.getPrice() * entry.getValue();
                }
                tvTotal.setText(String.format("%,dđ", serviceFee[0]));
            });
            rvServices.setLayoutManager(new LinearLayoutManager(this));
            rvServices.setAdapter(sAdapter);

            btnConfirm.setOnClickListener(v -> {
                dialog.dismiss();
                navigateToPayment(sAdapter.getSelectedServices(), serviceFee[0]);
            });
        } else {
            rvServices.setVisibility(View.GONE);
            btnConfirm.setOnClickListener(v -> {
                dialog.dismiss();
                navigateToPayment(new HashMap<>(), 0);
            });
        }
        dialog.show();
    }

    private void navigateToPayment(Map<String, Integer> services, long fee) {
        long finalTotal = (adapter.getSelectedSlots().size() * unitPrice) + fee;
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("selected_venue", selectedVenue);
        intent.putExtra("selected_date", tvSelectedDate.getText().toString());
        intent.putExtra("selected_slots", new ArrayList<>(adapter.getSelectedSlots()));
        intent.putExtra("selected_services", (Serializable) services);
        intent.putExtra("total_price", finalTotal);
        startActivity(intent);
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> {
            updateDateDisplay(d, m + 1, y);
            fetchBookingsFromFirebase();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateDisplay(int d, int m, int y) {
        tvSelectedDate.setText(String.format("%02d/%02d/%04d", d, m, y));
    }
}
