package ez.com.inside.business.network;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Wifi {

    @PrimaryKey
    @NonNull
    public String name;

    @ColumnInfo(name = "average_level")
    public int averageLevel;


}
