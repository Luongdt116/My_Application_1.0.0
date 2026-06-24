package huce.fit.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import huce.fit.myapplication.database.AppDatabase;
import huce.fit.myapplication.database.AdminAccountDao; // Đảm bảo đúng package database của bạn
import huce.fit.myapplication.objects.AdminAccount;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private AppDatabase db;
    private int currentUserId = 1; // ID của tài khoản đang đăng nhập (bạn có thể nhận qua Intent hoặc SharedPreferences sau này)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 1. Ánh xạ các View từ XML giao diện của bạn
        ImageView btnBack = findViewById(R.id.btnBackChangePassword);
        Button btnCancel = findViewById(R.id.btnCancelChangePassword);
        Button btnSave = findViewById(R.id.btnSaveNewPassword);

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);

        // Khởi tạo cơ sở dữ liệu
        db = AppDatabase.getInstance(this);

        // Nút quay lại và nút Hủy
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        if (btnCancel != null) btnCancel.setOnClickListener(v -> finish());

        // 2. Xử lý logic khi bấm nút Lưu mật khẩu mới
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                String currentPass = etCurrentPassword.getText().toString().trim();
                String newPass = etNewPassword.getText().toString().trim();
                String confirmPass = etConfirmNewPassword.getText().toString().trim();

                // Kiểm tra xem người dùng có bỏ trống ô nào không
                if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "Vui lòng nhập đầy đủ thông tin bắt buộc (*)", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lấy thông tin tài khoản hiện tại từ database lên để đối chiếu mật khẩu cũ
                AdminAccount account = db.adminAccountDao().getAccountById(currentUserId);

                if (account != null) {
                    // Kiểm tra xem mật khẩu hiện tại nhập vào có khớp với mật khẩu trong DB không
                    if (!account.getPassword().equals(currentPass)) {
                        Toast.makeText(ChangePasswordActivity.this, "Mật khẩu hiện tại không chính xác!", Toast.LENGTH_SHORT).show();
                    }
                    // Kiểm tra mật khẩu mới và mật khẩu nhập lại xem có trùng nhau không
                    else if (!newPass.equals(confirmPass)) {
                        Toast.makeText(ChangePasswordActivity.this, "Mật khẩu mới nhập lại không khớp!", Toast.LENGTH_SHORT).show();
                    }
                    // Kiểm tra nếu mật khẩu mới trùng lặp với mật khẩu cũ
                    else if (newPass.equals(currentPass)) {
                        Toast.makeText(ChangePasswordActivity.this, "Mật khẩu mới không được trùng với mật khẩu hiện tại!", Toast.LENGTH_SHORT).show();
                    }
                    // Nếu mọi điều kiện đều thỏa mãn -> Tiến hành cập nhật dữ liệu
                    else {
                        db.adminAccountDao().updatePassword(currentUserId, newPass);
                        Toast.makeText(ChangePasswordActivity.this, "Thay đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                        finish(); // Đóng màn hình đổi mật khẩu và quay lại trang trước
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Không tìm thấy thông tin tài khoản!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}