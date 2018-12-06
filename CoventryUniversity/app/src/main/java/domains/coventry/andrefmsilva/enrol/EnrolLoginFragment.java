/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : EnrolLoginFragment.java                          :
 : Last modified 06 Dec 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.enrol;


import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import domains.coventry.andrefmsilva.coventryuniversity.EnrolFragment;
import domains.coventry.andrefmsilva.dashboard.DashboardInfoFragment;
import domains.coventry.andrefmsilva.utils.MySQLConnector;
import domains.coventry.andrefmsilva.coventryuniversity.R;

import static domains.coventry.andrefmsilva.utils.Utils.setToolbarText;
import static domains.coventry.andrefmsilva.utils.Utils.APP_DATE_FORMAT;
import static domains.coventry.andrefmsilva.utils.Utils.SQL_DATE_FORMAT;
import static domains.coventry.andrefmsilva.utils.Utils.formatDate;
import static domains.coventry.andrefmsilva.utils.Utils.setChildrenEnabled;

public class EnrolLoginFragment extends Fragment implements MySQLConnector, DatePickerDialog.OnDateSetListener {
    ViewGroup layoutEnrolLogin;
    EditText editTxtStudentId;
    EditText editTxtBirthDate;
    Button btnEnrol;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enrollogin, container, false);

        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.dashboard_tab_enrol, R.string.app_name);

        layoutEnrolLogin = view.findViewById(R.id.enrollogin_layout);
        editTxtStudentId = view.findViewById(R.id.enrollogin_studentid);
        editTxtBirthDate = view.findViewById(R.id.enrollogin_birthdate);
        btnEnrol = view.findViewById(R.id.enrollogin_enrol);

        editTxtStudentId.requestFocus();

        editTxtBirthDate.setOnFocusChangeListener((v, hasFocus) ->
        {
            if (hasFocus) {
                Calendar c = Calendar.getInstance();

                if (!editTxtBirthDate.getText().toString().equals("")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(APP_DATE_FORMAT, Locale.UK);

                    try {
                        c.setTime(simpleDateFormat.parse(editTxtBirthDate.getText().toString()));
                    } catch (ParseException e) {
                        Log.e("editTxtBirthDate.setOnFocusChangeListener", e.getMessage(), e);
                    }

                } else
                    c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 18); // If no date was set on the editbox, start 18 years earlier, usually is when people start university


                DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getContext()), EnrolLoginFragment.this,
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.setOnCancelListener(dialog -> editTxtBirthDate.clearFocus());
                datePickerDialog.setOnDismissListener(dialog -> editTxtBirthDate.clearFocus());

                datePickerDialog.show();
            }
        });

        btnEnrol.setOnClickListener(v ->
        {

            HashMap<String, String> resquestInfo = new HashMap<>();
            resquestInfo.put("type", "login");
            resquestInfo.put("id", editTxtStudentId.getText().toString());

            try {
                resquestInfo.put("birth_date", formatDate(editTxtBirthDate.getText().toString(), APP_DATE_FORMAT, SQL_DATE_FORMAT));
            } catch (ParseException e) {
                Log.e("btnEnrol.setOnClickListener", e.getMessage(), e);
            }

            new connectMySQL(new WeakReference<>(EnrolLoginFragment.this), FILE_ENROL, resquestInfo, "Login Details", false).execute();
        });

        return view;
    }

    @Override
    public void connectionStarted() {
        // Disable all layout children
        setChildrenEnabled(layoutEnrolLogin, false);
    }

    @Override
    public void connectionSuccessful(HashMap<String, String> results) {
        SharedPreferences.Editor sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext())).edit();
        sharedPreferences.putString("status", "enrolment");
        sharedPreferences.putInt("id", Integer.valueOf(Objects.requireNonNull(results.get("id"))));
        sharedPreferences.putString("name", String.format("%s, %s", results.get("last_name"), results.get("first_name")));
        sharedPreferences.apply();

        Toast.makeText(getContext(), R.string.dashboard_login_successful, Toast.LENGTH_SHORT).show();

        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new DashboardInfoFragment())
                .commit();
    }

    @Override
    public void connectionUnsuccessful(Boolean canRetry) {
        Toast.makeText(getContext(), R.string.dashboard_incorrect_id_date, Toast.LENGTH_LONG).show();

        // Enable layout children
        setChildrenEnabled(layoutEnrolLogin, true);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(APP_DATE_FORMAT, Locale.UK);

        c.set(year, month, dayOfMonth);

        editTxtBirthDate.setText(simpleDateFormat.format(c.getTime()));
        editTxtBirthDate.clearFocus();

        btnEnrol.performClick();
    }
}
