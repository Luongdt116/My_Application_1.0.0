package huce.fit.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etEditName, etEditPhone, etEditEmail, etEditDob;
    private Spinner spinnerGender;
    private ImageView imgEditAvatar;
    private DatabaseReference mDatabase;
    private String userId;
    private String dbUrl = "https://app-moblie-131d8-default-rtdb.firebaseio.com/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 1. Lấy userId từ SharedPreferences
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = pref.getString("userId", "");

        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(this, "Không tìm thấy phiên đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Khởi tạo Firebase
        mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference();

        // 3. Ánh xạ View
        ImageView btnBack = findViewById(R.id.btnBackEditProfile);
        Button btnCancel = findViewById(R.id.btnCancelEdit);
        Button btnSave = findViewById(R.id.btnSaveEdit);

        etEditName = findViewById(R.id.etEditName);
        etEditPhone = findViewById(R.id.etEditPhone);
        etEditEmail = findViewById(R.id.etEditEmail);
        etEditDob = findViewById(R.id.etEditDob);
        spinnerGender = findViewById(R.id.spinnerGender);
        imgEditAvatar = findViewById(R.id.imgEditAvatar);

        // 4. Thiết lập Spinner Giới tính
        String[] genders = {"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinnerGender != null) {
            spinnerGender.setAdapter(adapter);
        }

        // 5. Tải dữ liệu cũ từ Firebase
        loadCurrentUserData();

        // 6. Xử lý sự kiện
        etEditDob.setOnClickListener(v -> showDatePickerDialog());
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveProfileDataToFirebase();
            }
        });
    }

    private void loadCurrentUserData() {
        mDatabase.child("Accounts").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("full_name").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String dob = snapshot.child("dob").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);

                    if (name != null) etEditName.setText(name);
                    if (phone != null) etEditPhone.setText(phone);
                    if (email != null) etEditEmail.setText(email);
                    if (dob != null) etEditDob.setText(dob);
                    
                    if (gender != null && spinnerGender != null) {
                        for (int i = 0; i < spinnerGender.getCount(); i++) {
                            if (gender.equals(spinnerGender.getItemAtPosition(i).toString())) {
                                spinnerGender.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EDIT_PROFILE", "Lỗi tải dữ liệu: " + error.getMessage());
            }
        });
    }

    private void saveProfileDataToFirebase() {
        String name = etEditName.getText().toString().trim();
        String phone = etEditPhone.getText().toString().trim();
        String dob = etEditDob.getText().toString().trim();
        String gender = spinnerGender != null ? spinnerGender.getSelectedItem().toString() : "Khác";

        Map<String, Object> updates = new HashMap<>();
        updates.put("full_name", name);
        updates.put("phone", phone);
        updates.put("dob", dob);
        updates.put("gender", gender);

        mDatabase.child("Accounts").child(userId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật lại SharedPreferences để các màn hình khác (Home, Profile) thấy tên mới ngay
                    SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    pref.edit().putString("username", name).apply();

                    Toast.makeText(EditProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    etEditDob.setText(String.format("%02d/%02d/%d", selectedDay, (selectedMonth + 1), selectedYear));
                }, year, month, day);
        datePickerDialog.show();
    }

    private boolean validateInput() {
        String name = etEditName.getText().toString().trim();
        String phone = etEditPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etEditName.setError("Vui lòng nhập tên");
            return false;
        }
        if (phone.length() < 10) {
            etEditPhone.setError("Số điện thoại không hợp lệ");
            return false;
        }
        return true;
    }
}
