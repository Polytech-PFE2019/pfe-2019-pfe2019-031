package ez.com.inside.business.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Wifi implements Serializable {

    @PrimaryKey (autoGenerate = true)
    @NonNull
    protected Integer key;

    @ColumnInfo (name = "name")
    @NonNull
    public String name;

    @ColumnInfo(name = "level")
    public int level;

    @ColumnInfo (name = "date")
    public String date;

    @Ignore
    private List<Wifi> wifiSameName;

    public void addWifi(Wifi wifi){
        if(wifiSameName == null)
            wifiSameName = new ArrayList<>();
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
