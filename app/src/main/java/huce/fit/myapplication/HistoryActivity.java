package huce.fit.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import huce.fit.myapplication.adapter.HistoryAdapter;
import huce.fit.myapplication.viewmodel.HistoryViewModel;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private HistoryViewModel viewModel;
    private ProgressBar progressBar;
    private TextView tvEmpty;

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

        // Lấy userId để tải lịch sử
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = pref.getString("userId", "");
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

        adapter = new HistoryAdapter();
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        // Quan sát danh sách lịch sử
        viewModel.getBookingList().observe(this, bookings -> {
            if (bookings != null && !bookings.isEmpty()) {
                adapter.setBookings(bookings);
                tvEmpty.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });

        // Quan sát trạng thái tải dữ liệu
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void setupListeners() {
        ImageView btnBack = findViewById(R.id.btnBackHistory);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
