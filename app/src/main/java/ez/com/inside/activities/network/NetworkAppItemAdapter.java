package ez.com.inside.activities.network;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ez.com.inside.R;
import ez.com.inside.business.network.NetworkAppInfo;

/**
 * Created by Monz on 12/12/2017.
 */

public class NetworkAppItemAdapter extends RecyclerView.Adapter<NetworkAppItemAdapter.ViewHolder>{

    private List<NetworkAppInfo> objects;
    private Context context;

    public NetworkAppItemAdapter(Context context, List<NetworkAppInfo> list) {
        this.objects = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.network_app_item, parent, false);

        ViewHolder holder = new ViewHolder(layout);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NetworkAppInfo item = objects.get(position);

        Drawable icon = null;
        try
        {
            icon = context.getPackageManager().getApplicationIcon(item.getPackageName());
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        holder.appIcon.setImageDrawable(icon);

        holder.textName.setText(item.getAppName());
        holder.datasValue.setText(item.getDataConsumedFormated());
        holder.timeValue.setText(item.getTimeFormated());
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView appIcon;
        public TextView textName;
        public TextView datasValue;
        public TextView timeValue;

        public ViewHolder(View itemView)
        {
            super(itemView);
            textName = itemView.findViewById(R.id.networkItem_textname);
            datasValue = itemView.findViewById(R.id.networkItem_datasvalue);
            appIcon = itemView.findViewById(R.id.networkItem_imageView);
            timeValue = itemView.findViewById(R.id.networkItem_timevalue);
        }
    }
}
