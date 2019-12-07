package ez.com.inside.activities.helpers;

/**
 * Created by Charly on 04/12/2017.
 */

public final class TimeFormatHelper
{
    private TimeFormatHelper() {}

    public static String minutesToHours(long nbMinutes)
    {
        int nbHours = 0, nbMinutesLeft = 0;

        nbHours = (int) nbMinutes / 60;
        nbMinutesLeft = (int) nbMinutes % 60;

        if(nbHours == 0)
            return nbMinutesLeft + "min";
        else
            return nbHours + "h" + nbMinutesLeft + "min";
    }
}
