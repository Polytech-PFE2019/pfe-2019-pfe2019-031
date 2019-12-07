package ez.com.inside.activities;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ez.com.inside.R;
import ez.com.inside.activities.network.NetworkActivity;
import ez.com.inside.activities.permissions.PermissionsActivity;
import ez.com.inside.activities.settings.SettingsActivity;
import ez.com.inside.activities.usage.UsageActivity;
import ez.com.inside.dialogs.AuthorisationDialog;

public class StartActivity extends AppCompatActivity
{
    private static final int REQUEST_PERMISSION_RESPONSE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Setting the default values. Called only once in the lifecycle of the app, hence the "false".
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        if(!hasUsageStatsPermission())
        {
            DialogFragment fragment = new AuthorisationDialog();
            fragment.show(getSupportFragmentManager(), "autorisation");
            return;
        }
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
}
