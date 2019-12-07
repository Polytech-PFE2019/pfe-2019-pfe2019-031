package ez.com.inside.business.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Charly on 03/12/2017.
 */

public final class CalendarHelper
{
    private CalendarHelper() {}

    /**
     *
     * @return Array containing the past nbDays under the form of "dd/MM".
     */
    public static String[] pastDaysOfTheWeek()
    {
        String[] days = new String[8];

        DateFormat dateFormat = new SimpleDateFormat("dd/MM");

        for(int i = days.length - 1, index = 0; i >= 0; i--, index++)
        {
            Calendar day = Calendar.getInstance();
            day.add(Calendar.DATE, -i);
            days[index] = dateFormat.format(day.getTime());
        }

        return days;
    }

    /**
     *
     * @return Array containing the past hours, each one separated of 3 hours, under the form of "XXh".
     */
    public static String[] pastHoursOfTheDay()
    {
        String[] hours = new String[9];

        DateFormat dateFormat = new SimpleDateFormat("hh");

        for(int i = hours.length - 1, index = 0; i >= 0; i--, index++)
        {
            Calendar hour = Calendar.getInstance();
            hour.add(Calendar.HOUR_OF_DAY, -i * 3);
            hours[index] = dateFormat.format(hour.getTime()) + "h";
        }

        return hours;
    }

    /**
     *
     * @return Array containing the past days of the month, separated every 3 days (1/10, 4/10...)
     */
    public static String[] pastDaysOfTheMonth()
    {
        String[] days = new String[10];

        DateFormat dateFormat = new SimpleDateFormat("dd/MM");

        for(int i = days.length - 1, index = 0; i >= 0; i--, index++)
        {
            Calendar day = Calendar.getInstance();
            day.add(Calendar.DATE, -i * 3);
            days[index] = dateFormat.format(day.getTime());
        }

        return days;
    }
}
