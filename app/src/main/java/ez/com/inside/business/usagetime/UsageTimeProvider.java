package ez.com.inside.business.usagetime;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ez.com.inside.business.helpers.PackagesSingleton;

/**
 * Created by Monz on 21/11/2017.
 *
 * Build the apps usage time on the device.
 */
public class UsageTimeProvider {

    private UsageStatsManager manager;
    private PackageManager pm;

    /**
     * Construct a UsageTimeProvider with a context.
     * @param context
     */
    public UsageTimeProvider(Context context){
        this.manager=(UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
        this.pm = context.getPackageManager();
    }

    /**
     * Return the UsageStatManager of the class
     * @return
     */
    public UsageStatsManager getManager() {
        return manager;
    }

    /**
     * Set the UsageStatManager of the class
     * @param manager
     */
    public void setManager(UsageStatsManager manager) {
        this.manager = manager;
    }

    /**
     * Return a Map of the usage in minute of each packages used in the last days.
     * If days is not positive, return an empty map.
     * @param days
     * @return
     */
    public Map<String, Long> getUsageTime(int days)
    {
        Map<String, Long> result = new HashMap<String, Long>();
        if(days<0){
            return result;
        }

        Calendar now = Calendar.getInstance();
        Calendar last = Calendar.getInstance();
        last.set(Calendar.HOUR_OF_DAY, 0);
        last.set(Calendar.MINUTE, 0);
        last.set(Calendar.SECOND, 0);
        last.set(Calendar.MILLISECOND, 0);
        last.add(Calendar.DATE, -days);

       // Log.d("lassst ", last.toString());

        Map<String, UsageStats> stats = manager.queryAndAggregateUsageStats(last.getTimeInMillis(),now.getTimeInMillis());

        PackagesSingleton helper = PackagesSingleton.getInstance(pm);

        for(Map.Entry<String, UsageStats> entry : stats.entrySet())
        {
            // If the package is not a launcher package, exclude it from the return result.
            if(!helper.isLauncherPackage(entry.getKey()))
                continue;

            long time = entry.getValue().getTotalTimeInForeground() / 60000;
            result.put(entry.getKey(),time);
        }

        return result;
    }


    /**
     * @return the number of minutes spent in foreground of an app between c1 and c2.
     */
    public long getAppUsageTime(String packageName, Calendar c1, Calendar c2)
    {
        Map<String, UsageStats> stats = manager.queryAndAggregateUsageStats(c1.getTimeInMillis(), c2.getTimeInMillis());

        if(!stats.containsKey(packageName))
            return 0;

        return stats.get(packageName).getTotalTimeInForeground() / 60000;
    }


    public Map<String, Long> getUsageTimeForInterval(Calendar c1, Calendar c2)
    {
        Map<String, UsageStats> stats = manager.queryAndAggregateUsageStats(c1.getTimeInMillis(), c2.getTimeInMillis());
        Map<String, Long> result = new HashMap<>();

        PackagesSingleton singleton = PackagesSingleton.getInstance(pm);

        for(Map.Entry<String,UsageStats> entry : stats.entrySet()) {
            if(singleton.isLauncherPackage(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue().getTotalTimeInForeground());
            }
        }
        return result;
    }

}
