package huce.fit.myapplication.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import huce.fit.myapplication.objects.AdminAccount;

@Dao
public interface AdminAccountDao {
    @Query("SELECT * FROM admin_accounts WHERE id = :id")
    AdminAccount getAccountById(int id);
    @Insert
    void insert(AdminAccount account);

    @Query("SELECT * FROM admin_accounts WHERE username = :username AND password = :password")
    AdminAccount login(String username, String password);

    @Query("UPDATE admin_accounts SET password = :newPassword WHERE id = :id")
    void updatePassword(int id, String newPassword);
}
