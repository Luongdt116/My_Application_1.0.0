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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import huce.fit.myapplication.adapter.FieldAdapter;
import huce.fit.myapplication.objects.Venue;
import huce.fit.myapplication.viewmodel.HomeViewModel;

public class HomeActivity extends Fragment {
    private TextView tvFullName;
    private MaterialButton btnHomeLogin, btnHomeRegister; 
    private LinearLayout layoutAuthButtons;
    private RecyclerView rvFields;
    private FieldAdapter fieldAdapter;
    private HomeViewModel homeViewModel;
    private List<Venue> fullVenueList = new ArrayList<>();

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
        rvFields.setNestedScrollingEnabled(false);

        // 3. Kết nối MVVM và Lắng nghe dữ liệu
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getFields().observe(getViewLifecycleOwner(), fields -> {
            if (fields != null) {
                fullVenueList = fields;
                fieldAdapter.setFields(fields);
            }
        });

        // 4. Gọi lệnh tải dữ liệu
        homeViewModel.fetchFieldsFromFirebase();

        // 5. Khôi phục chức năng lọc theo môn thể thao
        setupSportFilters(view);

        // 6. Sự kiện Click cho từng sân
        fieldAdapter.setOnFieldClickListener(new FieldAdapter.OnFieldClickListener() {
            @Override
            public void onBookClick(Venue venue) {
                Intent intent = new Intent(getActivity(), BookingActivity.class);
                intent.putExtra("selected_venue", venue);
                startActivity(intent);
            }

            @Override
            public void onItemClick(Venue venue) {
                Intent intent = new Intent(getActivity(), BookingActivity.class);
                intent.putExtra("selected_venue", venue);
                startActivity(intent);
            }
        });

        // 7. Auth buttons
        if (btnHomeLogin != null) btnHomeLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        if (btnHomeRegister != null) btnHomeRegister.setOnClickListener(v -> startActivity(new Intent(getActivity(), SignUpActivity.class)));

        updateUIStatus();
        return view;
    }

    private void setupSportFilters(View view) {
        View btnPickleball = view.findViewById(R.id.btnFilterPickleball);
        View btnBadminton = view.findViewById(R.id.btnFilterBadminton);
        View btnFootball = view.findViewById(R.id.btnFilterFootball);
        View btnTennis = view.findViewById(R.id.btnFilterTennis);
        View btnVolleyball = view.findViewById(R.id.btnFilterVolleyball);
        View btnBasketball = view.findViewById(R.id.btnFilterBasketball);

        if (btnPickleball != null) btnPickleball.setOnClickListener(v -> filterBySport("Pickleball"));
        if (btnBadminton != null) btnBadminton.setOnClickListener(v -> filterBySport("Cầu lông"));
        if (btnFootball != null) btnFootball.setOnClickListener(v -> filterBySport("Bóng đá"));
        if (btnTennis != null) btnTennis.setOnClickListener(v -> filterBySport("Tennis"));
        if (btnVolleyball != null) btnVolleyball.setOnClickListener(v -> filterBySport("Bóng chuyền"));
        if (btnBasketball != null) btnBasketball.setOnClickListener(v -> filterBySport("Bóng rổ"));
    }

    private void filterBySport(String sportName) {
        if (fullVenueList == null) return;
        List<Venue> filtered = new ArrayList<>();
        for (Venue venue : fullVenueList) {
            if (venue.getSport_name() != null && venue.getSport_name().toLowerCase().contains(sportName.toLowerCase())) {
                filtered.add(venue);
            }
        }
        fieldAdapter.setFields(filtered);
        if (filtered.isEmpty()) {
            Toast.makeText(getActivity(), "Không tìm thấy sân " + sportName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Đang hiện: " + sportName, Toast.LENGTH_SHORT).show();
        }
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
