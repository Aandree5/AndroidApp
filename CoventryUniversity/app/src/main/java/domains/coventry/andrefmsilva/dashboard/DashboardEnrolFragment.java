/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 :                                                  :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : DashboardEnrolFragment                           :
 : Last modified 10 Nov 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

import domains.coventry.andrefmsilva.views.EnrolButton;
import domains.coventry.andrefmsilva.coventryuniversity.MainActivity;
import domains.coventry.andrefmsilva.utils.MySQLConnector;
import domains.coventry.andrefmsilva.coventryuniversity.R;

import static domains.coventry.andrefmsilva.coventryuniversity.MainActivity.setToolbarText;
import static domains.coventry.andrefmsilva.utils.Utils.setChildrenEnabled;

public class DashboardEnrolFragment extends Fragment implements MySQLConnector
{
    ScrollView enrolLayout;
    EnrolButton enrolCourseRegistration;
    EnrolButton enrolItRegistration;
    EnrolButton enrolFinancialRegistration;
    EnrolButton enrolEmergencyContactsRegistration;
    EnrolButton enrolIdCardPhoto;
    EnrolButton enrolDisabilityRegistration;
    EnrolButton enrolAddressRegistration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dashboard_fragment_enrol, container, false);

        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.dashboard_enrolement, R.string.app_name);

        enrolLayout = view.findViewById(R.id.enrol_layout);
        enrolCourseRegistration = view.findViewById(R.id.enrol_course_registration);
        enrolItRegistration = view.findViewById(R.id.enrol_it_registration);
        enrolFinancialRegistration = view.findViewById(R.id.enrol_financial_registration);
        enrolEmergencyContactsRegistration = view.findViewById(R.id.enrol_emergency_contact_registration);
        enrolIdCardPhoto = view.findViewById(R.id.enrol_id_card_photo);
        enrolDisabilityRegistration = view.findViewById(R.id.enrol_disability_registration);
        enrolAddressRegistration = view.findViewById(R.id.enrol_address_registration);

        enrolCourseRegistration.setOnClickListener(v -> Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new DashboardEnrolCrouseFragment())
                .addToBackStack(null)
                .commit());

        enrolItRegistration.setOnClickListener(v -> Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new DashboardEnrolItFragment())
                .addToBackStack(null)
                .commit());

        enrolFinancialRegistration.setOnClickListener(v -> Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new DashboardEnrolFinancialFragment())
                .addToBackStack(null)
                .commit());

        enrolEmergencyContactsRegistration.setOnClickListener(v -> Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new DashboardEnrolEmergencyContacts())
                .addToBackStack(null)
                .commit());

        enrolIdCardPhoto.setOnClickListener(v -> Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new DashboardEnrolPhotoFragment())
                .addToBackStack(null)
                .commit());

        enrolDisabilityRegistration.setOnClickListener(v -> Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new DashboardEnrolDisabilityFragment())
                .addToBackStack(null)
                .commit());

        enrolAddressRegistration.setOnClickListener(v -> Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new DashboardEnrolAddressFragment())
                .addToBackStack(null)
                .commit());

        view.findViewById(R.id.enrol_vote_register).setOnClickListener(v ->
        {
            String url = "https://www.gov.uk/get-on-electoral-register";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));

            startActivity(intent);
        });

        view.findViewById(R.id.enrol_vote_information).setOnClickListener(v ->
        {
            String url = "http://www.coventry.gov.uk/info/8/elections_and_voting/765/registering_to_vote/4";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));

            startActivity(intent);
        });


        connectWithRetry();

        return view;
    }

    @Override
    public void connectWithRetry()
    {
        HashMap<String, String> requestInfo = new HashMap<>();
        requestInfo.put("type", "getState");
        requestInfo.put("id", String.valueOf(((MainActivity) Objects.requireNonNull(getActivity())).getUserID()));

        new connectMySQL(new WeakReference<>(this), FILE_ENROL, requestInfo, "Enrolment Status").execute();
    }

    @Override
    public void connectionStarted()
    {
        // Disable all layout children
        setChildrenEnabled(enrolLayout, false);
    }

    @Override
    public void connectionSuccessful(HashMap<String, String> results)
    {
        // Enable all layout children
        setChildrenEnabled(enrolLayout, true);

        enrolCourseRegistration.setActivated(Objects.equals(results.get("course_registration"), "1"));
        enrolItRegistration.setActivated(Objects.equals(results.get("it_registration"), "1"));
        enrolFinancialRegistration.setActivated(Objects.equals(results.get("financial_registration"), "1"));
        enrolEmergencyContactsRegistration.setActivated(Objects.equals(results.get("emergency_contacts_registration"), "1"));
        enrolIdCardPhoto.setActivated(Objects.equals(results.get("id_card_photo"), "1"));
        enrolDisabilityRegistration.setActivated(Objects.equals(results.get("disability_registration"), "1"));
        enrolAddressRegistration.setActivated(Objects.equals(results.get("address_registration"), "1"));
    }
}
