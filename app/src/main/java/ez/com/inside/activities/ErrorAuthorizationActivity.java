package ez.com.inside.activities;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ez.com.inside.R;

public class ErrorAuthorizationActivity extends AppCompatActivity
{
    private static final int REQUEST_PERMISSION_RESPONSE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_authorization);
    }

    /**
     * OnClick() method.
     */
    public void requestUsageStatsPermission(View v)
    {
        startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                REQUEST_PERMISSION_RESPONSE);
    }

    private boolean hasUsageStatsPermission()
    {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);

        int mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode)
        {
            case REQUEST_PERMISSION_RESPONSE :
            {
                if(hasUsageStatsPermission())
                {
                    startActivity(new Intent(this, StartActivity.class));
                    finish();
                }
            }
        }
    }
}
