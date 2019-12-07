package ez.com.inside.business.permission;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Monz on 23/11/2017.
 * An object with the application name, the package name, the number of permission-group requested
 * and given and a map of all permission-group for one application.
 */
public class AppPermissions implements Parcelable{

    private String packageName;
    private String appName;
    private long numberPermissionsRequested;
    private long numberPermissionsGiven;

    /**
     * All the permissions group requested (false) and granted (true) for an application.
     */
    private Map<String,Boolean> permissionsGroup;

    /**
     * Construct a AppPermissions from the PackageInfo, the map of permission
     * (use PermissionMapping.getAppsPermissions) and a PackageManager.
     * @param info
     * @param permissionsGroup
     * @param manager
     */
    public AppPermissions(PackageInfo info, Map<String, Boolean> permissionsGroup, PackageManager manager) {
        this.packageName = info.packageName;
        this.permissionsGroup = permissionsGroup;
        this.initData(info,permissionsGroup,manager);
    }

    /**
     * Constructor from parcel.
     * @param in
     */
    protected AppPermissions(Parcel in) {
        packageName = in.readString();
        appName = in.readString();
        numberPermissionsRequested = in.readLong();
        numberPermissionsGiven = in.readLong();
        permissionsGroup = in.readHashMap(HashMap.class.getClassLoader());
    }

    /**
     * Override for parcel.
     */
    public static final Creator<AppPermissions> CREATOR = new Creator<AppPermissions>() {
        @Override
        public AppPermissions createFromParcel(Parcel in) {
            return new AppPermissions(in);
        }

        @Override
        public AppPermissions[] newArray(int size) {
            return new AppPermissions[size];
        }
    };

    /**
     * Initialize the object with methods call.
     * @param info
     * @param permissionsGroup
     * @param manager
     */
    private void initData(PackageInfo info, Map<String, Boolean> permissionsGroup, PackageManager manager){
        this.appName = manager.getApplicationLabel(info.applicationInfo).toString();
        this.numberPermissionsRequested = permissionsGroup.size();
        this.numberPermissionsGiven = countNumberGranted(permissionsGroup);
    }

    /**
     * Count the number of value true in the permissionsGroup map.
     * @param permissionsGroup
     * @return
     */
    private int countNumberGranted(Map<String, Boolean> permissionsGroup){
        int count = 0;
        for(Map.Entry<String, Boolean> info : permissionsGroup.entrySet()){
            if(info.getValue()){
                count++;
            }
        }
        return count;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAppName() {
        return appName;
    }

    public long getNumberPermissionsRequested() {
        return numberPermissionsRequested;
    }

    public long getNumberPermissionsGiven() {
        return numberPermissionsGiven;
    }

    public Map<String, Boolean> getPermissionsGroup() {
        return permissionsGroup;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageName);
        dest.writeString(appName);
        dest.writeLong(numberPermissionsRequested);
        dest.writeLong(numberPermissionsGiven);
        dest.writeMap(permissionsGroup);
    }
}
