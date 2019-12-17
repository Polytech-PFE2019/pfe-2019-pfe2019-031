package ez.com.inside.activities.network;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ez.com.inside.R;
import ez.com.inside.business.network.Utils;
import ez.com.inside.business.network.Wifi;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.MyViewHolder>{

    private List<Wifi> wifis;
    private OnClickListenerTransition clickListener;
    private Utils utils = new Utils();

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private TextView level;
        public ConstraintLayout layout;
        private OnClickListenerTransition clickListener;

        public MyViewHolder(ConstraintLayout v, OnClickListenerTransition clickListener) {
            super(v);
            this.name = v.findViewById(R.id.wifi_item_name);
            this.level = v.findViewById(R.id.wifi_item_signal);
            this.clickListener = clickListener;
            layout = v;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View V){
            clickListener.onClick(name, getAdapterPosition());
        }
    }

    public WifiAdapter(List<Wifi> wifis, OnClickListenerTransition clickListener) {
        this.wifis = wifis;
        this.clickListener = clickListener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wifi_item, parent, false);

        return new MyViewHolder(v, clickListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Wifi item = wifis.get(position);
        holder.name.setText(item.name);

        int sum = 0;
        for (int i = 0; i < item.getWifiSameName().size(); i++) {
            sum += item.getWifiSameName().get(i).level;
        }
        int average = sum / item.getWifiSameName().size();
        utils.setWifiText(average,holder.level);

    }

    @Override
    public int getItemCount() {
        return wifis.size();
    }
}
