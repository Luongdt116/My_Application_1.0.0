package huce.fit.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;
import java.util.Map;

import huce.fit.myapplication.viewmodel.EditProfileViewModel;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etEditName, etEditPhone, etEditEmail, etEditDob;
    private Spinner spinnerGender;
    private ImageView imgEditAvatar;
    private EditProfileViewModel viewModel;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        initViews();
        setupViewModel();
        setupListeners();

        // Lấy userId và bảo ViewModel tải dữ liệu
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = pref.getString("userId", "");
        if (!TextUtils.isEmpty(userId)) {
            viewModel.loadUserData(userId);
        } else {
            Toast.makeText(this, "Lỗi: Chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        etEditName = findViewById(R.id.etEditName);
        etEditPhone = findViewById(R.id.etEditPhone);
        etEditEmail = findViewById(R.id.etEditEmail);
        etEditDob = findViewById(R.id.etEditDob);
        spinnerGender = findViewById(R.id.spinnerGender);
        imgEditAvatar = findViewById(R.id.imgEditAvatar);

        // Thiết lập Spinner
        String[] genders = {"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinnerGender != null) spinnerGender.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);

        // Lắng nghe dữ liệu người dùng để điền vào Form
        viewModel.getUserData().observe(this, data -> {
            if (data != null) {
                etEditName.setText(data.get("full_name"));
                etEditPhone.setText(data.get("phone"));
                etEditEmail.setText(data.get("email"));
                etEditDob.setText(data.get("dob"));
                
                String gender = data.get("gender");
                if (gender != null && spinnerGender != null) {
                    for (int i = 0; i < spinnerGender.getCount(); i++) {
                        if (gender.equals(spinnerGender.getItemAtPosition(i).toString())) {
                            spinnerGender.setSelection(i);
                            break;
                        }
                    }
                }
            }
        });

        // Lắng nghe trạng thái cập nhật thành công
        viewModel.getUpdateSuccess().observe(this, success -> {
            if (success) {
                // Cập nhật lại SharedPreferences
                SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                pref.edit().putString("username", etEditName.getText().toString().trim()).apply();
                
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Lắng nghe lỗi
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        findViewById(R.id.btnBackEditProfile).setOnClickListener(v -> finish());
        findViewById(R.id.btnCancelEdit).setOnClickListener(v -> finish());
        
        etEditDob.setOnClickListener(v -> showDatePicker());

        findViewById(R.id.btnSaveEdit).setOnClickListener(v -> {
            String name = etEditName.getText().toString().trim();
            String phone = etEditPhone.getText().toString().trim();
            String dob = etEditDob.getText().toString().trim();
            String gender = spinnerGender.getSelectedItem().toString();

            if (validate(name, phone)) {
                viewModel.updateProfile(userId, name, phone, dob, gender);
            }
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            etEditDob.setText(String.format("%02d/%02d/%d", day, (month + 1), year));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private boolean validate(String name, String phone) {
        if (TextUtils.isEmpty(name)) {
            etEditName.setError("Vui lòng nhập tên");
            return false;
        }
        if (phone.length() < 10) {
            etEditPhone.setError("SĐT không hợp lệ");
            return false;
        }
        return true;
    }
}
