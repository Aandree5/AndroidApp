/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : EnrolFinancialFragment.java                      :
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

import domains.coventry.andrefmsilva.views.TitledTextView;
import domains.coventry.andrefmsilva.coventryuniversity.MainActivity;
import domains.coventry.andrefmsilva.coventryuniversity.R;
import domains.coventry.andrefmsilva.utils.MySQLConnector;

import static domains.coventry.andrefmsilva.utils.Utils.setToolbarText;
import static domains.coventry.andrefmsilva.utils.Utils.setChildrenEnabled;

public class EnrolFinancialFragment extends Fragment implements MySQLConnector {
    ViewGroup enrolFinancialLayout;
    TitledTextView ttvFees;
    TitledTextView ttvInstalments;
    TitledTextView ttvValue;
    TitledTextView ttvTotal;
    TextView txtViewDescription;
    RadioButton rBtnSLC;
    RadioGroup rGrpChoices;

    private float fees;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enrolfinancial, container, false);

        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.enrol_financial, R.string.enrolement);

        fees = 0.f;

        enrolFinancialLayout = view.findViewById(R.id.enrolfinancial_layout);

        connectWithRetry();

        ttvFees = view.findViewById(R.id.enrolfinancial_fees);
        ttvInstalments = view.findViewById(R.id.enrolfinancial_instalments);
        ttvValue = view.findViewById(R.id.enrolfinancial_value);
        ttvTotal = view.findViewById(R.id.enrolfinancial_total);
        txtViewDescription = view.findViewById(R.id.enrolfinancial_description);
        rBtnSLC = view.findViewById(R.id.enrolfinancial_slc);
        rGrpChoices = view.findViewById(R.id.enrolfinancial_choices);

        rGrpChoices.setOnCheckedChangeListener((group, checkedId) ->
        {
            switch (checkedId) {
                case R.id.enrolfinancial_slc:
                    txtViewDescription.setText(R.string.enrolfinancial_desc_slc);
                    ttvInstalments.setText("0");
                    ttvValue.setText("£0");
                    ttvTotal.setText("£0");
                    break;

                case R.id.enrolfinancial_directdebit:
                    txtViewDescription.setText(R.string.enrolfinancial_desc_ddebit);
                    ttvInstalments.setText("1");
                    ttvValue.setText(ttvFees.getText());
                    ttvTotal.setText(ttvFees.getText());
                    break;

                case R.id.enrolfinancial_recurrent:
                    txtViewDescription.setText(R.string.enrolfinancial_desc_recurrent);
                    ttvInstalments.setText("8");
                    ttvValue.setText(String.format("£%s", fees / 8));
                    ttvTotal.setText(ttvFees.getText());
                    break;

                case R.id.enrolfinancial_directinstalements:
                    txtViewDescription.setText(R.string.enrolfinancial_desc_ddintalmnts);
                    ttvInstalments.setText("8");
                    ttvValue.setText(String.format("£%s", fees / 8));
                    ttvTotal.setText(ttvFees.getText());
                    break;
            }
        });

        view.findViewById(R.id.enrolfinancial_confirm).setOnClickListener(v ->
        {
            HashMap<String, String> requestInfo = new HashMap<>();
            requestInfo.put("type", "confirm_financial_registration");
            requestInfo.put("id", String.valueOf(MainActivity.getUserID()));

            new connectMySQL(new WeakReference<>(this), FILE_ENROL, requestInfo, "Confirm Financial", false).execute();
        });

        return view;
    }

    @Override
    public void connectWithRetry() {
        HashMap<String, String> requestInfo = new HashMap<>();
        requestInfo.put("type", "get_financial_registration");
        requestInfo.put("id", String.valueOf(MainActivity.getUserID()));

        new connectMySQL(new WeakReference<>(this), FILE_ENROL, requestInfo, "Financial Registration").execute();
    }

    @Override
    public void connectionStarted() {
        setChildrenEnabled(enrolFinancialLayout, false);
    }

    @Override
    public void connectionSuccessful(HashMap<String, String> results) {
        // If not empty, it's because was trying to get info from teh database
        if (results.size() > 0) {
            fees = Float.valueOf(Objects.requireNonNull(results.get("fees_uk")));

            ttvFees.setText(String.format("£%s", fees));
            ttvInstalments.setText("1");
            ttvValue.setText(String.format("£%s", fees));
            ttvTotal.setText(String.format("£%s", fees));

            // Enable all layout children
            setChildrenEnabled(enrolFinancialLayout, true);

            // SLC option is only for undergranduate students, i.e. BSc, BA
            if (!Objects.requireNonNull(results.get("type")).startsWith("B"))
                rBtnSLC.setEnabled(false);
            else
                rBtnSLC.setEnabled(true);
        } else {
            Toast.makeText(getContext(), "Financial registration successful", Toast.LENGTH_SHORT).show();
            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    }

    @Override

    public void connectionUnsuccessful(Boolean canRetry) {
        if (!canRetry) {
            Toast.makeText(getContext(), "Couldn't get data", Toast.LENGTH_SHORT).show();

            // Enable all layout children
            setChildrenEnabled(enrolFinancialLayout, true);
        }
    }
}
