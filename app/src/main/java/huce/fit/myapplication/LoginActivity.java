package huce.fit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private TextView btnSignup;
    private EditText edEmail, edPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_constraint);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        edEmail = findViewById(R.id.edUsername); 
        edPassword = findViewById(R.id.edPassword);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(view -> {
                String email = edEmail.getText().toString().trim();
                String password = edPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            
                            // Truy cập node Accounts để lấy role và status
                            mDatabase.child("Accounts").child(userId).get().addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful() && dbTask.getResult().exists()) {
                                    DataSnapshot snapshot = dbTask.getResult();
                                    
                                    // Kiểm tra trạng thái hoạt động
                                    Integer status = snapshot.child("status").getValue(Integer.class);
                                    if (status != null && status == 0) {
                                        mAuth.signOut();
                                        Toast.makeText(this, "Tài khoản của bạn đã bị khóa!", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    String fullName = snapshot.child("full_name").getValue(String.class);
                                    Integer role = snapshot.child("role").getValue(Integer.class);
                                    
                                    // Lưu vào SharedPreferences
                                    SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                                    pref.edit()
                                        .putString("userId", userId)
                                        .putString("username", fullName)
                                        .putInt("role", role != null ? role : 1)
                                        .putBoolean("isLoggedIn", true)
                                        .apply();
                                    
                                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(this, "Không tìm thấy thông tin tài khoản!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(this, "Sai email hoặc mật khẩu!", Toast.LENGTH_LONG).show();
                        }
                    });
            });
        }

        if (btnSignup != null) {
            btnSignup.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        }
    }
}
