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
import java.util.concurrent.Executors;
import huce.fit.myapplication.database.AppDatabase;
import huce.fit.myapplication.objects.CustomerAccount;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private TextView btnSignup; // Sửa từ Button thành TextView cho khớp với XML
    private EditText edUsername, edPassword;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_constraint);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        try {
            // Ánh xạ an toàn
            btnLogin = findViewById(R.id.btnLogin);
            btnSignup = findViewById(R.id.btnSignup); // Hệ thống sẽ tìm thấy TextView này
            edUsername = findViewById(R.id.edUsername);
            edPassword = findViewById(R.id.edPassword);

            db = AppDatabase.getInstance(this);

            if (btnLogin != null) {
                btnLogin.setOnClickListener(view -> {
                    if (edUsername == null || edPassword == null) return;
                    
                    String username = edUsername.getText().toString().trim();
                    String password = edPassword.getText().toString().trim();

                    if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                        Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Kiểm tra database trên luồng ngầm
                    Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            CustomerAccount account = db.customerAccountDao().login(username, password);
                            runOnUiThread(() -> {
                                if (account != null) {
                                    SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                                    pref.edit().putString("username", username).putBoolean("isLoggedIn", true).apply();
                                    
                                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, HomeActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(this, "Tài khoản hoặc mật khẩu sai", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                });
            }

            if (btnSignup != null) {
                btnSignup.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
            }

            // Nút back (mũi tên quay lại)
            View btnBack = findViewById(R.id.btnBackLogin);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
