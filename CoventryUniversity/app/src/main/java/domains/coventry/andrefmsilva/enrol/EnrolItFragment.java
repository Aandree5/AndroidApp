/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 :                                                  :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : EnrolItFragment                         :
 : Last modified 10 Nov 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.enrol;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

import domains.coventry.andrefmsilva.views.TitledTextView;
import domains.coventry.andrefmsilva.coventryuniversity.MainActivity;
import domains.coventry.andrefmsilva.coventryuniversity.R;
import domains.coventry.andrefmsilva.utils.InfoDialog;
import domains.coventry.andrefmsilva.utils.MySQLConnector;

import static domains.coventry.andrefmsilva.utils.Utils.setToolbarText;
import static domains.coventry.andrefmsilva.utils.Utils.setChildrenEnabled;

public class EnrolItFragment extends Fragment implements MySQLConnector {
    ViewGroup enrolItLayout;
    TitledTextView ttvUsername;
    TitledTextView ttvEmail;
    EditText editTPassword;
    EditText editTPasswordConfirm;
    Button btnConfirm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enrolit, container, false);

        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.enrol_it, R.string.enrolement);

        enrolItLayout = view.findViewById(R.id.enrolit_layout);

        connectWithRetry();

        ttvUsername = view.findViewById(R.id.enrolit_username);
        ttvEmail = view.findViewById(R.id.enrolit_email);
        editTPassword = view.findViewById(R.id.enrolit_password);
        editTPasswordConfirm = view.findViewById(R.id.enrolit_password_confirm);
        btnConfirm = view.findViewById(R.id.enrolit_confirm);

        btnConfirm.setOnClickListener(v ->
        {
            if (!editTPassword.getText().toString().equals("") && !editTPasswordConfirm.getText().toString().equals("") &&
                    editTPassword.getText().toString().equals(editTPasswordConfirm.getText().toString()) && !ttvUsername.isTextEmpty() && !ttvEmail.isTextEmpty()) {
                HashMap<String, String> requestInfo = new HashMap<>();
                requestInfo.put("type", "confirm_it_registration");
                requestInfo.put("id", String.valueOf(MainActivity.getUserID()));
                requestInfo.put("password", editTPassword.getText().toString());

                new connectMySQL(new WeakReference<>(this), FILE_ENROL, requestInfo, "IT Services Status", false).execute();
            } else {
                Toast.makeText(getContext(), R.string.dashboard_passwords_no_match, Toast.LENGTH_LONG).show();
            }
        });

        editTPasswordConfirm.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_DONE)
                btnConfirm.performClick();

            // Return false so system will handle action after and close the soft keyboard
            return false;
        });

        return view;
    }

    @Override
    public void connectWithRetry() {
        HashMap<String, String> requestInfo = new HashMap<>();
        requestInfo.put("type", "get_it_registration");
        requestInfo.put("id", String.valueOf(MainActivity.getUserID()));

        new connectMySQL(new WeakReference<>(this), FILE_ENROL, requestInfo, "Checking IT Status").execute();
    }

    @Override
    public void connectionStarted() {
        // Disable all layout children
        setChildrenEnabled(enrolItLayout, false);
    }

    @Override
    public void connectionSuccessful(HashMap<String, String> results) {
        if (results.size() > 0) {
            ttvUsername.setText(results.get("username"));
            ttvEmail.setText(results.get("email"));

            // Enable all layout children
            setChildrenEnabled(enrolItLayout, true);
        } else {
            SharedPreferences.Editor sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext())).edit();
            sharedPreferences.putString("status", "logged");
            sharedPreferences.putString("username", ttvUsername.getText());
            sharedPreferences.putString("email", ttvEmail.getText());
            sharedPreferences.apply();

            Toast.makeText(getContext(), "IT registered successfully.", Toast.LENGTH_SHORT).show();

            InfoDialog.newIntance(InfoDialog.Type.OK, getString(R.string.enrolit_loggedin), getString(R.string.enrolit_you_have_been_logged))
                    .showNow(Objects.requireNonNull(getFragmentManager()), null);

            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    }
}
