/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 :                                                  :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : TitledTextView                                   :
 : Last modified 10 Nov 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import domains.coventry.andrefmsilva.coventryuniversity.R;

public class TitledTextView extends LinearLayout
{

    /**
     * Configure LinearLayout and read view attributes
     *
     * @param context Context to add the view
     * @param attrs   View attributes set in XML
     */
    public TitledTextView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.cv_titledtextview, this, true);

        String title;
        String text;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TitledTextView, 0, 0);

        try
        {
            title = a.getString(R.styleable.TitledTextView_title);
            text = a.getString(R.styleable.TitledTextView_text);
        }
        finally
        {
            a.recycle();
        }

        setTitle(title);
        setText(text);
    }

    /**
     * Set title
     *
     * @param title String for the title
     */
    public void setTitle(String title)
    {
        ((TextView) findViewById(R.id.titledtextview_title)).setText(title);
    }

    /**
     * Set text
     *
     * @param text String for the text
     */
    public void setText(String text)
    {
        ((TextView) findViewById(R.id.titledtextview_text)).setText(text);
    }

    /**
     * Set title
     *
     * @param title Resource id for the title
     */
    public void setTitle(int title)
    {
        setTitle(getResources().getString(title));
    }

    /**
     * Set text
     *
     * @param text Resource id for the text
     */
    public void setText(int text)
    {
        setText(getResources().getString(text));
    }

    /**
     * Get the title as a String
     *
     * @return Title as a String
     */
    public String getTitle()
    {
        return ((TextView) findViewById(R.id.titledtextview_title)).getText().toString();
    }

    /**
     * Get the text as a String
     *
     * @return text as a String
     */
    public String getText()
    {
        return ((TextView) findViewById(R.id.titledtextview_text)).getText().toString();
    }

    /**
     * Check if text is empty
     *
     * @return True if is empty, false otherwise
     */
    public Boolean isTextEmpty()
    {
        return ((TextView) findViewById(R.id.titledtextview_text)).getText().toString().equals("");
    }
}
