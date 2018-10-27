package domains.coventry.andrefmsilva.CustomViews;

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

    public TitledTextView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.cv_titledtextview, this, true);

        String title;
        String text;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TitledTextView, 0, 0);

        try {
            title = a.getString(R.styleable.TitledTextView_ttv_title);
            text = a.getString(R.styleable.TitledTextView_ttv_text);
        } finally {
            a.recycle();
        }

        init(title, text);
    }

    // Setup views
    private void init(String title, String text) {
        setTitle(title);
        setText(text);
    }

    public void setTitle(String title)
    {
        ((TextView)findViewById(R.id.titledtextview_title)).setText(title);
    }

    public void setText(String text)
    {
        ((TextView)findViewById(R.id.titledtextview_text)).setText(text);
    }

    public void setTitle(int title)
    {
        setTitle(getResources().getString(title));
    }

    public void setText(int text)
    {
        setText(getResources().getString(text));
    }

    public String getTitle()
    {
        return ((TextView)findViewById(R.id.titledtextview_title)).getText().toString();
    }

    public String getText()
    {
        return ((TextView)findViewById(R.id.titledtextview_text)).getText().toString();
    }

    public Boolean isTextEmpty()
    {
        return ((TextView)findViewById(R.id.titledtextview_text)).getText().toString().equals("");
    }
}
