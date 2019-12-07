package ez.com.inside.business.permission;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ez.com.inside.R;
import ez.com.inside.business.helpers.PackagesSingleton;

/**
 * Created by Monz on 29/11/2017.
 *
 * Give the list of permission-group for each applications available in the Android launcher.
 * Each permission-group have a boolean to represent if the permissions-group is granted.
 */
public class PermissionsFinder {

    /**
     * Not a permission.
     */
    private static final String NOT_A_PERMISSION = "NOT_A_PERMISSION";

    /**
     * The first string of each row is the permission-group name.
     * See https://developer.android.com/guide/topics/permissions/requesting.html
     */
    private static final String[][] PERMISSIONS_MAPPING = {
            {"CALENDAR", "READ_CALENDAR", "WRITE_CALENDAR"},
            {"CAMERA", "CAMERA"},
            {"CONTACTS", "READ_CONTACTS", "WRITE_CONTACTS", "GET_ACCOUNTS"},
            {"LOCATION", "ACCESS_FINE_LOCATION", "ACCESS_COARSE_LOCATION"},
            {"MICROPHONE", "RECORD_AUDIO"},
            {"PHONE", "READ_PHONE_STATE", "READ_PHONE_NUMBERS", "CALL_PHONE", "ANSWER_PHONE_CALLS" /*On runtime*/
                    , "READ_CALL_LOG", "WRITE_CALL_LOG", "ADD_VOICEMAIL", "USE_SIP", "PROCESS_OUTGOING_CALLS"},
            {"SENSORS", "BODY_SENSORS"},
            {"SMS", "SEND_SMS", "RECEIVE_SMS", "READ_SMS", "RECEIVE_WAP_PUSH", "RECEIVE_MMS"},
            {"STORAGE", "READ_EXTERNAL_STORAGE", "WRITE_EXTERNAL_STRORAGE"}
    };

    /**
     * Construct a list of AppPermissions with all the the permissions-group for all application
     * in the Android launcher.
     * @param pm
     * @return
     */
    public static List<AppPermissions> getAppsPermissions(PackageManager pm){
        List<AppPermissions> apps = new ArrayList<>();

        List<PackageInfo> packages = pm.getInstalledPackages(
                PackageManager.GET_META_DATA + PackageManager.GET_PERMISSIONS);

        List<String> launcherPackage = PackagesSingleton.getInstance(pm).getLauncherPackages();

        for (PackageInfo packageInfo : packages) {
            if(launcherPackage.contains(packageInfo.packageName)) {
                Map<PermissionInfo, Boolean> permissions = getDangerousPermissions(pm, packageInfo);
                Map<String, Boolean> permissionsGroup = PermissionsFinder.getAllPermissionsGroupsFromPermissionsMap(permissions);
                if (
                        permissionsGroup.size() > 0
                        ) {
                    apps.add(new AppPermissions(packageInfo, permissionsGroup, pm));
                }
            }
        }

        return apps;
    }

    /**
     * Return the name of the permission-group for the corresponding permission name.
     * @param permission
     * @return
     */
    public static String getPermissionGroup(String permission){

        for(int i=0;i<PERMISSIONS_MAPPING.length;i++){
            for(int j=0;j<PERMISSIONS_MAPPING[i].length;j++){
                if(permission.contains(PERMISSIONS_MAPPING[i][j])){
                    return PERMISSIONS_MAPPING[i][0];
                }
            }
        }

        return NOT_A_PERMISSION;
    }

    /**
     * Return all the permission-group name and if they are granted from a list of permission name.
     * @param permissions
     * @return
     */
    public static Map<String, Boolean> getAllPermissionsGroupsFromPermissionsMap(Map<PermissionInfo, Boolean> permissions){

        Map<String, Boolean> permissionsGroup = new HashMap<>();

        for(Map.Entry<PermissionInfo, Boolean> info : permissions.entrySet()){
            String s = getPermissionGroup(info.getKey().name);
            if(!s.equals(NOT_A_PERMISSION)){
                if(!permissionsGroup.containsKey(s)) {
                    permissionsGroup.put(s,info.getValue());
                }else{
                    if(info.getValue() && !permissionsGroup.get(s)){
                        permissionsGroup.remove(s);
                        permissionsGroup.put(s,true);
                    }
                }
            }
        }

        return permissionsGroup;
    }

    /**
     * Return all the dangerous permissions from an application.
     * Source : https://stackoverflow.com/questions/40794628/android-m-get-all-dangerous-permissions-listed-in-the-manifest
     * @param pm
     * @param packageInfo
     * @return
     */
    public static Map<PermissionInfo, Boolean> getDangerousPermissions(PackageManager pm, PackageInfo packageInfo) {
        Map<PermissionInfo, Boolean> dangerousPermissions = new HashMap<>();

        if (packageInfo.requestedPermissions != null) {
            for (int i=0;i<packageInfo.requestedPermissions.length;i++) {

                String requestedPermission = packageInfo.requestedPermissions[i];
                Boolean granted = (packageInfo.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0;
                try {
                    PermissionInfo permissionInfo = pm.getPermissionInfo(requestedPermission, 0);
                    switch (permissionInfo.protectionLevel & PermissionInfo.PROTECTION_MASK_BASE) {
                        case PermissionInfo.PROTECTION_DANGEROUS:
                            dangerousPermissions.put(permissionInfo, granted);
                            break;
                    }
                } catch (PackageManager.NameNotFoundException ignored) {
                    // unknown permission
                }
            }
        }
        return dangerousPermissions;
    }

    /**
     * Return the list of all apps filtered by permission-group.
     * @param pm
     * @return
     */
    public static List<PermissionGroupInfo> getPermissionsGroupsInfo(PackageManager pm){

        List<PermissionGroupInfo> result = new ArrayList<>();
        List<AppPermissions> listAppPermissions = getAppsPermissions(pm);

        for(int i=0;i<PERMISSIONS_MAPPING.length;i++){
            result.add(new PermissionGroupInfo(PERMISSIONS_MAPPING[i][0]));
        }

        for(AppPermissions appPermissions : listAppPermissions){
            Set<Map.Entry<String, Boolean>> set = appPermissions.getPermissionsGroup().entrySet();
            for(Map.Entry<String, Boolean> entry : set){
                int i = 0;
                while(!entry.getKey().equals(result.get(i).getName()) && i<result.size()){
                    i++;
                }
                if(i<result.size()){
                    result.get(i).addAppPermissions(appPermissions,entry.getValue());
                }
            }
        }

        return result;
    }

    /**
     * Return the permission-group label.
     * @param permissionGroup
     * @param ctx
     * @return
     */
    public static String getPermissionGroupLabel(String permissionGroup, Context ctx){
        switch(permissionGroup){
            case "CALENDAR": return ctx.getString(R.string.permission_group_calendar);
            case "CAMERA": return ctx.getString(R.string.permission_group_camera);
            case "CONTACTS": return ctx.getString(R.string.permission_group_contacts);
            case "LOCATION": return ctx.getString(R.string.permission_group_location);
            case "MICROPHONE": return ctx.getString(R.string.permission_group_microphone);
            case "PHONE": return ctx.getString(R.string.permission_group_phone);
            case "SENSORS": return ctx.getString(R.string.permission_group_sensors);
            case "SMS": return ctx.getString(R.string.permission_group_sms);
            case "STORAGE": return ctx.getString(R.string.permission_group_storage);
            default: return ctx.getString(R.string.permission_group_not_found);
        }
    }
}
