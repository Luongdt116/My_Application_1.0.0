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

import java.util.HashMap;
import java.util.Map;

public class EditProfileViewModel extends ViewModel {

    private final MutableLiveData<Map<String, String>> mUserData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mUpdateSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> mError = new MutableLiveData<>();
    
    private final DatabaseReference mDatabase;
    private final String dbUrl = "https://app-moblie-131d8-default-rtdb.firebaseio.com/";

    public EditProfileViewModel() {
        mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference();
    }

    public LiveData<Map<String, String>> getUserData() {
        return mUserData;
    }

    public LiveData<Boolean> getUpdateSuccess() {
        return mUpdateSuccess;
    }

    public LiveData<String> getError() {
        return mError;
    }

    public void loadUserData(String userId) {
        if (userId == null || userId.isEmpty()) return;

        mDatabase.child("Accounts").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, String> data = new HashMap<>();
                    data.put("full_name", snapshot.child("full_name").getValue(String.class));
                    data.put("phone", snapshot.child("phone").getValue(String.class));
                    data.put("email", snapshot.child("email").getValue(String.class));
                    data.put("dob", snapshot.child("dob").getValue(String.class));
                    data.put("gender", snapshot.child("gender").getValue(String.class));
                    mUserData.setValue(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mError.setValue(error.getMessage());
            }
        });
    }

    public void updateProfile(String userId, String name, String phone, String dob, String gender) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("full_name", name);
        updates.put("phone", phone);
        updates.put("dob", dob);
        updates.put("gender", gender);

        mDatabase.child("Accounts").child(userId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> mUpdateSuccess.setValue(true))
                .addOnFailureListener(e -> mError.setValue(e.getMessage()));
    }
}
