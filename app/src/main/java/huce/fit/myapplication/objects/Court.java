package huce.fit.myapplication.objects;

import java.io.Serializable;

public class Court implements Serializable {
    private String name;
    private String type;
    private int status; // 1: active, 0: maintenance

    public Court() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}
