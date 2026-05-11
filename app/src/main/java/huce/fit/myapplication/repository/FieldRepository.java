package huce.fit.myapplication.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import huce.fit.myapplication.objects.Venue;

public class FieldRepository {
    private DatabaseReference mDatabase;
    private static final String TAG = "FIREBASE_CHECK";

    public FieldRepository() {
        // CẬP NHẬT: Sử dụng URL máy chủ Mỹ (US) theo đúng Project của bạn
        String dbUrl = "https://app-moblie-131d8-default-rtdb.firebaseio.com/";
        
        try {
            mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference("Venues");
            Log.d(TAG, "Đã khởi tạo kết nối tới máy chủ Mỹ: " + dbUrl);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi kết nối URL: " + e.getMessage());
            // Dự phòng: Tự động lấy từ google-services.json
            mDatabase = FirebaseDatabase.getInstance().getReference("Venues");
        }
    }

    public interface OnDataLoaded {
        void onSuccess(List<Venue> venueList);
        void onFailure(String error);
    }

    public void fetchAllFields(OnDataLoaded callback) {
        if (mDatabase == null) return;

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Venue> venueList = new ArrayList<>();
                Log.d(TAG, "Dữ liệu có tồn tại trên Web không? " + snapshot.exists());
                
                if (snapshot.exists()) {
                    Log.d(TAG, "THÀNH CÔNG! Đã thấy " + snapshot.getChildrenCount() + " sân bóng.");
                    for (DataSnapshot data : snapshot.getChildren()) {
                        try {
                            Venue venue = data.getValue(Venue.class);
                            if (venue != null) {
                                venue.setVenueId(data.getKey());
                                venueList.add(venue);
                                Log.d(TAG, "Đã nạp sân: " + venue.getVenue_name());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi khi nạp dữ liệu tại " + data.getKey() + ": " + e.getMessage());
                        }
                    }
                } else {
                    Log.e(TAG, "THẤT BẠI: Nút 'Venues' rỗng. Hãy kiểm tra lại tên nút trên Web (phải đúng chữ V viết hoa)");
                }
                callback.onSuccess(venueList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "LỖI FIREBASE: " + error.getMessage());
                callback.onFailure(error.getMessage());
            }
        });
    }
}
