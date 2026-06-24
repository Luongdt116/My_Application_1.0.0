package huce.fit.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView btnBack = findViewById(R.id.btnBackEditProfile);
        Button btnCancel = findViewById(R.id.btnCancelEdit);
        Button btnSave = findViewById(R.id.btnSaveEdit);

        // Nút Back trên toolbar
        btnBack.setOnClickListener(v -> finish());

        // Nút Hủy dưới footer
        btnCancel.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            finish();
    }
}