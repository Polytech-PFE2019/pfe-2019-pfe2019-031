package ez.com.inside.activities.usage;


import java.io.Serializable;


/**
 * Created by Charly on 07/12/2017.
 */

public class AppUsage implements Serializable
{
    public String appName;
    public String packageName;
    public long usageTime;

    public AppUsage(String appName)
    {
        this.appName = appName;
    }


}
