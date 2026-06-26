package huce.fit.myapplication;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

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
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        initViews(view);
        setupRecyclerView();
        setupViewModel();
        setupListeners(view);

        // Khởi động các tác vụ tải dữ liệu
        homeViewModel.checkLoginStatus(getContext());
        homeViewModel.fetchFieldsFromFirebase();

        return view;
    }

    private void initViews(View view) {
        scrollViewHome = view.findViewById(R.id.scrollViewHome);
        rvFields = view.findViewById(R.id.rvFields);
        btnHomeLogin = view.findViewById(R.id.btnHomeLogin);
        btnHomeRegister = view.findViewById(R.id.btnHomeRegister);
        btnLoadMore = view.findViewById(R.id.btnLoadMore);
        layoutAuthButtons = view.findViewById(R.id.layoutAuthButtons);
        tvFullName = view.findViewById(R.id.tvUserName);
        etSearchHome = view.findViewById(R.id.etSearchHome);
        tvClearFilter = view.findViewById(R.id.tvClearFilter);
        progressBar = view.findViewById(R.id.pbHome);
    }

    private void setupRecyclerView() {
        fieldAdapter = new FieldAdapter();
        // RESPONSIVE: Khi xoay ngang chia 2 cột, xoay dọc 1 cột
        int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1;
        rvFields.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
        rvFields.setAdapter(fieldAdapter);
        rvFields.setNestedScrollingEnabled(false);
    }

    private void setupViewModel() {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        
        // 1. Lắng nghe danh sách hiển thị
        homeViewModel.getDisplayList().observe(getViewLifecycleOwner(), venues -> {
            if (venues != null) {
                fieldAdapter.setFields(venues);
            }
        });

        // 2. Lắng nghe trạng thái Loading
        homeViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (progressBar != null) progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // 3. Lắng nghe nút Xem thêm
        homeViewModel.getShowLoadMore().observe(getViewLifecycleOwner(), show -> {
            if (btnLoadMore != null) btnLoadMore.setVisibility(show ? View.VISIBLE : View.GONE);
        });

        // 4. Lắng nghe nút Xóa lọc
        homeViewModel.getShowClearFilter().observe(getViewLifecycleOwner(), show -> {
            if (tvClearFilter != null) tvClearFilter.setVisibility(show ? View.VISIBLE : View.GONE);
        });

        // 5. Lắng nghe trạng thái đăng nhập để cập nhật Header
        homeViewModel.getIsLoggedIn().observe(getViewLifecycleOwner(), isLoggedIn -> {
            if (layoutAuthButtons != null) layoutAuthButtons.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
            if (tvFullName != null) tvFullName.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        });

        homeViewModel.getWelcomeMessage().observe(getViewLifecycleOwner(), message -> {
            if (tvFullName != null) tvFullName.setText(message);
        });
    }

    private void setupListeners(View view) {
        // Tìm kiếm: Báo cho ViewModel xử lý
        if (etSearchHome != null) {
            etSearchHome.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    homeViewModel.search(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        if (tvClearFilter != null) {
            tvClearFilter.setOnClickListener(v -> {
                etSearchHome.setText("");
                homeViewModel.clearFilter();
            });
        }

        if (btnLoadMore != null) {
            btnLoadMore.setOnClickListener(v -> homeViewModel.loadNextPage());
        }

        setupSportIcons(view);

        fieldAdapter.setOnFieldClickListener(new FieldAdapter.OnFieldClickListener() {
            @Override public void onBookClick(Venue venue) { goToBooking(venue); }
            @Override public void onItemClick(Venue venue) { goToBooking(venue); }
        });

        if (btnHomeLogin != null) btnHomeLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        if (btnHomeRegister != null) btnHomeRegister.setOnClickListener(v -> startActivity(new Intent(getActivity(), SignUpActivity.class)));
    }

    private void setupSportIcons(View view) {
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

    private void goToBooking(Venue venue) {
        Intent intent = new Intent(getActivity(), BookingActivity.class);
        intent.putExtra("selected_venue", venue);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật lại trạng thái đăng nhập khi quay lại trang Home
        homeViewModel.checkLoginStatus(getContext());
    }
}
