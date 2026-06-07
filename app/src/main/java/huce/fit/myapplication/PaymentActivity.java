package huce.fit.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;
import huce.fit.myapplication.objects.Court;
import huce.fit.myapplication.objects.Venue;
import huce.fit.myapplication.util.CreateOrder;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvVenueName, tvVenueAddress, tvDate, tvDetail, tvTotalPrice;
    private EditText etName, etPhone, etNote;
    private MaterialButton btnConfirm;
    private ImageView btnBack;

    private Venue selectedVenue;
    private String selectedDate;
    private ArrayList<String> selectedSlots;
    private long totalPrice;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 1. Khởi tạo SDK bằng APP_ID từ ZaloPayConstant
        ZaloPaySDK.init(Integer.parseInt(ZaloPayConstant.APP_ID), Environment.SANDBOX);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // 2. Nhận dữ liệu
        selectedVenue = (Venue) getIntent().getSerializableExtra("selected_venue");
        selectedDate = getIntent().getStringExtra("selected_date");
        selectedSlots = getIntent().getStringArrayListExtra("selected_slots");
        totalPrice = getIntent().getLongExtra("total_price", 0);

        // 3. Ánh xạ UI
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

        if (selectedVenue != null) {
            tvVenueName.setText("Tên CLB: " + selectedVenue.getVenue_name());
            tvVenueAddress.setText("Địa chỉ: " + selectedVenue.getAddress_detail());
            tvDate.setText("Ngày: " + selectedDate);
            tvDetail.setText(formatSelectedSlots(selectedSlots));
            tvTotalPrice.setText("Tổng tiền: " + String.format("%,d", totalPrice) + "đ");
        }

        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            if (etName.getText().toString().trim().isEmpty() || etPhone.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập thông tin liên hệ", Toast.LENGTH_SHORT).show();
                return;
            }
            requestZaloPay();
        });
    }

    private void requestZaloPay() {
        CreateOrder orderApi = new CreateOrder();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                JSONObject data = orderApi.createOrder(String.valueOf(totalPrice));
                if (data == null) {
                    handler.post(() -> Toast.makeText(PaymentActivity.this, "Lỗi kết nối server ZaloPay", Toast.LENGTH_SHORT).show());
                    return;
                }
                
                Log.d("ZaloPay_Debug", "Response: " + data.toString());
                String code = data.getString("return_code");

                handler.post(() -> {
                    if (code.equals("1")) {
                        try {
                            String token = data.getString("zp_trans_token");
                            // Sử dụng APP_ID từ Constant để xây dựng Redirect URL động
                            String backUrl = "zp-redirect-" + ZaloPayConstant.APP_ID + "://app";
                            
                            ZaloPaySDK.getInstance().payOrder(PaymentActivity.this, token, backUrl, new PayOrderListener() {
                                @Override
                                public void onPaymentSucceeded(String transactionId, String transToken, String appTransID) {
                                    saveBookingsToFirebase(appTransID);
                                }

                                @Override
                                public void onPaymentCanceled(String zpTransToken, String appTransID) {
                                    Toast.makeText(PaymentActivity.this, "Hủy thanh toán", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                                    Toast.makeText(PaymentActivity.this, "Lỗi: " + zaloPayError.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        String msg = data.optString("return_message", "Lỗi tạo đơn hàng");
                        Toast.makeText(PaymentActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(PaymentActivity.this, "Lỗi hệ thống", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void saveBookingsToFirebase(String transactionId) {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "GUEST";
        long now = System.currentTimeMillis();
        
        String[] dateParts = selectedDate.split("/");
        String firebaseDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

        for (String slot : selectedSlots) {
            String[] parts = slot.split("\\|");
            String courtName = parts[0];
            int hour = Integer.parseInt(parts[1]);

            String courtId = "";
            if (selectedVenue.getCourts() != null) {
                for (Map.Entry<String, Court> entry : selectedVenue.getCourts().entrySet()) {
                    if (entry.getValue().getName().equals(courtName)) {
                        courtId = entry.getKey();
                        break;
                    }
                }
            }

            Map<String, Object> bookingData = new HashMap<>();
            bookingData.put("account_id", userId);
            bookingData.put("venue_id", selectedVenue.getVenueId());
            bookingData.put("court_id", courtId);
            bookingData.put("court_name", courtName);
            bookingData.put("booking_date", firebaseDate);
            bookingData.put("start_time", String.format("%02d:00", hour));
            bookingData.put("end_time", String.format("%02d:00", hour + 1));
            bookingData.put("total_price_snapshot", totalPrice / selectedSlots.size());
            bookingData.put("status", 1);
            bookingData.put("created_at", now);
            bookingData.put("customer_name", etName.getText().toString().trim());
            bookingData.put("customer_phone", etPhone.getText().toString().trim());
            bookingData.put("note", etNote.getText().toString().trim());

            Map<String, Object> paymentInfo = new HashMap<>();
            paymentInfo.put("method", "ZaloPay");
            paymentInfo.put("transaction_code", transactionId);
            paymentInfo.put("payment_status", 1);
            paymentInfo.put("amount", totalPrice / selectedSlots.size());
            paymentInfo.put("paid_at", now);
            
            bookingData.put("payment_info", paymentInfo);

            mDatabase.child("Bookings").push().setValue(bookingData);
        }

        Toast.makeText(this, "Đặt sân thành công!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private String formatSelectedSlots(ArrayList<String> slots) {
        if (slots == null || slots.isEmpty()) return "Chưa chọn ca";
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}
