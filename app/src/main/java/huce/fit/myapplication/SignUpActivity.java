package huce.fit.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText edUsername, edEmail, edPassword, edRepassword, edPhone;
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

        // Ánh xạ ID từ signup_activity.xml
        edPhone = findViewById(R.id.edPhone);
        edEmail = findViewById(R.id.edEmail);
        edUsername = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edPassword);
        edRepassword = findViewById(R.id.edRepassword);
        btnSignupAction = findViewById(R.id.btnSignupAction);
        btnLoginRedirect = findViewById(R.id.btnLoginRedirect);

        if (btnSignupAction != null) {
            btnSignupAction.setOnClickListener(v -> {
                String phone = edPhone.getText().toString().trim();
                String email = edEmail.getText().toString().trim();
                String fullName = edUsername.getText().toString().trim();
                String password = edPassword.getText().toString().trim();
                String rePassword = edRepassword.getText().toString().trim();

                if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "Vui lòng nhập đủ các trường (*)", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(rePassword)) {
                    edRepassword.setError("Mật khẩu không khớp!");
                    return;
                }

                btnSignupAction.setEnabled(false);
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            
                            // Lưu dữ liệu khớp 100% với cấu trúc JSON bạn cung cấp
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("full_name", fullName);
                            userMap.put("phone", phone);
                            userMap.put("email", email);
                            userMap.put("role", 1); // 1: Khách hàng, 0: Chủ sân (như mẫu JSON)
                            userMap.put("status", 1); // 1: Hoạt động, 0: Bị khóa
                            userMap.put("created_at", System.currentTimeMillis());

                            mDatabase.child("Accounts").child(userId).setValue(userMap)
                                .addOnSuccessListener(aVoid -> showSuccessDialog())
                                .addOnFailureListener(e -> {
                                    btnSignupAction.setEnabled(true);
                                    Toast.makeText(this, "Lỗi lưu Database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                        } else {
                            btnSignupAction.setEnabled(true);
                            Toast.makeText(this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            });
        }

        if (btnLoginRedirect != null) btnLoginRedirect.setOnClickListener(v -> finish());
    }

    private void showSuccessDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_success, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create();
        dialogView.findViewById(R.id.btnOk).setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
        dialog.show();
    }
}
