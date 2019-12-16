package ez.com.inside.activities;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ez.com.inside.R;
import ez.com.inside.activities.helpers.TimeFormatHelper;
import ez.com.inside.activities.network.NetworkActivity;
import ez.com.inside.activities.permissions.PermissionsActivity;
import ez.com.inside.activities.settings.SettingsActivity;
import ez.com.inside.activities.usage.AppUsage;
import ez.com.inside.activities.usage.DashboardAdapter;
import ez.com.inside.activities.usage.GraphMode;
import ez.com.inside.activities.usage.GraphTimeActivity;
import ez.com.inside.activities.usage.ItemDecoration;
import ez.com.inside.activities.usage.OnClickListenerTransition;
import ez.com.inside.activities.usage.UsageActivity;
import ez.com.inside.activities.usage.UsageAdapter;
import ez.com.inside.business.helpers.PackagesSingleton;
import ez.com.inside.business.usagerate.UsageRateProviderImpl;
import ez.com.inside.business.usagetime.UsageTimeProvider;
import ez.com.inside.dialogs.AuthorisationDialog;

import static ez.com.inside.activities.usage.UsageActivity.EXTRA_APPNAME;
import static ez.com.inside.activities.usage.UsageActivity.EXTRA_APPPKGNAME;
import static ez.com.inside.activities.usage.UsageActivity.EXTRA_GRAPHMODE;

public class StartActivity extends AppCompatActivity
{
    private static final int REQUEST_PERMISSION_RESPONSE = 0;
    private List<AppUsage> usages = new ArrayList<>();
    private int currentDay;

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

        Calendar c = Calendar.getInstance();
        TextView dashDate = findViewById(R.id.dashDate);
        TextView dashDate2 = findViewById(R.id.dashDate2);
        TextView dashDate3 = findViewById(R.id.dashDate3);

        String[]monthName={"Janvier","Fevrier","Mars", "Avril", "Mai", "Juin", "Juillet",
                "Août", "Septembre", "Octobre", "Novembre", "Decembre"};
        String text = c.get(Calendar.DAY_OF_MONTH) + " " + monthName[c.get(Calendar.MONTH)];
        dashDate.setText(text);
        dashDate2.setText(text);
        dashDate3.setText(text);

        initializeRecyclerView();
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
        startActivity(new Intent(this,UsageActivity.class));
    }

    public void onClickNetworkActivity(View v){
        startActivity(new Intent(this, NetworkActivity.class));
    }

    private int generateUsages(int nbDays) throws PackageManager.NameNotFoundException
    {
        UsageRateProviderImpl rateProvider = new UsageRateProviderImpl(getApplicationContext());
        Map<String, Double> mapRates = rateProvider.allUsageRates(nbDays);

        UsageTimeProvider timeProvider = new UsageTimeProvider(getApplicationContext());
        Map<String, Long> mapTimes = timeProvider.getUsageTime(nbDays);

        PackagesSingleton singleton = PackagesSingleton.getInstance(getApplicationContext().getPackageManager());

        int time = 0;

        for(Map.Entry<String, Double> entry : mapRates.entrySet())
        {
            if(entry.getValue() < 0.1)
                continue;

            String packageName = entry.getKey();

            AppUsage appUsage = new AppUsage(singleton.packageToAppName(packageName));
            appUsage.packageName = packageName;
            appUsage.usageRate = entry.getValue();

            appUsage.usageTime = mapTimes.get(packageName);

            appUsage.icon = getApplicationContext().getPackageManager().getApplicationIcon(packageName);
            time += appUsage.usageTime;
            usages.add(appUsage);
        }

        Collections.sort(usages, new Comparator<AppUsage>() {
            @Override
            public int compare(AppUsage appUsage, AppUsage t1) {
                if(appUsage.usageRate < t1.usageRate)
                    return 1;
                if(appUsage.usageRate > t1.usageRate)
                    return -1;
                return 0;
            }
        });

        return time;
    }

    private void initializeRecyclerView()
    {
        Calendar c = Calendar.getInstance();
        currentDay  = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (currentDay == 0)
            currentDay = 7;

        int time = 0;
        try {
            time = generateUsages(currentDay);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        usages = usages.subList(0,3);

        TextView timeTotal = findViewById(R.id.dashtotaltime);
        timeTotal.setText(TimeFormatHelper.minutesToHours(time));


        RecyclerView recyclerView = findViewById(R.id.list_usage);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new DashboardAdapter(usages, time);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new ItemDecoration());
    }
}
