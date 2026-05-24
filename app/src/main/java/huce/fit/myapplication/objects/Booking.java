package huce.fit.myapplication.objects;

import java.io.Serializable;
import java.util.Map;

public class Booking implements Serializable {
    private String account_id;
    private String venue_id;
    private String court_id;
    private String court_name;
    private String booking_date;
    private String start_time;
    private String end_time;
    private long total_price_snapshot;
    private int status; // 1: active, 0: cancelled
    private long created_at;

    public Booking() {}

    public String getAccount_id() { return account_id; }
    public void setAccount_id(String account_id) { this.account_id = account_id; }

    public String getVenue_id() { return venue_id; }
    public void setVenue_id(String venue_id) { this.venue_id = venue_id; }

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
}
