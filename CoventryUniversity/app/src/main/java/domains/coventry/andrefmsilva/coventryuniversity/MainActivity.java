package domains.coventry.andrefmsilva.coventryuniversity;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.tweetui.TweetUi;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a new status bar, as a toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find the draier view and store a reference
        drawer = findViewById(R.id.drawer_layout);

        // Find the navigation view and set an item selected listner that is implemented in this class
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Add the toggle butto to the top of the app, to open and close the app
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // Add and sync toggle button with drawer
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Default fragment when app starts
        // If app is starting from frest the savedInstanceState will be null
        // Used because when de device is rotated the activity gets destroyd and its redrawn
        // When the previous state should maintain the same (the selected option from the drawer)
        // The same when opening several apps and the device tries to free memory but after the user  wants to get back to the app
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }


        // Initialize twitter api to get data for the news section
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.twitter_key),
                        getResources().getString(R.string.twitter_secret)))
                .debug(true)
                .build();


        Twitter.initialize(config);
    }

    // Called when an item is pressed inside the drawer
    // With clicked menu item as input, returns true for handled click and false otherwise
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        // Fiind wich button was pressed
        switch(menuItem.getItemId()){
            case R.id.nav_news:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewsFragment()).commit();
                break;
            case R.id.nav_dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
                break;
            case R.id.nav_timetable:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TimetableFragment()).commit();
                break;
            case R.id.nav_library:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LibraryFragment()).commit();
                break;
            case R.id.nav_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_about:
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
                break;
        }

        // Close drawer after choosing an option
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    // Called when back button is pressed
    // If drawer is open then closes it, otherwise fallback to the back button action
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}
