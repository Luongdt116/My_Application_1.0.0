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

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<Map<String, String>> mUserData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsLoggedOut = new MutableLiveData<>();
    private DatabaseReference mDatabase;
    private final String dbUrl = "https://app-moblie-131d8-default-rtdb.firebaseio.com/";

    public ProfileViewModel() {
        mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference();
    }

    public LiveData<Map<String, String>> getUserData() {
        return mUserData;
    }

    public LiveData<Boolean> getIsLoggedOut() {
        return mIsLoggedOut;
    }

    public void fetchUserInfo(String uid) {
        if (uid == null || uid.isEmpty()) return;

        mDatabase.child("Accounts").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, String> data = new HashMap<>();
                    data.put("name", snapshot.child("full_name").getValue(String.class));
                    data.put("email", snapshot.child("email").getValue(String.class));
                    mUserData.setValue(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void logout() {
        mIsLoggedOut.setValue(true);
    }
}
