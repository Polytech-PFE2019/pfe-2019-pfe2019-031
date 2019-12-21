package ez.com.inside.activities.usage;

import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ez.com.inside.R;
import ez.com.inside.activities.helpers.TimeFormatHelper;
import lecho.lib.hellocharts.model.SliceValue;

public class DashboardAdapter extends  RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    private List<AppUsage> dataset;
    private int time;
    private static DecimalFormat df = new DecimalFormat("0");

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView icon;
        private TextView usageTimeView;
        private ProgressBar progressBar;

        public ViewHolder(View itemView)
        {
            super(itemView);
            this.usageTimeView = itemView.findViewById(R.id.appUsageTime);
            this.icon = itemView.findViewById(R.id.appIcon);
            this.progressBar = itemView.findViewById(R.id.progressBar);
        }

    }

    public DashboardAdapter(@NonNull List<AppUsage> rates, int time)
    {
        this.dataset = rates;
        this.time = time;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dahsboard_usage_item, parent, false);

        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        AppUsage item = dataset.get(position);
        holder.usageTimeView.setText(TimeFormatHelper.minutesToHours(item.usageTime));
        try {
            holder.icon.setImageDrawable(holder.progressBar.getContext().getPackageManager().getApplicationIcon(item.packageName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        float data = ((float) item.usageTime/time)*100;
        holder.progressBar.setProgress((int) data);

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
