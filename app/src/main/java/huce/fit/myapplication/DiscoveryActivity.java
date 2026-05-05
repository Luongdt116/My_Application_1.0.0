package huce.fit.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

public class DiscoveryActivity extends Fragment {

    private MaterialButton btnOffer;
    private MaterialButton btnDetail1, btnDetail2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discovery, container, false);

        // --- ÁNH XẠ NỘI DUNG (GIỮ NGUYÊN ID) ---
        btnOffer = view.findViewById(R.id.btnDiscoveryOffer);
        btnDetail1 = view.findViewById(R.id.btnDiscoveryViewDetail1);
        btnDetail2 = view.findViewById(R.id.btnDiscoveryViewDetail2);

        // Chuyển sang trang Offers (Gọi hàm từ MainActivity)
        if (btnOffer != null) {
            btnOffer.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).navigateToOffers();
                }
            });
        }

        // Các nút Xem chi tiết sang trang Booking (vẫn giữ là Activity)
        if (btnDetail1 != null) {
            btnDetail1.setOnClickListener(v -> startActivity(new Intent(getActivity(), BookingActivity.class)));
        }
        if (btnDetail2 != null) {
            btnDetail2.setOnClickListener(v -> startActivity(new Intent(getActivity(), BookingActivity.class)));
        }

        return view;
    }
}
