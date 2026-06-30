package huce.fit.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import huce.fit.myapplication.adapter.FieldAdapter;
import huce.fit.myapplication.objects.Venue;
import huce.fit.myapplication.viewmodel.HomeViewModel;

public class DiscoveryActivity extends Fragment {

    private RecyclerView rvDiscoveryFields;
    private FieldAdapter fieldAdapter;
    private HomeViewModel discoveryViewModel;
    private MaterialButton btnAll, btnOffer, btnLoadMore;
    private EditText etSearchDiscovery;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int currentTab = 0; // 0: Tất cả, 1: Ưu đãi

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discovery, container, false);
        initViews(view);
        setupRecyclerView();
        setupViewModel();
        setupListeners();
        
        selectTab(0);
        return view;
    }

    private void initViews(View view) {
        rvDiscoveryFields = view.findViewById(R.id.rvDiscoveryFields);
        btnAll = view.findViewById(R.id.btnAll);
        btnOffer = view.findViewById(R.id.btnDiscoveryOffer);
        btnLoadMore = view.findViewById(R.id.btnLoadMoreDiscovery);
        etSearchDiscovery = view.findViewById(R.id.etSearchDiscovery);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshDiscovery);
    }

    private void setupRecyclerView() {
        fieldAdapter = new FieldAdapter();
        rvDiscoveryFields.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvDiscoveryFields.setAdapter(fieldAdapter);
        rvDiscoveryFields.setNestedScrollingEnabled(false);
    }

    private void setupViewModel() {
        discoveryViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        discoveryViewModel.getDisplayList().observe(getViewLifecycleOwner(), venues -> {
            if (currentTab == 0 && venues != null) {
                fieldAdapter.setFields(venues);
            }
        });

        discoveryViewModel.getPromotedFields().observe(getViewLifecycleOwner(), venues -> {
            if (currentTab == 1 && venues != null) {
                fieldAdapter.setFields(venues);
                if (venues.size() > 0) {
                    Toast.makeText(getActivity(), "Tìm thấy " + venues.size() + " sân có ưu đãi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        discoveryViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) swipeRefreshLayout.setRefreshing(isLoading);
        });

        discoveryViewModel.getShowLoadMoreDiscovery().observe(getViewLifecycleOwner(), show -> {
            if (btnLoadMore != null) btnLoadMore.setVisibility(show ? View.VISIBLE : View.GONE);
        });
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (currentTab == 0) discoveryViewModel.refreshFields();
            else discoveryViewModel.refreshPromotedFields();
        });

        if (etSearchDiscovery != null) {
            etSearchDiscovery.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (currentTab == 0) discoveryViewModel.search(s.toString());
                    else discoveryViewModel.searchDiscovery(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        btnAll.setOnClickListener(v -> selectTab(0));
        btnOffer.setOnClickListener(v -> selectTab(1));

        fieldAdapter.setOnFieldClickListener(new FieldAdapter.OnFieldClickListener() {
            @Override public void onBookClick(Venue venue) { openBooking(venue); }
            @Override public void onItemClick(Venue venue) { openBooking(venue); }
        });

        if (btnLoadMore != null) {
            btnLoadMore.setOnClickListener(v -> {
                if (currentTab == 0) discoveryViewModel.loadNextPage();
                else discoveryViewModel.loadNextPageDiscovery();
            });
        }
    }

    private void selectTab(int tabIndex) {
        this.currentTab = tabIndex;
        updateTabUI(tabIndex);
        if (etSearchDiscovery != null) etSearchDiscovery.setText("");

        if (tabIndex == 0) {
            List<Venue> data = discoveryViewModel.getDisplayList().getValue();
            if (data == null || data.isEmpty()) discoveryViewModel.refreshFields();
            else fieldAdapter.setFields(data);
        } else {
            List<Venue> data = discoveryViewModel.getPromotedFields().getValue();
            if (data == null || data.isEmpty()) discoveryViewModel.refreshPromotedFields();
            else fieldAdapter.setFields(data);
        }
    }

    private void updateTabUI(int activeTab) {
        int colorActive = Color.parseColor("#09A459");
        int colorInactive = Color.WHITE;
        if (activeTab == 0) {
            btnAll.setBackgroundTintList(ColorStateList.valueOf(colorActive));
            btnAll.setTextColor(Color.WHITE);
            btnOffer.setBackgroundTintList(ColorStateList.valueOf(colorInactive));
            btnOffer.setTextColor(Color.BLACK);
        } else {
            btnOffer.setBackgroundTintList(ColorStateList.valueOf(colorActive));
            btnOffer.setTextColor(Color.WHITE);
            btnAll.setBackgroundTintList(ColorStateList.valueOf(colorInactive));
            btnAll.setTextColor(Color.BLACK);
        }
    }

    private void openBooking(Venue venue) {
        Intent intent = new Intent(getActivity(), BookingActivity.class);
        intent.putExtra("selected_venue", venue);
        startActivity(intent);
    }
}
