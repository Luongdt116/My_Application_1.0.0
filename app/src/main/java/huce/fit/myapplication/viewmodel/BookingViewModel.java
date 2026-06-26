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
import huce.fit.myapplication.objects.Court;

public class BookingViewModel extends ViewModel {
    private final MutableLiveData<List<Court>> mCourtList = new MutableLiveData<>();
    private final MutableLiveData<List<Booking>> mDayBookings = new MutableLiveData<>();
    private final MutableLiveData<String> mErrorMessage = new MutableLiveData<>();
    
    private final DatabaseReference mDatabase;
    private final String dbUrl = "https://app-moblie-131d8-default-rtdb.firebaseio.com/";

    public BookingViewModel() {
        mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference();
    }

    public LiveData<List<Court>> getCourtList() { return mCourtList; }
    public LiveData<List<Booking>> getDayBookings() { return mDayBookings; }
    public LiveData<String> getErrorMessage() { return mErrorMessage; }

    // Tải danh sách sân (courts) theo cấu trúc: Venues/{venueId}/courts
    public void loadCourts(String venueId) {
        if (venueId == null) return;
        
        mDatabase.child("Venues").child(venueId).child("courts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Court> list = new ArrayList<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Court court = data.getValue(Court.class);
                                if (court != null) list.add(court);
                            }
                            mCourtList.setValue(list);
                        } else {
                            mErrorMessage.setValue("Không tìm thấy danh sách sân con!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        mErrorMessage.setValue("Lỗi Firebase: " + error.getMessage());
                    }
                });
    }

    // Tải lịch đã đặt (Bookings) để khóa các ô đã có người tập
    public void loadBookings(String venueId, String date) {
        if (venueId == null || date == null) return;
        
        // Chuyển định dạng ngày từ dd/MM/yyyy sang yyyy-MM-dd để khớp JSON
        String[] parts = date.split("/");
        String queryDate = parts[2] + "-" + parts[1] + "-" + parts[0];

        mDatabase.child("Bookings")
                .orderByChild("venue_id").equalTo(venueId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Booking> list = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Booking b = data.getValue(Booking.class);
                            if (b != null && queryDate.equals(b.getBooking_date())) {
                                list.add(b);
                            }
                        }
                        mDayBookings.setValue(list);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) { }
                });
    }
}
