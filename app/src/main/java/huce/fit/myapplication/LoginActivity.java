package huce.fit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

import huce.fit.myapplication.objects.Venue;
import huce.fit.myapplication.viewmodel.LoginViewModel;
import huce.fit.myapplication.viewmodel.PaymentViewModel;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin, btnGoogleLogin;
    private TextView btnSignup, tvForgotPassword;
    private EditText edEmail, edPassword;
    private GoogleSignInClient mGoogleSignInClient;
    private LoginViewModel loginViewModel;
    private PaymentViewModel paymentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_constraint);

        initViews();
        setupViewModel();
        setupGoogleSignIn();
        setupListeners();
    }

    private void initViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        btnSignup = findViewById(R.id.btnSignup);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        edEmail = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edPassword);
    }

    private void setupViewModel() {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);

        loginViewModel.getLoginSuccess().observe(this, userState -> {
            if (userState != null) {
                handlePostLogin(userState.uid, userState.name, userState.email, userState.role);
            }
        });

        loginViewModel.getLoginError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handlePostLogin(String uid, String name, String email, int role) {
        // Kiểm tra xem có đơn hàng chờ không
        if (getIntent().getBooleanExtra("pending_booking", false)) {
            Venue venue = (Venue) getIntent().getSerializableExtra("selected_venue");
            String date = getIntent().getStringExtra("selected_date");
            ArrayList<String> slots = getIntent().getStringArrayListExtra("selected_slots");
            HashMap services = (HashMap) getIntent().getSerializableExtra("selected_services");
            long total = getIntent().getLongExtra("total_price", 0);
            String cName = getIntent().getStringExtra("customer_name");
            String cPhone = getIntent().getStringExtra("customer_phone");
            String cNote = getIntent().getStringExtra("customer_note");

            // Lưu vào Firebase với trạng thái "Chưa thanh toán" (status 2)
            paymentViewModel.saveBookings(uid, venue, date, slots, services, total, null, cName, cPhone, cNote);
            
            Toast.makeText(this, "Đã lưu đơn hàng vào lịch sử. Vui lòng thanh toán để hoàn tất.", Toast.LENGTH_LONG).show();
        }

        saveToPrefsAndGoMain(uid, name, email, role);
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupListeners() {
        if (btnLogin != null) {
            btnLogin.setOnClickListener(view -> {
                String email = edEmail.getText().toString().trim();
                String password = edPassword.getText().toString().trim();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    loginViewModel.signInWithEmail(email, password);
                } else {
                    Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnGoogleLogin != null) {
            btnGoogleLogin.setOnClickListener(v -> {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                googleSignInLauncher.launch(signInIntent);
            });
        }

        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
        }

        if (btnSignup != null) {
            btnSignup.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        }
    }

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null) {
                            loginViewModel.firebaseAuthWithGoogle(account.getIdToken());
                        }
                    } catch (ApiException e) {
                        Log.e("GOOGLE_AUTH", "Lỗi: " + e.getStatusCode());
                    }
                }
            }
    );

    private void saveToPrefsAndGoMain(String uid, String name, String email, int role) {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        pref.edit()
                .putString("userId", uid)
                .putString("username", name)
                .putString("email", email)
                .putInt("role", role)
                .putBoolean("isLoggedIn", true)
                .apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
