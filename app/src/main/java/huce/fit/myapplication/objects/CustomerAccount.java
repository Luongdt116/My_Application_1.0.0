package huce.fit.myapplication.objects;

import java.io.Serializable;

public class CustomerAccount implements Serializable {
    private int id;
    private String username;
    private String password;
    private String email;
    private int levelId;

    public CustomerAccount() {
    }

    public CustomerAccount(String username, String password, String email, int levelId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.levelId = levelId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getLevelId() { return levelId; }
    public void setLevelId(int levelId) { this.levelId = levelId; }
}
