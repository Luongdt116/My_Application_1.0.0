package huce.fit.myapplication.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import huce.fit.myapplication.objects.Venue;

public class FieldRepository {
    private DatabaseReference mDatabase;
    private static final String TAG = "FIREBASE_CHECK";

    public FieldRepository() {
        String dbUrl = "https://app-moblie-131d8-default-rtdb.firebaseio.com/";
        try {
            mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference();
        } catch (Exception e) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
        }
    }

    public interface OnDataLoaded {
        void onSuccess(List<Venue> venueList);
        void onFailure(String error);
    }

    public void fetchAllFields(OnDataLoaded callback) {
        mDatabase.child("Venues").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Venue> venueList = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            Venue venue = data.getValue(Venue.class);
                            if (venue != null && venue.getStatus() == 1) {
                                venue.setVenueId(data.getKey());
                                venueList.add(venue);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi nạp dữ liệu: " + e.getMessage());
                        }
                    }
                }
                callback.onSuccess(venueList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public void fetchPromotedFields(OnDataLoaded callback) {
        // BƯỚC 1: Lấy danh sách ID các sân có ưu đãi (status = 1)
        mDatabase.child("Promotions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot promoSnapshot) {
                Set<String> promotedVenueIds = new HashSet<>();
                for (DataSnapshot promoData : promoSnapshot.getChildren()) {
                    Integer status = promoData.child("status").getValue(Integer.class);
                    String venueId = promoData.child("venue_id").getValue(String.class);
                    if (status != null && status == 1 && venueId != null) {
                        promotedVenueIds.add(venueId);
                    }
                }

                // BƯỚC 2: Lấy thông tin chi tiết các sân đó
                mDatabase.child("Venues").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot venueSnapshot) {
                        List<Venue> promotedVenues = new ArrayList<>();
                        for (DataSnapshot venueData : venueSnapshot.getChildren()) {
                            if (promotedVenueIds.contains(venueData.getKey())) {
                                Venue venue = venueData.getValue(Venue.class);
                                if (venue != null && venue.getStatus() == 1) {
                                    venue.setVenueId(venueData.getKey());
                                    promotedVenues.add(venue);
                                }
                            }
                        }
                        callback.onSuccess(promotedVenues);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }
}
