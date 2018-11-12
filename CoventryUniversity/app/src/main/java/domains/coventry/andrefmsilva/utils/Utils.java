/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 :                                                  :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : Utils                                            :
 : Last modified 10 Nov 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.utils;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

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

    /**
     * Clear all the children views, clear edittext, uncheck checkbox
     *
     * @param layout Layout to clear the children text
     */
    public static void clearChildren(@NonNull ViewGroup layout)
    {
        for (int i = 0; i < layout.getChildCount(); i++)
        {
            View child = layout.getChildAt(i);

            if (child instanceof ViewGroup)
                clearChildren((ViewGroup) child);
            else if (child instanceof EditText)
                ((EditText) child).setText(null);
            else if (child instanceof CheckBox)
                ((CheckBox) child).setChecked(false);
        }
    }

    /**
     * Set toolbar title
     *
     * @param activity Activity from where to get the toolbar
     * @param title    Title to set on the toolbar
     */
    public static void setToolbarText(@NonNull AppCompatActivity activity, String title)
    {
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle(title);
    }

    /**
     * Set toolbar title and subtitle
     *
     * @param activity Activity from where to get the toolbar
     * @param title    Title to set on the toolbar
     * @param subtitle Subtitle to set on the toolbar
     */
    public static void setToolbarText(@NonNull AppCompatActivity activity, String title, String subtitle)
    {
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle(title);
        Objects.requireNonNull(activity.getSupportActionBar()).setSubtitle(subtitle);
    }

    /**
     * Set toolbar title
     *
     * @param activity Activity from where to get the toolbar
     * @param title    Title to set on the toolbar
     */
    public static void setToolbarText(@NonNull AppCompatActivity activity, int title)
    {
        setToolbarText(activity, activity.getResources().getString(title));
    }

    /**
     * Set toolbar title and subtitle
     *
     * @param activity Activity from where to get the toolbar
     * @param title    Title to set on the toolbar
     * @param subtitle Subtitle to set on the toolbar
     */
    public static void setToolbarText(@NonNull AppCompatActivity activity, int title, int subtitle)
    {
        setToolbarText(activity, activity.getResources().getString(title), activity.getResources().getString(subtitle));
    }

    /**
     * Show back button on activity action bar
     *
     * @param activity Activity to show the back button
     */
    public static void showToolbarBack(@NonNull AppCompatActivity activity)
    {
        ActionBar actionBar = activity.getSupportActionBar();

        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }
}
