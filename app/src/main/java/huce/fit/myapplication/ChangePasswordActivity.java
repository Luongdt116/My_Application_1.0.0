package huce.fit.myapplication;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;

import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText etCurrentPassword,
            etNewPassword,
            etConfirmNewPassword;

    private Button btnSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();

        ImageView btnBack =
                findViewById(R.id.btnBackChangePassword);

        Button btnCancel =
                findViewById(R.id.btnCancelChangePassword);

        btnSave =
                findViewById(R.id.btnSaveNewPassword);

        etCurrentPassword =
                findViewById(R.id.etCurrentPassword);

        etNewPassword =
                findViewById(R.id.etNewPassword);

        etConfirmNewPassword =
                findViewById(R.id.etConfirmNewPassword);

        // Ban đầu disable nút lưu
        btnSave.setEnabled(false);

        TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {

                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        etCurrentPassword.addTextChangedListener(textWatcher);
        etNewPassword.addTextChangedListener(textWatcher);
        etConfirmNewPassword.addTextChangedListener(textWatcher);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        }

        if (btnSave != null) {

            btnSave.setOnClickListener(v -> {

                String currentPassword =
                        etCurrentPassword.getText()
                                .toString()
                                .trim();

                String newPassword =
                        etNewPassword.getText()
                                .toString()
                                .trim();

                String confirmPassword =
                        etConfirmNewPassword.getText()
                                .toString()
                                .trim();

                if (TextUtils.isEmpty(currentPassword)
                        || TextUtils.isEmpty(newPassword)
                        || TextUtils.isEmpty(confirmPassword)) {

                    Toast.makeText(
                            this,
                            "Vui lòng nhập đầy đủ thông tin",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                if (!newPassword.equals(confirmPassword)) {

                    etConfirmNewPassword.setError(
                            "Mật khẩu xác nhận không khớp"
                    );

                    return;
                }

                if (newPassword.length() < 6) {

                    etNewPassword.setError(
                            "Mật khẩu tối thiểu 6 ký tự"
                    );

                    return;
                }

                FirebaseUser user = mAuth.getCurrentUser();

                if (user == null) {

                    Toast.makeText(
                            this,
                            "Người dùng chưa đăng nhập",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                String email = user.getEmail();

                AuthCredential credential =
                        EmailAuthProvider.getCredential(
                                email,
                                currentPassword
                        );

                // Xác thực lại mật khẩu cũ
                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {

                                // Đổi mật khẩu mới
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(updateTask -> {

                                            if (updateTask.isSuccessful()) {

                                                Toast.makeText(
                                                        this,
                                                        "Đổi mật khẩu thành công",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                finish();

                                            } else {

                                                Toast.makeText(
                                                        this,
                                                        "Đổi mật khẩu thất bại",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            }
                                        });

                            } else {

                                Toast.makeText(
                                        this,
                                        "Mật khẩu hiện tại không đúng",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
            });
        }
    }

    private void checkInputs() {

        String current =
                etCurrentPassword.getText()
                        .toString()
                        .trim();

        String newPass =
                etNewPassword.getText()
                        .toString()
                        .trim();

        String confirm =
                etConfirmNewPassword.getText()
                        .toString()
                        .trim();

        if (!current.isEmpty()
                && !newPass.isEmpty()
                && !confirm.isEmpty()) {

            btnSave.setEnabled(true);

            btnSave.setBackgroundTintList(
                    ColorStateList.valueOf(
                            Color.parseColor("#09A459")
                    )
            );

        } else {

            btnSave.setEnabled(false);

            btnSave.setBackgroundTintList(
                    ColorStateList.valueOf(
                            Color.parseColor("#DDDDDD")
                    )
            );
        }
    }
}