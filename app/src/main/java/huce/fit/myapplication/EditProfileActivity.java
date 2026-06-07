package huce.fit.myapplication;

import android.app.DatePickerDialog;
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

import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etEditName, etEditPhone, etEditEmail, etEditDob;
    private Spinner spinnerGender;
    private ImageView imgEditAvatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 1. Ánh xạ các View điều khiển
        ImageView btnBack = findViewById(R.id.btnBackEditProfile);
        Button btnCancel = findViewById(R.id.btnCancelEdit);
        Button btnSave = findViewById(R.id.btnSaveEdit);

        etEditName = findViewById(R.id.etEditName);
        etEditPhone = findViewById(R.id.etEditPhone);
        etEditEmail = findViewById(R.id.etEditEmail);
        etEditDob = findViewById(R.id.etEditDob);
       // spinnerGender = findViewById(R.id.spinnerGender);
        imgEditAvatar = findViewById(R.id.imgEditAvatar);

        // 2. Thiết lập dữ liệu cho Spinner Giới tính
        String[] genders = {"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        // 3. Xử lý sự kiện khi click vào ô Ngày Sinh (Hiện DatePicker)
        etEditDob.setFocusable(false); // Ngăn không cho bàn phím hiện lên
        etEditDob.setOnClickListener(v -> showDatePickerDialog());

        // Nút Back trên toolbar
        btnBack.setOnClickListener(v -> finish());

        // Nút Hủy dưới footer
        btnCancel.setOnClickListener(v -> finish());

        // 4. Nút Lưu hồ sơ
        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveProfileData();
            }
        });
    }

    // Hàm hiển thị hộp thoại chọn ngày tháng năm
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Định dạng hiển thị dd/MM/yyyy
                    String dateStr = String.format("%02d/%02d/%d", selectedDay, (selectedMonth + 1), selectedYear);
                    etEditDob.setText(dateStr);
                }, year, month, day);
        datePickerDialog.show();
    }

    // Hàm kiểm tra tính hợp lệ của dữ liệu nhập vào
    private boolean validateInput() {
        String name = etEditName.getText().toString().trim();
        String phone = etEditPhone.getText().toString().trim();
        String email = etEditEmail.getText().toString().trim();
        String dob = etEditDob.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etEditName.setError("Vui lòng nhập tên đầy đủ");
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            etEditPhone.setError("Vui lòng nhập số điện thoại");
            return false;
        } else if (phone.length() < 10 || phone.length() > 11) {
            etEditPhone.setError("Số điện thoại không hợp lệ (9-11 số)");
            return false;
        }

        if (!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEditEmail.setError("Định dạng Email không đúng");
            return false;
        }

        if (TextUtils.isEmpty(dob)) {
            etEditDob.setError("Vui lòng chọn ngày sinh");
            return false;
        }

        return true;
    }

    // Hàm thực hiện lưu (Nơi bạn sẽ viết tiếp logic SQLite/Room hoặc API)
    private void saveProfileData() {
        String finalName = etEditName.getText().toString().trim();
        String finalPhone = etEditPhone.getText().toString().trim();
        String finalEmail = etEditEmail.getText().toString().trim();
        String finalDob = etEditDob.getText().toString().trim();
        String finalGender = spinnerGender.getSelectedItem().toString();

        // TODO: Viết lệnh tương tác với Database ở đây (ví dụ: dbHelper.updateProfile(...))

        Toast.makeText(this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();

        // Đóng activity sau khi lưu thành công
        finish();
    }
}