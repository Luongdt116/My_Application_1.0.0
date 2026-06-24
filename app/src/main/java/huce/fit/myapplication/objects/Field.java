package huce.fit.myapplication.objects;

import java.io.Serializable;

public class Field implements Serializable {
    private int id;
    private String name;
    private String location;
    private String type; // Loại sân (Bóng đá, Cầu lông,...)
    private int imageResId; // ID ảnh trong drawable

    // 1. Constructor mặc định (Bắt buộc cho Firebase)
    public Field() {
    }

    // 2. Constructor có tham số
    public Field(String name, String location, String type, int imageResId) {
        this.name = name;
        this.location = location;
        this.type = type;
        this.imageResId = imageResId;
    }

    // 3. Getter và Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}
