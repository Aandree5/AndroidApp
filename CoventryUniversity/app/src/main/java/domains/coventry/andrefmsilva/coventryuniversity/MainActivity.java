package domains.coventry.andrefmsilva.coventryuniversity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import java.util.Objects;

import domains.coventry.andrefmsilva.dashboard.DashboardFragment;

public class MainActivity extends AppCompatActivity
{
    private DrawerLayout drawerLayout;
    private UserStatus status = UserStatus.NONE;
    private int userID = 0;
    private String name = null;
    private String username = null;
    private String email = null;

    // Variable can't be local, so it doesn't get garbadge collected
    @SuppressWarnings("FieldCanBeLocal")
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    public enum UserStatus
    {
        NONE,
        LOGGED,
        ENROLMENT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a new action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.nav_dashboard);
        toolbar.setSubtitle(R.string.app_name);

        drawerLayout = findViewById(R.id.drawer_layout);

        // Find the navigation view and set an item selected listner that is implemented in this class
        // Returns true for handled click and false otherwise
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem ->
        {
            // Set toolbar title and subtitle
            // Always set subtitle because it changes on some options
            toolbar.setTitle(menuItem.getTitle());
            toolbar.setSubtitle(R.string.app_name);

            switch (menuItem.getItemId())
            {
                case R.id.nav_news:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new NewsFragment())
                            .addToBackStack(null)
                            .commit();

                    // Update subtitle info
                    toolbar.setSubtitle(menuItem.getTitle());
                    break;
                case R.id.nav_dashboard:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new DashboardFragment())
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.nav_timetable:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new TimetableFragment())
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.nav_library:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new LibraryFragment())
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.nav_settings:
                    Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_about:
                    getPreferences(Context.MODE_PRIVATE).edit().clear().apply();
                    readSharedPreferences();
                    Toast.makeText(MainActivity.this, "Cleared Shared Preferences", Toast.LENGTH_LONG).show();
                    break;
            }

            // Close drawer after choosing an option
            drawerLayout.closeDrawer(GravityCompat.START);

            return true;
        });

        // Add and sync the button to toggle the drawer in the toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Update activity info everytime shared preferences is changed
        // getPreferences only olds a weak reference to the listener, so we need to have a strong reference in order for it to not be garbadge collected
        listener = (sharedPreferences, key) -> readSharedPreferences();
        getPreferences(Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(listener);

        // When de device is rotated, or the app is returned from the background, the activity gets destroyd and its redrawn
        // When the previous state should maintain the same (the selected option from the drawer)
        if (savedInstanceState == null)
        {
            readSharedPreferences();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();

            navigationView.setCheckedItem(R.id.nav_dashboard);
        }
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Objects.requireNonNull(getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment"))).commit();

        // Initialize twitter api to get data for the news section
        TwitterConfig config = new TwitterConfig.Builder(this).logger(new DefaultLogger(Log.DEBUG)).twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.twitter_key), getResources().getString(R.string.twitter_secret))).debug(true).build();
        Twitter.initialize(config);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "currentFragment", Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.fragment_container)));
    }

    @Override
    public void onBackPressed()
    {
        // If drawer is open then closes it, otherwise fallback to the back button action
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);

        else
            super.onBackPressed();
    }

    /**
     * Update navigation drawer with user loaded info, from main activity
     */
    public void updateDrawer()
    {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        Menu menuView = navigationView.getMenu();

        ((TextView) headerView.findViewById(R.id.nav_user_name)).setText(name);
        ((TextView) headerView.findViewById(R.id.nav_user_email)).setText(email);
        ((TextView) headerView.findViewById(R.id.nav_user_id)).setText(String.valueOf(userID));

        // Only show for logged in users
        menuView.findItem(R.id.nav_timetable).setEnabled(!username.equals(""));
        menuView.findItem(R.id.nav_library).setEnabled(!username.equals(""));
    }

    /**
     * Update main activity variables with user info, saved on shared preferences
     * Calls updateDrawer
     */
    private void readSharedPreferences()
    {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        if (Objects.equals(sharedPreferences.getString("status", null), "logged"))
        {
            userID = sharedPreferences.getInt("id", 0);
            name = sharedPreferences.getString("name", null);
            username = sharedPreferences.getString("username", null);
            email = sharedPreferences.getString("email", null);

            status = UserStatus.LOGGED;
        }
        else if (Objects.equals(sharedPreferences.getString("status", null), "enrolment"))
        {
            userID = sharedPreferences.getInt("id", 0);
            name = sharedPreferences.getString("name", null);
            username = "";
            email = "";

            status = UserStatus.ENROLMENT;
        }
        else
        {
            userID = 0;
            name = "";
            username = "";
            email = "";

            status = UserStatus.NONE;
        }

        updateDrawer();
    }

    /**
     * Set toolbar title
     *
     * @param activity Activity from where to get the toolbar
     * @param title    Title to set on the toolbar
     */
    public static void setToolbarText(@NonNull AppCompatActivity activity, String title)
    {
        Objects.requireNonNull(activity.getSupportActionBar()).setSubtitle(title);
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
     * Get the user status
     *
     * @return UserStatus of the current user status
     */
    public UserStatus getStatus()
    {
        return status;
    }

    /**
     * Get user ID
     *
     * @return The user ID
     */
    public int getUserID()
    {
        return userID;
    }

    /**
     * Get user name
     *
     * @return The user name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get user username
     *
     * @return The user username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Get user email
     *
     * @return The user email
     */
    public String getEmail()
    {
        return email;
    }
}
