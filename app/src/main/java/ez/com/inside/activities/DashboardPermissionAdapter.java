package ez.com.inside.activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ez.com.inside.R;
import ez.com.inside.business.permission.PermissionGroupInfo;
import ez.com.inside.business.permission.PermissionsFinder;

public class DashboardPermissionAdapter extends RecyclerView.Adapter<DashboardPermissionAdapter.MyViewHolder> {

    private List<PermissionGroupInfo> permissionGroupInfoList = new ArrayList<>();
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        public ConstraintLayout layout;
        public MyViewHolder(ConstraintLayout v) {
            super(v);
            this.imageView =  v.findViewById(R.id.dash_permi_image);
            this.textView = v.findViewById(R.id.dash_permi_text);
            layout = v;
        }
    }

    public DashboardPermissionAdapter(Context context, List<PermissionGroupInfo> myDataset) {
        for(int i=0; i < myDataset.size(); i++){
            if(isInList(PermissionsFinder.getPermissionGroupLabel(myDataset.get(i).getName(), context))){
                this.permissionGroupInfoList.add(myDataset.get(i));
            }
        }

        this.context = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dashboard_permission_item, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PermissionGroupInfo item = permissionGroupInfoList.get(position);
        if(item!=null){
            String permissionType = PermissionsFinder.getPermissionGroupLabel(item.getName(), context);

                int icon = 0;
                try {
                    icon = context.getPackageManager().getPermissionGroupInfo(
                            "android.permission-group." + item.getName(), PackageManager.GET_META_DATA).icon;
                } catch (PackageManager.NameNotFoundException e) {
                    //e.printStackTrace();
                }
                holder.imageView.setImageResource(icon);
                String text = item.getNumberAppGranted() + " application(s) autorise(s) " + permissionType;

                holder.textView.setText(text);

        }
    }

    private boolean isInList(String name){
        String[] permissions = {"Calendrier", "Position", "Microphone"};
        for(int i= 0; i<permissions.length; i++){
            if(permissions[i].equals(name))
                return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return permissionGroupInfoList.size();
    }
}
