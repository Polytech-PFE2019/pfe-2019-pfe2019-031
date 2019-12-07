package ez.com.inside.activities.permissions;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import ez.com.inside.R;
import ez.com.inside.business.permission.AppPermissions;
import ez.com.inside.business.permission.PermissionGroupInfo;
import ez.com.inside.business.permission.PermissionsFinder;

public class PermissionsActivity extends AppCompatActivity
{
    public static final String EXTRA_APPPERMISSIONS = "ez.com.inside.extraapppermissions";

    private boolean isInForeground = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        setTitle(getString(R.string.card_apps_autho_title));

        /**
         * If this activity was called from PermissionsGroupActivity.
         */
        Bundle b = getIntent().getExtras();
        if(b!=null)
        {
            PermissionGroupInfo info = b.getParcelable("info");
            if(info!=null)
            {
                setTitle(PermissionsFinder.getPermissionGroupLabel(info.getName(),this));
                info.sortAppPermissions();
                launchPermissionListFragment(info.getAppsList());
                return;
            }
        }

        /**
         * Otherwise.
         */
        FragmentLoading fragmentLoading = new FragmentLoading();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragmentLoading).commit();

        new AsyncTaskLoadingPermissions(this).execute(null, null);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        isInForeground = false;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        isInForeground = true;
    }

    public void launchPermissionListFragment(List<AppPermissions> permissions)
    {
        FragmentListPermissions fragmentListPermissions = new FragmentListPermissions();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_APPPERMISSIONS, (ArrayList) permissions);
        fragmentListPermissions.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(android.R.id.content, fragmentListPermissions).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.permissions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_go_permission_group :
            {
                Intent intent = new Intent(this, PermissionsGroupActivity.class);
                startActivity(intent);
                return true;
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public boolean getIsInForeground() {return isInForeground;}
}
