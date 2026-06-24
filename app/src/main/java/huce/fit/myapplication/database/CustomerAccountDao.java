package huce.fit.myapplication.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import huce.fit.myapplication.objects.CustomerAccount;

@Dao
public interface CustomerAccountDao {
    @Insert
    long insert(CustomerAccount account);

    @Query("SELECT * FROM customer_accounts WHERE username = :username AND password = :password")
    CustomerAccount login(String username, String password);
}
