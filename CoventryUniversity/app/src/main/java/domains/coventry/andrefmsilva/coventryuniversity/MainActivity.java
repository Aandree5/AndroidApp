/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 :                                                  :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : MainActivity                                     :
 : Last modified 10 Nov 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.coventryuniversity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
{
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private static UserStatus status;
    private static int userID;
    private static String name;
    private static String username;
    private static String email;

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
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem ->
        {
            if (navigationView.getCheckedItem() == menuItem)
                return false;

            // Set toolbar title and subtitle
            // Always set subtitle because it changes on some options
            if (!menuItem.getTitle().equals("Settings"))
            {
                toolbar.setTitle(menuItem.getTitle());
                toolbar.setSubtitle(R.string.app_name);
            }

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
                    Intent intent = new Intent(this, SettingsActivity.class);
                    if (intent.resolveActivity(getPackageManager()) != null)
                        startActivityForResult(intent, 1);
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
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(listener);

        // When de device is rotated, or the app is returned from the background, the activity gets destroyd and its redrawn
        // When the previous state should maintain the same fragment
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new DashboardFragment())
                    .addToBackStack(null)
                    .commit();

            navigationView.setCheckedItem(R.id.nav_dashboard);
        }

        readSharedPreferences();

        // Initialize twitter api to get data for the news section
        TwitterConfig config = new TwitterConfig.Builder(this).logger(new DefaultLogger(Log.DEBUG)).twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.twitter_key), getResources().getString(R.string.twitter_secret))).debug(true).build();
        Twitter.initialize(config);
    }

    @Override
    public void onBackPressed()
    {
        // If drawer is open then closes it, otherwise fallback to the back button action
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);

        else
        {
            super.onBackPressed();

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (fragment instanceof NewsFragment)
                navigationView.setCheckedItem(R.id.nav_news);

            else if (fragment instanceof TimetableFragment)
                navigationView.setCheckedItem(R.id.nav_timetable);

            else if (fragment instanceof LibraryFragment)
                navigationView.setCheckedItem(R.id.nav_library);

            else
                navigationView.setCheckedItem(R.id.nav_dashboard);
        }
    }

    /**
     * Update navigation drawer with user loaded info, from main activity
     */
    public void updateDrawer(@Nullable String photo)
    {
        View headerView = navigationView.getHeaderView(0);
        Menu menuView = navigationView.getMenu();

        ((TextView) headerView.findViewById(R.id.nav_user_name)).setText(name);
        ((TextView) headerView.findViewById(R.id.nav_user_email)).setText(email);
        ((TextView) headerView.findViewById(R.id.nav_user_id)).setText(userID != 0 ? String.valueOf(userID) : "");

        // Only show for logged in users
        menuView.findItem(R.id.nav_timetable).setEnabled(!username.equals(""));
        menuView.findItem(R.id.nav_library).setEnabled(!username.equals(""));

        if (photo != null)
        {
            FileInputStream fInpStream = null;

            try
            {
                fInpStream = openFileInput(photo);

                ((ImageView) headerView.findViewById(R.id.nav_user_photo)).setImageBitmap(BitmapFactory.decodeStream(fInpStream));
            }
            catch (Exception e)
            {
                Log.e("updateDrawer", e.getMessage(), e);
            }
            finally
            {
                if (fInpStream != null)
                {
                    try
                    {
                        fInpStream.close();
                    }
                    catch (IOException e)
                    {
                        Log.e("updateDrawer", e.getMessage(), e);
                    }
                }
            }
        }
        else
            ((ImageView) headerView.findViewById(R.id.nav_user_photo)).setImageResource(R.drawable.ic_user_photo);
    }

    /**
     * Update main activity variables with user info, saved on shared preferences
     * Calls updateDrawer
     */
    private void readSharedPreferences()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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

        updateDrawer(sharedPreferences.getString("photo", null));
    }

    /**
     * Log out the user from the app
     */
    public void logOutUser()
    {
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();

        // Have to force reading, should be called by the shared preferences listener, but clear() doesn't notify the listener of the change
        readSharedPreferences();

        // Clear all fragments on back stack, app starts fresh
        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new DashboardFragment())
                .addToBackStack(null)
                .commit();

        Toast.makeText(this, "Log out successful", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            if (data != null && data.getBooleanExtra("logout", false))
                logOutUser();
        }
    }

    /**
     * Get the user status
     *
     * @return UserStatus of the current user status
     */
    public static UserStatus getStatus()
    {
        return status;
    }

    /**
     * Get user ID
     *
     * @return The user ID
     */
    public static int getUserID()
    {
        return userID;
    }

    /**
     * Get user name
     *
     * @return The user name
     */
    public static String getName()
    {
        return name;
    }
}
