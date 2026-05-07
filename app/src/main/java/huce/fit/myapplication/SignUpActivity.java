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

import huce.fit.myapplication.database.UserDatabaseHelper;

public class SignUpActivity extends AppCompatActivity {
    // 1. Khai báo các biến UI
    private EditText edUsername, edEmail, edPassword, edRepassword;
    private Button btnSignupAction;
    private TextView btnLoginRedirect;

    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        // 2. Khởi tạo Database
        dbHelper = new UserDatabaseHelper(this);

        // 3. Ánh xạ ID từ XML (Hãy chắc chắn file signup_activity.xml đã có id: edEmail)
        edUsername = findViewById(R.id.edUsername);
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

            // Kiểm tra trùng username
            if (dbHelper.checkUsername(fullName)) {
                edUsername.setError("Tên đăng nhập đã tồn tại!");
                return;
            }

            // Kiểm tra trùng email
            if (dbHelper.checkEmail(email)) {
                edEmail.setError("Email đã được sử dụng!");
                return;
            }

            // Lưu người dùng vào database
            boolean success = dbHelper.insertUser(fullName, email, password);
            if (success) {
                showSuccessDialog();
            } else {
                Toast.makeText(this, "Đăng ký thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
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