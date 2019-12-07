package ez.com.inside.activities.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.recyclerview.widget.RecyclerView;
import  androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ez.com.inside.R;
import ez.com.inside.business.permission.PermissionsFinder;

/**
 * Created by Monz on 30/11/2017.
 */

public class PermissionDetailAdapter extends RecyclerView.Adapter<PermissionDetailAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView icon;
        private TextView textName;
        private ImageView imageGranted;

        public ViewHolder(View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.AppPermissionDetail_textview);
            icon = itemView.findViewById(R.id.AppPermissionDetail_imageView);
            imageGranted = itemView.findViewById(R.id.AppPermissionDetail_imageGranted);
        }
    }

    private Context context;
    private List<Map.Entry<String, Boolean>> permissionsGroup;

    public PermissionDetailAdapter(@NonNull Context context, @NonNull Map<String,Boolean> permissionsGroup){
        this.permissionsGroup = new ArrayList<>(permissionsGroup.entrySet());
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.permissions_detail_item, parent, false);

        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Map.Entry<String, Boolean> item = permissionsGroup.get(position);
        if (item!= null) {
            try {
                holder.textName.setText(PermissionsFinder.getPermissionGroupLabel(item.getKey(),context));
            }
            catch(Exception e){
                holder.textName.setText("");
            }
            int icon = 0;
            try {
                icon = context.getPackageManager().getPermissionGroupInfo(
                        "android.permission-group." + item.getKey(),PackageManager.GET_META_DATA).icon;
            } catch (PackageManager.NameNotFoundException e) {
                //e.printStackTrace();
            }
            holder.icon.setImageResource(icon);

           if(item.getValue().equals(Boolean.TRUE))
               holder.imageGranted.setImageResource(R.drawable.authorization_granted);
           else
               holder.imageGranted.setImageResource(R.drawable.authorization_not_granted);
        }
    }

    @Override
    public int getItemCount() {
        return permissionsGroup.size();
    }
}
