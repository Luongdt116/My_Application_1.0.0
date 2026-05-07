package huce.fit.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

public class OffersActivity extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sử dụng layout activity_offers.xml nhưng bỏ phần include footer
        View view = inflater.inflate(R.layout.activity_offers, container, false);

        MaterialButton btnAll = view.findViewById(R.id.btnOffersAll);
        if (btnAll != null) {
            btnAll.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).navigateToDiscovery();
                }
            });
        }

        return view;
    }
}
