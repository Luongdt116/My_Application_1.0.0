package huce.fit.myapplication.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import huce.fit.myapplication.R;
import huce.fit.myapplication.objects.AdminAccount;
import huce.fit.myapplication.objects.CustomerAccount;
import huce.fit.myapplication.objects.CustomerInfo;
import huce.fit.myapplication.objects.Field;
import huce.fit.myapplication.objects.Level;
import java.util.concurrent.Executors;

@Database(entities = {Field.class, Level.class, CustomerAccount.class, AdminAccount.class, CustomerInfo.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract FieldDao fieldDao();
    public abstract LevelDao levelDao();
    public abstract CustomerAccountDao customerAccountDao();
    public abstract AdminAccountDao adminAccountDao();
    public abstract CustomerInfoDao customerInfoDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadExecutor().execute(() -> {
                // Lấy instance của Database để thao tác
                AppDatabase database = instance;
                
                // 1. Thêm dữ liệu mẫu cho các sân thể thao (Fields)
                FieldDao fieldDao = database.fieldDao();
                fieldDao.insert(new Field("Sân cầu lông ABC", "1.8km | Hoàng Mai, Hà Nội", "Cầu lông", R.drawable.badminton));
                fieldDao.insert(new Field("Sân Pickleball YYS", "3.2km | Cầu Giấy, Hà Nội", "Pickleball", R.drawable.pickleball));
                fieldDao.insert(new Field("Sân bóng đá HUCE", "0.5km | Hai Bà Trưng, Hà Nội", "Bóng đá", R.drawable.football));
                fieldDao.insert(new Field("Sân Tennis Sport", "5.0km | Đống Đa, Hà Nội", "Tennis", R.drawable.tennis));
                fieldDao.insert(new Field("Sân bóng rổ 365", "2.5km | Thanh Xuân, Hà Nội", "Bóng rổ", R.drawable.basketball));
                fieldDao.insert(new Field("Sân bóng chuyền ĐH", "1.2km | Ba Đình, Hà Nội", "Bóng chuyền", R.drawable.volleyball));

                // 2. Thêm dữ liệu phân quyền (Levels)
                LevelDao levelDao = database.levelDao();
                levelDao.insert(new Level("Admin"));    // Sẽ có levelId = 1
                levelDao.insert(new Level("Customer")); // Sẽ có levelId = 2

                // 3. Thêm tài khoản Admin mẫu
                AdminAccountDao adminDao = database.adminAccountDao();
                adminDao.insert(new AdminAccount("admin", "123", 1));

                // 4. Thêm tài khoản Khách hàng mẫu và thông tin chi tiết
                CustomerAccountDao custAccountDao = database.customerAccountDao();
                CustomerInfoDao custInfoDao = database.customerInfoDao();

                long customerId = custAccountDao.insert(new CustomerAccount("huce", "123456", "huce@fit.edu.vn", 2));
                custInfoDao.insert(new CustomerInfo((int) customerId, "Nguyễn Văn A", "0987654321", "1999", "Nam"));
            });
        }
    };
}
