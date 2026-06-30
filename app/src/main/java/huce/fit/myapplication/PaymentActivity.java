package huce.fit.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

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
    private ProgressDialog progressDialog;

    private boolean isRetryPayment = false;
    private String pendingBookingId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        ZaloPaySDK.init(2554, Environment.SANDBOX);

        initViews(); // Khởi tạo View trước
        getDataFromIntent(); // Sau đó mới lấy data điền vào view
        setupViewModel();
        setupListeners();
        displayBookingInfo();
        
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý giao dịch...");
        progressDialog.setCancelable(false);

        if (isRetryPayment) {
            etName.setEnabled(false);
            etPhone.setEnabled(false);
            etNote.setEnabled(false);
        }
    }

    private void getDataFromIntent() {
        isRetryPayment = getIntent().getBooleanExtra("is_retry_payment", false);
        pendingBookingId = getIntent().getStringExtra("booking_id");

        selectedVenue = (Venue) getIntent().getSerializableExtra("selected_venue");
        selectedDate = getIntent().getStringExtra("selected_date");
        selectedSlots = getIntent().getStringArrayListExtra("selected_slots");
        
        Object servicesObj = getIntent().getSerializableExtra("selected_services");
        selectedServices = (servicesObj instanceof Map) ? (Map<String, Integer>) servicesObj : new HashMap<>();
        
        totalPrice = getIntent().getLongExtra("total_price", 0);

        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        currentUserId = pref.getString("userId", "GUEST");

        if (isRetryPayment) {
            etName.setText(getIntent().getStringExtra("customer_name"));
            etPhone.setText(getIntent().getStringExtra("customer_phone"));
            etNote.setText(getIntent().getStringExtra("customer_note"));
        }
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

        viewModel.getZaloToken().observe(this, token -> {
            if (token != null) openZaloPayApp(token);
        });

        viewModel.getPaymentSuccess().observe(this, success -> {
            if (success) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                showSuccessDialog();
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thành công")
                .setMessage("Đã thanh toán thành công! Lịch đặt sân của bạn đã được lưu lại.")
                .setCancelable(false)
                .setPositiveButton("Về trang chủ", (dialog, axis) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    private void displayBookingInfo() {
        if (selectedVenue != null) {
            tvVenueName.setText("Tên CLB: " + selectedVenue.getVenue_name());
            tvVenueAddress.setText("Địa chỉ: " + selectedVenue.getAddress_detail());
            tvDate.setText("Ngày: " + selectedDate);
            tvTotalPrice.setText(String.format(Locale.getDefault(), "%,dđ", totalPrice));
            tvDetail.setText(viewModel.formatSummary(selectedSlots, selectedServices, selectedVenue));
        }
    }

    private void setupListeners() {
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> {
                if (currentUserId.equals("GUEST")) {
                    showLoginRequiredDialog();
                    return;
                }

                String name = etName.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                if (name.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập tên và số điện thoại", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewModel.createZaloPayOrder(totalPrice);
            });
        }
    }

    private void showLoginRequiredDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Yêu cầu đăng nhập")
                .setMessage("Bạn cần đăng nhập để thực hiện đặt sân. Sau khi đăng nhập, đơn hàng của bạn sẽ được lưu vào lịch sử để bạn có thể tiếp tục thanh toán.")
                .setPositiveButton("Đăng nhập ngay", (dialog, which) -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("pending_booking", true);
                    intent.putExtra("selected_venue", selectedVenue);
                    intent.putExtra("selected_date", selectedDate);
                    intent.putStringArrayListExtra("selected_slots", selectedSlots);
                    intent.putExtra("selected_services", (HashMap)selectedServices);
                    intent.putExtra("total_price", totalPrice);
                    intent.putExtra("customer_name", etName.getText().toString());
                    intent.putExtra("customer_phone", etPhone.getText().toString());
                    intent.putExtra("customer_note", etNote.getText().toString());
                    startActivity(intent);
                })
                .setNegativeButton("Để sau", null)
                .show();
    }

    private void openZaloPayApp(String token) {
        ZaloPaySDK.getInstance().payOrder(this, token, "demozpdk://app", new PayOrderListener() {
            @Override
            public void onPaymentSucceeded(String transactionId, String transToken, String appTransID) {
                progressDialog.show();
                if (isRetryPayment && !pendingBookingId.isEmpty()) {
                    viewModel.updateBookingStatus(pendingBookingId, appTransID);
                } else {
                    viewModel.saveBookings(
                            currentUserId, selectedVenue, selectedDate, selectedSlots, 
                            selectedServices, totalPrice, appTransID,
                            etName.getText().toString().trim(),
                            etPhone.getText().toString().trim(),
                            etNote.getText().toString().trim()
                    );
                }
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
