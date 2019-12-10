package ez.com.inside.activities.network;

import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ez.com.inside.R;
import ez.com.inside.business.network.Wifi;
import ez.com.inside.business.network.WifiDatabase;

public class WifiFragment extends Fragment {
    WifiDatabase db;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_wifi, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        db = WifiDatabase.getInstance(getView().getContext());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getWifiLevel();
    }


    private void getWifiLevel()
    {
        WifiManager wifiManager = (WifiManager) getView().getContext().getApplicationContext().getSystemService(getView().getContext().WIFI_SERVICE);
        int linkSpeed = wifiManager.getConnectionInfo().getRssi();
        int level = WifiManager.calculateSignalLevel(linkSpeed, 5);

        TextView type = getView().findViewById(R.id.WifiType);
        type.setText("Wifi");
        TextView name = getView().findViewById(R.id.WifiName);
        name.setText(wifiManager.getConnectionInfo().getSSID());
        setWifiText(level);

        Wifi wifi = new Wifi();
        wifi.name = wifiManager.getConnectionInfo().getSSID();
        wifi.averageLevel = level;

        //db.wifiDao().InsertAll(wifi);

        Log.d("Hey", Integer.toString(db.wifiDao().getAll().size()));
    }


    private void setWifiText(int level){

        String text = "Pas de donnée";
        int color = Color.parseColor("#000000");

        switch (level){
            case 1:
                text = "Très faible";
                color = Color.parseColor("#990000");
                break;
            case  2:
                text = "Faible";
                color = Color.parseColor("#992600");
                break;
            case 3:
                text = "Correct";
                color = Color.parseColor("#739900");
                break;
            case 4:
                text = "Bonne qualité";
                color = Color.parseColor("#009999");
                break;
            case 5:
                text = "Excellent";
                color = Color.parseColor("#009933");
                break;
        }

        TextView levelTextV = getView().findViewById(R.id.WifiLevel);
        levelTextV.setText(text);
        levelTextV.setTextColor(color);

    }


}
