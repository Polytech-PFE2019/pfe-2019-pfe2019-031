package ez.com.inside.activities.usage;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ez.com.inside.R;
import ez.com.inside.activities.helpers.TimeFormatHelper;
import ez.com.inside.business.helpers.PackagesSingleton;
import ez.com.inside.business.usagerate.UsageRateProviderImpl;
import ez.com.inside.business.usagetime.CorrespondanceItem;
import ez.com.inside.business.usagetime.UsageTimeProvider;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;


/**
 * Created by Charly on 08/12/2017.
 */

public class MonthlyFragment extends Fragment {

    private ColumnChartView chart;
    private ColumnChartData data;
    private long totaltime = 0;
    private List<AppUsage> usages;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_monthly_usage, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usages = new ArrayList<>();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        String[]monthName={"Janvier","Fevrier","Mars", "Avril", "Mai", "Juin", "Juillet",
                "Août", "Septembre", "Octobre", "Novembre", "Decembre"};
        Calendar c = Calendar.getInstance();
        String monthText = monthName[c.get(Calendar.MONTH)];

        TextView month = getView().findViewById(R.id.month);
        month.setText(monthText);

        chart = getView().findViewById(R.id.graph);

        try{
            generateUsages(c.get(Calendar.DAY_OF_MONTH));
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView totalTime = getView().findViewById(R.id.timeMonthly);
        totalTime.setText(TimeFormatHelper.minutesToHours(totaltime));


        setRecycleView();
        addAppVisual();
        generateDefaultData();

    }

    private void generateDefaultData() {
        int numColumns = usages.size();

        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        List<AxisValue> date = new ArrayList<>();

        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<>();
            long time = usages.get(i).usageTime;
            values.add(new SubcolumnValue(time / 60, -13388315));
            date.add(new AxisValue(i).setLabel(usages.get(i).appName));
            Column column = new Column(values);
            column.setHasLabels(true);
            columns.add(column);
        }

        data = new ColumnChartData(columns);
        Axis axisX = new Axis();
        axisX.setMaxLabelChars(10);
        axisX.setValues(date);
        data.setAxisXBottom(axisX);
        chart.setColumnChartData(data);
    }

    private void addAppVisual(){
        if(usages.get(0) != null){
            ImageView app1 = getView().findViewById(R.id.app1);
            app1.setImageDrawable(usages.get(0).icon);
        }
        if(usages.size() > 1){
            ImageView app2 = getView().findViewById(R.id.app2);
            app2.setImageDrawable(usages.get(1).icon);
        }
        if(usages.size() > 2){
            ImageView app3  = getView().findViewById(R.id.app3);
            app3.setImageDrawable(usages.get(2).icon);
        }
        if(usages.size() > 3){
            ImageView app4 = getView().findViewById(R.id.app4);
            app4.setImageDrawable(usages.get(3).icon);
        }
        if(usages.size() > 4){
            ImageView app5 = getView().findViewById(R.id.app5);
            app5.setImageDrawable(usages.get(4).icon);
        }
    }

    private void setRecycleView(){
        List<CorrespondanceItem> listCI = new ArrayList<>();

        listCI.add(new CorrespondanceItem(getResources().getDrawable(R.drawable.movies), totaltime/120, "films visionnés"));
        listCI.add(new CorrespondanceItem(getResources().getDrawable(R.drawable.fruit), totaltime/30, "pauses repas"));
        listCI.add(new CorrespondanceItem(getResources().getDrawable(R.drawable.coffee), totaltime/10, "pauses cafés"));
        listCI.add(new CorrespondanceItem(getResources().getDrawable(R.drawable.series), totaltime/45, "épisodes de séries"));
        listCI.add(new CorrespondanceItem(getResources().getDrawable(R.drawable.worker), totaltime/8, "journées travaillées"));

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        CorrespondanceAdapter correspondanceAdapter = new CorrespondanceAdapter(listCI);
        recyclerView.setAdapter(correspondanceAdapter);

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

            totaltime += appUsage.usageTime;

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


}
