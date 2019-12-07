package ez.com.inside.business.permission;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Monz on 07/12/2017.
 */

public class PermissionGroupInfo implements Parcelable{

    private String name;
    private List<AppPermissions> appsList;
    private int numberAppRequested;
    private int numberAppGranted;

    public PermissionGroupInfo(String name) {
        this.name = name;
        this.appsList = new ArrayList<AppPermissions>();
        this.numberAppRequested = 0;
        this.numberAppGranted = 0;
    }

    protected PermissionGroupInfo(Parcel in) {
        name = in.readString();
        appsList = in.createTypedArrayList(AppPermissions.CREATOR);
        numberAppRequested = in.readInt();
        numberAppGranted = in.readInt();
    }

    public static final Creator<PermissionGroupInfo> CREATOR = new Creator<PermissionGroupInfo>() {
        @Override
        public PermissionGroupInfo createFromParcel(Parcel in) {
            return new PermissionGroupInfo(in);
        }

        @Override
        public PermissionGroupInfo[] newArray(int size) {
            return new PermissionGroupInfo[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getNumberAppRequested() {
        return numberAppRequested;
    }

    public int getNumberAppGranted() {
        return numberAppGranted;
    }

    public List<AppPermissions> getAppsList() {
        return appsList;
    }

    public void addAppPermissions(AppPermissions app, boolean isGranted){
        appsList.add(app);
        numberAppRequested++;
        if(isGranted){
            numberAppGranted++;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(appsList);
        dest.writeInt(numberAppRequested);
        dest.writeInt(numberAppGranted);
    }

    public void sortAppPermissions(){
        Collections.sort(appsList, new Comparator<AppPermissions>() {
            @Override
            public int compare(AppPermissions appPermissions, AppPermissions t1)
            {
                int nbGranted1 = Collections.frequency(appPermissions.getPermissionsGroup().values(), true);
                int nbGranted2 = Collections.frequency(t1.getPermissionsGroup().values(), true);

                if(nbGranted1 == nbGranted2)
                    return 0;
                if(nbGranted1 < nbGranted2)
                    return 1;
                return -1;
            }
        });
    }
}
