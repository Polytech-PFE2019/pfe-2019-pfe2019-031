package ez.com.inside.activities.network;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ez.com.inside.R;
import ez.com.inside.business.network.NetworkAppInfo;
import ez.com.inside.business.network.NetworkUsageProvider;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import static ez.com.inside.business.network.NetworkAppInfo.MULTIPLIER;

public class NetworkFragment extends Fragment {

    private NetworkUsageProvider networkProvider;

    private TextView totalValueText;
    private RecyclerView recyclerView;

    private LineChartView graph;
    private LineChartData data;

    private Long[] datasConsummed;
    private String[] labels;

    private int dayOfMonthBegining = 1;
    private long amount_forfait = 5 * (long) MULTIPLIER * (long) MULTIPLIER * (long) MULTIPLIER;
    private int value_amount_forfait = 5;
    private String unit_forfait = "Go";

    private static final int CURVE_COLOR = Color.BLACK;
    private static final int THRESHOLD_COLOR = Color.RED;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_network, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    /************************************************
     ****************** Inits methods ***************
     ***********************************************/
    private void init(){
        initPref();

        initializeGraph();
        initText();
        initializeRecyclerView();
    }

    private void initPref(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getView().getContext());
        String beginning_forfait = prefs.getString("beginning_forfait", "19");
        try{
            this.dayOfMonthBegining = Integer.parseInt(beginning_forfait);
        }catch(Exception e){
            this.dayOfMonthBegining = 1;
        }
        String amount_forfait = prefs.getString("amount_forfait", "5");
        try{
            this.value_amount_forfait = Integer.parseInt(amount_forfait);
        }catch(Exception e){
            this.value_amount_forfait = 5;
        }
        this.unit_forfait = prefs.getString("unit_forfait", "Go");
        try{
            if(this.unit_forfait.equals("Go")){
                this.amount_forfait = value_amount_forfait * (long) MULTIPLIER * (long) MULTIPLIER * (long) MULTIPLIER;
            }else{
                this.amount_forfait = value_amount_forfait * (long) MULTIPLIER * (long) MULTIPLIER;
            }
        }catch(Exception e){
            this.amount_forfait *= (long) MULTIPLIER * (long) MULTIPLIER * (long) MULTIPLIER;
        }

        networkProvider = new NetworkUsageProvider(getView().getContext(),this.dayOfMonthBegining);

        totalValueText = (TextView)getView().findViewById(R.id.network_totalValue);
    }

    private void initText(){
        String s = this.getString(R.string.network_totalValue_string) + " ";
        double value = (double)(datasConsummed[datasConsummed.length-1]/MULTIPLIER);
        DecimalFormat format = new DecimalFormat("#.##");
        if(value>MULTIPLIER){
            value = (double)(value/MULTIPLIER);
            if(value>MULTIPLIER){
                value = (double)(value/MULTIPLIER);
                s += format.format(value) + " Go";
            }else{
                s +=  format.format(value) + " Mo";
            }
        }else{
            s +=  format.format(value) + " ko";
        }
        totalValueText.setText(s);
    }



    /************************************************
     *************** RecyclerView methods ***********
     ***********************************************/
    private void initializeRecyclerView(){

        recyclerView = getView().findViewById(R.id.network_recyclerview);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getView().getContext());
        recyclerView.setLayoutManager(layoutManager);

        List<NetworkAppInfo> list = new ArrayList<NetworkAppInfo>();
        try {
            list = networkProvider.getNetworkUsageForAppsForCurrentPeriod();
        } catch (RemoteException e) {
            ;
        }
        RecyclerView.Adapter adapter = new NetworkAppItemAdapter(getView().getContext(),list);
        recyclerView.setAdapter(adapter);

    }

    /************************************************
     *************** Graph methods ******************
     ***********************************************/
    private void initializeGraph()
    {
        graph = getView().findViewById(R.id.network_graph);
        data = new LineChartData();

        Axis y = initializeAxisY();
        data.setAxisYLeft(y);

        // Axis X
        Axis x = initializeMonthAxisX();
        x.setTextColor(new TextView(getView().getContext()).getCurrentTextColor());
        data.setAxisXBottom(x);

        // Data
        List<PointValue> values = new ArrayList<PointValue>();
        for(int i = 0; i < datasConsummed.length; i++)
        {
            values.add(new PointValue(i, datasConsummed[i]));
        }

        // Lines
        Line line = new Line(values).setColor(CURVE_COLOR).setCubic(false);
        line.setPointRadius(0);
        List<Line> lines = new ArrayList<>();
        lines.add(line);
        lines.add(buildLineForThreshold());

        data.setLines(lines);
        graph.setLineChartData(data);

        final Viewport v = new Viewport(graph.getMaximumViewport());
        v.top = this.amount_forfait;
        v.bottom = 0;
        graph.setMaximumViewport(v);
        graph.setCurrentViewport(v);

    }

    private Line buildLineForThreshold(){
        List<PointValue> values = new ArrayList<PointValue>();
        values.add(new PointValue(0,0));
        values.add(new PointValue(datasConsummed.length-1, this.amount_forfait));
        Line line = new Line(values).setColor(THRESHOLD_COLOR).setCubic(false);
        line.setPointRadius(0);
        return line;
    }

    private Axis initializeAxisY(){
        Axis y = new Axis();
        List<AxisValue> list = new ArrayList<AxisValue>();

        long multiple;
        if(unit_forfait.equals("Mo")){
            multiple = (long)MULTIPLIER * (long)MULTIPLIER;
        }else{
            multiple = (long)MULTIPLIER * (long)MULTIPLIER * (long)MULTIPLIER;
        }
        double value = ((double)this.value_amount_forfait)/4.0;

        list.add(new AxisValue(0).setLabel("0 " + this.unit_forfait));
        for(int i=1;i<5;i++){
            list.add(new AxisValue((long)(multiple * value * i)).setLabel(((long)(value * i)) + " " + this.unit_forfait));
        }

        y.setValues(list);

        return y;
    }

    /************************************************
     *************** Month methods ******************
     ***********************************************/

    private Axis initializeMonthAxisX()
    {
        Axis x = new Axis();

        generateTimesMonth();
        List<AxisValue> x_values = new ArrayList<>();

        for(int i = 0; i < labels.length; i++)
        {
            x_values.add(new AxisValue(i).setLabel(labels[i]));
        }

        x.setValues(x_values);

        return x;
    }

    private void generateTimesMonth()
    {
        List<Pair<Long,String>> list;
        try {
            list = networkProvider.getNetworkUsageForCurrentPeriod();
            int nbDays = list.size();
            datasConsummed = new Long[nbDays];
            labels = new String[nbDays];
            for(int i=0;i<nbDays;i++){
                datasConsummed[i]=list.get(i).first;
                labels[i]=list.get(i).second;
            }
        } catch (RemoteException e) {
            datasConsummed = new Long[1];
            labels = new String[]{""};
        }
    }
}
