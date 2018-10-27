package domains.coventry.andrefmsilva.CustomViews;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import domains.coventry.andrefmsilva.coventryuniversity.R;

public class LoadingView extends LinearLayout
{
    public LoadingView(Context context)
    {
        super(context);

        init(context, null);
    }

    public LoadingView(Context context, String text)
    {
        super(context);

        init(context, text);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        String text;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadingView, 0, 0);

        try
        {
            text = a.getString(R.styleable.LoadingView_lv_text);
        }
        finally
        {
            a.recycle();
        }

        init(context, text);
    }


    private void init(Context context, String text)
    {

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));



        LayoutInflater.from(context).inflate(R.layout.cv_loadingview, this, true);

        int width = Resources.getSystem().getDisplayMetrics().widthPixels / 2;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels / 2;

        if (height > width)
            height /= 2;
        else if (width > height)
            width /= 2;

        findViewById(R.id.loadingview_frame).setLayoutParams(new LayoutParams(width, height));

        setText(text);
    }

    public void setText(String text)
    {
        TextView textView = findViewById(R.id.loadingview_text);

        textView.setText(text);
        textView.setVisibility((text == null || text.equals("")) ? GONE : VISIBLE);
    }

    public void setText(int text)
    {
        setText(getResources().getString(text));
    }
}
