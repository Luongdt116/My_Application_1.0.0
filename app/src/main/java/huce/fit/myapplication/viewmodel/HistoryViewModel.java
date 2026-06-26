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
    private final DatabaseReference mDatabase;
    private final String dbUrl = "https://app-moblie-131d8-default-rtdb.firebaseio.com/";

    public HistoryViewModel() {
        mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference();
    }

    public LiveData<List<Booking>> getBookingList() { return mBookingList; }
    public LiveData<Boolean> getIsLoading() { return mIsLoading; }

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
                            if (b != null) list.add(b);
                        }
                        mBookingList.setValue(list);
                        mIsLoading.setValue(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        mIsLoading.setValue(false);
                    }
                });
    }
}
