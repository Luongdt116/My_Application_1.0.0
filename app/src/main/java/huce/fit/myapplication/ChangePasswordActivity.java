package huce.fit.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();

        // 1. Ánh xạ các View từ XML
        ImageView btnBack = findViewById(R.id.btnBackChangePassword);
        Button btnCancel = findViewById(R.id.btnCancelChangePassword);
        Button btnSave = findViewById(R.id.btnSaveNewPassword);

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);

        // Nút quay lại và nút Hủy
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        if (btnCancel != null) btnCancel.setOnClickListener(v -> finish());

        // 2. Xử lý logic khi bấm nút Lưu mật khẩu mới
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                String currentPass = etCurrentPassword.getText().toString().trim();
                String newPass = etNewPassword.getText().toString().trim();
                String confirmPass = etConfirmNewPassword.getText().toString().trim();

                // Kiểm tra dữ liệu đầu vào
                if (TextUtils.isEmpty(currentPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc (*)", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPass.equals(confirmPass)) {
                    Toast.makeText(this, "Mật khẩu mới nhập lại không khớp!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPass.length() < 6) {
                    Toast.makeText(this, "Mật khẩu mới phải từ 6 ký tự trở lên!", Toast.LENGTH_SHORT).show();
                    return;
                }

                performChangePassword(currentPass, newPass);
            });
        }
    }

    private void performChangePassword(String currentPass, String newPass) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            // Xác thực lại người dùng trước khi cập nhật mật khẩu để bảo mật
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPass);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Xác thực lại thành công, tiến hành cập nhật mật khẩu mới
                    user.updatePassword(newPass).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this, "Thay đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Lỗi: " + updateTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // Xác thực lại thất bại (sai mật khẩu hiện tại)
                    Toast.makeText(ChangePasswordActivity.this, "Mật khẩu hiện tại không chính xác!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập lại để thực hiện chức năng này", Toast.LENGTH_SHORT).show();
        }
    }
}
