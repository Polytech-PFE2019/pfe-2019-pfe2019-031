package ez.com.inside.business.network;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Wifi {

    @PrimaryKey (autoGenerate = true)
    @NonNull
    public Integer key;

    @ColumnInfo (name = "name")
    @NonNull
    public String name;

    @ColumnInfo(name = "level")
    public int level;

    @ColumnInfo (name = "date")
    public String date;

    @Ignore
    private List<Wifi> wifiSameName = new ArrayList<>();

    public void addWifi(Wifi wifi){
        wifiSameName.add(wifi);
    }

    public List<Wifi> getWifiSameName(){
        return wifiSameName;
    }

    @Override
    public String toString() {
        return name + ", level : " + level + ", date : "+ date;
    }

}
