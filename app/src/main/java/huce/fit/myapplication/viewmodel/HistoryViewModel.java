package huce.fit.myapplication.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import huce.fit.myapplication.objects.Booking;

public class HistoryViewModel extends ViewModel {
    private final MutableLiveData<List<Booking>> mBookingList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private final MutableLiveData<String> mStatusMessage = new MutableLiveData<>();
    
    private final DatabaseReference mDatabase;
    private final String dbUrl = "https://app-moblie-131d8-default-rtdb.firebaseio.com/";

    public HistoryViewModel() {
        mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference();
    }

    public LiveData<List<Booking>> getBookingList() { return mBookingList; }
    public LiveData<Boolean> getIsLoading() { return mIsLoading; }
    public LiveData<String> getStatusMessage() { return mStatusMessage; }

    public void fetchBookingHistory(String userId) {
        if (userId == null || userId.isEmpty()) return;
        
        mIsLoading.setValue(true);
        mDatabase.child("Bookings")
                .orderByChild("account_id").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Booking> list = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Booking b = data.getValue(Booking.class);
                            if (b != null) {
                                b.setBookingId(data.getKey());
                                list.add(b);
                            }
                        }
                        // Sắp xếp mới nhất lên đầu
                        list.sort((b1, b2) -> Long.compare(b2.getCreated_at(), b1.getCreated_at()));
                        mBookingList.setValue(list);
                        mIsLoading.setValue(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        mIsLoading.setValue(false);
                    }
                });
    }

    public void deleteBooking(String bookingId) {
        if (bookingId == null) return;
        mDatabase.child("Bookings").child(bookingId).removeValue()
                .addOnSuccessListener(aVoid -> mStatusMessage.setValue("Đã xóa đơn hàng"))
                .addOnFailureListener(e -> mStatusMessage.setValue("Lỗi khi xóa: " + e.getMessage()));
    }

    public void cancelBooking(String bookingId) {
        if (bookingId == null) return;
        mDatabase.child("Bookings").child(bookingId).child("status").setValue(0)
                .addOnSuccessListener(aVoid -> mStatusMessage.setValue("Hủy lịch thành công"))
                .addOnFailureListener(e -> mStatusMessage.setValue("Lỗi khi hủy lịch: " + e.getMessage()));
    }
}
