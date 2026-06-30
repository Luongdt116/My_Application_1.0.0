package huce.fit.myapplication.objects;

import com.google.firebase.database.Exclude;
import java.io.Serializable;
import java.util.Map;

public class Booking implements Serializable {
    @Exclude
    private String bookingId; // Firebase key
    
    private String account_id;
    private String venue_id;
    private String venue_name;
    private String court_id;
    private String court_name;
    private String booking_date;
    private String start_time;
    private String end_time;
    private long total_price_snapshot;
    private int status; // 1: active, 0: cancelled
    private long created_at;
    private Map<String, Integer> selected_services;
    private String customer_name;
    private String customer_phone;
    private String note;
    private Map<String, Object> payment_info;

    public Booking() {}

    @Exclude
    public String getBookingId() { return bookingId; }
    @Exclude
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getAccount_id() { return account_id; }
    public void setAccount_id(String account_id) { this.account_id = account_id; }

    public String getVenue_id() { return venue_id; }
    public void setVenue_id(String venue_id) { this.venue_id = venue_id; }

    public String getVenue_name() { return venue_name; }
    public void setVenue_name(String venue_name) { this.venue_name = venue_name; }

    public String getCourt_id() { return court_id; }
    public void setCourt_id(String court_id) { this.court_id = court_id; }

    public String getCourt_name() { return court_name; }
    public void setCourt_name(String court_name) { this.court_name = court_name; }

    public String getBooking_date() { return booking_date; }
    public void setBooking_date(String booking_date) { this.booking_date = booking_date; }

    public String getStart_time() { return start_time; }
    public void setStart_time(String start_time) { this.start_time = start_time; }

    public String getEnd_time() { return end_time; }
    public void setEnd_time(String end_time) { this.end_time = end_time; }

    public long getTotal_price_snapshot() { return total_price_snapshot; }
    public void setTotal_price_snapshot(long total_price_snapshot) { this.total_price_snapshot = total_price_snapshot; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public long getCreated_at() { return created_at; }
    public void setCreated_at(long created_at) { this.created_at = created_at; }

    public Map<String, Integer> getSelected_services() { return selected_services; }
    public void setSelected_services(Map<String, Integer> selected_services) { this.selected_services = selected_services; }

    public String getCustomer_name() { return customer_name; }
    public void setCustomer_name(String customer_name) { this.customer_name = customer_name; }

    public String getCustomer_phone() { return customer_phone; }
    public void setCustomer_phone(String customer_phone) { this.customer_phone = customer_phone; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Map<String, Object> getPayment_info() { return payment_info; }
    public void setPayment_info(Map<String, Object> payment_info) { this.payment_info = payment_info; }
}
