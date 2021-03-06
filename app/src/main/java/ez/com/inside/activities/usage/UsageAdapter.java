package ez.com.inside.activities.usage;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;
import ez.com.inside.R;
import ez.com.inside.activities.helpers.TimeFormatHelper;

/**
 * Created by Monz on 03/12/2017.
 */

public class UsageAdapter extends RecyclerView.Adapter<UsageAdapter.ViewHolder>
{
    private List<AppUsage> dataset;
    private final OnClickListenerTransition clickListener;
    private int totalTime;
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView icon;
        private TextView usageTimeView;
        private ProgressBar progressBar;
        private OnClickListenerTransition clickListener;

        public ViewHolder(View itemView, OnClickListenerTransition clickListener)
        {
            super(itemView);
            this.usageTimeView = itemView.findViewById(R.id.appUsageTime);
            this.icon = itemView.findViewById(R.id.appIcon);
            this.progressBar = itemView.findViewById(R.id.progressBar2);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(usageTimeView, icon, getAdapterPosition());
        }
    }

    public UsageAdapter(@NonNull List<AppUsage> rates, int totalTime, OnClickListenerTransition clickListener)
    {
        this.clickListener = clickListener;
        this.dataset = rates;
        this.totalTime = totalTime;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.usagerate_item, parent, false);

        return new ViewHolder(layout, clickListener);
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
        float data = ((float) item.usageTime/totalTime)*100;
        holder.progressBar.setProgress((int) data);

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
