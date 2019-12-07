package ez.com.inside.activities.usage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.palette.graphics.Palette;
import ez.com.inside.R;
import ez.com.inside.activities.helpers.DrawableHelper;
import ez.com.inside.activities.helpers.TimeFormatHelper;
import ez.com.inside.business.helpers.CalendarHelper;
import ez.com.inside.business.usagetime.UsageTimeProvider;
import ez.com.inside.dialogs.PointInformationDialog;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class GraphTimeActivity extends AppCompatActivity
{
    private String appName;
    private String packageName;
    private Drawable icon;

    private LineChartView graph;
    private LineChartData data;

    private GraphMode graphMode;

    private long[] times;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_time);
        setTitle("Suivi d\'utilisation");

        Intent intent = getIntent();
        graphMode = (GraphMode) intent.getSerializableExtra(UsageActivity.EXTRA_GRAPHMODE);
        appName = intent.getStringExtra(UsageActivity.EXTRA_APPNAME);
        packageName = intent.getStringExtra(UsageActivity.EXTRA_APPPKGNAME);

        try
        {
            icon = getPackageManager().getApplicationIcon(packageName);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView appNameView = findViewById(R.id.appName);
        appNameView.setText(appName);

        ImageView iconView = findViewById(R.id.icon);
        iconView.setImageDrawable(icon);

        initializeGraph();

        TextView totalTimeView = findViewById(R.id.total_time);

        long total = 0;
        for(long time : times)
            total += time;

        totalTimeView.setText("Temps total : " + TimeFormatHelper.minutesToHours(total));
    }

    private void initializeGraph()
    {
        graph = findViewById(R.id.graph);
        data = new LineChartData();

        Axis x = new Axis();

        switch(graphMode)
        {
            case WEEKLY:
            {
                x = initializeWeekAxisX();
                generateTimesWeek();
                break;
            }
            case MONTHLY:
            {
                x = initializeMonthAxisX();
                generateTimesMonth();
                break;
            }
        }

        // Axis
        x.setTextColor(new TextView(this).getCurrentTextColor());
        data.setAxisXBottom(x);

        // Data
        List<PointValue> values = new ArrayList<PointValue>();
        for(int i = 0; i < times.length; i++)
        {
            values.add(new PointValue(i, times[i]));
        }

        // Line
        Line line = new Line(values).setColor(initializeCurveColor()).setCubic(false);
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        graph.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                DialogFragment fragment = new PointInformationDialog();
                ((PointInformationDialog) fragment).setValue(TimeFormatHelper.minutesToHours((long) value.getY()));
                fragment.show(getSupportFragmentManager(), "informations");
            }

            @Override
            public void onValueDeselected() {
                // Nothing.
            }
        });

        data.setLines(lines);
        graph.setLineChartData(data);
    }

    private int initializeCurveColor()
    {
        Palette palette = Palette.from(DrawableHelper.drawableToBitmap(icon)).generate();
        return palette.getVibrantColor(Color.parseColor("#000000"));
    }

    /************************************************
     *************** Week methods *******************
     ***********************************************/

    private Axis initializeWeekAxisX()
    {
        Axis x = new Axis();

        List<AxisValue> x_values = new ArrayList<>();

        String[] labels = CalendarHelper.pastDaysOfTheWeek();
        for(int i = 0; i < labels.length; i++)
        {
            x_values.add(new AxisValue(i).setLabel(labels[i]));
        }

        x.setValues(x_values);

        return x;
    }

    private void generateTimesWeek()
    {
        times = new long[8];

        UsageTimeProvider provider = new UsageTimeProvider(this);

        for(int i = times.length - 1, index = 0; i >= 0; i--, index++)
        {
            Calendar c1 = Calendar.getInstance();
            c1.add(Calendar.DATE, -i - 1);
            Calendar c2 = Calendar.getInstance();
            c2.add(Calendar.DATE, -i);

            times[index] = provider.getAppUsageTime(packageName, c1, c2);
        }
    }

    /************************************************
     *************** Month methods ******************
     ***********************************************/

    private Axis initializeMonthAxisX()
    {
        Axis x = new Axis();

        List<AxisValue> x_values = new ArrayList<>();

        String[] labels = CalendarHelper.pastDaysOfTheMonth();
        for(int i = 0; i < labels.length; i++)
        {
            x_values.add(new AxisValue(i).setLabel(labels[i]));
        }

        x.setValues(x_values);

        return x;
    }

    private void generateTimesMonth()
    {
        times = new long[10];

        UsageTimeProvider provider = new UsageTimeProvider(this);

        for(int i = times.length - 1, index = 0; i >= 0; i--, index++)
        {
            Calendar c1 = Calendar.getInstance();
            c1.add(Calendar.DATE, (-i - 1) * 3);
            Calendar c2 = Calendar.getInstance();
            c2.add(Calendar.DATE, -i * 3);

            times[index] = provider.getAppUsageTime(packageName, c1, c2);
        }
    }
}
