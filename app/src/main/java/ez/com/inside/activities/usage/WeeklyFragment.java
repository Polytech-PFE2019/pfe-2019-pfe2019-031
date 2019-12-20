package ez.com.inside.activities.usage;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ez.com.inside.R;
import ez.com.inside.business.helpers.PackagesSingleton;
import ez.com.inside.business.usagerate.UsageRateProviderImpl;
import ez.com.inside.business.usagetime.UsageTimeProvider;
import ez.com.inside.business.usagetime.Utils;


import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

import static ez.com.inside.activities.usage.UsageActivity.EXTRA_APPNAME;
import static ez.com.inside.activities.usage.UsageActivity.EXTRA_APPPKGNAME;
import static ez.com.inside.activities.usage.UsageActivity.EXTRA_GRAPHMODE;

/**
 * Created by Charly on 08/12/2017.
 */

public class WeeklyFragment extends Fragment
{
    private List<AppUsage> usages;
    private long[] times;

    private ColumnChartView chart;
    private ColumnChartData data;
    private Utils utils = new Utils();
    private int currentDay = 1;
    int sum = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        usages = new ArrayList<>();
        View rootView = inflater.inflate(R.layout.fragment_usage, container, false);
        chart = rootView.findViewById(R.id.AppRate_chart);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        Calendar c = Calendar.getInstance(Locale.FRANCE);
        //Moins un pour que la semaine commence le lundi
        currentDay  = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (currentDay == 0)
            currentDay = 7;

        try {
            generateUsages(currentDay);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        generateDefaultData();

        TextView average = getView().findViewById(R.id.moyenne_hebdo);
        average.setText("Moyenne hebdomadaire " + (sum/60)/currentDay + "h");

        initializeRecyclerView();
    }



    private void generateDefaultData() {
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;

        times = new long[currentDay];
        int nbDay = 0;
        for(int i = currentDay - 1; i >= 0; i--)
        {
            if(nbDay == 1){
                times[i] = getUsageTime(nbDay);
            }
            else {
                times[i] = getUsageTime(nbDay);
            }
            nbDay ++;
        }

        //Generate first part of graph => day of week until today
        for (int i = 0; i < currentDay; ++i) {
            values = new ArrayList<>();
            values.add(new SubcolumnValue( times[i]/60, -13388315));
            Column column = new Column(values);
            column.setHasLabels(true);
            columns.add(column);
        }

        //Generate second part of graph => day until sunday starting tomorrow
        for(int i = currentDay + 1 ; i <= 7; i++){
            values = new ArrayList<>();
            values.add(new SubcolumnValue(0, -13388315));
            Column column = new Column(values);
            column.setHasLabels(true);
            columns.add(column);
        }


        List<AxisValue> date = new ArrayList<>();
        for(int i= 1; i <= 7; i++){
            date.add(new AxisValue(i-1).setLabel(utils.getDayName(i)));
        }
        data = new ColumnChartData(columns);
        Axis axisX = new Axis();
        axisX.setMaxLabelChars(4);
        axisX.setValues(date);
        data.setAxisXBottom(axisX);
        chart.setColumnChartData(data);
    }


    public long getUsageTime(int beginAt)
    {
        Calendar now = Calendar.getInstance();
        Calendar last = Calendar.getInstance();
        last.set(Calendar.HOUR_OF_DAY, 0);
        last.set(Calendar.MINUTE, 0);
        last.set(Calendar.SECOND, 0);
        last.set(Calendar.MILLISECOND, 0);
        last.add(Calendar.DATE, -beginAt);

        now.set(Calendar.HOUR_OF_DAY, 23);
        now.set(Calendar.MINUTE, 59);
        now.set(Calendar.SECOND, 59);
        now.set(Calendar.MILLISECOND, 59);
        now.add(Calendar.DATE, -beginAt);

        UsageStatsManager manager=(UsageStatsManager)getContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Map<String, UsageStats> stats = manager.queryAndAggregateUsageStats(last.getTimeInMillis(),now.getTimeInMillis());

        PackagesSingleton helper = PackagesSingleton.getInstance( getContext().getPackageManager());

        long timeSpend = 0;
        for(Map.Entry<String, UsageStats> entry : stats.entrySet())
        {
            // If the package is not a launcher package, exclude it from the return result.
            if(!helper.isLauncherPackage(entry.getKey()))
                continue;

            Log.d("data", entry.getKey() + " " + entry.getValue().getTotalTimeInForeground() );
            timeSpend += entry.getValue().getTotalTimeInForeground() / 60000;

        }

        return timeSpend;

    }



    private void generateUsages(int nbDays) throws PackageManager.NameNotFoundException
    {
        UsageRateProviderImpl rateProvider = new UsageRateProviderImpl(getContext());
        Map<String, Double> mapRates = rateProvider.allUsageRates(nbDays);

        UsageTimeProvider timeProvider = new UsageTimeProvider(getContext());
        Map<String, Long> mapTimes = timeProvider.getUsageTime(nbDays);

        PackagesSingleton singleton = PackagesSingleton.getInstance(getContext().getPackageManager());

        for(Map.Entry<String, Double> entry : mapRates.entrySet())
        {
            if(entry.getValue() < 0.1)
                continue;

            String packageName = entry.getKey();

            AppUsage appUsage = new AppUsage(singleton.packageToAppName(packageName));
            appUsage.packageName = packageName;
            appUsage.usageRate = entry.getValue();

            appUsage.usageTime = mapTimes.get(packageName);

            sum += appUsage.usageTime;

            appUsage.icon = getContext().getPackageManager().getApplicationIcon(packageName);
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
    }

    private void initializeRecyclerView()
    {
        RecyclerView recyclerView = getView().findViewById(R.id.list_usage);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new UsageAdapter(usages, sum, new OnClickListenerTransition() {
            @Override
            public void onClick(TextView appNameView, ImageView appIconView, int position) {
                Intent intent = new Intent(getContext(), GraphTimeActivity.class);
                intent.putExtra(EXTRA_APPNAME, usages.get(position).appName);
                intent.putExtra(EXTRA_APPPKGNAME, usages.get(position).packageName);
                intent.putExtra("TOTALTIME", sum);

                View sharedViewAppName = appNameView;
                String transitionNameAppName = getString(R.string.transition_appName);
                Pair<View, String> sharedElementsAppName = Pair.create(sharedViewAppName, transitionNameAppName);


                View sharedViewAppIcon = appIconView;
                String transitionNameAppIcon = getString(R.string.transition_appIcon);
                Pair<View, String> sharedElementsAppIcon = Pair.create(sharedViewAppIcon, transitionNameAppIcon);

                ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        sharedElementsAppName, sharedElementsAppIcon);
                startActivity(intent, transitionActivityOptions.toBundle());
            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new ItemDecoration());
    }
}
