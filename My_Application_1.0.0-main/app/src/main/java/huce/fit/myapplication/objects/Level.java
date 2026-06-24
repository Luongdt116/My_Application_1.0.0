package huce.fit.myapplication.objects;

public class Level {
    private int levelId;
    private String levelName;

    public Level() {
    }

    public Level(String levelName) {
        this.levelName = levelName;
    }

    public int getLevelId() { return levelId; }
    public void setLevelId(int levelId) { this.levelId = levelId; }
    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }
}
