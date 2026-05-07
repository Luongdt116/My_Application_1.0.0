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
    private TextView btnSignup; // Đúng kiểu TextView từ XML
    private EditText edUsername, edPassword;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_constraint);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        edUsername = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edPassword);
        db = AppDatabase.getInstance(this);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(view -> {
                String username = edUsername.getText().toString().trim();
                String password = edPassword.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                Executors.newSingleThreadExecutor().execute(() -> {
                    CustomerAccount account = db.customerAccountDao().login(username, password);
                    runOnUiThread(() -> {
                        if (account != null) {
                            SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                            pref.edit().putString("username", username).putBoolean("isLoggedIn", true).apply();
                            
                            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            // CHUYỂN VỀ MAINACTIVITY (Vì Home hiện tại là Fragment trong Main)
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Tài khoản hoặc mật khẩu sai", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            });
        }

        if (btnSignup != null) {
            btnSignup.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        }
    }
}
