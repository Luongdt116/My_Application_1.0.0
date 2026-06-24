package huce.fit.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import huce.fit.myapplication.adapter.HistoryAdapter;
import huce.fit.myapplication.objects.Booking;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private List<Booking> bookingList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 1. Ánh xạ UI
        rvHistory = findViewById(R.id.rvHistory);
        progressBar = findViewById(R.id.pbHistory);
        tvEmpty = findViewById(R.id.tvEmptyHistory);
        ImageView btnBack = findViewById(R.id.btnBackHistory);

        // 2. Setup RecyclerView
        adapter = new HistoryAdapter();
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);

        // 3. Firebase Init
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // 4. Sự kiện
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        loadHistory();
    }

    private void loadHistory() {
        if (mAuth.getCurrentUser() == null) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("Vui lòng đăng nhập để xem lịch sử");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        mDatabase.child("Bookings")
                .orderByChild("account_id")
                .equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bookingList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Booking booking = data.getValue(Booking.class);
                                if (booking != null) {
                                    bookingList.add(booking);
                                }
                            }
                            
                            // Sắp xếp đơn mới nhất lên đầu
                            Collections.sort(bookingList, (b1, b2) -> Long.compare(b2.getCreated_at(), b1.getCreated_at()));
                            
                            adapter.setBookings(bookingList);
                            tvEmpty.setVisibility(View.GONE);
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                        tvEmpty.setText("Lỗi: " + error.getMessage());
                    }
                });
    }
}
