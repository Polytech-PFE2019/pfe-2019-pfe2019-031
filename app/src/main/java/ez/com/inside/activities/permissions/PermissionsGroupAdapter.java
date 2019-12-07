package ez.com.inside.activities.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ez.com.inside.R;
import ez.com.inside.activities.listeners.OnClickListener;
import ez.com.inside.business.permission.PermissionGroupInfo;
import ez.com.inside.business.permission.PermissionsFinder;

/**
 * Created by Monz on 05/12/2017.
 */

public class PermissionsGroupAdapter  extends RecyclerView.Adapter<PermissionsGroupAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView icon;
        public TextView textName;
        public TextView textValue;
        public CardView card;
        private OnClickListener clickListener;

        public ViewHolder(View itemView, OnClickListener clickListener) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.PermissionsGroupItem_name);
            textValue = (TextView) itemView.findViewById(R.id.PermissionsGroupItem_value);
            icon = (ImageView) itemView.findViewById(R.id.PermissionsGroupItem_imageView);
            card = (CardView) itemView.findViewById(R.id.PermissionsGroupItem_card);

            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(itemView,getAdapterPosition());
        }
    }

    Context context;
    private List<PermissionGroupInfo> permissionGroupInfoList;
    private final OnClickListener clickListener;

    public PermissionsGroupAdapter(Context context, List<PermissionGroupInfo> permissionGroupInfoList, OnClickListener clickListener) {
        this.context = context;
        this.permissionGroupInfoList = permissionGroupInfoList;
        this.clickListener=clickListener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.permissions_group_item, parent, false);

        return new ViewHolder(layout,clickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(position<0 || position>permissionGroupInfoList.size()){
            return;
        }
        PermissionGroupInfo item = permissionGroupInfoList.get(position);
        if(item!=null){
            int icon = 0;
            try {
                icon = context.getPackageManager().getPermissionGroupInfo(
                        "android.permission-group." + item.getName(), PackageManager.GET_META_DATA).icon;
            } catch (PackageManager.NameNotFoundException e) {
                //e.printStackTrace();
            }
            holder.icon.setImageResource(icon);
            holder.textName.setText(PermissionsFinder.getPermissionGroupLabel(item.getName(),context));

            String text = item.getNumberAppGranted() + " application(s) autorisée(s) sur " + item.getNumberAppRequested();

            holder.textValue.setText(text);
        }
    }

    @Override
    public int getItemCount() {
        return permissionGroupInfoList.size();
    }

    public String getName(int position){
        if(position<0 || position>=permissionGroupInfoList.size()){
            return "Autorisation";
        }
        return permissionGroupInfoList.get(position).getName();
    }
}
