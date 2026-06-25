package huce.fit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
    private TextView btnSignup, tvForgotPassword;
    private EditText edEmail, edPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String dbUrl = "https://app-moblie-131d8-default-rtdb.firebaseio.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_constraint);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference();

        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        edEmail = findViewById(R.id.edUsername); 
        edPassword = findViewById(R.id.edPassword);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(view -> {
                String email = edEmail.getText().toString().trim();
                String password = edPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            
                            // Lấy thông tin từ nút Accounts/{userId} theo đúng JSON bạn cung cấp
                            mDatabase.child("Accounts").child(userId).get().addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful() && dbTask.getResult().exists()) {
                                    DataSnapshot snapshot = dbTask.getResult();
                                    
                                    // 1. Kiểm tra trạng thái status (1: Active, 0: Blocked)
                                    Long status = snapshot.child("status").getValue(Long.class);
                                    if (status != null && status == 0) {
                                        mAuth.signOut();
                                        Toast.makeText(this, "Tài khoản của bạn đã bị khóa!", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    String fullName = snapshot.child("full_name").getValue(String.class);
                                    Long role = snapshot.child("role").getValue(Long.class);
                                    
                                    // 2. Lưu vào SharedPreferences
                                    SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                                    pref.edit()
                                        .putString("userId", userId)
                                        .putString("username", fullName)
                                        .putString("email", email)
                                        .putInt("role", role != null ? role.intValue() : 1)
                                        .putBoolean("isLoggedIn", true)
                                        .apply();
                                    
                                    Toast.makeText(this, "Chào mừng " + fullName, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(this, "Dữ liệu tài khoản không tồn tại trên hệ thống!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            });
        }

        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            });
        }

        if (btnSignup != null) {
            btnSignup.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        }
    }
}
