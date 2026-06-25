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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends Fragment {

    private View layoutNotLoggedIn, layoutLoggedIn, layoutActivitySection, layoutPrivateSystem;
    private View btnProfileLogin, btnProfileRegister, btnLogout, layoutLicense, btnLayoutChangePassword;
    private TextView tvProfileName, tvProfileEmail, tvHistory;
    private ImageView btnEditProfile;
    private DatabaseReference mDatabase;
    private String dbUrl = "https://app-moblie-131d8-default-rtdb.firebaseio.com/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);

        mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference();

        // 1. Ánh xạ View
        layoutNotLoggedIn = view.findViewById(R.id.layoutNotLoggedIn);
        layoutLoggedIn = view.findViewById(R.id.layoutLoggedIn);
        layoutActivitySection = view.findViewById(R.id.layoutActivitySection);
        layoutPrivateSystem = view.findViewById(R.id.layoutPrivateSystem);
        
        btnProfileLogin = view.findViewById(R.id.btnProfileLogin);
        btnProfileRegister = view.findViewById(R.id.btnProfileRegister);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        layoutLicense = view.findViewById(R.id.layoutLicense);
        tvHistory = view.findViewById(R.id.tvHistory);
        btnLayoutChangePassword = view.findViewById(R.id.btnLayoutChangePassword);

        // 2. Sự kiện Click
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
                Toast.makeText(getActivity(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
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
        String userId = pref.getString("userId", "");
        String username = pref.getString("username", "");
        String email = pref.getString("email", "");

        if (isLoggedIn && !userId.isEmpty()) {
            if (layoutLoggedIn != null) layoutLoggedIn.setVisibility(View.VISIBLE);
            if (layoutNotLoggedIn != null) layoutNotLoggedIn.setVisibility(View.GONE);
            if (layoutActivitySection != null) layoutActivitySection.setVisibility(View.VISIBLE);
            if (layoutPrivateSystem != null) layoutPrivateSystem.setVisibility(View.VISIBLE);
            
            // HIỂN THỊ NGAY THÔNG TIN TỪ BỘ NHỚ TẠM
            if (tvProfileName != null) tvProfileName.setText(username.isEmpty() ? "Người dùng" : username);
            if (tvProfileEmail != null) tvProfileEmail.setText(email.isEmpty() ? "Đang tải..." : email);
            
            // ĐỒNG BỘ MỚI NHẤT TỪ FIREBASE
            fetchUserInfoFromFirebase(userId);
        } else {
            if (layoutLoggedIn != null) layoutLoggedIn.setVisibility(View.GONE);
            if (layoutNotLoggedIn != null) layoutNotLoggedIn.setVisibility(View.VISIBLE);
            if (layoutActivitySection != null) layoutActivitySection.setVisibility(View.GONE);
            if (layoutPrivateSystem != null) layoutPrivateSystem.setVisibility(View.GONE);
        }
    }

    private void fetchUserInfoFromFirebase(String uid) {
        mDatabase.child("Accounts").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && isAdded()) {
                    String name = snapshot.child("full_name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    if (tvProfileName != null && name != null) tvProfileName.setText(name);
                    if (tvProfileEmail != null && email != null) tvProfileEmail.setText(email);
                    
                    // Cập nhật lại bộ nhớ tạm
                    if (getContext() != null) {
                        SharedPreferences pref = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        pref.edit().putString("username", name).putString("email", email).apply();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
