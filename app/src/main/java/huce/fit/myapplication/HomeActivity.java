package huce.fit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class HomeActivity extends AppCompatActivity {
    private TextView tvFullName;
    private MaterialButton btnHomeLogin, btnHomeRegister; 
    private Button btnBookCourt1, btnBookCourt2;
    private LinearLayout layoutAuthButtons, layoutDiscoveryTab, layoutProfileTab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // --- ÁNH XẠ HEADER (Sử dụng đúng kiểu dữ liệu từ XML) ---
        btnHomeLogin = findViewById(R.id.btnHomeLogin);
        btnHomeRegister = findViewById(R.id.btnHomeRegister);
        layoutAuthButtons = findViewById(R.id.layoutAuthButtons);
        tvFullName = findViewById(R.id.tvUserName);

        // --- ÁNH XẠ CÁC NÚT ĐẶT LỊCH (Sử dụng đúng kiểu dữ liệu Button) ---
        btnBookCourt1 = findViewById(R.id.btnBookCourt1);
        btnBookCourt2 = findViewById(R.id.btnBookCourt2);

        // --- GÁN SỰ KIỆN CLICK ĐẶT LỊCH ---
        if (btnBookCourt1 != null) {
            btnBookCourt1.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, BookingActivity.class));
            });
        }
        if (btnBookCourt2 != null) {
            btnBookCourt2.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, BookingActivity.class));
            });
        }

        setupFooterNavigation();
        
        // Gán sự kiện Auth
        if (btnHomeLogin != null) {
            btnHomeLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        }
        if (btnHomeRegister != null) {
            btnHomeRegister.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        }

        updateUIStatus();
    }

    private void setupFooterNavigation() {
        layoutDiscoveryTab = findViewById(R.id.layoutDiscoveryTab);
        layoutProfileTab = findViewById(R.id.layoutProfileTab);
        
        // Highlight tab Home
        ImageView ivHome = findViewById(R.id.ivHomeTab);
        TextView tvHome = findViewById(R.id.tvHomeTab);
        if (ivHome != null) ivHome.setImageTintList(ColorStateList.valueOf(Color.parseColor("#09A459")));
        if (tvHome != null) tvHome.setTextColor(Color.parseColor("#09A459"));

        if (layoutDiscoveryTab != null) {
            layoutDiscoveryTab.setOnClickListener(v -> {
                Intent intent = new Intent(this, DiscoveryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            });
        }

        if (layoutProfileTab != null) {
            layoutProfileTab.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIStatus();
    }

    private void updateUIStatus() {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = pref.getBoolean("isLoggedIn", false);
        String username = pref.getString("username", "");

        if (isLoggedIn && tvFullName != null) {
            tvFullName.setText("Chào, " + username);
            tvFullName.setVisibility(View.VISIBLE);
            if (layoutAuthButtons != null) layoutAuthButtons.setVisibility(View.GONE);
        } else if (tvFullName != null) {
            tvFullName.setVisibility(View.GONE);
            if (layoutAuthButtons != null) layoutAuthButtons.setVisibility(View.VISIBLE);
        }
    }
}
