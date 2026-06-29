package huce.fit.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import huce.fit.myapplication.adapter.FieldAdapter;
import huce.fit.myapplication.objects.Venue;
import huce.fit.myapplication.viewmodel.HomeViewModel;

public class DiscoveryActivity extends Fragment {

    private RecyclerView rvDiscoveryFields;
    private FieldAdapter fieldAdapter;
    private HomeViewModel discoveryViewModel;
    private MaterialButton btnAll, btnOffer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discovery, container, false);

        // 1. Ánh xạ UI
        rvDiscoveryFields = view.findViewById(R.id.rvDiscoveryFields);
        btnAll = view.findViewById(R.id.btnAll);
        btnOffer = view.findViewById(R.id.btnDiscoveryOffer);

        // 2. Thiết lập RecyclerView
        fieldAdapter = new FieldAdapter();
        rvDiscoveryFields.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvDiscoveryFields.setAdapter(fieldAdapter);
        rvDiscoveryFields.setNestedScrollingEnabled(false);

        // 3. Kết nối ViewModel
        discoveryViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        
        // Lắng nghe danh sách sân (Tất cả)
        discoveryViewModel.getDisplayList().observe(getViewLifecycleOwner(), venues -> {
            if (venues != null && isTabActive(btnAll)) {
                fieldAdapter.setFields(venues);
            }
        });

        // Lắng nghe danh sách Khuyến mãi
        discoveryViewModel.getPromotedFields().observe(getViewLifecycleOwner(), venues -> {
            if (venues != null && isTabActive(btnOffer)) {
                fieldAdapter.setFields(venues);
                if (venues.isEmpty()) {
                    Toast.makeText(getActivity(), "Hiện không có sân nào có ưu đãi!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 4. Thiết lập sự kiện Click cho Adapter
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

        // 5. Xử lý chuyển đổi Tab
        btnAll.setOnClickListener(v -> {
            updateTabUI(true);
            discoveryViewModel.fetchFieldsFromFirebase();
        });

        btnOffer.setOnClickListener(v -> {
            updateTabUI(false);
            discoveryViewModel.fetchPromotedFieldsFromFirebase();
        });

        // Mặc định nạp tab Ưu đãi khi vào trang Khám phá
        updateTabUI(false);
        discoveryViewModel.fetchPromotedFieldsFromFirebase();

        return view;
    }

    private void updateTabUI(boolean isAllActive) {
        int activeColor = Color.parseColor("#09A459");
        int inactiveColor = Color.WHITE;
        int activeText = Color.WHITE;
        int inactiveText = Color.BLACK;

        if (isAllActive) {
            btnAll.setBackgroundTintList(ColorStateList.valueOf(activeColor));
            btnAll.setTextColor(activeText);
            btnOffer.setBackgroundTintList(ColorStateList.valueOf(inactiveColor));
            btnOffer.setTextColor(inactiveText);
        } else {
            btnOffer.setBackgroundTintList(ColorStateList.valueOf(activeColor));
            btnOffer.setTextColor(activeText);
            btnAll.setBackgroundTintList(ColorStateList.valueOf(inactiveColor));
            btnAll.setTextColor(inactiveText);
        }
    }

    private boolean isTabActive(MaterialButton btn) {
        return btn.getTextColors().getDefaultColor() == Color.WHITE;
    }
}
