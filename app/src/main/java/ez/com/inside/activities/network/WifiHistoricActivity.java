package ez.com.inside.activities.network;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ez.com.inside.R;

public class WifiHistoricActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_historic);
        setTitle("WIFI");
        Intent intent = getIntent();
    }



}
