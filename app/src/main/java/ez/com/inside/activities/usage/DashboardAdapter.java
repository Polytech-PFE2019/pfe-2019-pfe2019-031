package ez.com.inside.activities.usage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ez.com.inside.R;
import ez.com.inside.activities.helpers.TimeFormatHelper;

public class DashboardAdapter extends  RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    private List<AppUsage> dataset;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView icon;
        private TextView usageTimeView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            this.usageTimeView = itemView.findViewById(R.id.appUsageTime);
            this.icon = itemView.findViewById(R.id.appIcon);
        }

    }

    public DashboardAdapter(@NonNull List<AppUsage> rates)
    {
        this.dataset = rates;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dahsboard_usage, parent, false);

        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        AppUsage item = dataset.get(position);
        holder.usageTimeView.setText(TimeFormatHelper.minutesToHours(item.usageTime));
        holder.icon.setImageDrawable(item.icon);

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
