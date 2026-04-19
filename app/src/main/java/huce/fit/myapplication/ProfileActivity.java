package huce.fit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private View layoutNotLoggedIn, layoutLoggedIn, layoutNotification, layoutPrivateSystem;
    private View btnProfileLogin, btnProfileRegister, btnLogout, layoutLicense;
    private TextView tvProfileName, tvHistory, tvChangePassword;
    private ImageView btnEditProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setupFooterNavigation();
        setupContent();
        updateUIStatus();
    }

    private void setupFooterNavigation() {
        View tabHome = findViewById(R.id.layoutHomeTab);
        View tabDiscovery = findViewById(R.id.layoutDiscoveryTab);
        
        TextView tvProfile = findViewById(R.id.tvProfileTab);
        if (tvProfile != null) {
            tvProfile.setTextColor(Color.parseColor("#09A459"));
            tvProfile.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        if (tabHome != null) {
            tabHome.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));
        }

        if (tabDiscovery != null) {
            tabDiscovery.setOnClickListener(v -> startActivity(new Intent(this, DiscoveryActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));
        }
    }

    private void setupContent() {
        layoutNotLoggedIn = findViewById(R.id.layoutNotLoggedIn);
        layoutLoggedIn = findViewById(R.id.layoutLoggedIn);
        layoutNotification = findViewById(R.id.layoutNotification);
        layoutPrivateSystem = findViewById(R.id.layoutPrivateSystem);
        
        btnProfileLogin = findViewById(R.id.btnProfileLogin);
        btnProfileRegister = findViewById(R.id.btnProfileRegister);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        tvProfileName = findViewById(R.id.tvProfileName);
        btnLogout = findViewById(R.id.btnLogout);
        layoutLicense = findViewById(R.id.layoutLicense);
        tvHistory = findViewById(R.id.tvHistory);

        // --- ÁNH XẠ TEXTVIEW ĐỔI MẬT KHẨU ---
        tvChangePassword = findViewById(R.id.tvChangePassword);

        if (tvChangePassword != null) {
            tvChangePassword.setOnClickListener(v -> {
                startActivity(new Intent(this, ChangePasswordActivity.class));
            });
        }

        if (tvHistory != null) {
            tvHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        }

        if (btnProfileLogin != null) btnProfileLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        if (btnProfileRegister != null) btnProfileRegister.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        if (btnEditProfile != null) btnEditProfile.setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));
        if (layoutLicense != null) layoutLicense.setOnClickListener(v -> startActivity(new Intent(this, LicenseActivity.class)));
        
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                pref.edit().clear().apply();
                updateUIStatus();
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

        if (isLoggedIn) {
            if (layoutLoggedIn != null) layoutLoggedIn.setVisibility(View.VISIBLE);
            if (layoutNotLoggedIn != null) layoutNotLoggedIn.setVisibility(View.GONE);
            if (layoutNotification != null) layoutNotification.setVisibility(View.VISIBLE);
            if (layoutPrivateSystem != null) layoutPrivateSystem.setVisibility(View.VISIBLE);
            if (tvProfileName != null) tvProfileName.setText(username);
        } else {
            if (layoutLoggedIn != null) layoutLoggedIn.setVisibility(View.GONE);
            if (layoutNotLoggedIn != null) layoutNotLoggedIn.setVisibility(View.VISIBLE);
            if (layoutNotification != null) layoutNotification.setVisibility(View.GONE);
            if (layoutPrivateSystem != null) layoutPrivateSystem.setVisibility(View.GONE);
        }
    }
}
