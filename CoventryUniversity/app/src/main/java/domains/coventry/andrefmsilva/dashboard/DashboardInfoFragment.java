/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 :                                                  :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : DashboardInfoFragment                            :
 : Last modified 10 Nov 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

import domains.coventry.andrefmsilva.coventryuniversity.EnrolActivity;
import domains.coventry.andrefmsilva.views.TitledTextView;
import domains.coventry.andrefmsilva.coventryuniversity.MainActivity;
import domains.coventry.andrefmsilva.coventryuniversity.R;
import domains.coventry.andrefmsilva.utils.MySQLConnector;

import static domains.coventry.andrefmsilva.utils.Utils.setChildrenEnabled;
import static domains.coventry.andrefmsilva.utils.Utils.setToolbarText;

public class DashboardInfoFragment extends Fragment implements MySQLConnector
{
    ImageView imgUserPhoto;
    TitledTextView ttl_year;
    TitledTextView ttl_course;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.nav_dashboard, R.string.app_name);

        // Test
        view.findViewById(R.id.dashboard_enrolment).setOnClickListener(v ->
        {
            Intent intent = new Intent(getContext(), EnrolActivity.class);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                startActivity(intent);
        });

        view.findViewById(R.id.dashboard_enrolment_reset).setOnClickListener(v ->
        {
            HashMap<String, String> requestInfo = new HashMap<>();
            requestInfo.put("id", String.valueOf(MainActivity.getUserID()));

            new MySQLConnector.connectMySQL(new WeakReference<>(this), FILE_RESETENROL, requestInfo, "Reseting...", false).execute();
        });

        ((TitledTextView) view.findViewById(R.id.info_name)).setText(MainActivity.getName());
        ((TitledTextView) view.findViewById(R.id.info_id)).setText(String.valueOf(MainActivity.getUserID()));

        ttl_year = view.findViewById(R.id.info_year);
        ttl_course = view.findViewById(R.id.info_course);
        imgUserPhoto = view.findViewById(R.id.info_photo);

        loadUserData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
    }

    /**
     * Load the user photo from the file, if doesn't exist load default image
     */
    public void loadUserData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (sharedPreferences != null) {
            String year = sharedPreferences.getString("year", null);
            String courseType = sharedPreferences.getString("courseType", null);
            String courseName = sharedPreferences.getString("courseName", null);
            String courseCode = sharedPreferences.getString("courseCode", null);

            if (year != null) {
                setChildrenEnabled(ttl_year, true);
                ttl_year.setText(sharedPreferences.getString("year", null));
            } else {
                setChildrenEnabled(ttl_year, false);
                ttl_year.setText(R.string.info_not_enroled_yet);
            }

            if (courseType != null && courseName != null && courseCode != null) {
                setChildrenEnabled(ttl_course, true);
                ttl_course.setText(String.format("%s %s (%s)", courseType, courseName, courseCode));
            } else {
                setChildrenEnabled(ttl_course, false);
                ttl_course.setText(R.string.info_not_enroled_yet);
            }
            String photo = sharedPreferences.getString("photo", null);
            if (photo != null) {
                FileInputStream fInpStream = null;

                try {
                    fInpStream = Objects.requireNonNull(getContext()).openFileInput(photo);

                    imgUserPhoto.setImageBitmap(BitmapFactory.decodeStream(fInpStream));
                } catch (Exception e) {
                    Log.e("infoLoadingPhoto", e.getMessage(), e);
                } finally {
                    if (fInpStream != null) {
                        try {
                            fInpStream.close();
                        } catch (IOException e) {
                            Log.e("infoLoadingPhoto", e.getMessage(), e);
                        }
                    }
                }
            } else
                imgUserPhoto.setImageResource(R.drawable.ic_user_photo);
        }
    }

    @Override
    public void connectionStarted()
    {
    }

    @Override
    public void connectionSuccessful(HashMap<String, String> results)
    {
        Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void connectionUnsuccessful(Boolean canRetry)
    {
        Toast.makeText(getContext(), "Error connecting to database", Toast.LENGTH_LONG).show();
    }
}
