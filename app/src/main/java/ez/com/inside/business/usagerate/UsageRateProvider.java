package ez.com.inside.business.usagerate;

import java.util.Map;

import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by Charly on 21/11/2017.
 */

public interface UsageRateProvider
{
    public Map<String, Double> allUsageRates(int nbDays);
}
