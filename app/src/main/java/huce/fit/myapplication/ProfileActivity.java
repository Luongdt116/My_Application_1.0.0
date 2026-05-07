package huce.fit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileActivity extends Fragment {

    private View layoutNotLoggedIn, layoutLoggedIn, layoutActivitySection, layoutPrivateSystem;
    private View btnProfileLogin, btnProfileRegister, btnLogout, layoutLicense, btnLayoutChangePassword;
    private TextView tvProfileName, tvHistory;
    private ImageView btnEditProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sử dụng layout profile.xml
        View view = inflater.inflate(R.layout.profile, container, false);

        // --- ÁNH XẠ CÁC KHỐI CHÍNH ---
        layoutNotLoggedIn = view.findViewById(R.id.layoutNotLoggedIn);
        layoutLoggedIn = view.findViewById(R.id.layoutLoggedIn);
        layoutActivitySection = view.findViewById(R.id.layoutActivitySection); // Khối chứa Lịch đã đặt & Thông báo
        layoutPrivateSystem = view.findViewById(R.id.layoutPrivateSystem);     // Khối chứa Đổi mật khẩu & Đăng xuất
        
        // --- ÁNH XẠ NÚT BẤM & TEXTVIEW ---
        btnProfileLogin = view.findViewById(R.id.btnProfileLogin);
        btnProfileRegister = view.findViewById(R.id.btnProfileRegister);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        btnLogout = view.findViewById(R.id.btnLogout);
        layoutLicense = view.findViewById(R.id.layoutLicense);
        tvHistory = view.findViewById(R.id.tvHistory);
        btnLayoutChangePassword = view.findViewById(R.id.btnLayoutChangePassword);

        // --- GÁN SỰ KIỆN CLICK ---
        if (btnProfileLogin != null) btnProfileLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        if (btnProfileRegister != null) btnProfileRegister.setOnClickListener(v -> startActivity(new Intent(getActivity(), SignUpActivity.class)));
        if (btnEditProfile != null) btnEditProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfileActivity.class)));
        if (layoutLicense != null) layoutLicense.setOnClickListener(v -> startActivity(new Intent(getActivity(), LicenseActivity.class)));
        
        if (tvHistory != null) {
            tvHistory.setOnClickListener(v -> startActivity(new Intent(getActivity(), HistoryActivity.class)));
        }

        if (btnLayoutChangePassword != null) {
            btnLayoutChangePassword.setOnClickListener(v -> startActivity(new Intent(getActivity(), ChangePasswordActivity.class)));
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                if (getActivity() == null) return;
                SharedPreferences pref = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                pref.edit().clear().apply();
                updateUIStatus();
            });
        }

        updateUIStatus();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUIStatus();
    }

    private void updateUIStatus() {
        if (getContext() == null) return;
        SharedPreferences pref = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = pref.getBoolean("isLoggedIn", false);
        String username = pref.getString("username", "");

        if (isLoggedIn) {
            // ĐÃ ĐĂNG NHẬP: Hiện thông tin cá nhân và các mục riêng tư
            if (layoutLoggedIn != null) layoutLoggedIn.setVisibility(View.VISIBLE);
            if (layoutNotLoggedIn != null) layoutNotLoggedIn.setVisibility(View.GONE);
            if (layoutActivitySection != null) layoutActivitySection.setVisibility(View.VISIBLE);
            if (layoutPrivateSystem != null) layoutPrivateSystem.setVisibility(View.VISIBLE);
            if (tvProfileName != null) tvProfileName.setText(username);
        } else {
            // CHƯA ĐĂNG NHẬP: Ẩn thông tin cá nhân và các mục riêng tư
            if (layoutLoggedIn != null) layoutLoggedIn.setVisibility(View.GONE);
            if (layoutNotLoggedIn != null) layoutNotLoggedIn.setVisibility(View.VISIBLE);
            if (layoutActivitySection != null) layoutActivitySection.setVisibility(View.GONE);
            if (layoutPrivateSystem != null) layoutPrivateSystem.setVisibility(View.GONE);
        }
    }
}
