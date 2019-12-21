package ez.com.inside.activities;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ez.com.inside.R;
import ez.com.inside.activities.helpers.TimeFormatHelper;
import ez.com.inside.activities.network.NetworkActivity;
import ez.com.inside.activities.permissions.PermissionsActivity;
import ez.com.inside.activities.settings.SettingsActivity;
import ez.com.inside.activities.usage.AppUsage;
import ez.com.inside.activities.usage.DashboardAdapter;
import ez.com.inside.activities.usage.ItemDecoration;
import ez.com.inside.activities.usage.UsageActivity;
import ez.com.inside.business.network.Utils;
import ez.com.inside.business.permission.PermissionGroupInfo;
import ez.com.inside.business.permission.PermissionsFinder;
import ez.com.inside.business.usagetime.UsageTimeProvider;
import ez.com.inside.dialogs.AuthorisationDialog;


public class StartActivity extends AppCompatActivity
{
    private static final int REQUEST_PERMISSION_RESPONSE = 0;
    private List<AppUsage> usages = new ArrayList<>();
    private int currentDay;
    int time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        // Setting the default values. Called only once in the lifecycle of the app, hence the "false".
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        if(!hasUsageStatsPermission())
        {
            DialogFragment fragment = new AuthorisationDialog();
            fragment.show(getSupportFragmentManager(), "autorisation");
            return;
        }
        initializeRecyclerView();

        Calendar c = Calendar.getInstance();
        TextView dashDate = findViewById(R.id.dashDate);
        TextView dashDate2 = findViewById(R.id.dashDate2);
        TextView dashDate3 = findViewById(R.id.dashDate3);

        String[]monthName={"Janvier","Fevrier","Mars", "Avril", "Mai", "Juin", "Juillet",
                "Août", "Septembre", "Octobre", "Novembre", "Decembre"};
        String text = c.get(Calendar.DAY_OF_MONTH) + " " + monthName[c.get(Calendar.MONTH)];
        dashDate.setText("Aujourd\'hui," + text);
        dashDate2.setText("Aujourd\'hui," + text);
        dashDate3.setText("Aujourd\'hui," + text);

        setPermission();
        getWifiLevel();

    }

    private boolean hasUsageStatsPermission()
    {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);

        int mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public void requestUsageStatsPermission()
    {
        startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                REQUEST_PERMISSION_RESPONSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode)
        {
            case REQUEST_PERMISSION_RESPONSE :
            {
                /* Go to ErrorAuthorizationActivity if the permission has not been granted.
                 * And kill this activity so the user cannot just press Back and continue.
                 */
                if(!hasUsageStatsPermission())
                {
                    startActivity(new Intent(this, ErrorAuthorizationActivity.class));
                    finish();
                }
            }
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_settings :
            {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void onClickApplicationsAuthorizationsActivity(View v){
        startActivity(new Intent(this,PermissionsActivity.class));
    }

    public void onClickApplicationsUsageActivity(View v){
        Intent intent = new Intent(this, UsageActivity.class);
        intent.putExtra("AppUsages", (Serializable) usages);
        intent.putExtra("TotalTime", time);
        startActivity(intent);
    }

    public void onClickNetworkActivity(View v){
        startActivity(new Intent(this, NetworkActivity.class));
    }

    private void setPermission() {
        RecyclerView recyclerView = findViewById(R.id.recycleView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<PermissionGroupInfo> permissionGroupList = PermissionsFinder.getPermissionsGroupsInfo(this.getPackageManager());
        RecyclerView.Adapter adapter = new DashboardPermissionAdapter(this, permissionGroupList);
        recyclerView.setAdapter(adapter);
    }

    private void getWifiLevel()
    {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int linkSpeed = wifiManager.getConnectionInfo().getRssi();
        int levelCurrentW = WifiManager.calculateSignalLevel(linkSpeed, 5);
        Utils utils = new Utils();
        TextView type = findViewById(R.id.WifiType);
        type.setText("Wifi");
        TextView name = findViewById(R.id.WifiName);
        name.setText(wifiManager.getConnectionInfo().getSSID());
        utils.setWifiText(levelCurrentW, (TextView) findViewById(R.id.WifiLevel));
    }


    private void initializeRecyclerView()
    {
        Calendar c = Calendar.getInstance();
        currentDay  = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (currentDay == 0)
            currentDay = 7;

        UsageTimeProvider timeProvider = new UsageTimeProvider(getApplicationContext());

        try {
            Pair<List<AppUsage>, Integer> end = timeProvider.setAdapterListForWeek(currentDay);
            usages = end.first;
            time = end.second;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView timeTotal = findViewById(R.id.dashtotaltime);
        timeTotal.setText(TimeFormatHelper.minutesToHours(time));


        RecyclerView recyclerView = findViewById(R.id.list_usage);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new DashboardAdapter(usages.subList(0,3), time);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new ItemDecoration());
    }
}
