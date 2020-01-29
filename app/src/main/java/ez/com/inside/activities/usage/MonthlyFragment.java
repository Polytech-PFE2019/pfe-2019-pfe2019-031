package ez.com.inside.activities.usage;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ez.com.inside.R;
import ez.com.inside.activities.helpers.TimeFormatHelper;
import ez.com.inside.business.usagetime.CorrespondanceItem;
import ez.com.inside.business.usagetime.UsageTimeProvider;

import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

import static android.widget.LinearLayout.VERTICAL;


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


        initializeRecyclerView();

        String[] monthName = {"Janvier","Fevrier","Mars", "Avril", "Mai", "Juin", "Juillet",
                "Août", "Septembre", "Octobre", "Novembre", "Décembre"};

        Calendar c = Calendar.getInstance();
        String monthText = monthName[c.get(Calendar.MONTH)];

        TextView month = getView().findViewById(R.id.month);
        month.setText(monthText);

        chart = getView().findViewById(R.id.graph);

        TextView totalTime = getView().findViewById(R.id.timeMonthly);
        totalTime.setText(TimeFormatHelper.minutesToHours(totaltime));

        setRecycleView();
        addAppVisual();
        generateDefaultData();


    }

    private void generateDefaultData() {
        int numColumns = 5;

        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;

        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<>();
            if(usages.size() > i ) {
                long time = usages.get(i).usageTime;
                values.add(new SubcolumnValue(time / 60, -13388315));
                Column column = new Column(values);
                column.setHasLabels(true);
                columns.add(column);
            }
        }


        data = new ColumnChartData(columns);
        chart.setColumnChartData(data);
    }

    private void initializeRecyclerView()
    {

        UsageTimeProvider timeProvider = new UsageTimeProvider(getContext().getApplicationContext());
        Calendar c = Calendar.getInstance();
        try {
            Pair<List<AppUsage>, Integer> result = timeProvider.setAdapterListForWeek(c.get(Calendar.DAY_OF_MONTH));
            usages = result.first;
            totaltime = result.second;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = getView().findViewById(R.id.recycleViewUsage);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new DashboardAdapter(usages, (int) totaltime);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new ItemDecoration());
    }

    private void addAppVisual(){
        if(usages.get(0) != null)
            setIcon((ImageView) getView().findViewById(R.id.app1), usages.get(0).packageName);
        if(usages.size() > 1)
            setIcon((ImageView) getView().findViewById(R.id.app2), usages.get(1).packageName);
        if(usages.size() > 2)
            setIcon((ImageView) getView().findViewById(R.id.app3), usages.get(2).packageName);
        if(usages.size() > 3)
            setIcon((ImageView) getView().findViewById(R.id.app4), usages.get(3).packageName);
        if(usages.size() > 4)
            setIcon((ImageView) getView().findViewById(R.id.app5), usages.get(4).packageName);

    }

    private void setIcon(ImageView view, String packageName){
        try {
            view.setImageDrawable(getContext().getPackageManager().getApplicationIcon(packageName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
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



}
