package huce.fit.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView btnBack = findViewById(R.id.btnBackChangePassword);
        Button btnCancel = findViewById(R.id.btnCancelChangePassword);
        Button btnSave = findViewById(R.id.btnSaveNewPassword);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        if (btnCancel != null) btnCancel.setOnClickListener(v -> finish());
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                // Logic đổi mật khẩu sau này
                finish();
            });
        }
    }
}
