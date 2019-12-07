package ez.com.inside.activities.usage;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import ez.com.inside.R;
import ez.com.inside.business.helpers.PackagesSingleton;
import ez.com.inside.business.usagerate.UsageRateProviderImpl;
import ez.com.inside.business.usagetime.UsageTimeProvider;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

import static ez.com.inside.activities.usage.UsageActivity.COLOR_PANEL;
import static ez.com.inside.activities.usage.UsageActivity.COLOR_OTHER;

/**
 * Created by Charly on 07/12/2017.
 */

public class DailyFragment extends Fragment
{
    private PieChartView chart;
    private PieChartData data;

    private List<AppUsage> usages;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_usage, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        usages = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        try
        {
            generateUsages(1);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        generateColors();

        initializePieChart();

        initializeRecyclerView();
    }

    private void initializePieChart()
    {
        chart = getView().findViewById(R.id.AppRate_chart);

        generateChartData();
        chart.setPieChartData(data);
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

    private void generateChartData()
    {
        List<SliceValue> values = new ArrayList<>();

        int i = 0;

        SliceValue other = null;

        for(AppUsage usage : usages)
        {
            if(usage.usageRate > 2)
            {
                SliceValue sliceValue = new SliceValue((float) usage.usageRate, usage.color[0]);
                sliceValue.setLabel(usage.appName);
                values.add(sliceValue);
            }
            else
            {
                if(other == null)
                {
                    other = new SliceValue();
                    other.setColor(usage.color[0]);
                    other.setLabel("Autres");
                }
                other.setValue(other.getValue() + (float) usage.usageRate);
            }
        }
        if(other != null)
        {
            values.add(other);
        }

        data = new PieChartData(values);
        data.setSlicesSpacing(2);

        data.setHasCenterCircle(true);
        data.setCenterCircleColor(Color.WHITE);
        data.setCenterCircleScale((float)0.5);

        data.setHasLabels(true);
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
                // Nothing to do.
            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new ItemDecoration());
    }
}
