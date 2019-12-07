package ez.com.inside.business.network;

import java.text.DecimalFormat;

/**
 * Created by Monz on 12/12/2017.
 */

public class NetworkAppInfo {

    public static final double MULTIPLIER = 1024;
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    private String packageName;
    private String appName;
    private long dataConsumed;
    private long time;

    public NetworkAppInfo(String packageName, String appName, long dataConsumed, long time) {
        this.packageName = packageName;
        this.appName = appName;
        this.dataConsumed = dataConsumed;
        this.time = time;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAppName() {
        return appName;
    }

    public long getDataConsumed() {
        return dataConsumed;
    }

    public long getTime() {
        return time;
    }

    public String getDataConsumedFormated(){
        double value = (double)(dataConsumed/MULTIPLIER);
        if(value>MULTIPLIER){
            value = (double)(value/MULTIPLIER);
            if(value>MULTIPLIER){
                value = (double)(value/MULTIPLIER);
                return FORMAT.format(value) + " Go";
            }else{
                return FORMAT.format(value) + " Mo";
            }
        }else{
            return FORMAT.format(value) + " ko";
        }
    }

    public String getTimeFormated(){
        if(time==0){
            return "-";
        }
        long value = time/1000;
        if(value<60){
            return value + "s";
        }else {
            value /= 60;
            if (value < 60) {
                return value + "min";
            } else {
                return value / 60 + "h" + value % 60 + "min";
            }
        }
    }
}
