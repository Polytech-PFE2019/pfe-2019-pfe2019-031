package ez.com.inside.business.usagerate;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ez.com.inside.business.helpers.PackagesSingleton;

/**
 * Created by Charly on 21/11/2017.
 */

public class UsageRateProviderImpl implements UsageRateProvider
{
    private PackageManager pm;
    private UsageStatsManager manager;

    public UsageRateProviderImpl(Context context)
    {
        this.manager=(UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
        this.pm = context.getPackageManager();
    }


    public Map<String, Double> allUsageRates(int days)
    {
        Map<String, Double> result = new HashMap<String, Double>();
        if(days<0){
            return result;
        }

        Calendar now = Calendar.getInstance();
        Calendar last = Calendar.getInstance();
        last.add(Calendar.DATE,-days);

        Map<String, UsageStats> stats = manager.queryAndAggregateUsageStats(last.getTimeInMillis(),now.getTimeInMillis());

        PackagesSingleton helper = PackagesSingleton.getInstance(pm);

        double totalTime = 0.;

        List<String> apps = new ArrayList<>();
        List<Double> times = new ArrayList<>();

        for(Map.Entry<String, UsageStats> entry : stats.entrySet())
        {
            if(!helper.isLauncherPackage(entry.getKey()))
                continue;

            apps.add(entry.getKey());

            double time = entry.getValue().getTotalTimeInForeground() / 60000;
            totalTime += time;
            times.add(time);
        }

        for(int i = 0; i < apps.size(); i++)
        {
            double rate = 100.0 * (times.get(i) / totalTime);
            result.put(apps.get(i), rate);
        }

        return result;
    }
}
