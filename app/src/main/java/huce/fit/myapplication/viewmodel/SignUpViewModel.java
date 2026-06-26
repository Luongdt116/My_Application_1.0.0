package huce.fit.myapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpViewModel extends ViewModel {

    private final MutableLiveData<Boolean> mSignUpSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> mSignUpError = new MutableLiveData<>();
    private final FirebaseAuth mAuth;
    private final DatabaseReference mDatabase;
    private final String dbUrl = "https://app-moblie-131d8-default-rtdb.firebaseio.com/";

    public SignUpViewModel() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference();
    }

    public LiveData<Boolean> getSignUpSuccess() {
        return mSignUpSuccess;
    }

    public LiveData<String> getSignUpError() {
        return mSignUpError;
    }

    public void signUp(String email, String password, String fullName, String phone) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        saveUserToDatabase(userId, fullName, email, phone);
                    } else {
                        mSignUpError.setValue(task.getException() != null ? task.getException().getMessage() : "Đăng ký thất bại");
                    }
                });
    }

    private void saveUserToDatabase(String userId, String fullName, String email, String phone) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("full_name", fullName);
        userMap.put("phone", phone);
        userMap.put("email", email);
        userMap.put("role", 1); // 1: Khách hàng
        userMap.put("status", 1); // 1: Hoạt động
        userMap.put("created_at", System.currentTimeMillis());

        mDatabase.child("Accounts").child(userId).setValue(userMap)
                .addOnSuccessListener(aVoid -> mSignUpSuccess.setValue(true))
                .addOnFailureListener(e -> mSignUpError.setValue("Lỗi lưu Database: " + e.getMessage()));
    }
}
