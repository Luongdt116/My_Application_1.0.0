package huce.fit.myapplication.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import huce.fit.myapplication.objects.Field;

@Dao
public interface FieldDao {
    @Query("SELECT * FROM fields")
    LiveData<List<Field>> getAllFields();

    @Insert
    void insert(Field field);

    @Update
    void update(Field field);

    @Delete
    void delete(Field field);

    @Query("SELECT * FROM fields WHERE id = :id")
    Field getFieldById(int id);
}
