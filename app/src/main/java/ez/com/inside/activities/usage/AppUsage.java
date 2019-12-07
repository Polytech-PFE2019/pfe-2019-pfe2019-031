package ez.com.inside.activities.usage;

import android.graphics.drawable.Drawable;

/**
 * Created by Charly on 07/12/2017.
 */

public class AppUsage
{
    public String appName;
    public String packageName;
    public Drawable icon;
    public long usageTime;
    public double usageRate;
    public int[] color;

    public AppUsage(String appName)
    {
        this.appName = appName;
    }
}
