package huce.fit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import huce.fit.myapplication.adapter.FieldAdapter;
import huce.fit.myapplication.viewmodel.HomeViewModel;

public class HomeActivity extends Fragment {
    private TextView tvFullName;
    private MaterialButton btnHomeLogin, btnHomeRegister; 
    private LinearLayout layoutAuthButtons;
    private RecyclerView rvFields;
    private FieldAdapter fieldAdapter;
    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        // 1. Ánh xạ UI
        rvFields = view.findViewById(R.id.rvFields);
        btnHomeLogin = view.findViewById(R.id.btnHomeLogin);
        btnHomeRegister = view.findViewById(R.id.btnHomeRegister);
        layoutAuthButtons = view.findViewById(R.id.layoutAuthButtons);
        tvFullName = view.findViewById(R.id.tvUserName);

        // 2. Thiết lập RecyclerView
        fieldAdapter = new FieldAdapter();
        rvFields.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFields.setAdapter(fieldAdapter);
        rvFields.setNestedScrollingEnabled(false); // Quan trọng để cuộn mượt trong NestedScrollView

        // 3. Kết nối MVVM và Lắng nghe dữ liệu
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getFields().observe(getViewLifecycleOwner(), fields -> {
            if (fields != null && !fields.isEmpty()) {
                fieldAdapter.setFields(fields);
                Log.d("HomeActivity", "Đã nhận " + fields.size() + " sân từ ViewModel");
            } else {
                Toast.makeText(getActivity(), "Chưa có dữ liệu sân từ Firebase!", Toast.LENGTH_LONG).show();
            }
        });

        // 4. Gọi lệnh tải dữ liệu
        homeViewModel.fetchFieldsFromFirebase();

        // 5. Sự kiện Click
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
