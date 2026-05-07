package huce.fit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

public class HomeActivity extends Fragment {
    private TextView tvFullName;
    private MaterialButton btnHomeLogin, btnHomeRegister; 
    private Button btnBookCourt1, btnBookCourt2;
    private LinearLayout layoutAuthButtons;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sử dụng giao diện home.xml (đã dọn dẹp phần include footer)
        View view = inflater.inflate(R.layout.home, container, false);

        // --- ÁNH XẠ HEADER ---
        btnHomeLogin = view.findViewById(R.id.btnHomeLogin);
        btnHomeRegister = view.findViewById(R.id.btnHomeRegister);
        layoutAuthButtons = view.findViewById(R.id.layoutAuthButtons);
        tvFullName = view.findViewById(R.id.tvUserName);

        // --- ÁNH XẠ NÚT ĐẶT LỊCH ---
        btnBookCourt1 = view.findViewById(R.id.btnBookCourt1);
        btnBookCourt2 = view.findViewById(R.id.btnBookCourt2);

        if (btnBookCourt1 != null) {
            btnBookCourt1.setOnClickListener(v -> startActivity(new Intent(getActivity(), BookingActivity.class)));
        }
        if (btnBookCourt2 != null) {
            btnBookCourt2.setOnClickListener(v -> startActivity(new Intent(getActivity(), BookingActivity.class)));
        }

        // Auth Buttons
        if (btnHomeLogin != null) btnHomeLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        if (btnHomeRegister != null) btnHomeRegister.setOnClickListener(v -> startActivity(new Intent(getActivity(), SignUpActivity.class)));

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
