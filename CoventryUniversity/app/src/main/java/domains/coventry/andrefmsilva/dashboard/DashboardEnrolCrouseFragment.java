/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 :                                                  :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : DashboardEnrolCrouseFragment                     :
 : Last modified 10 Nov 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Objects;

import domains.coventry.andrefmsilva.coventryuniversity.MainActivity;
import domains.coventry.andrefmsilva.coventryuniversity.R;
import domains.coventry.andrefmsilva.utils.MySQLConnector;
import domains.coventry.andrefmsilva.views.TitledTextView;

import static domains.coventry.andrefmsilva.coventryuniversity.MainActivity.setToolbarText;
import static domains.coventry.andrefmsilva.utils.Utils.APP_DATE_FORMAT;
import static domains.coventry.andrefmsilva.utils.Utils.SQL_DATE_FORMAT;
import static domains.coventry.andrefmsilva.utils.Utils.formatDate;
import static domains.coventry.andrefmsilva.utils.Utils.setChildrenEnabled;

public class DashboardEnrolCrouseFragment extends Fragment implements MySQLConnector
{
    ViewGroup enrolcourseLayout;
    TitledTextView ttlTxtName;
    TitledTextView ttlTxtCode;
    TitledTextView ttlTxtType;
    TitledTextView ttlTxtStudyOption;
    TitledTextView ttlTxtYear;
    TitledTextView ttlTxtStart;
    TitledTextView ttlTxtEnd;
    TitledTextView ttlTxtLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dashboard_fragment_enrolcourse, container, false);

        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.enrol_course, R.string.dashboard_enrolement);

        enrolcourseLayout = view.findViewById(R.id.enrolcourse_layout);
        ttlTxtName = view.findViewById(R.id.enrolcourse_name);
        ttlTxtCode = view.findViewById(R.id.enrolcourse_code);
        ttlTxtType = view.findViewById(R.id.enrolcourse_type);
        ttlTxtStudyOption = view.findViewById(R.id.enrolcourse_study_option);
        ttlTxtYear = view.findViewById(R.id.enrolcourse_year);
        ttlTxtStart = view.findViewById(R.id.enrolcourse_start);
        ttlTxtEnd = view.findViewById(R.id.enrolcourse_end);
        ttlTxtLocation = view.findViewById(R.id.enrolcourse_location);

        view.findViewById(R.id.enrolcourse_confirm).setOnClickListener(v ->
        {
            if (!ttlTxtName.isTextEmpty() && !ttlTxtCode.isTextEmpty() && !ttlTxtType.isTextEmpty() && !ttlTxtStudyOption.isTextEmpty()
                    && !ttlTxtYear.isTextEmpty() && !ttlTxtStart.isTextEmpty() && !ttlTxtEnd.isTextEmpty() && !ttlTxtLocation.isTextEmpty())
            {
                HashMap<String, String> requestInfo = new HashMap<>();
                requestInfo.put("type", "confirm_course_registration");
                requestInfo.put("id", String.valueOf(((MainActivity) Objects.requireNonNull(getActivity())).getUserID()));

                new connectMySQL(new WeakReference<>(DashboardEnrolCrouseFragment.this), FILE_ENROL, requestInfo, "Confirming Course", false).execute();
            }
        });

        view.findViewById(R.id.enrolcourse_cancel).setOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());

        connectWithRetry();

        return view;
    }

    @Override
    public void connectWithRetry()
    {
        HashMap<String, String> requestInfo = new HashMap<>();
        requestInfo.put("type", "get_course_registration");
        requestInfo.put("id", String.valueOf(((MainActivity) Objects.requireNonNull(getActivity())).getUserID()));

        new connectMySQL(new WeakReference<>(this), FILE_ENROL, requestInfo, "Registered Course").execute();
    }

    @Override
    public void connectionStarted()
    {
        // Disable all layout children
        setChildrenEnabled(enrolcourseLayout, false);
    }

    @Override
    public void connectionSuccessful(HashMap<String, String> results)
    {
        // If not empty, it's because was trying to get info from teh database
        if (results.size() > 0)
        {
            ttlTxtName.setText(results.get("name"));
            ttlTxtCode.setText(results.get("code"));
            ttlTxtType.setText(results.get("type"));
            ttlTxtStudyOption.setText(results.get("study_option"));
            ttlTxtYear.setText(results.get("course_year"));
            ttlTxtLocation.setText(results.get("location"));


            try
            {
                ttlTxtStart.setText(formatDate(Objects.requireNonNull(results.get("start_date")), SQL_DATE_FORMAT, APP_DATE_FORMAT));
                ttlTxtEnd.setText(formatDate(Objects.requireNonNull(results.get("end_date")), SQL_DATE_FORMAT, APP_DATE_FORMAT));
            }
            catch (ParseException e)
            {
                Log.e("DashboardEnrolCrouseFragment", e.getMessage(), e);
            }

            // Enable all layout children
            setChildrenEnabled(enrolcourseLayout, true);
        }
        else
        {
            Toast.makeText(getContext(), "Course registered successfully.", Toast.LENGTH_SHORT).show();
            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    }

    @Override
    public void connectionUnsuccessful(Boolean canRetry)
    {
        if (!canRetry)
        {
            Toast.makeText(getContext(), "Unable to connect, please try again.", Toast.LENGTH_LONG).show();

            // Enable all layout children
            setChildrenEnabled(enrolcourseLayout, true);
        }
    }
}
