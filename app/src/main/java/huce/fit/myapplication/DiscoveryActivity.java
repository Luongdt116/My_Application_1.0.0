package huce.fit.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class DiscoveryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discovery);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setupNavigation();
        setupFooterNavigation();
    }

    private void setupNavigation() {
        // Xử lý nút "Ưu đãi" để sang trang Offers
        MaterialButton btnOffer = findViewById(R.id.btnDiscoveryOffer);
        if (btnOffer != null) {
            btnOffer.setOnClickListener(v -> {
                Intent intent = new Intent(this, OffersActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            });
        }

        // Các nút "Xem chi tiết" (giữ nguyên logic bạn đã thiết lập)
        View btnDetail1 = findViewById(R.id.btnDiscoveryViewDetail1);
        View btnDetail2 = findViewById(R.id.btnDiscoveryViewDetail2);
        if (btnDetail1 != null) btnDetail1.setOnClickListener(v -> startActivity(new Intent(this, BookingActivity.class)));
        if (btnDetail2 != null) btnDetail2.setOnClickListener(v -> startActivity(new Intent(this, BookingActivity.class)));
    }

    private void setupFooterNavigation() {
        View tabHome = findViewById(R.id.layoutHomeTab);
        View tabProfile = findViewById(R.id.layoutProfileTab);
        
        TextView tvDiscovery = findViewById(R.id.tvDiscoveryTab);
        if (tvDiscovery != null) {
            tvDiscovery.setTextColor(Color.parseColor("#09A459"));
            tvDiscovery.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        if (tabHome != null) {
            tabHome.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));
        }
        if (tabProfile != null) {
            tabProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));
        }
    }
}
