package huce.fit.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import huce.fit.myapplication.viewmodel.HomeViewModel;

public class DiscoveryActivity extends Fragment {

    private MaterialButton btnOffer;
    private RecyclerView rvDiscoveryFields;
    private FieldAdapter fieldAdapter;
    private HomeViewModel discoveryViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discovery, container, false);

        // 1. Ánh xạ UI
        btnOffer = view.findViewById(R.id.btnDiscoveryOffer);
        rvDiscoveryFields = view.findViewById(R.id.rvDiscoveryFields);

        // 2. Thiết lập RecyclerView (Dùng FieldAdapter giống trang Home)
        fieldAdapter = new FieldAdapter();
        rvDiscoveryFields.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvDiscoveryFields.setAdapter(fieldAdapter);
        rvDiscoveryFields.setNestedScrollingEnabled(false);

        // 3. Kết nối ViewModel và Lắng nghe dữ liệu (Tận dụng HomeViewModel vì nó đã có hàm fetchFields)
        discoveryViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        discoveryViewModel.getFields().observe(getViewLifecycleOwner(), fields -> {
            if (fields != null && !fields.isEmpty()) {
                fieldAdapter.setFields(fields);
                Log.d("DiscoveryActivity", "Đã tải " + fields.size() + " sân vào trang Discovery");
            } else {
                Toast.makeText(getActivity(), "Không có dữ liệu sân!", Toast.LENGTH_SHORT).show();
            }
        });

        // 4. Gọi lệnh tải dữ liệu từ Firebase
        discoveryViewModel.fetchFieldsFromFirebase();

        // 5. Sự kiện Click cho các thành phần khác
        if (btnOffer != null) {
            btnOffer.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).navigateToOffers();
                }
            });
        }

        // Xử lý click vào item sân (nếu cần)
        fieldAdapter.setOnFieldClickListener(new FieldAdapter.OnFieldClickListener() {
            @Override
            public void onBookClick(int position) {
                startActivity(new Intent(getActivity(), BookingActivity.class));
            }

            @Override
            public void onItemClick(int position) {
                // Có thể mở chi tiết sân ở đây
                startActivity(new Intent(getActivity(), BookingActivity.class));
            }
        });

        return view;
    }
}
