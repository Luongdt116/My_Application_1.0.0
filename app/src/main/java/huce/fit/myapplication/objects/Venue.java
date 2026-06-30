package huce.fit.myapplication.objects;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Venue implements Serializable {
    private String venueId; 
    private String venue_name;
    private String sport_name;
    private String address_detail;
    private String description;
    private String phone;
    private String opening_time;
    private String closing_time;
    private int status; 
    private Map<String, VenuePrice> venue_prices;
    private List<VenueImage> images;
    private Map<String, Court> courts;
    private Map<String, Service> services;
    
    // Trường bổ sung để hiển thị thông tin ưu đãi
    private String promotionTitle;
    private String promotionDescription;

    public Venue() {}

    public static class VenuePrice implements Serializable {
        public long fixed_price;
        public String day_of_week;
        public String start_time;
        public String end_time;
        public VenuePrice() {}
    }

    public static class VenueImage implements Serializable {
        public String url;
        public boolean is_main;
        public VenueImage() {}
    }

    public String getLocalImageName() {
        if (images != null && !images.isEmpty()) {
            for (VenueImage img : images) {
                if (img.is_main && img.url != null) return img.url;
            }
            return images.get(0).url;
        }
        if (sport_name == null) return "logo";
        String name = sport_name.toLowerCase();
        if (name.contains("bóng đá") || name.contains("football")) return "football";
        if (name.contains("cầu lông") || name.contains("badminton")) return "badminton";
        if (name.contains("pickleball")) return "pickleball";
        if (name.contains("tennis")) return "tennis";
        return "logo";
    }

    public String getDisplayPrice() {
        if (venue_prices == null || venue_prices.isEmpty()) return "Liên hệ";
        VenuePrice firstPrice = venue_prices.values().iterator().next();
        return String.format("%,dđ", firstPrice.fixed_price);
    }

    // --- GETTERS VÀ SETTERS ---
    public String getVenueId() { return venueId; }
    public void setVenueId(String venueId) { this.venueId = venueId; }
    public String getVenue_name() { return venue_name; }
    public void setVenue_name(String venue_name) { this.venue_name = venue_name; }
    public String getSport_name() { return sport_name; }
    public void setSport_name(String sport_name) { this.sport_name = sport_name; }
    public String getAddress_detail() { return address_detail; }
    public void setAddress_detail(String address_detail) { this.address_detail = address_detail; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getOpening_time() { return opening_time; }
    public void setOpening_time(String opening_time) { this.opening_time = opening_time; }
    public String getClosing_time() { return closing_time; }
    public void setClosing_time(String closing_time) { this.closing_time = closing_time; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public Map<String, VenuePrice> getVenue_prices() { return venue_prices; }
    public void setVenue_prices(Map<String, VenuePrice> venue_prices) { this.venue_prices = venue_prices; }
    public List<VenueImage> getImages() { return images; }
    public void setImages(List<VenueImage> images) { this.images = images; }
    public Map<String, Court> getCourts() { return courts; }
    public void setCourts(Map<String, Court> courts) { this.courts = courts; }
    public Map<String, Service> getServices() { return services; }
    public void setServices(Map<String, Service> services) { this.services = services; }
    public String getPromotionTitle() { return promotionTitle; }
    public void setPromotionTitle(String promotionTitle) { this.promotionTitle = promotionTitle; }
    public String getPromotionDescription() { return promotionDescription; }
    public void setPromotionDescription(String promotionDescription) { this.promotionDescription = promotionDescription; }
}
