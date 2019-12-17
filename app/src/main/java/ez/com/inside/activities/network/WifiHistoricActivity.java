package ez.com.inside.activities.network;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ez.com.inside.R;
import ez.com.inside.business.network.Wifi;

public class WifiHistoricActivity extends AppCompatActivity {

    private Wifi wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_historic);
        setTitle("WIFI");
        Intent intent = getIntent();
        wifi = (Wifi) intent.getSerializableExtra("wifi");

        TextView name = findViewById(R.id.historicWifiName);
        name.setText(wifi.name);

        Log.d("Historic wifi nb", wifi.getWifiSameName().size()+ "");
        setRecycleView();
    }

    private void setRecycleView(){
        RecyclerView recyclerView = findViewById(R.id.recycleView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        WifiHistoricAdapter wifiHistoricAdapter = new WifiHistoricAdapter(wifi.getWifiSameName());
        recyclerView.setAdapter(wifiHistoricAdapter);

    }




}
