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
import androidx.lifecycle.ViewModelProvider;

import huce.fit.myapplication.viewmodel.SignUpViewModel;

public class SignUpActivity extends AppCompatActivity {
    private EditText edUsername, edEmail, edPassword, edRepassword, edPhone;
    private Button btnSignupAction;
    private TextView btnLoginRedirect;
    private ImageView btnBack;
    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        initViews();
        setupViewModel();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edPhone = findViewById(R.id.edPhone);
        edEmail = findViewById(R.id.edEmail);
        edUsername = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edPassword);
        edRepassword = findViewById(R.id.edRepassword);
        btnSignupAction = findViewById(R.id.btnSignupAction);
        btnLoginRedirect = findViewById(R.id.btnLoginRedirect);
    }

    private void setupViewModel() {
        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        // Lắng nghe sự kiện đăng ký thành công
        signUpViewModel.getSignUpSuccess().observe(this, success -> {
            if (success) {
                showSuccessDialog();
            }
        });

        // Lắng nghe thông báo lỗi
        signUpViewModel.getSignUpError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                btnSignupAction.setEnabled(true);
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnSignupAction != null) {
            btnSignupAction.setOnClickListener(v -> {
                String phone = edPhone.getText().toString().trim();
                String email = edEmail.getText().toString().trim();
                String fullName = edUsername.getText().toString().trim();
                String password = edPassword.getText().toString().trim();
                String rePassword = edRepassword.getText().toString().trim();

                if (validateInput(fullName, email, password, rePassword)) {
                    btnSignupAction.setEnabled(false);
                    signUpViewModel.signUp(email, password, fullName, phone);
                }
            });
        }

        if (btnLoginRedirect != null) {
            btnLoginRedirect.setOnClickListener(v -> {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        }
    }

    private boolean validateInput(String name, String email, String pass, String rePass) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Vui lòng nhập đủ các trường có dấu (*)", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!pass.equals(rePass)) {
            edRepassword.setError("Mật khẩu không khớp!");
            return false;
        }
        if (pass.length() < 6) {
            edPassword.setError("Mật khẩu tối thiểu 6 ký tự");
            return false;
        }
        return true;
    }

    private void showSuccessDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_success, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create();
        View btnOk = dialogView.findViewById(R.id.btnOk);
        if (btnOk != null) {
            btnOk.setOnClickListener(v -> {
                dialog.dismiss();
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            });
        }
        dialog.show();
    }
}
