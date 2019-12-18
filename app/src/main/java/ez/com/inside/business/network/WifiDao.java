package ez.com.inside.business.network;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface WifiDao {

    @Query("SELECT * FROM wifi")
    List<Wifi> getAll();

    @Query("SELECT * FROM wifi WHERE name LIKE :first")
    List<Wifi> findByName(String first);

    @Insert
    void InsertAll(Wifi... wifis);

    @Query("DELETE FROM wifi")
    public void deleteAll();

}
