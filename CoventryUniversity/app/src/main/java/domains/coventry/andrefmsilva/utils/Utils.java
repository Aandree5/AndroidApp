package domains.coventry.andrefmsilva.utils;

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

    public static String formatDate(String date, String inFormat, String outFormat) throws ParseException
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(inFormat, Locale.UK);
        Calendar c = Calendar.getInstance();

        c.setTime(simpleDateFormat.parse(date));

        simpleDateFormat.applyPattern(outFormat);

        return simpleDateFormat.format(c.getTime());
    }

    public static void setChildsEnabled(ViewGroup layout, Boolean enabled)
    {
        layout.setEnabled(enabled);

        for (int i = 0; i < layout.getChildCount(); i++)
        {
            View child = layout.getChildAt(i);

            if (child instanceof ViewGroup)
                setChildsEnabled((ViewGroup)child, enabled);
            else
                child.setEnabled(enabled);
        }
    }
}
