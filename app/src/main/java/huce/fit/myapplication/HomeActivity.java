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
import androidx.core.widget.NestedScrollView;
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
    private TextView tvFullName, tvClearFilter;
    private MaterialButton btnHomeLogin, btnHomeRegister, btnLoadMore; 
    private LinearLayout layoutAuthButtons;
    private RecyclerView rvFields;
    private FieldAdapter fieldAdapter;
    private HomeViewModel homeViewModel;
    private EditText etSearchHome;
    private NestedScrollView scrollViewHome;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        // 1. Ánh xạ UI
        scrollViewHome = view.findViewById(R.id.scrollViewHome);
        rvFields = view.findViewById(R.id.rvFields);
        btnHomeLogin = view.findViewById(R.id.btnHomeLogin);
        btnHomeRegister = view.findViewById(R.id.btnHomeRegister);
        btnLoadMore = view.findViewById(R.id.btnLoadMore);
        layoutAuthButtons = view.findViewById(R.id.layoutAuthButtons);
        tvFullName = view.findViewById(R.id.tvUserName);
        etSearchHome = view.findViewById(R.id.etSearchHome);
        tvClearFilter = view.findViewById(R.id.tvClearFilter);

        // 2. Thiết lập RecyclerView
        fieldAdapter = new FieldAdapter();
        rvFields.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFields.setAdapter(fieldAdapter);
        rvFields.setNestedScrollingEnabled(false);

        // 3. Kết nối MVVM và Lắng nghe dữ liệu từ ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        
        // Lắng nghe danh sách sân đã được phân trang và lọc
        homeViewModel.getPaginatedFields().observe(getViewLifecycleOwner(), venues -> {
            if (venues != null) {
                fieldAdapter.setFields(venues);
            }
        });

        // Lắng nghe trạng thái ẩn/hiện nút Xem thêm
        homeViewModel.getShowLoadMore().observe(getViewLifecycleOwner(), show -> {
            if (btnLoadMore != null) {
                btnLoadMore.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

        // Lắng nghe trạng thái nút Xóa lọc
        homeViewModel.getShowClearFilter().observe(getViewLifecycleOwner(), show -> {
            if (tvClearFilter != null) {
                tvClearFilter.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

        // 4. Gọi lệnh tải dữ liệu ban đầu
        homeViewModel.fetchFieldsFromFirebase();

        // 5. Thiết lập sự kiện Tìm kiếm (Gửi query cho ViewModel xử lý)
        if (etSearchHome != null) {
            etSearchHome.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    homeViewModel.search(s.toString());
                    // Cuộn về đầu khi tìm kiếm
                    if (scrollViewHome != null) scrollViewHome.smoothScrollTo(0, 0);
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        // 6. Nút Xóa lọc
        if (tvClearFilter != null) {
            tvClearFilter.setOnClickListener(v -> {
                etSearchHome.setText("");
                homeViewModel.clearFilter();
            });
        }

        // 7. Nút Xem thêm (Phân trang)
        if (btnLoadMore != null) {
            btnLoadMore.setOnClickListener(v -> homeViewModel.loadNextPage());
        }

        // 8. Thiết lập lọc nhanh qua Icon môn thể thao
        setupSportFilters(view);

        // 9. Sự kiện Click Item
        fieldAdapter.setOnFieldClickListener(new FieldAdapter.OnFieldClickListener() {
            @Override public void onBookClick(Venue venue) { goToBooking(venue); }
            @Override public void onItemClick(Venue venue) { goToBooking(venue); }
        });

        // 10. Auth Buttons
        if (btnHomeLogin != null) btnHomeLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        if (btnHomeRegister != null) btnHomeRegister.setOnClickListener(v -> startActivity(new Intent(getActivity(), SignUpActivity.class)));

        updateUIStatus();
        return view;
    }

    private void goToBooking(Venue venue) {
        Intent intent = new Intent(getActivity(), BookingActivity.class);
        intent.putExtra("selected_venue", venue);
        startActivity(intent);
    }

    private void setupSportFilters(View view) {
        int[] ids = {R.id.btnFilterPickleball, R.id.btnFilterBadminton, R.id.btnFilterFootball, 
                     R.id.btnFilterTennis, R.id.btnFilterVolleyball, R.id.btnFilterBasketball};
        String[] sports = {"Pickleball", "Cầu lông", "Bóng đá", "Tennis", "Bóng chuyền", "Bóng rổ"};

        for (int i = 0; i < ids.length; i++) {
            final String name = sports[i];
            View btn = view.findViewById(ids[i]);
            if (btn != null) {
                btn.setOnClickListener(v -> {
                    etSearchHome.setText(name);
                    homeViewModel.search(name);
                });
            }
        }
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

    @Override
    public void onResume() {
        super.onResume();
        updateUIStatus();
    }
}
