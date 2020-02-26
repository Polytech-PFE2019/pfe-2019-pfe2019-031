package ez.com.inside.activities.usage;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ez.com.inside.R;
import ez.com.inside.business.helpers.PackagesSingleton;
import ez.com.inside.business.usagetime.Utils;


import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

import static android.widget.LinearLayout.VERTICAL;
import static ez.com.inside.activities.usage.UsageActivity.EXTRA_APPNAME;
import static ez.com.inside.activities.usage.UsageActivity.EXTRA_APPPKGNAME;
import static ez.com.inside.activities.usage.UsageActivity.TOTALTIME;
import static ez.com.inside.activities.usage.UsageActivity.USAGES;
import ez.com.inside.business.usagetime.UsageTimeProvider;

/**
 * Created by Charly on 08/12/2017.
 */

public class WeeklyFragment extends Fragment
{

    private long[] times;
    private UsageTimeProvider usageTimeProvider;
    private Utils utils = new Utils();
    private int currentDay = 1;
    private ColumnChartView chart;
    private ColumnChartData data;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_usage, container, false);
        chart = rootView.findViewById(R.id.AppRate_chart);
        usageTimeProvider = new UsageTimeProvider(getContext());
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


        generateDefaultData();

        TextView average = getView().findViewById(R.id.moyenne_hebdo);
        average.setText("Moyenne hebdomadaire " + (TOTALTIME/60)/currentDay + "h");

        initializeRecyclerView();
    }



    private void generateDefaultData() {
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;

        times = new long[currentDay];
        int nbDay = 0;
        for(int i = currentDay - 1; i >= 0; i--)
        {
            times[i] = usageTimeProvider.getTotalUsageDay(nbDay);
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



    private void initializeRecyclerView()
    {
        RecyclerView recyclerView = getView().findViewById(R.id.list_usage);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new UsageAdapter(USAGES, TOTALTIME, new OnClickListenerTransition() {
            @Override
            public void onClick(TextView appNameView, ImageView appIconView, int position) {
                Intent intent = new Intent(getContext(), GraphTimeActivity.class);
                intent.putExtra(EXTRA_APPNAME, USAGES.get(position).appName);
                intent.putExtra(EXTRA_APPPKGNAME, USAGES.get(position).packageName);
                intent.putExtra("TOTALTIME", TOTALTIME);

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
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL));

    }


}
