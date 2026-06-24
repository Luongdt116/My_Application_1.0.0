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

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_constraint);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        edPassword = findViewById(R.id.edPassword);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(view -> {
                String password = edPassword.getText().toString().trim();

                    Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                                SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                                pref.edit().putString("username", username).putBoolean("isLoggedIn", true).apply();
                                
                                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                        } else {
                        }
                    });
            });
        }

        if (btnSignup != null) {
            btnSignup.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        }
    }
}
