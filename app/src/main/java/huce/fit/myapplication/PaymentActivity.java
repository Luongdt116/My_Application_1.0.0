package huce.fit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import huce.fit.myapplication.objects.Venue;
import huce.fit.myapplication.viewmodel.PaymentViewModel;
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
    private Map<String, Integer> selectedServices;
    private long totalPrice;
    
    private PaymentViewModel viewModel;
    private String currentUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // 1. Khởi tạo ZaloPay
        ZaloPaySDK.init(2554, Environment.SANDBOX);

        // 2. Nhận dữ liệu từ Intent
        getDataFromIntent();

        // 3. Ánh xạ và thiết lập giao diện
        initViews();
        setupViewModel();
        setupListeners();

        // Hiển thị thông tin tóm tắt ban đầu
        displayBookingInfo();
    }

    private void getDataFromIntent() {
        selectedVenue = (Venue) getIntent().getSerializableExtra("selected_venue");
        selectedDate = getIntent().getStringExtra("selected_date");
        selectedSlots = getIntent().getStringArrayListExtra("selected_slots");
        
        Object servicesObj = getIntent().getSerializableExtra("selected_services");
        selectedServices = (servicesObj instanceof Map) ? (Map<String, Integer>) servicesObj : new HashMap<>();
        
        totalPrice = getIntent().getLongExtra("total_price", 0);

        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        currentUserId = pref.getString("userId", "GUEST");
    }

    private void initViews() {
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
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(PaymentViewModel.class);

        // Lắng nghe Token từ ZaloPay
        viewModel.getZaloToken().observe(this, token -> {
            if (token != null) {
                openZaloPayApp(token);
            }
        });

        // Lắng nghe trạng thái lưu Booking thành công
        viewModel.getPaymentSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Đặt sân thành công!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        // Lắng nghe lỗi
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayBookingInfo() {
        if (selectedVenue != null) {
            tvVenueName.setText("Tên CLB: " + selectedVenue.getVenue_name());
            tvVenueAddress.setText("Địa chỉ: " + selectedVenue.getAddress_detail());
            tvDate.setText("Ngày: " + selectedDate);
            tvTotalPrice.setText(String.format(Locale.getDefault(), "Tổng tiền: %,dđ", totalPrice));
            
            // ViewModel lo việc format text tóm tắt
            String summary = viewModel.formatSummary(selectedSlots, selectedServices, selectedVenue);
            tvDetail.setText(summary);
        }
    }

    private void setupListeners() {
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> {
                String name = etName.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                if (name.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập tên và số điện thoại", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Bắt đầu quy trình thanh toán qua ViewModel
                viewModel.createZaloPayOrder(totalPrice);
            });
        }
    }

    private void openZaloPayApp(String token) {
        ZaloPaySDK.getInstance().payOrder(this, token, "demozpdk://app", new PayOrderListener() {
            @Override
            public void onPaymentSucceeded(String transactionId, String transToken, String appTransID) {
                // Thanh toán app thành công -> Bảo ViewModel lưu vào Firebase
                viewModel.saveBookings(
                        currentUserId, selectedVenue, selectedDate, selectedSlots, 
                        selectedServices, totalPrice, appTransID,
                        etName.getText().toString().trim(),
                        etPhone.getText().toString().trim(),
                        etNote.getText().toString().trim()
                );
            }

            @Override
            public void onPaymentCanceled(String zpTransToken, String appTransID) {
                Toast.makeText(PaymentActivity.this, "Hủy thanh toán", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                Toast.makeText(PaymentActivity.this, "Lỗi thanh toán: " + zaloPayError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}
