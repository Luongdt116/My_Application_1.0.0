package huce.fit.myapplication.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        // 1. Tải danh sách Promotions trước để biết sân nào đang có ưu đãi
        mDatabase.child("Promotions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot promoSnapshot) {
                List<String> promotedIds = new ArrayList<>();
                for (DataSnapshot promo : promoSnapshot.getChildren()) {
                    Object statusObj = promo.child("status").getValue();
                    String vId = promo.child("venue_id").getValue(String.class);
                    if (vId != null && statusObj != null && String.valueOf(statusObj).equals("1")) {
                        promotedIds.add(vId);
                    }
                }

                // 2. Tải danh sách Venues và LỌC BỎ các sân có ưu đãi
                mDatabase.child("Venues").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Venue> venueList = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String vId = data.getKey();
                            // Chỉ thêm vào "Tất cả" nếu sân KHÔNG nằm trong danh sách ưu đãi
                            if (vId != null && !promotedIds.contains(vId)) {
                                Venue venue = data.getValue(Venue.class);
                                if (venue != null && venue.getStatus() == 1) {
                                    venue.setVenueId(vId);
                                    venueList.add(venue);
                                }
                            }
                        }
                        callback.onSuccess(venueList);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailure(error.getMessage()); }
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailure(error.getMessage()); }
        });
    }

    public void fetchPromotedFields(OnDataLoaded callback) {
        mDatabase.child("Promotions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot promoSnapshot) {
                Map<String, String> promotedMap = new HashMap<>();
                for (DataSnapshot promo : promoSnapshot.getChildren()) {
                    Object statusObj = promo.child("status").getValue();
                    String vId = promo.child("venue_id").getValue(String.class);
                    String title = promo.child("title").getValue(String.class);
                    
                    if (vId != null && statusObj != null && String.valueOf(statusObj).equals("1")) {
                        promotedMap.put(vId, title);
                    }
                }

                mDatabase.child("Venues").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot venueSnapshot) {
                        List<Venue> list = new ArrayList<>();
                        for (DataSnapshot vData : venueSnapshot.getChildren()) {
                            String venueId = vData.getKey();
                            if (venueId != null && promotedMap.containsKey(venueId)) {
                                Venue v = vData.getValue(Venue.class);
                                if (v != null && v.getStatus() == 1) {
                                    v.setVenueId(venueId);
                                    v.setPromotionTitle(promotedMap.get(venueId));
                                    list.add(v);
                                }
                            }
                        }
                        callback.onSuccess(list);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailure(error.getMessage()); }
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailure(error.getMessage()); }
        });
    }
}
