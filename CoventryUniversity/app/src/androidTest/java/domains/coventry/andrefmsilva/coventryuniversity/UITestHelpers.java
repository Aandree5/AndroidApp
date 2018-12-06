/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : UITestHelpers.java                               :
 : Last modified 06 Dec 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.coventryuniversity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;

class UITestHelpers
{
    private static Matcher<String> getResourceString(int id)
    {
        Context targetContext = InstrumentationRegistry.getTargetContext();
        return is(targetContext.getResources().getString(id));
    }

    @NonNull
    static ViewAction selectTabAtPosition(final int position)
    {
        return new ViewAction()
        {
            @Override
            public Matcher<View> getConstraints()
            {
                return allOf(isDisplayed(), isAssignableFrom(TabLayout.class));
            }

            @Override
            public String getDescription()
            {
                return "with tab at index" + String.valueOf(position);
            }

            @Override
            public void perform(UiController uiController, View view)
            {
                if (view instanceof TabLayout)
                {
                    TabLayout tabLayout = (TabLayout) view;
                    TabLayout.Tab tab = tabLayout.getTabAt(position);

                    if (tab != null)
                    {
                        tab.select();
                    }
                }
            }
        };
    }


    @NonNull
    static Matcher<View> toolbarHasText(final Matcher<String> title)
    {
        return new BoundedMatcher<View, Toolbar>(Toolbar.class)
        {

            @Override
            public void describeTo(Description description)
            {
                description.appendText("Checking tootlbar title: " + title);
            }

            @Override
            protected boolean matchesSafely(Toolbar toolbar)
            {
                return title.matches(toolbar.getTitle());
            }
        };
    }

    @NonNull
    static Matcher<View> toolbarHasText(final Matcher<String> title, final Matcher<String> subtitle)
    {
        return new BoundedMatcher<View, Toolbar>(Toolbar.class)
        {
            @Override
            public void describeTo(Description description)
            {
                description.appendText("Checking tootlbar title: " + title);
                description.appendText(" and subtitle: " + subtitle.toString());
            }

            @Override
            protected boolean matchesSafely(Toolbar foundView)
            {
                return title.matches(foundView.getTitle()) && subtitle.matches(foundView.getSubtitle());
            }
        };
    }

    @NonNull
    static Matcher<View> toolbarHasText(final int title)
    {
        return toolbarHasText(getResourceString(title));
    }

    @NonNull
    static Matcher<View> toolbarHasText(final int title, final int subtitle)
    {
        return toolbarHasText(getResourceString(title), getResourceString(subtitle));
    }
}
