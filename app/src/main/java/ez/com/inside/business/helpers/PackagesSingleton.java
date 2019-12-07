package ez.com.inside.business.helpers;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Charly on 01/12/2017.
 */

public final class PackagesSingleton
{
    private static volatile PackagesSingleton instance = null;

    private PackageManager pm;
    private List<String> launcherPackages = new ArrayList<>();

    private static final String GOOGLE_LAUNCHER = "com.google.android.googlequicksearchbox";

    private PackagesSingleton() {}

    //TODO : See if the reference to pm does not become null when the context of the caller changes.
    private PackagesSingleton(PackageManager pm) { this.pm = pm; }

    /**
     * Builds the list of launcher packages at instantiation-time.
     */
    public final static PackagesSingleton getInstance(PackageManager pm)
    {
        if(PackagesSingleton.instance == null)
        {
            synchronized(PackagesSingleton.class)
            {
                if(PackagesSingleton.instance == null)
                {
                    PackagesSingleton.instance = new PackagesSingleton(pm);
                    PackagesSingleton.instance.buildLauncherPackages();
                }
            }
        }
        return PackagesSingleton.instance;
    }

    private void buildLauncherPackages()
    {
        Intent launcher = new Intent(Intent.ACTION_MAIN,null);
        launcher.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolves = pm.queryIntentActivities(launcher,pm.MATCH_ALL);

        for(ResolveInfo info : resolves){

            launcherPackages.add(info.activityInfo.applicationInfo.packageName);
        }
    }

    public List<String> getLauncherPackages() {return launcherPackages;}

    public boolean isLauncherPackage(String packageName)
    {
        if(packageName.equals(GOOGLE_LAUNCHER))
            return false;

        return launcherPackages.contains(packageName);
    }

    @NonNull
    public String packageToAppName(String packageName) throws PackageManager.NameNotFoundException
    {
        ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
        return pm.getApplicationLabel(info).toString();
    }
}
