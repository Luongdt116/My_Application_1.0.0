package huce.fit.myapplication.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import huce.fit.myapplication.objects.CustomerInfo;

@Dao
public interface CustomerInfoDao {
    @Insert
    void insert(CustomerInfo info);

    @Update
    void update(CustomerInfo info);

    @Query("SELECT * FROM customer_info WHERE accountId = :accountId")
    CustomerInfo getInfoByAccountId(int accountId);
}
