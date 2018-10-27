package domains.coventry.andrefmsilva.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

import domains.coventry.andrefmsilva.CustomViews.EnrolButton;
import domains.coventry.andrefmsilva.CustomViews.LoadingView;
import domains.coventry.andrefmsilva.coventryuniversity.MainActivity;
import domains.coventry.andrefmsilva.utils.MySQLConnector;
import domains.coventry.andrefmsilva.coventryuniversity.R;

import static domains.coventry.andrefmsilva.utils.Utils.setChildsEnabled;

public class DashboardEnrolFragment extends Fragment implements MySQLConnector
{
    ScrollView enrolLayout;
    EnrolButton enrolCourseRegistration;
    EnrolButton enrolItRegistration;
    EnrolButton enrolFinancialRegistration;
    EnrolButton enrolModulesRegistration;
    EnrolButton enrolEmergencyContactsRegistration;
    EnrolButton enrolIdCardPhoto;
    EnrolButton enrolDisabilityRegistration;
    EnrolButton enrolAddressRegistration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dashboard_fragment_enrol, container, false);

        ((MainActivity)Objects.requireNonNull(getActivity(), "Activity must not be null.")).setToolbarText(R.string.dashboard_enrolement);

        HashMap<String, String> requestInfo = new HashMap<>();
        requestInfo.put("type", "getState");
        requestInfo.put("id", String.valueOf(((MainActivity)getActivity()).getUserID()));


        enrolLayout = view.findViewById(R.id.enrol_layout);

        new connectMySQL(new WeakReference<>(this), FILE_ENROL, requestInfo, "Checking Enrolment Status").execute();

        enrolCourseRegistration = view.findViewById(R.id.enrol_course_registration);
        enrolItRegistration = view.findViewById(R.id.enrol_it_registration);
        enrolFinancialRegistration = view.findViewById(R.id.enrol_financial_registration);
        enrolModulesRegistration = view.findViewById(R.id.enrol_modules_registration);
        enrolEmergencyContactsRegistration = view.findViewById(R.id.enrol_emergency_contact_registration);
        enrolIdCardPhoto = view.findViewById(R.id.enrol_id_card_photo);
        enrolDisabilityRegistration = view.findViewById(R.id.enrol_disability_registration);
        enrolAddressRegistration = view.findViewById(R.id.enrol_address_registration);

        enrolCourseRegistration.setOnClickListener(v ->
        {
            Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new DashboardEnrolCrouseFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void connectionStarted()
    {
        setChildsEnabled(enrolLayout, false);
    }

    @Override
    public void connectionSuccessful(HashMap<String, String> results)
    {
        setChildsEnabled(enrolLayout, true);

        enrolCourseRegistration.setActivated(Objects.equals(results.get("course_registration"), "1"));
        enrolItRegistration.setActivated(Objects.equals(results.get("it_registration"), "1"));
        enrolFinancialRegistration.setActivated(Objects.equals(results.get("financial_registration"), "1"));
        enrolModulesRegistration.setActivated(Objects.equals(results.get("modules_registration"), "1"));
        enrolEmergencyContactsRegistration.setActivated(Objects.equals(results.get("emergency_contacts_registration"), "1"));
        enrolIdCardPhoto.setActivated(Objects.equals(results.get("id_card_photo"), "1"));
        enrolDisabilityRegistration.setActivated(Objects.equals(results.get("disability_registration"), "1"));
        enrolAddressRegistration.setActivated(Objects.equals(results.get("address_registration"), "1"));
    }

    @Override
    public void connectionUnsuccessful()
    {
        Toast.makeText(getContext(), "Unable to connect to server, please try again.", Toast.LENGTH_LONG).show();

        SharedPreferences.Editor sharedPreferencesEditor = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE).edit();
        sharedPreferencesEditor.remove("status");
        sharedPreferencesEditor.remove("id");
        sharedPreferencesEditor.remove("name");
        sharedPreferencesEditor.remove("birth_date");
        sharedPreferencesEditor.apply();

        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
    }
}