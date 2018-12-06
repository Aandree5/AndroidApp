/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : EnrolEmergencyContacts.java                      :
 : Last modified 06 Dec 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.enrol;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

import domains.coventry.andrefmsilva.coventryuniversity.MainActivity;
import domains.coventry.andrefmsilva.coventryuniversity.R;
import domains.coventry.andrefmsilva.utils.MySQLConnector;

import static domains.coventry.andrefmsilva.utils.Utils.setToolbarText;
import static domains.coventry.andrefmsilva.utils.Utils.setChildrenEnabled;

public class EnrolEmergencyContacts extends Fragment implements MySQLConnector {
    ViewGroup enrolemergencyLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enrolemergency, container, false);

        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.enrol_emergencycontacts, R.string.app_name);

        enrolemergencyLayout = view.findViewById(R.id.enrolemergency_layout);

        EditText editTxtName1 = view.findViewById(R.id.enrolemergency_name1);
        EditText editTxtAddress1 = view.findViewById(R.id.enrolemergency_address1);
        EditText editTxtPostCode1 = view.findViewById(R.id.enrolemergency_postcode1);
        EditText editTxtCode1 = view.findViewById(R.id.enrolemergency_code1);
        EditText editTxtPhone1 = view.findViewById(R.id.enrolemergency_phone1);
        EditText editTxtEmail1 = view.findViewById(R.id.enrolemergency_email1);
        EditText editTxtName2 = view.findViewById(R.id.enrolemergency_name2);
        EditText editTxtAddress2 = view.findViewById(R.id.enrolemergency_address2);
        EditText editTxtPostCode2 = view.findViewById(R.id.enrolemergency_postcode2);
        EditText editTxtCode2 = view.findViewById(R.id.enrolemergency_code2);
        EditText editTxtPhone2 = view.findViewById(R.id.enrolemergency_phone2);
        EditText editTxtEmail2 = view.findViewById(R.id.enrolemergency_email2);

        view.findViewById(R.id.enrolemergency_confirm).setOnClickListener(v ->
        {
            if (!editTxtName1.getText().toString().isEmpty() && !editTxtAddress1.getText().toString().isEmpty() &&
                    !editTxtPostCode1.getText().toString().isEmpty() && !editTxtCode1.getText().toString().isEmpty() &&
                    !editTxtPhone1.getText().toString().isEmpty() && !editTxtEmail1.getText().toString().isEmpty() &&
                    !editTxtName2.getText().toString().isEmpty() && !editTxtAddress2.getText().toString().isEmpty() &&
                    !editTxtPostCode2.getText().toString().isEmpty() && !editTxtCode2.getText().toString().isEmpty() &&
                    !editTxtPhone2.getText().toString().isEmpty() && !editTxtEmail2.getText().toString().isEmpty()) {

                HashMap<String, String> requestInfo = new HashMap<>();
                requestInfo.put("type", "register_emergency_contacts");
                requestInfo.put("id", String.valueOf(MainActivity.getUserID()));
                requestInfo.put("ec_name1", editTxtName1.getText().toString());
                requestInfo.put("ec_address1", editTxtAddress1.getText().toString());
                requestInfo.put("ec_postcode1", editTxtPostCode1.getText().toString());
                requestInfo.put("ec_phone1", String.format("%s %s", editTxtCode1.getText().toString(), editTxtPhone1.getText().toString()));
                requestInfo.put("ec_email1", editTxtEmail1.getText().toString());
                requestInfo.put("ec_name2", editTxtName2.getText().toString());
                requestInfo.put("ec_address2", editTxtAddress2.getText().toString());
                requestInfo.put("ec_postcode2", editTxtPostCode2.getText().toString());
                requestInfo.put("ec_phone2", String.format("%s %s", editTxtCode2.getText().toString(), editTxtPhone2.getText().toString()));
                requestInfo.put("ec_email2", editTxtEmail2.getText().toString());

                new connectMySQL(new WeakReference<>(this), FILE_ENROL, requestInfo, "Emergency Contacts", false).execute();
            } else {
                Toast.makeText(getContext(), "All field must be filled", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void connectionStarted() {
        // Disable all layout children
        setChildrenEnabled(enrolemergencyLayout, false);
    }

    @Override
    public void connectionSuccessful(HashMap<String, String> results) {
        Toast.makeText(getContext(), "Emergency contacts registered", Toast.LENGTH_SHORT).show();
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    @Override
    public void connectionUnsuccessful(Boolean canRetry) {
        Toast.makeText(getContext(), "Couldn't get data", Toast.LENGTH_SHORT).show();

        // Enable all layout children
        setChildrenEnabled(enrolemergencyLayout, true);
    }
}
