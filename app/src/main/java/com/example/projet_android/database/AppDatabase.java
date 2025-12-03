package com.example.projet_android.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.projet_android.database.dao.ActivityDao;
import com.example.projet_android.database.dao.FoodLogDao;
import com.example.projet_android.database.dao.HealthDataDao;
import com.example.projet_android.database.dao.UserDao;
import com.example.projet_android.database.entities.Activity;
import com.example.projet_android.database.entities.FoodLog;
import com.example.projet_android.database.entities.HealthData;
import com.example.projet_android.database.entities.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {User.class, HealthData.class, FoodLog.class, Activity.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    public abstract UserDao userDao();
    public abstract HealthDataDao healthDataDao();
    public abstract FoodLogDao foodLogDao();
    public abstract ActivityDao activityDao();
    
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "health_tracker_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Si vous voulez ajouter des données initiales, utilisez databaseWriteExecutor
            databaseWriteExecutor.execute(() -> {
                // Ajouter des données d'exemple si nécessaire
                // AppDatabase database = AppDatabase.getDatabase(context);
                // UserDao dao = database.userDao();
            });
        }
    };
      // Migration example for future versions
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Exemple de migration pour une future version
            // database.execSQL("ALTER TABLE users ADD COLUMN new_column TEXT");
        }
    };
    
    /**
     * Fermer la base de données
     */
    public static void closeDatabase() {
        if (INSTANCE != null && INSTANCE.isOpen()) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }
    
    /**
     * Obtenir la taille de la base de données en bytes
     */
    public static long getDatabaseSize(Context context) {
        try {
            return context.getDatabasePath("health_tracker_database").length();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Vérifier si la base de données existe
     */
    public static boolean databaseExists(Context context) {
        return context.getDatabasePath("health_tracker_database").exists();
    }
    
    /**
     * Supprimer complètement la base de données (pour les tests ou reset)
     */
    public static void deleteDatabase(Context context) {
        closeDatabase();
        context.deleteDatabase("health_tracker_database");
    }
}
