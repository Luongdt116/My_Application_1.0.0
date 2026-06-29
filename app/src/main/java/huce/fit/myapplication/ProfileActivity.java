package huce.fit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import huce.fit.myapplication.viewmodel.ProfileViewModel;

public class ProfileActivity extends Fragment {

    private View layoutNotLoggedIn, layoutLoggedIn, layoutActivitySection, layoutPrivateSystem;
    private View btnProfileLogin, btnProfileRegister, btnLogout, layoutLicense, btnLayoutChangePassword;
    private TextView tvProfileName, tvProfileEmail, tvHistory;
    private ImageView btnEditProfile;
    
    private ProfileViewModel profileViewModel;
    private GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);

        initGoogleSignInClient();
        initViews(view);
        setupViewModel();
        setupListeners();

        updateUIStatus();
        return view;
    }

    private void initGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    private void initViews(View view) {
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
    }

    private void setupViewModel() {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        profileViewModel.getUserData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                String name = data.get("name");
                String email = data.get("email");
                if (tvProfileName != null && name != null) tvProfileName.setText(name);
                if (tvProfileEmail != null && email != null) tvProfileEmail.setText(email);
                saveUserToPrefs(name, email);
            }
        });

        profileViewModel.getIsLoggedOut().observe(getViewLifecycleOwner(), loggedOut -> {
            if (loggedOut) {
                performLogout();
            }
        });
    }

    private void setupListeners() {
        if (btnProfileLogin != null) btnProfileLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        if (btnProfileRegister != null) btnProfileRegister.setOnClickListener(v -> startActivity(new Intent(getActivity(), SignUpActivity.class)));
        if (btnEditProfile != null) btnEditProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfileActivity.class)));
        if (layoutLicense != null) layoutLicense.setOnClickListener(v -> startActivity(new Intent(getActivity(), LicenseActivity.class)));
        
        if (tvHistory != null) tvHistory.setOnClickListener(v -> startActivity(new Intent(getActivity(), HistoryActivity.class)));
        if (btnLayoutChangePassword != null) btnLayoutChangePassword.setOnClickListener(v -> startActivity(new Intent(getActivity(), ChangePasswordActivity.class)));

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> profileViewModel.logout());
        }
    }

    private void performLogout() {
        // 1. Đăng xuất khỏi Firebase Auth
        FirebaseAuth.getInstance().signOut();

        // 2. Đăng xuất khỏi Google (Ép buộc hiện lại bảng chọn tài khoản lần sau)
        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
            // 3. Xóa SharedPreferences
            SharedPreferences pref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            pref.edit().clear().apply();
            
            updateUIStatus();
            Toast.makeText(getActivity(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveUserToPrefs(String name, String email) {
        if (getContext() == null) return;
        SharedPreferences pref = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        pref.edit().putString("username", name).putString("email", email).apply();
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

        if (isLoggedIn && !userId.isEmpty()) {
            layoutLoggedIn.setVisibility(View.VISIBLE);
            layoutNotLoggedIn.setVisibility(View.GONE);
            layoutActivitySection.setVisibility(View.VISIBLE);
            layoutPrivateSystem.setVisibility(View.VISIBLE);
            tvProfileName.setText(username.isEmpty() ? "Người dùng" : username);
            profileViewModel.fetchUserInfo(userId);
        } else {
            layoutLoggedIn.setVisibility(View.GONE);
            layoutNotLoggedIn.setVisibility(View.VISIBLE);
            layoutActivitySection.setVisibility(View.GONE);
            layoutPrivateSystem.setVisibility(View.GONE);
        }
    }
}
