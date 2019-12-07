package ez.com.inside.activities.permissions;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ez.com.inside.business.permission.AppPermissions;
import ez.com.inside.business.permission.PermissionsFinder;

/**
 * Created by Charly on 11/12/2017.
 */

public class AsyncTaskLoadingPermissions extends AsyncTask<Void, Void, List<AppPermissions>>
{
    private PermissionsActivity activity;

    public AsyncTaskLoadingPermissions(PermissionsActivity activity)
    {
        super();
        this.activity = activity;
    }

    @Override
    protected List<AppPermissions> doInBackground(Void... params)
    {
        final PackageManager pm = activity.getPackageManager();

        List<AppPermissions> permissions = PermissionsFinder.getAppsPermissions(pm);

        Collections.sort(permissions, new Comparator<AppPermissions>() {
            @Override
            public int compare(AppPermissions appPermissions, AppPermissions t1)
            {
                int nbGranted1 = Collections.frequency(appPermissions.getPermissionsGroup().values(), true);
                int nbGranted2 = Collections.frequency(t1.getPermissionsGroup().values(), true);

                if(nbGranted1 == nbGranted2)
                    return 0;
                if(nbGranted1 < nbGranted2)
                    return 1;
                return -1;
            }
        });

        return permissions;
    }

    @Override
    protected void onPostExecute(List<AppPermissions> appPermissions)
    {
        if(activity.getIsInForeground())
            activity.launchPermissionListFragment(appPermissions);
    }
}
