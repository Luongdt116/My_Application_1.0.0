package huce.fit.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import huce.fit.myapplication.adapter.CourtBookingAdapter;
import huce.fit.myapplication.objects.Booking;
import huce.fit.myapplication.objects.Court;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        selectedVenue = (Venue) getIntent().getSerializableExtra("selected_venue");
        if (selectedVenue == null) {
            Toast.makeText(this, "Không tìm thấy thông tin sân!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy giá gốc để tính toán (giả sử lấy giá đầu tiên)
        if (selectedVenue.getVenue_prices() != null && !selectedVenue.getVenue_prices().isEmpty()) {
            unitPrice = selectedVenue.getVenue_prices().values().iterator().next().fixed_price;
        }

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

        // 1. ĐỒNG BỘ SEEKBAR -> CUỘN LỊCH
        zoomSlider.setMax(100);
        zoomSlider.setProgress(0);
        zoomSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Tính toán vị trí cuộn: (Tiến trình / 100) * (Chiều rộng nội dung - Chiều rộng màn hình)
                    if (hsvBookingTable.getChildAt(0) != null) {
                        int maxScrollX = hsvBookingTable.getChildAt(0).getWidth() - hsvBookingTable.getWidth();
                        if (maxScrollX > 0) {
                            int scrollX = (progress * maxScrollX) / 100;
                            hsvBookingTable.scrollTo(scrollX, 0);
                        }
                    }
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // 2. ĐỒNG BỘ CUỘN LỊCH -> SEEKBAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hsvBookingTable.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (hsvBookingTable.getChildAt(0) != null) {
                    int maxScrollX = hsvBookingTable.getChildAt(0).getWidth() - hsvBookingTable.getWidth();
                    if (maxScrollX > 0) {
                        int progress = (scrollX * 100) / maxScrollX;
                        zoomSlider.setProgress(progress);
                    }
                }
            });
        }

        // LẮNG NGHE SỰ THAY ĐỔI LỰA CHỌN
        adapter.setOnSelectionChangedListener(selectedCount -> {
            if (selectedCount > 0) {
                long total = selectedCount * unitPrice;
                btnNext.setText("TIẾP THEO (" + selectedCount + " ca - " + String.format("%,d", total) + "đ)");
            } else {
                btnNext.setText("TIẾP THEO");
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Calendar c = Calendar.getInstance();
        updateDateDisplay(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));

        tvSelectedDate.setOnClickListener(v -> showDatePicker());
        btnBack.setOnClickListener(v -> finish());
        
        btnNext.setOnClickListener(v -> {
            Set<String> selected = adapter.getSelectedSlots();
            if (selected.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một ca đánh!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(BookingActivity.this, PaymentActivity.class);
            intent.putExtra("selected_venue", selectedVenue);
            intent.putExtra("selected_date", tvSelectedDate.getText().toString());
            // Truyền danh sách các ca đã chọn
            intent.putExtra("selected_slots", (Serializable) new ArrayList<>(selected));
            intent.putExtra("total_price", selected.size() * unitPrice);
            startActivity(intent);
        });

        loadData();
    }

    private void loadData() {
        if (selectedVenue.getCourts() != null) {
            courtList = new ArrayList<>(selectedVenue.getCourts().values());
        }
        fetchBookingsFromFirebase();
    }

    private void fetchBookingsFromFirebase() {
        adapter.clearSelection(); // Reset khi đổi ngày
        String dateStr = tvSelectedDate.getText().toString();
        String[] parts = dateStr.split("/");
        String queryDate = parts[2] + "-" + String.format("%02d", Integer.parseInt(parts[1])) + "-" + String.format("%02d", Integer.parseInt(parts[0]));

        mDatabase.child("Bookings")
                .orderByChild("venue_id").equalTo(selectedVenue.getVenueId())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dayBookings.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Booking b = data.getValue(Booking.class);
                        if (b != null && queryDate.equals(b.getBooking_date())) {
                            dayBookings.add(b);
                        }
                    }
                }
                adapter.setData(courtList, dayBookings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BookingActivity", "Firebase Error: " + error.getMessage());
            }
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    updateDateDisplay(d, m + 1, y);
                    fetchBookingsFromFirebase();
                }, year, month, day);
        datePickerDialog.show();
    }

    private void updateDateDisplay(int d, int m, int y) {
        tvSelectedDate.setText(String.format("%02d/%02d/%04d", d, m, y));
    }
}
