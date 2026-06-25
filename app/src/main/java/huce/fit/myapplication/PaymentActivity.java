package huce.fit.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import huce.fit.myapplication.objects.Venue;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvVenueName, tvVenueAddress, tvDate, tvDetail, tvTotalPrice;
    private EditText etName, etPhone, etNote;
    private MaterialButton btnConfirm;
    private ImageView btnBack;

    private Venue selectedVenue;
    private String selectedDate;
    private ArrayList<String> selectedSlots;
    private long totalPrice;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 1. Nhận dữ liệu từ Intent
        selectedVenue = (Venue) getIntent().getSerializableExtra("selected_venue");
        selectedDate = getIntent().getStringExtra("selected_date");
        selectedSlots = getIntent().getStringArrayListExtra("selected_slots");
        totalPrice = getIntent().getLongExtra("total_price", 0);

        // 2. Ánh xạ UI
        tvVenueName = findViewById(R.id.tvPaymentVenueName);
        tvVenueAddress = findViewById(R.id.tvPaymentVenueAddress);
        tvDate = findViewById(R.id.tvPaymentDate);
        tvDetail = findViewById(R.id.tvPaymentDetail);
        tvTotalPrice = findViewById(R.id.tvPaymentTotalPrice);
        
        etName = findViewById(R.id.etPaymentName);
        etPhone = findViewById(R.id.etPaymentPhone);
        etNote = findViewById(R.id.etPaymentNote);
        
        btnConfirm = findViewById(R.id.btnConfirmPayment);
        btnBack = findViewById(R.id.btnBackPayment);

        // 3. Hiển thị dữ liệu
        if (selectedVenue != null) {
            tvVenueName.setText("Tên CLB: " + selectedVenue.getVenue_name());
            tvVenueAddress.setText("Địa chỉ: " + selectedVenue.getAddress_detail());
            tvDate.setText("Ngày: " + selectedDate);
            
            // Format danh sách ca đã chọn: "Sân 1|5, Sân 1|6" -> "Sân 1: 5h-6h, 6h-7h"
            tvDetail.setText(formatSelectedSlots(selectedSlots));
            
            tvTotalPrice.setText("Tổng tiền: " + String.format("%,d", totalPrice) + "đ");
        }

        // 4. Sự kiện
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            
            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên và số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }
            
            processBooking();
        });
    }

    private String formatSelectedSlots(ArrayList<String> slots) {
        if (slots == null || slots.isEmpty()) return "Chưa chọn ca";
        
        // Group by court name
        Map<String, List<Integer>> grouped = new HashMap<>();
        for (String s : slots) {
            String[] parts = s.split("\\|");
            if (parts.length == 2) {
                String courtName = parts[0];
                int hour = Integer.parseInt(parts[1]);
                if (!grouped.containsKey(courtName)) {
                    grouped.put(courtName, new ArrayList<>());
                }
                grouped.get(courtName).add(hour);
            }
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<Integer>> entry : grouped.entrySet()) {
            sb.append("- ").append(entry.getKey()).append(": ");
            List<Integer> hours = entry.getValue();
            Collections.sort(hours);
            for (int i = 0; i < hours.size(); i++) {
                int h = hours.get(i);
                sb.append(h).append("h-").append(h + 1).append("h");
                if (i < hours.size() - 1) sb.append(", ");
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    private void processBooking() {
        Toast.makeText(this, "Đặt sân thành công! Chúng tôi sẽ liên hệ sớm.", Toast.LENGTH_LONG).show();
        // Quay về màn hình chính
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
