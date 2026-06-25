package huce.fit.myapplication.objects;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "admin_accounts")
public class AdminAccount {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;
    private String password;
    private int levelId;

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
