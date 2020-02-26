package ez.com.inside.activities.usage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ez.com.inside.R;
import ez.com.inside.activities.helpers.TimeFormatHelper;
import ez.com.inside.business.usagetime.UsageTimeProvider;
import ez.com.inside.business.usagetime.Utils;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class GraphTimeActivity extends AppCompatActivity
{
    private String appName;
    private String packageName;
    private Drawable icon;

    private ColumnChartView chart;
    private ColumnChartData data;


    private long[] times;
    private int totalTime;

    private Utils utils = new Utils();

    private PieChartView pieChart;
    private PieChartData pieData;
    private int currentDay;
    private static DecimalFormat df = new DecimalFormat("0.0");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_time);
        Intent intent = getIntent();
        appName = intent.getStringExtra(UsageActivity.EXTRA_APPNAME);
        setTitle(appName);
        packageName = intent.getStringExtra(UsageActivity.EXTRA_APPPKGNAME);
        totalTime = intent.getIntExtra("TOTALTIME", 0);

        Calendar c = Calendar.getInstance(Locale.FRANCE);
        //Moins un pour que la semaine commence le lundi
        currentDay  = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (currentDay == 0)
            currentDay = 7;

        initializeGraph();
        initializePieChart();
        setHeader();

    }



    private void initializeGraph()
    {
        chart = findViewById(R.id.graph);
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        times = new long[currentDay];

        UsageTimeProvider provider = new UsageTimeProvider(this);
        int nbDay = 0;
        for(int i = currentDay - 1; i >= 0; i--)
        {
            Calendar beginning = Calendar.getInstance();
            beginning.set(Calendar.HOUR_OF_DAY, 0);
            beginning.set(Calendar.MINUTE, 0);
            beginning.set(Calendar.SECOND, 0);
            beginning.set(Calendar.MILLISECOND, 0);
            beginning.add(Calendar.DATE, -i);

            Calendar end = Calendar.getInstance();
            end.set(Calendar.HOUR_OF_DAY, 23);
            end.set(Calendar.MINUTE, 59);
            end.set(Calendar.SECOND, 59);
            end.set(Calendar.MILLISECOND, 59);
            end.add(Calendar.DATE, -i);

            times[nbDay] = provider.getAppUsageTime(packageName, beginning, end);
            nbDay ++;
        }

        for (int i = 0; i < currentDay; i++) {
            values = new ArrayList<>();
            values.add(new SubcolumnValue( times[i]/60, -13388315));
            Column column = new Column(values);
            column.setHasLabels(true);
            columns.add(column);
        }

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

    private void initializePieChart(){
        pieChart = findViewById(R.id.PieChart);
        List<SliceValue> values = new ArrayList<>();

        for (int i = 0; i < currentDay; i++) {
            float data = ((float) times[i]/totalTime)*100;
            SliceValue sliceValue = new SliceValue(data, -13388315);
            sliceValue.setLabel(utils.getDayName(i+1) + " " + df.format(data)+"%");
            values.add(sliceValue);
        }
        pieData = new PieChartData(values);
        pieData.setHasLabels(true);
        pieData.setHasLabelsOutside(false);
        pieData.setHasCenterCircle(true);
        pieChart.setPieChartData(pieData);
    }

    private void setHeader(){
        try{
            icon = getPackageManager().getApplicationIcon(packageName);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        ImageView iconView = findViewById(R.id.icon);
        iconView.setImageDrawable(icon);

        TextView totalTimeView = findViewById(R.id.total_time);
        int total = 0;
        for(long time : times)
            total += (int) time;

        totalTimeView.setText(TimeFormatHelper.minutesToHours(total));
        ProgressBar progressBar = findViewById(R.id.progressBar_graph_time);
        float data = ((float)total/totalTime)*100;
        progressBar.setProgress((int) data);
    }

}
