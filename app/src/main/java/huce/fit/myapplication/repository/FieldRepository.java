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
    private static final String TAG = "FIREBASE_LOG";
    private final String dbUrl = "https://app-moblie-131d8-default-rtdb.firebaseio.com/";

    public FieldRepository() {
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
        mDatabase.child("Venues").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Venue> venueList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Venue venue = data.getValue(Venue.class);
                    if (venue != null && venue.getStatus() == 1) {
                        venue.setVenueId(data.getKey());
                        venueList.add(venue);
                    }
                }
                callback.onSuccess(venueList);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailure(error.getMessage()); }
        });
    }

    public void fetchPromotedFields(OnDataLoaded callback) {
        // Lấy Promotions trước
        mDatabase.child("Promotions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot promoSnapshot) {
                Set<String> promotedIds = new HashSet<>();
                for (DataSnapshot promo : promoSnapshot.getChildren()) {
                    Object statusObj = promo.child("status").getValue();
                    String vId = promo.child("venue_id").getValue(String.class);
                    if (vId != null && statusObj != null) {
                        long status = statusObj instanceof Long ? (Long) statusObj : Long.parseLong(statusObj.toString());
                        if (status == 1) promotedIds.add(vId);
                    }
                }

                // Sau đó lấy chi tiết Venues
                mDatabase.child("Venues").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot venueSnapshot) {
                        List<Venue> list = new ArrayList<>();
                        for (DataSnapshot vData : venueSnapshot.getChildren()) {
                            if (promotedIds.contains(vData.getKey())) {
                                Venue v = vData.getValue(Venue.class);
                                if (v != null && v.getStatus() == 1) {
                                    v.setVenueId(vData.getKey());
                                    list.add(v);
                                }
                            }
                        }
                        Log.d(TAG, "Ưu đãi: Tìm thấy " + list.size() + " sân.");
                        callback.onSuccess(list);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailure(error.getMessage()); }
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailure(error.getMessage()); }
        });
    }
}
