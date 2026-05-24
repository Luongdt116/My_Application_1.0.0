package huce.fit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
        TextView tvForgotPassword =
                findViewById(R.id.tvForgotPassword);
        edEmail = findViewById(R.id.edUsername); // Lưu ý: Trong layout của bạn ID có thể vẫn là edUsername nhưng ta dùng để nhập Email
        edPassword = findViewById(R.id.edPassword);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(view -> {
                String email = edEmail.getText().toString().trim();
                String password = edPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Đăng nhập bằng Firebase Auth
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            
                            // Lấy thêm thông tin username từ Database
                            mDatabase.child("Users").child(userId).get().addOnCompleteListener(dbTask -> {
                                String username = email; // Mặc định dùng email nếu không lấy được username
                                if (dbTask.isSuccessful() && dbTask.getResult().exists()) {
                                    username = dbTask.getResult().child("username").getValue(String.class);
                                }
                                
                                SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                                pref.edit().putString("username", username).putBoolean("isLoggedIn", true).apply();
                                
                                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            });
                        } else {
                            Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                        }
                    });
            });
        }

        if (btnSignup != null) {
            btnSignup.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        }
        tvForgotPassword.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    ForgotPasswordActivity.class
            );

            startActivity(intent);
        });
    }
}
