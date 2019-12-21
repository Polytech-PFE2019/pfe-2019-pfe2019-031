package ez.com.inside.business.usagetime;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.util.Pair;
import ez.com.inside.activities.usage.AppUsage;
import ez.com.inside.business.helpers.PackagesSingleton;

/**
 * Created by Monz on 21/11/2017.
 *
 * Build the apps usage time on the device.
 */
public class UsageTimeProvider {

    private UsageStatsManager manager;
    private PackageManager pm;
    private Context context;

    /**
     * Construct a UsageTimeProvider with a context.
     * @param context
     */
    public UsageTimeProvider(Context context){
        this.manager=(UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
        this.pm = context.getPackageManager();
        this.context = context;
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
     * @param dayOfWeek current day of week
     * @return List of string, package name of used app
     */
    private List<String> getAppUsedWeek(int dayOfWeek)
    {

        List<String> appUsed = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        Calendar last = Calendar.getInstance();
        last.set(Calendar.HOUR_OF_DAY, 0);
        last.set(Calendar.MINUTE, 0);
        last.set(Calendar.SECOND, 0);
        last.set(Calendar.MILLISECOND, 0);
        last.add(Calendar.DATE, -dayOfWeek);

        now.set(Calendar.HOUR_OF_DAY, 23);
        now.set(Calendar.MINUTE, 59);
        now.set(Calendar.SECOND, 59);
        now.set(Calendar.MILLISECOND, 59);
        now.add(Calendar.DATE, 0);

        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Map<String, UsageStats> stats = manager.queryAndAggregateUsageStats(last.getTimeInMillis(),now.getTimeInMillis());

        PackagesSingleton helper = PackagesSingleton.getInstance(context.getPackageManager());

        for(Map.Entry<String, UsageStats> entry : stats.entrySet())
        {
            // If the package is not a launcher package, exclude it from the return result.
            if(!helper.isLauncherPackage(entry.getKey()))
                continue;
            appUsed.add(entry.getKey());
        }
        return appUsed;
    }


    /**
     *
     * @param dayOfWeek current day of the week
     * @return a list of AppUsage for a week
     * @return a list of AppUsage for a week
     * @throws PackageManager.NameNotFoundException
     */
    public Pair<List<AppUsage>, Integer> setAdapterListForWeek(int dayOfWeek) throws PackageManager.NameNotFoundException {

        List<AppUsage> usages = new ArrayList<>();
        UsageTimeProvider timeProvider = new UsageTimeProvider(context);
        PackagesSingleton singleton = PackagesSingleton.getInstance(context.getPackageManager());
        List<String> packageNames = timeProvider.getAppUsedWeek(dayOfWeek);

        int totalTime= 0;
        for(int i= 0; i< packageNames.size(); i++) {
            AppUsage appUsage = new AppUsage(singleton.packageToAppName(packageNames.get(i)));
            appUsage.packageName = packageNames.get(i);
            for (int day = dayOfWeek - 1; day >= 0; day--) {
                Calendar beginning = Calendar.getInstance();
                beginning.set(Calendar.HOUR_OF_DAY, 0);
                beginning.set(Calendar.MINUTE, 0);
                beginning.set(Calendar.SECOND, 0);
                beginning.set(Calendar.MILLISECOND, 0);
                beginning.add(Calendar.DATE, -day);

                Calendar end = Calendar.getInstance();
                end.set(Calendar.HOUR_OF_DAY, 23);
                end.set(Calendar.MINUTE, 59);
                end.set(Calendar.SECOND, 59);
                end.set(Calendar.MILLISECOND, 59);
                end.add(Calendar.DATE, -day);

                appUsage.usageTime += timeProvider.getAppUsageTime(packageNames.get(i), beginning, end);

            }

            if(appUsage.usageTime > 0.1) {
                totalTime +=  appUsage.usageTime;
                usages.add(appUsage);
            }
        }

        Collections.sort(usages, new Comparator<AppUsage>() {
            @Override
            public int compare(AppUsage appUsage, AppUsage t1) {
                if(appUsage.usageTime < t1.usageTime)
                    return 1;
                if(appUsage.usageTime > t1.usageTime)
                    return -1;
                return 0;
            }
        });

        Pair<List<AppUsage>, Integer> end = new Pair<>(usages, totalTime);
        return end;
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
