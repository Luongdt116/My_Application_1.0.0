package huce.fit.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView btnBack = findViewById(R.id.btnBackLicense);
        Button btnAgree = findViewById(R.id.btnAgree);

        // Xử lý nút Back: Quay lại trang trước đó (thường là Profile)
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Đồng ý: Quay lại trang trước đó
        btnAgree.setOnClickListener(v -> finish());
    }
}
