package huce.fit.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import huce.fit.myapplication.adapter.CourtBookingAdapter;
import huce.fit.myapplication.adapter.ServiceAdapter;
import huce.fit.myapplication.objects.Service;
import huce.fit.myapplication.objects.Venue;
import huce.fit.myapplication.viewmodel.BookingViewModel;

public class BookingActivity extends AppCompatActivity {

    private TextView tvSelectedDate, tvVenueName;
    private ImageView btnBack;
    private MaterialButton btnNext;
    private RecyclerView rvBookingCourts;
    private HorizontalScrollView hsvBookingTable;
    private SeekBar zoomSlider;
    private SwipeRefreshLayout swipeRefreshLayout;
    
    private CourtBookingAdapter adapter;
    private Venue selectedVenue;
    private BookingViewModel bookingViewModel;
    private long unitPrice = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        selectedVenue = (Venue) getIntent().getSerializableExtra("selected_venue");
        if (selectedVenue == null) {
            Toast.makeText(this, "Lỗi: Không nhận được dữ liệu!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupViewModel();
        setupListeners();
        
        // Tải dữ liệu ban đầu
        bookingViewModel.loadCourts(selectedVenue.getVenueId());
        refreshBookings();
    }

    private void initViews() {
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvVenueName = findViewById(R.id.tvVenueNameBooking);
        btnBack = findViewById(R.id.btnBackBooking);
        btnNext = findViewById(R.id.btnNext);
        rvBookingCourts = findViewById(R.id.rvBookingCourts);
        hsvBookingTable = findViewById(R.id.hsvBookingTable);
        zoomSlider = findViewById(R.id.zoomSlider);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshBooking);

        tvVenueName.setText(selectedVenue.getVenue_name());
        if (selectedVenue.getVenue_prices() != null && !selectedVenue.getVenue_prices().isEmpty()) {
            unitPrice = selectedVenue.getVenue_prices().values().iterator().next().fixed_price;
        }

        adapter = new CourtBookingAdapter();
        rvBookingCourts.setLayoutManager(new LinearLayoutManager(this));
        rvBookingCourts.setAdapter(adapter);

        Calendar c = Calendar.getInstance();
        updateDateDisplay(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
    }

    private void setupViewModel() {
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        // Lắng nghe danh sách sân con
        bookingViewModel.getCourtList().observe(this, courts -> {
            if (courts != null) {
                adapter.setData(courts, bookingViewModel.getDayBookings().getValue());
            }
        });

        // Lắng nghe danh sách ca đã đặt
        bookingViewModel.getDayBookings().observe(this, bookings -> {
            if (bookings != null) {
                adapter.setData(bookingViewModel.getCourtList().getValue(), bookings);
            }
        });

        // Lắng nghe trạng thái loading để ẩn vòng xoay refresh
        bookingViewModel.getIsLoading().observe(this, isLoading -> {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(isLoading);
            }
        });
    }

    private void setupListeners() {
        // Sự kiện Vuốt để làm mới
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeResources(R.color.primary_green);
            swipeRefreshLayout.setOnRefreshListener(() -> {
                bookingViewModel.loadCourts(selectedVenue.getVenueId());
                refreshBookings();
            });
        }

        tvSelectedDate.setOnClickListener(v -> showDatePicker());
        btnBack.setOnClickListener(v -> finish());
        
        btnNext.setOnClickListener(v -> {
            if (adapter.getSelectedSlots().isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một ca!", Toast.LENGTH_SHORT).show();
                return;
            }
            showServicesBottomSheet();
        });

        adapter.setOnSelectionChangedListener(count -> {
            if (count > 0) {
                btnNext.setText("TIẾP THEO (" + count + " ca - " + String.format("%,d", count * unitPrice) + "đ)");
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
    }

    private void refreshBookings() {
        bookingViewModel.loadBookings(selectedVenue.getVenueId(), tvSelectedDate.getText().toString());
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
            refreshBookings();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateDisplay(int d, int m, int y) {
        tvSelectedDate.setText(String.format("%02d/%02d/%04d", d, m, y));
    }
}
