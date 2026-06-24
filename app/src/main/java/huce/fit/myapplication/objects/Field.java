package huce.fit.myapplication.objects;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "fields") // Tên bảng trong SQLite
public class Field implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String name;
    private String location;
    private String type; // Loại sân (Bóng đá, Cầu lông,...)
    private int imageResId; // ID ảnh trong drawable

    public Field(String name, String location, String type, int imageResId) {
        this.name = name;
        this.location = location;
        this.type = type;
        this.imageResId = imageResId;
    }

    // Getter và Setter cần thiết cho Room
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
