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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin, btnGoogleLogin;
    private TextView btnSignup, tvForgotPassword;
    private EditText edEmail, edPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String dbUrl = "https://app-moblie-131d8-default-rtdb.firebaseio.com/";
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_constraint);

        // Đã gỡ bỏ getSupportActionBar().hide() để hiển thị lại ActionBar ở trang Login theo yêu cầu

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference();

        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Ánh xạ UI
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        btnSignup = findViewById(R.id.btnSignup);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        edEmail = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edPassword);

        // Sự kiện đăng nhập Email/Pass
        if (btnLogin != null) {
            btnLogin.setOnClickListener(view -> {
                String email = edEmail.getText().toString().trim();
                String password = edPassword.getText().toString().trim();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    signInWithEmail(email, password);
                } else {
                    Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Sự kiện đăng nhập Google
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
                            firebaseAuthWithGoogle(account.getIdToken());
                        }
                    } catch (ApiException e) {
                        Log.e("GOOGLE_AUTH", "Lỗi: " + e.getStatusCode());
                        Toast.makeText(this, "Google Sign-In thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkUserInDatabase(user);
                    } else {
                        Toast.makeText(this, "Xác thực Firebase thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserInDatabase(FirebaseUser user) {
        if (user == null) return;
        String userId = user.getUid();

        mDatabase.child("Accounts").child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (!snapshot.exists()) {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("full_name", user.getDisplayName());
                    userMap.put("email", user.getEmail());
                    userMap.put("phone", "");
                    userMap.put("role", 1);
                    userMap.put("status", 1);
                    userMap.put("created_at", System.currentTimeMillis());

                    mDatabase.child("Accounts").child(userId).setValue(userMap)
                            .addOnSuccessListener(aVoid -> saveToPrefsAndGoMain(userId, user.getDisplayName(), user.getEmail(), 1));
                } else {
                    String fullName = snapshot.child("full_name").getValue(String.class);
                    Long role = snapshot.child("role").getValue(Long.class);
                    saveToPrefsAndGoMain(userId, fullName, user.getEmail(), role != null ? role.intValue() : 1);
                }
            }
        });
    }

    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        mDatabase.child("Accounts").child(userId).get().addOnCompleteListener(dbTask -> {
                            if (dbTask.isSuccessful() && dbTask.getResult().exists()) {
                                DataSnapshot snapshot = dbTask.getResult();
                                Long status = snapshot.child("status").getValue(Long.class);
                                if (status != null && status == 0) {
                                    mAuth.signOut();
                                    Toast.makeText(this, "Tài khoản bị khóa!", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                String fullName = snapshot.child("full_name").getValue(String.class);
                                Long role = snapshot.child("role").getValue(Long.class);
                                saveToPrefsAndGoMain(userId, fullName, email, role != null ? role.intValue() : 1);
                            }
                        });
                    } else {
                        Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveToPrefsAndGoMain(String uid, String name, String email, int role) {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        pref.edit()
                .putString("userId", uid)
                .putString("username", name)
                .putString("email", email)
                .putInt("role", role)
                .putBoolean("isLoggedIn", true)
                .apply();

        Toast.makeText(this, "Chào mừng " + name, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
