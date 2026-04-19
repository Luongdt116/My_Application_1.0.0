package huce.fit.myapplication.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import huce.fit.myapplication.objects.Level;
import java.util.List;

@Dao
public interface LevelDao {
    @Insert
    void insert(Level level);

    @Query("SELECT * FROM levels")
    List<Level> getAllLevels();
}
