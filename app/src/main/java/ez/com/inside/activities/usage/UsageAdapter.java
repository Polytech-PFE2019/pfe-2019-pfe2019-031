package ez.com.inside.activities.usage;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
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

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView icon;
        private TextView appNameView;
        private TextView usageRateView;
        private TextView usageTimeView;
        private CardView card;
        private OnClickListenerTransition clickListener;

        public ViewHolder(View itemView, OnClickListenerTransition clickListener)
        {
            super(itemView);
            this.appNameView = itemView.findViewById(R.id.appName);
            this.usageRateView = itemView.findViewById(R.id.appUsageRate);
            this.usageTimeView = itemView.findViewById(R.id.appUsageTime);
            this.icon = itemView.findViewById(R.id.appIcon);
            this.card = itemView.findViewById(R.id.card);

            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(appNameView, icon, getAdapterPosition());
        }
    }

    public UsageAdapter(@NonNull List<AppUsage> rates, OnClickListenerTransition clickListener)
    {
        this.clickListener=clickListener;
        this.dataset = rates;
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

        holder.appNameView.setText(item.appName);

        DecimalFormat format = new DecimalFormat("#.#");

        holder.usageRateView.setText(format.format(item.usageRate) + "%");
        holder.usageTimeView.setText(TimeFormatHelper.minutesToHours(item.usageTime));

        holder.icon.setImageDrawable(item.icon);

        holder.card.setCardBackgroundColor(dataset.get(position).color[1]);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
