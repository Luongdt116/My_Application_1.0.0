package huce.fit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    private TextView tvFullName, tvSuggestedTitle;
    private MaterialButton btnHomeLogin, btnHomeRegister; 
    private LinearLayout layoutAuthButtons;
    private RecyclerView rvFields;
    private FieldAdapter fieldAdapter;
    private HomeViewModel homeViewModel;

    // Khai báo các nút môn thể thao
    private LinearLayout btnPickleball, btnBadminton, btnFootball, btnTennis, btnVolleyball, btnBasketball;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        // 1. Ánh xạ UI cơ bản (Sửa ID tvHomeListTitle cho khớp XML)
        rvFields = view.findViewById(R.id.rvFields);
        tvSuggestedTitle = view.findViewById(R.id.tvHomeListTitle); 
        btnHomeLogin = view.findViewById(R.id.btnHomeLogin);
        btnHomeRegister = view.findViewById(R.id.btnHomeRegister);
        layoutAuthButtons = view.findViewById(R.id.layoutAuthButtons);
        tvFullName = view.findViewById(R.id.tvUserName);

        // 2. Ánh xạ các nút môn thể thao (Sửa ID cho khớp XML)
        btnPickleball = view.findViewById(R.id.btnPickleball);
        btnBadminton = view.findViewById(R.id.btnBadminton);
        btnFootball = view.findViewById(R.id.btnFootball);
        btnTennis = view.findViewById(R.id.btnTennis);
        btnVolleyball = view.findViewById(R.id.btnVolleyball);
        btnBasketball = view.findViewById(R.id.btnBasketball);

        // 3. Thiết lập RecyclerView
        fieldAdapter = new FieldAdapter();
        rvFields.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFields.setAdapter(fieldAdapter);
        rvFields.setNestedScrollingEnabled(false);

        // 4. Kết nối MVVM
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getFields().observe(getViewLifecycleOwner(), venues -> {
            if (venues != null) {
                fieldAdapter.setFields(venues);
            }
        });

        // Tải dữ liệu ban đầu từ Firebase
        homeViewModel.fetchFieldsFromFirebase();

        // 5. Thiết lập sự kiện Click cho các nút môn thể thao
        setupFilterButtons();

        // Các sự kiện Auth
        if (btnHomeLogin != null) btnHomeLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        if (btnHomeRegister != null) btnHomeRegister.setOnClickListener(v -> startActivity(new Intent(getActivity(), SignUpActivity.class)));

        updateUIStatus();
        return view;
    }

    private void setupFilterButtons() {
        View.OnClickListener filterListener = v -> {
            String sport = "";
            int id = v.getId();
            if (id == R.id.btnPickleball) sport = "Pickleball";
            else if (id == R.id.btnBadminton) sport = "Cầu lông";
            else if (id == R.id.btnFootball) sport = "Bóng đá";
            else if (id == R.id.btnTennis) sport = "Tennis";
            else if (id == R.id.btnVolleyball) sport = "Bóng chuyền";
            else if (id == R.id.btnBasketball) sport = "Bóng rổ";

            // Cập nhật tiêu đề hiển thị
            if (tvSuggestedTitle != null) {
                tvSuggestedTitle.setText("Danh sách sân " + sport);
            }
            // Gọi ViewModel để lọc dữ liệu
            homeViewModel.filterBySport(sport);
        };

        if (btnPickleball != null) btnPickleball.setOnClickListener(filterListener);
        if (btnBadminton != null) btnBadminton.setOnClickListener(filterListener);
        if (btnFootball != null) btnFootball.setOnClickListener(filterListener);
        if (btnTennis != null) btnTennis.setOnClickListener(filterListener);
        if (btnVolleyball != null) btnVolleyball.setOnClickListener(filterListener);
        if (btnBasketball != null) btnBasketball.setOnClickListener(filterListener);
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
