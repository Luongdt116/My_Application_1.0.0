package huce.fit.myapplication.objects;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "levels")
public class Level {
    @PrimaryKey(autoGenerate = true)
    private int levelId;
    private String levelName;

    public Level(String levelName) {
        this.levelName = levelName;
    }

    public int getLevelId() { return levelId; }
    public void setLevelId(int levelId) { this.levelId = levelId; }
    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }
}
