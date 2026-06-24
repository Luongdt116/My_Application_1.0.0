package huce.fit.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {

    private ImageView btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        btnBack = findViewById(R.id.btnBackBooking);
        btnNext = findViewById(R.id.btnNext);

        tvSelectedDate.setOnClickListener(v -> showDatePicker());
        btnBack.setOnClickListener(v -> finish());
        
        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(BookingActivity.this, PaymentActivity.class);
            startActivity(intent);
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                }, year, month, day);
        datePickerDialog.show();


}
}
