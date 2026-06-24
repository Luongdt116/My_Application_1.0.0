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

        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Ánh xạ UI
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        edEmail = findViewById(R.id.edUsername); // Thường email dùng ID này trong layout của bạn
        edPassword = findViewById(R.id.edPassword);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(view -> {
                String email = edEmail.getText().toString().trim();
                String password = edPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Đăng nhập bằng Firebase Auth
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            
                            // Lấy thông tin full_name từ Realtime Database sau khi Auth thành công
                            mDatabase.child("Accounts").child(userId).get().addOnCompleteListener(dbTask -> {
                                String displayName = email; 
                                if (dbTask.isSuccessful() && dbTask.getResult().exists()) {
                                    displayName = dbTask.getResult().child("full_name").getValue(String.class);
                                }
                                
                                // Lưu trạng thái vào SharedPreferences
                                SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                                pref.edit()
                                    .putString("username", displayName)
                                    .putBoolean("isLoggedIn", true)
                                    .apply();
                                
                                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            });
                        } else {
                            Toast.makeText(this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            });
        }

        if (btnSignup != null) {
            btnSignup.setOnClickListener(v -> {
                startActivity(new Intent(this, SignUpActivity.class));
            });
        }
    }
}
