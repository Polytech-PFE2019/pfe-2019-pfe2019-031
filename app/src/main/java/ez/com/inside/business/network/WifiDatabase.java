package ez.com.inside.business.network;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Wifi.class, version = 1, exportSchema = false)
public abstract class WifiDatabase extends RoomDatabase {

    public abstract WifiDao wifiDao();
    private static WifiDatabase instance;

    public static synchronized WifiDatabase getInstance(Context context){
        if(instance == null)
            instance= Room.databaseBuilder(context.getApplicationContext(), WifiDatabase.class, "wifi_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();

        return instance;
    }
}
