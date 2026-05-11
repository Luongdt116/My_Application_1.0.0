package huce.fit.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private EditText edUsername, edEmail, edPassword, edRepassword;
    private Button btnSignupAction;
    private TextView btnLoginRedirect;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        edUsername = findViewById(R.id.edUsername);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        edRepassword = findViewById(R.id.edRepassword);
        btnSignupAction = findViewById(R.id.btnSignupAction);
        btnLoginRedirect = findViewById(R.id.btnLoginRedirect);

        btnSignupAction.setOnClickListener(v -> {
            String fullName = edUsername.getText().toString().trim();
            String email = edEmail.getText().toString().trim();
            String password = edPassword.getText().toString().trim();
            String rePassword = edRepassword.getText().toString().trim();

            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(rePassword)) {
                edRepassword.setError("Mật khẩu không khớp!");
                return;
            }

            if (password.length() < 6) {
                edPassword.setError("Mật khẩu phải ít nhất 6 ký tự");
                return;
            }

            // Đăng ký tài khoản trên Firebase Auth
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Lưu thông tin người dùng vào Realtime Database
                        String userId = mAuth.getCurrentUser().getUid();
                        Map<String, Object> user = new HashMap<>();
                        user.put("username", fullName);
                        user.put("email", email);
                        
                        mDatabase.child("Users").child(userId).setValue(user)
                            .addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful()) {
                                    showSuccessDialog();
                                }
                            });
                    } else {
                        Toast.makeText(this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        });

        btnLoginRedirect.setOnClickListener(v -> finish());
    }

    private void showSuccessDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_success, null);
        ImageView imgTick = dialogView.findViewById(R.id.imgTick);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        Button btnOk = dialogView.findViewById(R.id.btnOk);

        imgTick.setImageResource(R.drawable.logo);
        tvMessage.setText("Đã đăng ký thành công, mời bạn đăng nhập");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }
}
