package huce.fit.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    // 1. Khai báo các biến UI
    private EditText edUsername, edEmail, edPassword, edRepassword;
    private Button btnSignupAction;
    private TextView btnLoginRedirect;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        // 2. Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // 3. Ánh xạ ID từ XML
        edUsername = findViewById(R.id.edUsername); // Sử dụng làm Họ tên (Full Name)
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        edRepassword = findViewById(R.id.edRepassword);
        btnSignupAction = findViewById(R.id.btnSignupAction);
        btnLoginRedirect = findViewById(R.id.btnLoginRedirect);

        // 4. Xử lý sự kiện Click
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
                edPassword.setError("Mật khẩu phải từ 6 ký tự trở lên!");
                return;
            }

            // Thực hiện đăng ký bằng Firebase Auth
            btnSignupAction.setEnabled(false); // Tránh bấm nhiều lần
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Lấy UID vừa tạo
                        String userId = mAuth.getCurrentUser().getUid();

                        // Tạo Object thông tin người dùng để lưu vào Realtime Database
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("full_name", fullName);
                        userMap.put("email", email);
                        userMap.put("role", 1); // 1: Người dùng (theo cấu trúc chốt gần nhất)
                        userMap.put("status", 1); // 1: Active
                        userMap.put("created_at", System.currentTimeMillis());

                        // Lưu vào node Accounts/{userId}
                        mDatabase.child("Accounts").child(userId).setValue(userMap)
                            .addOnSuccessListener(aVoid -> {
                                showSuccessDialog();
                            })
                            .addOnFailureListener(e -> {
                                btnSignupAction.setEnabled(true);
                                Toast.makeText(this, "Lỗi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                    } else {
                        btnSignupAction.setEnabled(true);
                        String error = task.getException() != null ? task.getException().getMessage() : "Đăng ký thất bại";
                        Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
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

        if (imgTick != null) imgTick.setImageResource(R.drawable.logo);
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
