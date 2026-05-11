package huce.fit.myapplication.objects;

import java.io.Serializable;

public class AdminAccount implements Serializable {
    private int id;
    private String username;
    private String password;
    private int levelId;

    public AdminAccount() {
    }

    public AdminAccount(String username, String password, int levelId) {
        this.username = username;
        this.password = password;
        this.levelId = levelId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getLevelId() { return levelId; }
    public void setLevelId(int levelId) { this.levelId = levelId; }
}
