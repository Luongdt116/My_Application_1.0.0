package huce.fit.myapplication.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginViewModel extends ViewModel {

    public static class UserState {
        public final String uid;
        public final String name;
        public final String email;
        public final String phone;
        public final int role;

        public UserState(String uid, String name, String email, String phone, int role) {
            this.uid = uid;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.role = role;
        }
    }

    private final MutableLiveData<UserState> mLoginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> mLoginError = new MutableLiveData<>();
    private final FirebaseAuth mAuth;
    private final DatabaseReference mDatabase;
    private final String dbUrl = "https://app-moblie-131d8-default-rtdb.firebaseio.com/";

    public LoginViewModel() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference();
    }

    public LiveData<UserState> getLoginSuccess() { return mLoginSuccess; }
    public LiveData<String> getLoginError() { return mLoginError; }

    public void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        checkUserInDatabase(mAuth.getCurrentUser());
                    } else {
                        mLoginError.setValue("Email hoặc mật khẩu không chính xác");
                    }
                });
    }

    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        checkUserInDatabase(mAuth.getCurrentUser());
                    } else {
                        mLoginError.setValue("Xác thực Google thất bại");
                    }
                });
    }

    private void checkUserInDatabase(FirebaseUser user) {
        if (user == null) return;
        String userId = user.getUid();

        mDatabase.child("Accounts").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Nếu đăng nhập Google lần đầu -> Tạo node mới
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("full_name", user.getDisplayName());
                    userMap.put("email", user.getEmail());
                    userMap.put("phone", "");
                    userMap.put("role", 1); 
                    userMap.put("status", 1); 
                    userMap.put("created_at", System.currentTimeMillis());

                    mDatabase.child("Accounts").child(userId).setValue(userMap)
                            .addOnSuccessListener(aVoid -> mLoginSuccess.setValue(new UserState(userId, user.getDisplayName(), user.getEmail(), "", 1)));
                } else {
                    // Kiểm tra trạng thái tài khoản theo JSON: status: 1 (Active)
                    Long status = snapshot.child("status").getValue(Long.class);
                    if (status != null && status == 0) {
                        mAuth.signOut();
                        mLoginError.setValue("Tài khoản của bạn đã bị khóa!");
                        return;
                    }

                    String fullName = snapshot.child("full_name").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    Long role = snapshot.child("role").getValue(Long.class);
                    
                    mLoginSuccess.setValue(new UserState(userId, fullName, user.getEmail(), phone != null ? phone : "", role != null ? role.intValue() : 1));
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) { mLoginError.setValue(error.getMessage()); }
        });
    }
}
