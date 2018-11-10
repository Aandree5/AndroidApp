package domains.coventry.andrefmsilva.views;

import android.content.Context;
import android.util.AttributeSet;

import domains.coventry.andrefmsilva.coventryuniversity.R;

public class EnrolButton extends android.support.v7.widget.AppCompatButton
{
    public EnrolButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Set visual effects for the button
        setBackgroundResource(R.drawable.dr_enrol_button);
        setTextColor(getResources().getColorStateList(R.color.co_enrolbutton_text, context.getTheme()));
    }

    // When button is activated, meaning everything was done, disable the button so the user can press on it again
    @Override
    public void setActivated(boolean activated)
    {
        super.setActivated(activated);

        setEnabled(!activated);
    }
}
