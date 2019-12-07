package ez.com.inside.business.network;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import ez.com.inside.business.helpers.PackagesSingleton;
import ez.com.inside.business.usagetime.UsageTimeProvider;


/**
 * Created by Monz on 08/12/2017.
 */

public class NetworkUsageProvider {

    private UsageTimeProvider usageTimeProvider;

    private NetworkStatsManager networkManager;
    private PackageManager packageManager;
    private String subscribedId;

    private int dayOfMonthBegining;

    private static final int CONNECTIVITY_TYPE = ConnectivityManager.TYPE_MOBILE;

    public NetworkUsageProvider(Context context, int dayOfMonthBegining) {
        this.networkManager = (NetworkStatsManager)context.getSystemService(context.NETWORK_STATS_SERVICE);
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        this.packageManager = context.getPackageManager();
        this.usageTimeProvider = new UsageTimeProvider(context);
        try{
            this.subscribedId = tm.getSubscriberId();
        } catch(SecurityException e){
            this.subscribedId = "";
        }

        this.dayOfMonthBegining=dayOfMonthBegining;
    }

    public long getNetworkUsageReceivedDatas(int connectivityType, Calendar older, Calendar newer) throws RemoteException {

        long datas = 0;
/*
        NetworkStats.Bucket bucket2 = networkManager.querySummaryForDevice(connectivityType,subscribedId,
                older.getTimeInMillis(),newer.getTimeInMillis());
        datas += bucket2.getRxBytes() + bucket2.getTxBytes();
*/
        NetworkStats stats = networkManager.querySummary(connectivityType,subscribedId,
                older.getTimeInMillis(),newer.getTimeInMillis());
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();

        while(stats.hasNextBucket()){
            stats.getNextBucket(bucket);
            datas += bucket.getRxBytes() + bucket.getTxBytes();
        }

        return datas;
    }

    public List<Pair<Long,String>> getNetworkUsageForCurrentPeriod() throws RemoteException {

        List<Pair<Long,String>> result = new ArrayList<Pair<Long,String>>();

        // Date
        Calendar now = Calendar.getInstance();
        Calendar begin = (Calendar)now.clone();
        now.set(Calendar.HOUR_OF_DAY,0);
        now.set(Calendar.MINUTE,0);
        now.set(Calendar.SECOND,0);
        now.set(Calendar.MILLISECOND,0);
        begin.set(Calendar.DAY_OF_MONTH,dayOfMonthBegining);
        Calendar end;

        if(now.get(Calendar.DAY_OF_MONTH)<dayOfMonthBegining){
            begin.add(Calendar.MONTH,-1);
        }
        end = (Calendar)begin.clone();
        end.add(Calendar.MONTH,1);

        Calendar iterator = (Calendar)begin.clone();
        iterator.add(Calendar.DAY_OF_MONTH,1);
        //Loop to retreive datas
        while(!iterator.equals(end)){
            long value = getNetworkUsageReceivedDatas(CONNECTIVITY_TYPE,begin,iterator);
            String day = iterator.get(Calendar.DAY_OF_MONTH) + "/" + (iterator.get(Calendar.MONTH)+1);
            result.add(new Pair<Long,String>(value,day));
            iterator.add(Calendar.DAY_OF_MONTH,1);
        }
        long value = getNetworkUsageReceivedDatas(CONNECTIVITY_TYPE,begin,iterator);
        String day = iterator.get(Calendar.DAY_OF_MONTH) + "/" + (iterator.get(Calendar.MONTH)+1);
        result.add(new Pair<Long,String>(value,day));

        return result;
    }

    public List<Pair<Long,String>> getNetworkUsageForCurrentPeriod2() throws RemoteException {

        List<Pair<Long,String>> result = new ArrayList<>();

        // Date
        Calendar now = Calendar.getInstance();
        Calendar begin = (Calendar)now.clone();
        now.set(Calendar.HOUR_OF_DAY,0);
        now.set(Calendar.MINUTE,0);
        now.set(Calendar.SECOND,0);
        now.set(Calendar.MILLISECOND,0);
        begin.set(Calendar.DAY_OF_MONTH,dayOfMonthBegining);
        Calendar end;

        if(now.get(Calendar.DAY_OF_MONTH)<dayOfMonthBegining){
            begin.add(Calendar.MONTH,-1);
        }
        end = (Calendar)begin.clone();
        end.add(Calendar.MONTH,1);

        Calendar iterator = (Calendar)begin.clone();
        iterator.add(Calendar.DAY_OF_MONTH,1);
        //Loop to retreive datas
        PackagesSingleton singleton = PackagesSingleton.getInstance(packageManager);
        List<String> packages = singleton.getLauncherPackages();

        while(!iterator.equals(end)){
            long datas = 0;
            for(String packageName : packages){
                try {
                    PackageInfo info = packageManager.getPackageInfo(packageName,PackageManager.GET_META_DATA);
                    NetworkStats stats = networkManager.queryDetailsForUid(CONNECTIVITY_TYPE,subscribedId,
                            now.getTimeInMillis(),end.getTimeInMillis(),info.applicationInfo.uid);
                    NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                    while(stats.hasNextBucket()){
                        stats.getNextBucket(bucket);
                        datas += bucket.getRxBytes() + bucket.getTxBytes();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    System.out.println(packageName + " ERROR");
                }
            }

            String day = iterator.get(Calendar.DAY_OF_MONTH) + "/" + (iterator.get(Calendar.MONTH)+1);
            result.add(new Pair<Long,String>(datas,day));
            iterator.add(Calendar.DAY_OF_MONTH,1);
        }

        /*long value = getNetworkUsageReceivedDatas(CONNECTIVITY_TYPE,begin,iterator);
        String day = iterator.get(Calendar.DAY_OF_MONTH) + "/" + (iterator.get(Calendar.MONTH)+1);
        result.add(new Pair<Long,String>(value,day));*/

        return result;
    }

    public List<NetworkAppInfo> getNetworkUsageForAppsForCurrentPeriod() throws RemoteException {

        List<NetworkAppInfo> result = new ArrayList<>();

        // Date
        Calendar now = Calendar.getInstance();
        Calendar begin = (Calendar)now.clone();
        now.set(Calendar.HOUR_OF_DAY,0);
        now.set(Calendar.MINUTE,0);
        now.set(Calendar.SECOND,0);
        now.set(Calendar.MILLISECOND,0);
        begin.set(Calendar.DAY_OF_MONTH,dayOfMonthBegining);
        Calendar end;
        if(now.get(Calendar.DAY_OF_MONTH)<dayOfMonthBegining){
            begin.add(Calendar.MONTH,-1);
        }
        end = (Calendar)begin.clone();
        end.add(Calendar.MONTH,1);

        PackagesSingleton singleton = PackagesSingleton.getInstance(packageManager);
        List<String> packages = singleton.getLauncherPackages();

        Map<String,Long> usageMap = usageTimeProvider.getUsageTimeForInterval(begin,end);
        String previousName = "";

        for(String packageName : packages){
            long time = 0;
            if(usageMap.containsKey(packageName)){
                time = usageMap.get(packageName);
            }

            try {
                int uid;
                PackageInfo info = packageManager.getPackageInfo(packageName,
                        PackageManager.GET_META_DATA);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uid = packageManager.getPackageUid(packageName,PackageManager.GET_META_DATA);
                }else {
                    uid = info.applicationInfo.uid;
                }

                NetworkStats stats = networkManager.queryDetailsForUid(CONNECTIVITY_TYPE,subscribedId,
                        begin.getTimeInMillis(),end.getTimeInMillis(),uid);

                NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                long datas = 0;
                while(stats.hasNextBucket()){
                    stats.getNextBucket(bucket);
                    datas += bucket.getRxBytes() + bucket.getTxBytes();
                    System.out.println("Rx: " + bucket.getRxBytes() + " Tx: "+ bucket.getTxBytes());
                }
                NetworkAppInfo obj = new NetworkAppInfo(packageName,
                        packageManager.getApplicationLabel(info.applicationInfo).toString(),datas,
                        time);
                if(datas>0) {
                    if(!packageName.equals(previousName)) {
                        result.add(obj);
                        previousName=packageName;
                    }
                }

            } catch (PackageManager.NameNotFoundException e) {
                System.out.println(packageName + " ERROR");
            }
        }

        sortListNetworkAppInfo(result);

        return result;
    }

    private void sortListNetworkAppInfo(List<NetworkAppInfo> list){

        Collections.sort(list, new Comparator<NetworkAppInfo>() {
            @Override
            public int compare(NetworkAppInfo o1, NetworkAppInfo o2) {
                long val1 = o1.getDataConsumed();
                long val2 = o2.getDataConsumed();
                if(val1>val2){
                    return -1;
                }else if(val1<val2){
                    return 1;
                }else{
                    return 0;
                }
            }
        });
    }
}
