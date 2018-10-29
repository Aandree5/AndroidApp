package domains.coventry.andrefmsilva.dashboard;

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

import domains.coventry.andrefmsilva.CustomViews.EnrolButton;
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

        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.dashboard_enrolement, R.string.app_name);

        enrolLayout = view.findViewById(R.id.enrol_layout);

        connectWithRetry();

        enrolCourseRegistration = view.findViewById(R.id.enrol_course_registration);
        enrolItRegistration = view.findViewById(R.id.enrol_it_registration);
        enrolFinancialRegistration = view.findViewById(R.id.enrol_financial_registration);
        enrolModulesRegistration = view.findViewById(R.id.enrol_modules_registration);
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

        return view;
    }

    @Override
    public void connectWithRetry()
    {
        HashMap<String, String> requestInfo = new HashMap<>();
        requestInfo.put("type", "getState");
        requestInfo.put("id", String.valueOf(((MainActivity)Objects.requireNonNull(getActivity())).getUserID()));

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
        enrolModulesRegistration.setActivated(Objects.equals(results.get("modules_registration"), "1"));
        enrolEmergencyContactsRegistration.setActivated(Objects.equals(results.get("emergency_contacts_registration"), "1"));
        enrolIdCardPhoto.setActivated(Objects.equals(results.get("id_card_photo"), "1"));
        enrolDisabilityRegistration.setActivated(Objects.equals(results.get("disability_registration"), "1"));
        enrolAddressRegistration.setActivated(Objects.equals(results.get("address_registration"), "1"));
    }
}
