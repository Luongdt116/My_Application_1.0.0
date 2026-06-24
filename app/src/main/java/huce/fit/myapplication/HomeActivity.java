package huce.fit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    private EditText etSearchHome;
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
        etSearchHome = view.findViewById(R.id.etSearchHome);

        // 2. Thiết lập RecyclerView
        fieldAdapter = new FieldAdapter();
        rvFields.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFields.setAdapter(fieldAdapter);
        rvFields.setNestedScrollingEnabled(false);

        // 3. Kết nối MVVM và Lắng nghe dữ liệu
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getFields().observe(getViewLifecycleOwner(), venues -> {
            if (venues != null) {
                fullVenueList = venues;
                fieldAdapter.setFields(venues);
            }
        });

        // 4. Gọi lệnh tải dữ liệu
        homeViewModel.fetchFieldsFromFirebase();

        // 5. Thiết lập sự kiện Tìm kiếm
        if (etSearchHome != null) {
            etSearchHome.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterByName(s.toString());
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // 6. Thiết lập các nút lọc môn thể thao
        setupSportFilters(view);

        // 7. Sự kiện Click cho từng sân
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

        // 8. Auth Buttons
        if (btnHomeLogin != null) btnHomeLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        if (btnHomeRegister != null) btnHomeRegister.setOnClickListener(v -> startActivity(new Intent(getActivity(), SignUpActivity.class)));

        updateUIStatus();
        return view;
    }

    private void setupSportFilters(View view) {
        View pkb = view.findViewById(R.id.btnFilterPickleball);
        View bd = view.findViewById(R.id.btnFilterBadminton);
        View fb = view.findViewById(R.id.btnFilterFootball);
        View tn = view.findViewById(R.id.btnFilterTennis);
        View vb = view.findViewById(R.id.btnFilterVolleyball);
        View bb = view.findViewById(R.id.btnFilterBasketball);

        if (pkb != null) pkb.setOnClickListener(v -> filterBySport("Pickleball"));
        if (bd != null) bd.setOnClickListener(v -> filterBySport("Cầu lông"));
        if (fb != null) fb.setOnClickListener(v -> filterBySport("Bóng đá"));
        if (tn != null) tn.setOnClickListener(v -> filterBySport("Tennis"));
        if (vb != null) vb.setOnClickListener(v -> filterBySport("Bóng chuyền"));
        if (bb != null) bb.setOnClickListener(v -> filterBySport("Bóng rổ"));
    }

    private void filterByName(String query) {
        List<Venue> filtered = new ArrayList<>();
        for (Venue venue : fullVenueList) {
            if (venue.getVenue_name().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(venue);
            }
        }
        fieldAdapter.setFields(filtered);
    }

    private void filterBySport(String sport) {
        List<Venue> filtered = new ArrayList<>();
        for (Venue venue : fullVenueList) {
            if (venue.getSport_name() != null && venue.getSport_name().toLowerCase().contains(sport.toLowerCase())) {
                filtered.add(venue);
            }
        }
        fieldAdapter.setFields(filtered);
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
