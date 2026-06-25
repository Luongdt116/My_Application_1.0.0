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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import huce.fit.myapplication.adapter.FieldAdapter;
import huce.fit.myapplication.adapter.SearchHistoryAdapter;
import huce.fit.myapplication.objects.Venue;
import huce.fit.myapplication.viewmodel.HomeViewModel;

public class HomeActivity extends Fragment {
    private TextView tvFullName;
    private EditText etSearch;
    private ImageView btnClearSearch;
    private LinearLayout layoutNoResults;
    private View cardSearchHistory;
    private TextView btnClearAllHistory;
    private RecyclerView rvSearchHistory;
    private SearchHistoryAdapter historyAdapter;
    
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
        etSearch = view.findViewById(R.id.etSearch);
        btnClearSearch = view.findViewById(R.id.btnClearSearch);
        layoutNoResults = view.findViewById(R.id.layoutNoResults);
        
        cardSearchHistory = view.findViewById(R.id.cardSearchHistory);
        btnClearAllHistory = view.findViewById(R.id.btnClearAllHistory);
        rvSearchHistory = view.findViewById(R.id.rvSearchHistory);
        
        btnHomeLogin = view.findViewById(R.id.btnHomeLogin);
        btnHomeRegister = view.findViewById(R.id.btnHomeRegister);
        layoutAuthButtons = view.findViewById(R.id.layoutAuthButtons);
        tvFullName = view.findViewById(R.id.tvUserName);

        // 2. Thiết lập RecyclerView Sân
        fieldAdapter = new FieldAdapter();
        rvFields.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFields.setAdapter(fieldAdapter);
        rvFields.setNestedScrollingEnabled(false);
        
        // 3. Thiết lập RecyclerView Lịch sử
        historyAdapter = new SearchHistoryAdapter();
        rvSearchHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSearchHistory.setAdapter(historyAdapter);

        // 4. Kết nối MVVM
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        
        homeViewModel.getFields().observe(getViewLifecycleOwner(), fields -> {
            if (fields != null) {
                fieldAdapter.setFields(fields);
                if (fields.isEmpty() && !etSearch.getText().toString().isEmpty()) {
                    layoutNoResults.setVisibility(View.VISIBLE);
                } else {
                    layoutNoResults.setVisibility(View.GONE);
                }
            }
        });
        
        homeViewModel.getHistory().observe(getViewLifecycleOwner(), history -> {
            if (history != null) {
                historyAdapter.setData(history);
            }
        });

        // 5. Tải dữ liệu
        homeViewModel.fetchFieldsFromFirebase();

        // 6. Lọc theo môn thể thao
        setupSportFilters(view);

        // 7. Click sân
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

        // 8. Auth
        if (btnHomeLogin != null) btnHomeLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        if (btnHomeRegister != null) btnHomeRegister.setOnClickListener(v -> startActivity(new Intent(getActivity(), SignUpActivity.class)));

        // 9. Tìm kiếm & Lịch sử
        setupSearchLogic();

        updateUIStatus();
        return view;
    }

    private void setupSearchLogic() {
        if (etSearch == null) return;
        
        etSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && etSearch.getText().toString().isEmpty()) {
                cardSearchHistory.setVisibility(View.VISIBLE);
            } else {
                cardSearchHistory.setVisibility(View.GONE);
            }
        });
        
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                homeViewModel.filter(query);
                
                btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                
                // Khi đang gõ thì ẩn lịch sử, trừ khi xóa hết thì hiện lại
                if (query.isEmpty()) {
                    cardSearchHistory.setVisibility(View.VISIBLE);
                } else {
                    cardSearchHistory.setVisibility(View.GONE);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Lưu vào lịch sử khi nhấn Enter trên bàn phím
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                String query = etSearch.getText().toString();
                if (!query.isEmpty()) {
                    homeViewModel.addHistory(query);
                }
                hideKeyboard();
                etSearch.clearFocus();
                return true;
            }
            return false;
        });

        btnClearSearch.setOnClickListener(v -> etSearch.setText(""));
        
        btnClearAllHistory.setOnClickListener(v -> homeViewModel.clearAllHistory());
        
        historyAdapter.setOnHistoryItemClickListener(new SearchHistoryAdapter.OnHistoryItemClickListener() {
            @Override
            public void onItemClick(String query) {
                etSearch.setText(query);
                etSearch.clearFocus();
                hideKeyboard();
            }

            @Override
            public void onDeleteClick(String query) {
                homeViewModel.removeHistory(query);
            }
        });
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setupSportFilters(View view) {
        View btnPickleball = view.findViewById(R.id.btnFilterPickleball);
        View btnBadminton = view.findViewById(R.id.btnFilterBadminton);
        View btnFootball = view.findViewById(R.id.btnFilterFootball);
        View btnTennis = view.findViewById(R.id.btnFilterTennis);
        View btnVolleyball = view.findViewById(R.id.btnFilterVolleyball);
        View btnBasketball = view.findViewById(R.id.btnFilterBasketball);

        View.OnClickListener listener = v -> {
            String sport = "";
            if (v.getId() == R.id.btnFilterPickleball) sport = "Pickleball";
            else if (v.getId() == R.id.btnFilterBadminton) sport = "Cầu lông";
            else if (v.getId() == R.id.btnFilterFootball) sport = "Bóng đá";
            else if (v.getId() == R.id.btnFilterTennis) sport = "Tennis";
            else if (v.getId() == R.id.btnFilterVolleyball) sport = "Bóng chuyền";
            else if (v.getId() == R.id.btnFilterBasketball) sport = "Bóng rổ";
            
            etSearch.setText(sport);
            homeViewModel.addHistory(sport);
            etSearch.clearFocus();
        };

        if (btnPickleball != null) btnPickleball.setOnClickListener(listener);
        if (btnBadminton != null) btnBadminton.setOnClickListener(listener);
        if (btnFootball != null) btnFootball.setOnClickListener(listener);
        if (btnTennis != null) btnTennis.setOnClickListener(listener);
        if (btnVolleyball != null) btnVolleyball.setOnClickListener(listener);
        if (btnBasketball != null) btnBasketball.setOnClickListener(listener);
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
