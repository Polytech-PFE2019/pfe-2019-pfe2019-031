package ez.com.inside.activities.network;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ez.com.inside.R;
import ez.com.inside.business.network.Utils;
import ez.com.inside.business.network.Wifi;
import ez.com.inside.business.network.WifiDatabase;

public class WifiFragment extends Fragment {
    private WifiDatabase db;
    private int levelCurrentW;
    private WifiManager wifiManager;
    private Button addWifi;
    private Utils utils = new Utils();
    private List<Wifi> wifis;
    private WifiAdapter wifiAdapter;
    List<Wifi> newWifis = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        return inflater.inflate(R.layout.fragment_wifi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        db = WifiDatabase.getInstance(getView().getContext());
        addWifi = getView().findViewById(R.id.saveWifi);
        setWifiListFromBDD();
        setRecycleView();


        addWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //db.wifiDao().deleteAll();
                Wifi wifi = new Wifi();
                wifi.name = wifiManager.getConnectionInfo().getSSID();
                wifi.level = levelCurrentW;
                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);

                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c);
                wifi.date =  formattedDate;
                db.wifiDao().InsertAll(wifi);
                wifis.add(wifi);
                addWifi(wifi);
                wifiAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getWifiLevel();
    }


    private void getWifiLevel()
    {
        wifiManager = (WifiManager) getView().getContext().getApplicationContext().getSystemService(getView().getContext().WIFI_SERVICE);
        int linkSpeed = wifiManager.getConnectionInfo().getRssi();
        levelCurrentW = WifiManager.calculateSignalLevel(linkSpeed, 5);

        TextView type = getView().findViewById(R.id.WifiType);
        type.setText("Wifi");
        TextView name = getView().findViewById(R.id.WifiName);
        name.setText(wifiManager.getConnectionInfo().getSSID());
        utils.setWifiText(levelCurrentW, (TextView) getView().findViewById(R.id.WifiLevel));
    }


    private void setRecycleView(){
        RecyclerView recyclerView = getView().findViewById(R.id.recycleViewWifi);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        wifiAdapter = new WifiAdapter(newWifis, new OnClickListenerTransition(){
            @Override
            public void onClick(TextView name, int position) {
                Intent intent = new Intent(getContext(), WifiHistoricActivity.class);
                intent.putExtra("wifi", wifis.get(position));

                View sharedViewAppName = name;
                String transitionNameAppName = getString(R.string.transition_appName);
                Pair<View, String> sharedElementsAppName = Pair.create(sharedViewAppName, transitionNameAppName);

                ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        sharedElementsAppName);
                startActivity(intent, transitionActivityOptions.toBundle());
            }
        });
        recyclerView.setAdapter(wifiAdapter);
    }

    private void setWifiListFromBDD(){
        wifis = db.wifiDao().getAll();
        for(int i=0; i < wifis.size(); i++){
            addWifi(wifis.get(i));
        }
    }

    private void addWifi(Wifi wifi){
        int position = canAddWifi(wifi, newWifis);

        if( position == -2) {
            newWifis.add(wifi);
            newWifis.get(newWifis.indexOf(wifi)).addWifi(wifi);
            Log.d("addWifi -2",   newWifis.get(newWifis.indexOf(wifi)).getWifiSameName().size() +" ");

        }
        else if(position == -1) {
            newWifis.add(wifi);
            newWifis.get(newWifis.indexOf(wifi)).addWifi(wifi);
            Log.d("addWifi -1 ",   newWifis.get(newWifis.indexOf(wifi)).getWifiSameName().size() +" ");
        }
        else{
            Log.d("addWifi",   newWifis.get(position).toString());
            newWifis.get(position).addWifi(wifi);
        }


    }

    /**
     *
     * @param wifi en cours
     * @param wifis list des wifis
     * @return -2 si la liste est vide, -1 si la wifi n'est pas dans la liste
     * retourne int si la wifi est dans la liste
     */
    private int canAddWifi(Wifi wifi, List<Wifi> wifis){
        if(wifis.size() == 0){

            Log.d("canAddWifi",   "Wifi vide");
            return -2;

        }
        for(int i = 0; i < wifis.size(); i++){
            if(wifis.get(i).name.equals(wifi.name)){
                Log.d("canAddWifi",   "Wifi ajoutée " + i );

                return i;

            }
        }

        Log.d("canAddWifi",   "Wifi pas dans la liste " );

        return -1;
    }


}
