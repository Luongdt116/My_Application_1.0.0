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
import huce.fit.myapplication.objects.Venue;
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

        btnOffer = view.findViewById(R.id.btnDiscoveryOffer);
        rvDiscoveryFields = view.findViewById(R.id.rvDiscoveryFields);

        fieldAdapter = new FieldAdapter();
        rvDiscoveryFields.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvDiscoveryFields.setAdapter(fieldAdapter);
        rvDiscoveryFields.setNestedScrollingEnabled(false);

        discoveryViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        discoveryViewModel.getFields().observe(getViewLifecycleOwner(), fields -> {
            if (fields != null && !fields.isEmpty()) {
                fieldAdapter.setFields(fields);
            }
        });

        discoveryViewModel.fetchFieldsFromFirebase();

        if (btnOffer != null) {
            btnOffer.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).navigateToOffers();
                }
            });
        }

        // SỬA LỖI TẠI ĐÂY: Thay int position bằng Venue venue
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
