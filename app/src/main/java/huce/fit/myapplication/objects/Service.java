package huce.fit.myapplication.objects;

import java.io.Serializable;

public class Service implements Serializable {
    private String name;
    private long price;
    private String unit;

    public Service() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
