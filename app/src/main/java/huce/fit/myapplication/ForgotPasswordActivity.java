package huce.fit.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText edEmail;
    Button btnReset;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edEmail = findViewById(R.id.edEmail);
        btnReset = findViewById(R.id.btnReset);

        mAuth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(v -> {

            String email =
                    edEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {

                Toast.makeText(
                        this,
                        "Nhập email",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            Toast.makeText(
                                    this,
                                    "Đã gửi email khôi phục",
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Email không tồn tại",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        });
    }
}