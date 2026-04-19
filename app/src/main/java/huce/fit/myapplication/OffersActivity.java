package huce.fit.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class OffersActivity extends AppCompatActivity {

    private MaterialButton btnAll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // --- ÁNH XẠ NÚT TẤT CẢ (Sử dụng đúng kiểu MaterialButton) ---
        btnAll = findViewById(R.id.btnOffersAll);
        if (btnAll != null) {
            btnAll.setOnClickListener(v -> {
                Intent intent = new Intent(this, DiscoveryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            });
        }

        setupFooterNavigation();
    }

    private void setupFooterNavigation() {
        View tabHome = findViewById(R.id.layoutHomeTab);
        View tabProfile = findViewById(R.id.layoutProfileTab);
        View tabDiscovery = findViewById(R.id.layoutDiscoveryTab);

        if (tabHome != null) {
            tabHome.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));
        }
        if (tabProfile != null) {
            tabProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));
        }
        if (tabDiscovery != null) {
            tabDiscovery.setOnClickListener(v -> startActivity(new Intent(this, DiscoveryActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));
        }
    }
}
