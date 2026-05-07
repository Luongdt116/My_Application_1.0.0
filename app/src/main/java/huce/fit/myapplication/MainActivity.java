package huce.fit.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private Fragment homeFrag, discoveryFrag, profileFrag, offersFrag;
    private Fragment activeFragment;

    private View tabHome, tabDiscovery, tabProfile;
    private ImageView ivHome, ivDiscovery, ivProfile;
    private TextView tvHome, tvDiscovery, tvProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        fragmentManager = getSupportFragmentManager();

        // 1. Khởi tạo các Fragment từ chính các file Activity cũ
        homeFrag = new HomeActivity();
        discoveryFrag = new DiscoveryActivity();
        profileFrag = new ProfileActivity();
        offersFrag = new OffersActivity();

        // 2. Nạp tất cả vào Container bằng 1 Transaction duy nhất (Hide/Show để tối ưu)
        fragmentManager.beginTransaction()
            .add(R.id.fragment_container, offersFrag, "offers").hide(offersFrag)
            .add(R.id.fragment_container, profileFrag, "profile").hide(profileFrag)
            .add(R.id.fragment_container, discoveryFrag, "discovery").hide(discoveryFrag)
            .add(R.id.fragment_container, homeFrag, "home")
            .commit();
        
        activeFragment = homeFrag;

        initFooter();
        updateTabUI(R.id.layoutHomeTab);
    }

    private void initFooter() {
        tabHome = findViewById(R.id.layoutHomeTab);
        tabDiscovery = findViewById(R.id.layoutDiscoveryTab);
        tabProfile = findViewById(R.id.layoutProfileTab);

        ivHome = findViewById(R.id.ivHomeTab);
        ivDiscovery = findViewById(R.id.ivDiscoveryTab);
        ivProfile = findViewById(R.id.ivProfileTab);

        tvHome = findViewById(R.id.tvHomeTab);
        tvDiscovery = findViewById(R.id.tvDiscoveryTab);
        tvProfile = findViewById(R.id.tvProfileTab);

        // Chuyển Tab thông minh: Không nạp lại trang, giữ nguyên vị trí cuộn
        tabHome.setOnClickListener(v -> switchFragment(homeFrag, R.id.layoutHomeTab));
        tabDiscovery.setOnClickListener(v -> switchFragment(discoveryFrag, R.id.layoutDiscoveryTab));
        tabProfile.setOnClickListener(v -> switchFragment(profileFrag, R.id.layoutProfileTab));
    }

    private void switchFragment(Fragment fragment, int tabId) {
        if (activeFragment == fragment) return;
        fragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit();
        activeFragment = fragment;
        updateTabUI(tabId);
    }

    public void navigateToOffers() {
        switchFragment(offersFrag, R.id.layoutDiscoveryTab);
    }

    public void navigateToDiscovery() {
        switchFragment(discoveryFrag, R.id.layoutDiscoveryTab);
    }

    public void updateTabUI(int selectedTabId) {
        int grayColor = Color.parseColor("#888888");
        int greenColor = Color.parseColor("#09A459");

        if (ivHome != null) ivHome.setColorFilter(grayColor);
        if (ivProfile != null) ivProfile.setColorFilter(grayColor);
        if (ivDiscovery != null) ivDiscovery.setColorFilter(grayColor);
        
        if (tvHome != null) tvHome.setTextColor(grayColor);
        if (tvDiscovery != null) tvDiscovery.setTextColor(grayColor);
        if (tvProfile != null) tvProfile.setTextColor(grayColor);

        if (selectedTabId == R.id.layoutHomeTab) {
            if (ivHome != null) ivHome.setColorFilter(greenColor);
            if (tvHome != null) tvHome.setTextColor(greenColor);
        } else if (selectedTabId == R.id.layoutDiscoveryTab) {
            if (tvDiscovery != null) tvDiscovery.setTextColor(greenColor);
        } else if (selectedTabId == R.id.layoutProfileTab) {
            if (ivProfile != null) ivProfile.setColorFilter(greenColor);
            if (tvProfile != null) tvProfile.setTextColor(greenColor);
        }
    }
}
