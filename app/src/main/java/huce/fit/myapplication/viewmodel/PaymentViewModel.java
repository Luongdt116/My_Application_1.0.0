package huce.fit.myapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import huce.fit.myapplication.objects.Court;
import huce.fit.myapplication.objects.Venue;
import huce.fit.myapplication.util.CreateOrder;

public class PaymentViewModel extends ViewModel {

    private final MutableLiveData<String> mZaloToken = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mPaymentSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> mError = new MutableLiveData<>();
    
    private final DatabaseReference mDatabase;
    private final String dbUrl = "https://app-moblie-131d8-default-rtdb.firebaseio.com/";

    public PaymentViewModel() {
        mDatabase = FirebaseDatabase.getInstance(dbUrl).getReference();
    }

    public LiveData<String> getZaloToken() { return mZaloToken; }
    public LiveData<Boolean> getPaymentSuccess() { return mPaymentSuccess; }
    public LiveData<String> getError() { return mError; }

    public void createZaloPayOrder(long amount) {
        CreateOrder orderApi = new CreateOrder();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject data = orderApi.createOrder(String.valueOf(amount));
                if (data != null && data.getString("return_code").equals("1")) {
                    mZaloToken.postValue(data.getString("zp_trans_token"));
                } else {
                    mError.postValue("Không thể tạo đơn hàng ZaloPay");
                }
            } catch (Exception e) {
                mError.postValue("Lỗi hệ thống: " + e.getMessage());
            }
        });
    }

    public void saveBookings(String userId, Venue venue, String date, ArrayList<String> slots, 
                             Map<String, Integer> services, long total, String transId, 
                             String name, String phone, String note) {
        
        long now = System.currentTimeMillis();
        String[] dateParts = date.split("/");
        String firebaseDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

        // Ánh xạ ID dịch vụ sang Tên dịch vụ để hiển thị lịch sử dễ dàng
        Map<String, Integer> serviceNamesMap = new HashMap<>();
        if (services != null && venue.getServices() != null) {
            for (Map.Entry<String, Integer> entry : services.entrySet()) {
                String srvId = entry.getKey();
                if (venue.getServices().containsKey(srvId)) {
                    String srvName = venue.getServices().get(srvId).getName();
                    serviceNamesMap.put(srvName, entry.getValue());
                } else {
                    serviceNamesMap.put(srvId, entry.getValue());
                }
            }
        }

        final int totalSlots = slots.size();
        final AtomicInteger savedCount = new AtomicInteger(0);

        for (String slot : slots) {
            String[] parts = slot.split("\\|");
            String courtName = parts[0];
            int hour = Integer.parseInt(parts[1]);

            String courtId = "";
            if (venue.getCourts() != null) {
                for (Map.Entry<String, Court> entry : venue.getCourts().entrySet()) {
                    if (entry.getValue().getName().equals(courtName)) {
                        courtId = entry.getKey();
                        break;
                    }
                }
            }

            Map<String, Object> bookingData = new HashMap<>();
            bookingData.put("account_id", userId);
            bookingData.put("venue_id", venue.getVenueId());
            bookingData.put("venue_name", venue.getVenue_name());
            bookingData.put("court_id", courtId);
            bookingData.put("court_name", courtName);
            bookingData.put("booking_date", firebaseDate);
            bookingData.put("start_time", String.format(Locale.getDefault(), "%02d:00", hour));
            bookingData.put("end_time", String.format(Locale.getDefault(), "%02d:00", hour + 1));
            bookingData.put("status", 1);
            bookingData.put("created_at", now);
            bookingData.put("customer_name", name);
            bookingData.put("customer_phone", phone);
            bookingData.put("note", note);
            bookingData.put("selected_services", serviceNamesMap); // Lưu Map với Key là Tên dịch vụ
            bookingData.put("total_price_snapshot", total / totalSlots);

            Map<String, Object> paymentInfo = new HashMap<>();
            paymentInfo.put("method", "ZaloPay");
            paymentInfo.put("transaction_code", transId);
            paymentInfo.put("payment_status", 1);
            paymentInfo.put("amount", total / totalSlots);
            paymentInfo.put("paid_at", now);
            bookingData.put("payment_info", paymentInfo);

            mDatabase.child("Bookings").push().setValue(bookingData)
                .addOnCompleteListener(task -> {
                    if (savedCount.incrementAndGet() == totalSlots) {
                        mPaymentSuccess.setValue(true);
                    }
                });
        }
    }

    public String formatSummary(ArrayList<String> slots, Map<String, Integer> services, Venue venue) {
        StringBuilder sb = new StringBuilder();
        Map<String, List<Integer>> groupedSlots = new HashMap<>();
        if (slots != null) {
            for (String s : slots) {
                String[] parts = s.split("\\|");
                if (parts.length == 2) {
                    String cName = parts[0];
                    int h = Integer.parseInt(parts[1]);
                    if (!groupedSlots.containsKey(cName)) groupedSlots.put(cName, new ArrayList<>());
                    groupedSlots.get(cName).add(h);
                }
            }
        }
        for (Map.Entry<String, List<Integer>> entry : groupedSlots.entrySet()) {
            sb.append("- ").append(entry.getKey()).append(": ");
            List<Integer> hours = entry.getValue();
            Collections.sort(hours);
            for (int i = 0; i < hours.size(); i++) {
                sb.append(hours.get(i)).append("h-").append(hours.get(i) + 1).append("h");
                if (i < hours.size() - 1) sb.append(", ");
            }
            sb.append("\n");
        }
        if (services != null && !services.isEmpty() && venue.getServices() != null) {
            sb.append("\nDịch vụ:\n");
            for (Map.Entry<String, Integer> entry : services.entrySet()) {
                if (venue.getServices().containsKey(entry.getKey())) {
                    sb.append("- ").append(venue.getServices().get(entry.getKey()).getName())
                      .append(" (x").append(entry.getValue()).append(")\n");
                }
            }
        }
        return sb.toString().trim();
    }
}
