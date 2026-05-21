package huce.fit.myapplication;

import android.content.Intent;
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

public class OffersActivity extends Fragment {

    private RecyclerView rvOffersFields;
    private FieldAdapter fieldAdapter;
    private HomeViewModel offersViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_offers, container, false);

        // 1. Ánh xạ UI
        MaterialButton btnAll = view.findViewById(R.id.btnOffersAll);
        rvOffersFields = view.findViewById(R.id.rvOffersFields);

        // 2. Thiết lập RecyclerView
        fieldAdapter = new FieldAdapter();
        rvOffersFields.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvOffersFields.setAdapter(fieldAdapter);
        rvOffersFields.setNestedScrollingEnabled(false);

        // 3. Kết nối ViewModel
        offersViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        offersViewModel.getPromotedFields().observe(getViewLifecycleOwner(), fields -> {
            if (fields != null && !fields.isEmpty()) {
                fieldAdapter.setFields(fields);
            } else {
                Toast.makeText(getActivity(), "Hiện tại chưa có ưu đãi nào!", Toast.LENGTH_SHORT).show();
            }
        });

        // 4. Tải dữ liệu các sân có ưu đãi
        offersViewModel.fetchPromotedFieldsFromFirebase();

        // 5. Sự kiện Click
        if (btnAll != null) {
            btnAll.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).navigateToDiscovery();
                }
            });
        }

        // SỬA LỖI TẠI ĐÂY: Thay int position bằng Venue venue để đồng bộ với Interface
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

        return view;
    }
}
