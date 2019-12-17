package ez.com.inside.activities.network;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ez.com.inside.R;
import ez.com.inside.business.network.Wifi;

public class WifiHistoricAdapter extends RecyclerView.Adapter<WifiHistoricAdapter.MyViewHolder> {

    private List<Wifi> wifis;


    public static class MyViewHolder extends RecyclerView.ViewHolder {


        private TextView level;
        private TextView date;
        private ConstraintLayout layout;
        public MyViewHolder(ConstraintLayout v ) {
            super(v);
            this.level = v.findViewById(R.id.historic_level);
            this.date = v.findViewById(R.id.historic_date);

            layout = v;
        }
    }

    public WifiHistoricAdapter(List<Wifi> wifis) {
        this.wifis = wifis;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wifi_historic_item, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Wifi item = wifis.get(position);
        holder.date.setText(item.date);
        holder.level.setText(item.level);
    }

    @Override
    public int getItemCount() {
        return wifis.size();
    }
}
