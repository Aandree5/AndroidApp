package domains.coventry.andrefmsilva.utils;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Utils
{
    public static final String SQL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String APP_DATE_FORMAT = "dd / MM / yyyy";

    /**
     * Formats a string of a date into the requested format
     *
     * @param date      Date to be formatted
     * @param inFormat  Format of the given date
     * @param outFormat Desired format for the date
     * @return A date in the requested format
     * @throws ParseException Error parsing date, wrong format
     */
    public static String formatDate(@NonNull String date, @NonNull String inFormat, @NonNull String outFormat) throws ParseException
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(inFormat, Locale.UK);
        Calendar c = Calendar.getInstance();

        c.setTime(simpleDateFormat.parse(date));

        simpleDateFormat.applyPattern(outFormat);

        return simpleDateFormat.format(c.getTime());
    }

    /**
     * Set all children of the given layout to the given enabled state
     *
     * @param layout  Layout to find the children
     * @param enabled State in which to put the children
     */
    public static void setChildrenEnabled(@NonNull ViewGroup layout, @NonNull Boolean enabled)
    {
        layout.setEnabled(enabled);

        for (int i = 0; i < layout.getChildCount(); i++)
        {
            View child = layout.getChildAt(i);

            if (child instanceof ViewGroup)
                setChildrenEnabled((ViewGroup) child, enabled);
            else
                child.setEnabled(enabled);
        }
    }
}
