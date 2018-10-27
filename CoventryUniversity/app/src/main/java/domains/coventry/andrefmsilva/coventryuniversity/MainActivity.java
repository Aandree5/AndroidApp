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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import java.util.Objects;

import domains.coventry.andrefmsilva.CustomViews.LoadingView;
import domains.coventry.andrefmsilva.dashboard.DashboardFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout drawerLayout;
    private UserStatus status = UserStatus.NONE;
    private int userID = 0;
    private String name = null;
    private String username = null;
    private String email = null;
    private static LoadingView loadingView;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    public enum UserStatus
    {
        NONE,
        LOGGED,
        ENROLMENT
    }

    public static LoadingView getLoadginView()
    {
        return loadingView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a new status bar, as a toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set toolbar title and subtitle first
        toolbar.setTitle(R.string.nav_dashboard);
        toolbar.setSubtitle(R.string.app_name);

        // Find the draier view and store a reference
        drawerLayout = findViewById(R.id.drawer_layout);

        // Find the navigation view and set an item selected listner that is implemented in this class
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Add the toggle butto to the top of the app, to open and close the app
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // Add and sync toggle button with drawer
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Update activity info everytime shared preferences is changed
        // getPreferences only olds a weak reference to the listener, so we need to have a strong reference in order for it to not be garbadge collected
        listener = (sharedPreferences, key) -> readSharedPreferences();
        getPreferences(Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(listener);

        // Default fragment when app starts
        // If app is starting from frest the savedInstanceState will be null
        // Used because when de device is rotated the activity gets destroyd and its redrawn
        // When the previous state should maintain the same (the selected option from the drawer)
        // The same when opening several apps and the device tries to free memory but after the user  wants to get back to the app
        if (savedInstanceState == null)
        {
            readSharedPreferences();

            // Load the given fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();

            // Show selected item to user
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Objects.requireNonNull(getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment"))).commit();

        // Initialize twitter api to get data for the news section
        TwitterConfig config = new TwitterConfig.Builder(this).logger(new DefaultLogger(Log.DEBUG)).twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.twitter_key), getResources().getString(R.string.twitter_secret))).debug(true).build();


        Twitter.initialize(config);

        loadingView = findViewById(R.id.loadingview);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "currentFragment", Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.fragment_container)));
    }

    // Called when an item is pressed inside the drawer
    // With clicked menu item as input, returns true for handled click and false otherwise
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Set toolbar title and subtitle
        // Always set subtitle because when user chooses news the subtutle changes, all the other is the same
        toolbar.setTitle(menuItem.getTitle());
        toolbar.setSubtitle(R.string.app_name);


        // Fiind wich button was pressed and load the respective fragment
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
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_about:
                getPreferences(Context.MODE_PRIVATE).edit().clear().apply();
                readSharedPreferences();
                Toast.makeText(this, "Cleared Shared Preferences", Toast.LENGTH_LONG).show();
                break;
        }

        // Close drawer after choosing an option
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    // Called when back button is pressed
    // If drawer is open then closes it, otherwise fallback to the back button action
    @Override
    public void onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    public void updateDrawer()
    {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        Menu menuView = navigationView.getMenu();

        ((TextView) headerView.findViewById(R.id.nav_user_name)).setText(name);
        ((TextView) headerView.findViewById(R.id.nav_user_email)).setText(email);
        ((TextView) headerView.findViewById(R.id.nav_user_id)).setText(String.valueOf(userID));

        menuView.findItem(R.id.nav_timetable).setEnabled(!username.equals(""));
        menuView.findItem(R.id.nav_library).setEnabled(!username.equals(""));
    }

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
        else if(Objects.equals(sharedPreferences.getString("status", null), "enrolment"))
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




    public void setToolbarText(String title)
    {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    public void setToolbarText(String title, String subtitle)
    {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(subtitle);
    }

    public void setToolbarText(int title)
    {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    public void setToolbarText(int title, int subtitle)
    {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(subtitle);
    }



    // Getters

    public UserStatus getStatus()
    {
        return status;
    }

    public int getUserID()
    {
        return userID;
    }

    public String getName()
    {
        return name;
    }

    public String getUsername()
    {
        return username;
    }

    public String getEmail()
    {
        return email;
    }
}
