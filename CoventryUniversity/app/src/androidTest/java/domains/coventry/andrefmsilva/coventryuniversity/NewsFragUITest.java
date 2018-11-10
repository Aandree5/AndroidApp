package domains.coventry.andrefmsilva.coventryuniversity;

import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.INVISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static domains.coventry.andrefmsilva.coventryuniversity.UITestHelpers.selectTabAtPosition;
import static domains.coventry.andrefmsilva.coventryuniversity.UITestHelpers.toolbarHasText;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class NewsFragUITest
{

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void openNewsFragment()
    {
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(open());

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_news));
    }

    @Test
    public void clickMoodleNewsTab_LoadsMoodleNews() throws Exception
    {
        onView(withId(R.id.news_tab_layout)).perform(selectTabAtPosition(0));

        onView(withId(R.id.toolbar)).check(matches(toolbarHasText(R.string.news_tab_moodle, R.string.nav_news)));

        Thread.sleep(1000);

        onView(withId(R.id.news_recyclerview)).check(matches(hasMinimumChildCount(1)));

        onView(withId(R.id.news_progressbar)).check(matches(withEffectiveVisibility(INVISIBLE)));
    }

    @Test
    public void clickTwitterNewsTab_LoadsTwitterNews() throws Exception
    {
        onView(withId(R.id.news_tab_layout)).perform(selectTabAtPosition(1));

        onView(withId(R.id.toolbar)).check(matches(toolbarHasText(R.string.news_tab_twitter, R.string.nav_news)));

        Thread.sleep(1000);

        onView(withId(R.id.news_recyclerview)).check(matches(hasMinimumChildCount(1)));

        onView(withId(R.id.news_progressbar)).check(matches(withEffectiveVisibility(INVISIBLE)));
    }

    @Test
    public void clickFacultyNewsTab_LoadsFacultyNews() throws Exception
    {
        onView(withId(R.id.news_tab_layout)).perform(selectTabAtPosition(2));

        onView(withId(R.id.toolbar)).check(matches(toolbarHasText(R.string.news_tab_faculty, R.string.nav_news)));

        Thread.sleep(1000);

        onView(withId(R.id.news_recyclerview)).check(matches(hasMinimumChildCount(1)));

        onView(withId(R.id.news_progressbar)).check(matches(withEffectiveVisibility(INVISIBLE)));
    }
}
