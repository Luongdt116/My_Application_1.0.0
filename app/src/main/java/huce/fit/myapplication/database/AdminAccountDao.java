package huce.fit.myapplication.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import huce.fit.myapplication.objects.AdminAccount;

@Dao
public interface AdminAccountDao {
    @Insert
    void insert(AdminAccount account);

    @Query("SELECT * FROM admin_accounts WHERE username = :username AND password = :password")
    AdminAccount login(String username, String password);
}
