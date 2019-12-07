package ez.com.inside.activities.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ez.com.inside.R;
import ez.com.inside.activities.listeners.OnClickListener;
import ez.com.inside.business.permission.AppPermissions;

/**
 * Created by Monz on 23/11/2017.
 */

public class PermissionsAdapter extends RecyclerView.Adapter<PermissionsAdapter.ViewHolder>{

    private Context context;
    private List<AppPermissions> objects;
    private final OnClickListener clickListener;

    public PermissionsAdapter(@NonNull Context context, @NonNull List<AppPermissions> objects, OnClickListener clickListener) {
        this.context=context;
        this.objects=objects;
        this.clickListener=clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.permissions_item, parent, false);

        ViewHolder holder = new ViewHolder(layout, clickListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        final AppPermissions item = objects.get(position);

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

        for(Map.Entry<String, Boolean> entry : item.getPermissionsGroup().entrySet())
        {
            holder.iconsPermissions.get(entry.getKey()).setVisibility(View.VISIBLE);

            int iconPermission = 0;
            try
            {
                iconPermission = context.getPackageManager().getPermissionGroupInfo(
                        "android.permission-group." + entry.getKey(), PackageManager.GET_META_DATA).icon;
            }
            catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            holder.iconsPermissions.get(entry.getKey()).setImageResource(iconPermission);

            if(entry.getValue().equals(Boolean.FALSE))
                holder.iconsPermissions.get(entry.getKey()).setColorFilter(Color.LTGRAY);
        }

        GridLayout grid = holder.gridPermissions;
        for(int i = 0; i < grid.getChildCount(); i++)
        {
            if(grid.getChildAt(i).getVisibility() != View.VISIBLE)
            {
                grid.removeViewAt(i);
                // When removing, the grid recalculates its indexes.
                // So two consecutive items to be removed would remove only the first item.
                // Need to go back of 1 in i.
                i--;
            }
        }
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public ImageView appIcon;
        public TextView textName;
        public CardView card;
        public GridLayout gridPermissions;

        public Map<String, ImageView> iconsPermissions;

        private OnClickListener clickListener;

        public ViewHolder(View itemView, OnClickListener clickListener)
        {
            super(itemView);
            textName = itemView.findViewById(R.id.AppAutho_name);
            appIcon = itemView.findViewById(R.id.AppAutho_imageView);
            card = itemView.findViewById(R.id.cardAutho);
            gridPermissions = itemView.findViewById(R.id.permissionicon_grid);

            iconsPermissions = new HashMap<>();
            iconsPermissions.put("CAMERA", (ImageView) itemView.findViewById(R.id.permissionicon_camera));
            iconsPermissions.put("MICROPHONE", (ImageView) itemView.findViewById(R.id.permissionicon_micro));
            iconsPermissions.put("LOCATION", (ImageView) itemView.findViewById(R.id.permissionicon_position));
            iconsPermissions.put("CONTACTS", (ImageView) itemView.findViewById(R.id.permissionicon_contacts));
            iconsPermissions.put("PHONE", (ImageView) itemView.findViewById(R.id.permissionicon_phone));
            iconsPermissions.put("SMS", (ImageView) itemView.findViewById(R.id.permissionicon_sms));
            iconsPermissions.put("STORAGE", (ImageView) itemView.findViewById(R.id.permissionicon_storage));
            iconsPermissions.put("CALENDAR", (ImageView) itemView.findViewById(R.id.permissionicon_calendar));
            iconsPermissions.put("SENSORS", (ImageView) itemView.findViewById(R.id.permissionicon_sensors));

            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(itemView, getAdapterPosition());
        }
    }
}
