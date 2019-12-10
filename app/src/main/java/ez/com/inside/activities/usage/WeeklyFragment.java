package ez.com.inside.activities.usage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import ez.com.inside.R;
import ez.com.inside.business.helpers.PackagesSingleton;
import ez.com.inside.business.usagerate.UsageRateProviderImpl;
import ez.com.inside.business.usagetime.UsageTimeProvider;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

import static ez.com.inside.activities.usage.UsageActivity.COLOR_OTHER;
import static ez.com.inside.activities.usage.UsageActivity.COLOR_PANEL;
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
        try {
            generateUsages(8);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        generateColors();
        generateDefaultData();

        initializeRecyclerView();
    }

    private void generateColors()
    {
        int i = 0;
        for(int j = 0; j < usages.size(); j++)
        {
            if(usages.get(j).usageRate > 2)
            {
                usages.get(j).color = COLOR_PANEL[i++ % COLOR_PANEL.length];
            }
            else
            {
                usages.get(j).color = COLOR_OTHER;
            }
        }
    }

    private void generateDefaultData() {
        int numSubcolumns = 1;
        int numColumns = 7;

        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;

        times = new long[7];

        UsageTimeProvider provider = new UsageTimeProvider(getContext());
        for(AppUsage usage : usages)
        {
            for(int i = times.length - 1, index = 0; i >= 0; i--, index++)
            {
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DATE, -i - 1);
                Calendar c2 = Calendar.getInstance();
                c2.add(Calendar.DATE, -i);
                times[index] += provider.getAppUsageTime(usage.packageName, c1, c2);
            }
        }


        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<>();
            for (int j = 0; j < numSubcolumns; ++j) {
                //Create bar with the current value
                values.add(new SubcolumnValue( times[i]/60, ChartUtils.pickColor()));
            }
            Column column = new Column(values);
            column.setHasLabels(true);
            columns.add(column);
        }

        Calendar calendar = Calendar.getInstance();
        List<Integer> days = setDayOrder(calendar.get(Calendar.DAY_OF_WEEK));
        List<AxisValue> date = new ArrayList<>();
        for(int i= 0; i < days.size(); i++){
            date.add(new AxisValue(i).setLabel(getDayName(days.get(i))));
        }

        data = new ColumnChartData(columns);
        Axis axisX = new Axis();
        axisX.setMaxLabelChars(4);
        axisX.setValues(date);
        data.setAxisXBottom(axisX);
        chart.setColumnChartData(data);

    }

    private List<Integer> setDayOrder(int firstDay){
        List<Integer> values = new ArrayList<>();
        int temp = 1;
        Boolean restart = false;
        for(int index = 1; index < 7; index ++){
            if(firstDay+index == 7){
                values.add(firstDay+index);
                restart = true;

            } else if (firstDay == 7) {
                restart = true;
            }


            if(restart){
                values.add(temp);
                temp++;
            }
            else{
                values.add(firstDay+index);
            }
        }

        if(values.size() == 6){
            values.add(7);
        }
        return values;
    }

    private String getDayName(int value){
        switch (value){
            case 1:
                return "Dim.";
            case 2:
                return "Lun.";
            case 3:
                return "Mar.";
            case 4:
                return "Mer.";
            case 5:
                return "Jeu.";
            case 6:
                return "Ven.";
            case 7:
                return "Sam.";

        }
        return "";
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

        RecyclerView.Adapter adapter = new UsageAdapter(usages, new OnClickListenerTransition() {
            @Override
            public void onClick(TextView appNameView, ImageView appIconView, int position) {
                Intent intent = new Intent(getContext(), GraphTimeActivity.class);
                intent.putExtra(EXTRA_APPNAME, usages.get(position).appName);
                intent.putExtra(EXTRA_APPPKGNAME, usages.get(position).packageName);
                intent.putExtra(EXTRA_GRAPHMODE, GraphMode.WEEKLY);

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
