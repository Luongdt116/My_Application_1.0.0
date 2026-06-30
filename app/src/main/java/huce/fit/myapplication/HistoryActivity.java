package huce.fit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;

import huce.fit.myapplication.adapter.HistoryAdapter;
import huce.fit.myapplication.objects.Booking;
import huce.fit.myapplication.objects.Venue;
import huce.fit.myapplication.viewmodel.HistoryViewModel;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private HistoryViewModel viewModel;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupViewModel();
        setupListeners();

        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = pref.getString("userId", "");
        
        if (!userId.isEmpty()) {
            viewModel.fetchBookingHistory(userId);
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập để xem lịch sử", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        rvHistory = findViewById(R.id.rvHistory);
        progressBar = findViewById(R.id.pbHistory);
        tvEmpty = findViewById(R.id.tvEmptyHistory);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshHistory);

        adapter = new HistoryAdapter();
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        viewModel.getBookingList().observe(this, bookings -> {
            if (bookings != null && !bookings.isEmpty()) {
                adapter.setBookings(bookings);
                tvEmpty.setVisibility(View.GONE);
            } else {
                adapter.setBookings(new ArrayList<>());
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                swipeRefreshLayout.setRefreshing(isLoading);
            }
        });

        viewModel.getStatusMessage().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                viewModel.fetchBookingHistory(userId);
            }
        });
    }

    private void setupListeners() {
        ImageView btnBack = findViewById(R.id.btnBackHistory);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeResources(R.color.primary_green);
            swipeRefreshLayout.setOnRefreshListener(() -> {
                if (!userId.isEmpty()) {
                    viewModel.fetchBookingHistory(userId);
                }
            });
        }

        adapter.setOnBookingActionListener(new HistoryAdapter.OnBookingActionListener() {
            @Override
            public void onPayNow(Booking booking) {
                retryPayment(booking);
            }

            @Override
            public void onDelete(Booking booking) {
                showDeleteConfirmDialog(booking);
            }
        });
    }

    private void retryPayment(Booking booking) {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("is_retry_payment", true);
        intent.putExtra("booking_id", booking.getBookingId());
        
        Venue venue = new Venue();
        venue.setVenueId(booking.getVenue_id());
        venue.setVenue_name(booking.getVenue_name());
        // Lưu ý: Địa chỉ thường không được lưu trực tiếp trong Booking cũ, 
        // ta có thể hiển thị tạm hoặc truyền trống, PaymentActivity sẽ xử lý.
        venue.setAddress_detail("Dữ liệu từ lịch sử"); 
        
        intent.putExtra("selected_venue", venue);
        intent.putExtra("selected_date", booking.getBooking_date());
        
        // Reconstruct slots
        ArrayList<String> slots = new ArrayList<>();
        String hour = booking.getStart_time().split(":")[0];
        slots.add(booking.getCourt_name() + "|" + hour);
        intent.putStringArrayListExtra("selected_slots", slots);
        
        intent.putExtra("selected_services", (HashMap)booking.getSelected_services());
        intent.putExtra("total_price", booking.getTotal_price_snapshot());
        intent.putExtra("customer_name", booking.getCustomer_name());
        intent.putExtra("customer_phone", booking.getCustomer_phone());
        intent.putExtra("customer_note", booking.getNote());
        
        startActivity(intent);
    }

    private void showDeleteConfirmDialog(Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa đơn hàng này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteBooking(booking.getBookingId());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
