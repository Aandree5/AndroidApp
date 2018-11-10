/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 :                                                  :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : DashboardEnrolDisabilityFragment                 :
 : Last modified 10 Nov 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import domains.coventry.andrefmsilva.coventryuniversity.MainActivity;
import domains.coventry.andrefmsilva.coventryuniversity.R;
import domains.coventry.andrefmsilva.utils.MySQLConnector;

import static domains.coventry.andrefmsilva.coventryuniversity.MainActivity.setToolbarText;
import static domains.coventry.andrefmsilva.utils.Utils.setChildrenEnabled;

public class DashboardEnrolDisabilityFragment extends Fragment implements MySQLConnector
{
    LinearLayout enrolDisabilityLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dashboard_fragment_enroldisability, container, false);

        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.enrol_disability, R.string.app_name);

        enrolDisabilityLayout = view.findViewById(R.id.enroldisability_layout);

        Spinner spinner1 = view.findViewById(R.id.enroldisability_spinner1);
        Spinner spinner2 = view.findViewById(R.id.enroldisability_spinner2);
        Spinner spinner3 = view.findViewById(R.id.enroldisability_spinner3);
        Spinner spinner4 = view.findViewById(R.id.enroldisability_spinner4);

        // Used to disable and enable options
        spinner1.setNextFocusForwardId(R.id.enroldisability_spinner2);
        spinner1.setNextFocusDownId(R.id.enroldisability_2);
        spinner2.setNextFocusForwardId(R.id.enroldisability_spinner3);
        spinner2.setNextFocusDownId(R.id.enroldisability_3);
        spinner3.setNextFocusForwardId(R.id.enroldisability_spinner4);
        spinner3.setNextFocusDownId(R.id.enroldisability_4);

        final List<CharSequence> disabilitiesList = new ArrayList<>();
        disabilitiesList.add("Select");
        disabilitiesList.add("AD(H)D");
        disabilitiesList.add("Autistic spectrum disorder");
        disabilitiesList.add("Blind");
        disabilitiesList.add("Deaf");
        disabilitiesList.add("Dyslexia");
        disabilitiesList.add("Dyspraxia");
        disabilitiesList.add("Long standing illness");
        disabilitiesList.add("Mental Health");
        disabilitiesList.add("Mobility problem");
        disabilitiesList.add("Other disability not listed");
        disabilitiesList.add("Requiring Carer");
        disabilitiesList.add("Serious hearing impairment");
        disabilitiesList.add("Serious visual impairment uncorrected by glasses");
        disabilitiesList.add("Wheelchair User");

        // Used to set spinner options and the first option be a disabled color
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, disabilitiesList)
        {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
            {
                View view = super.getDropDownView(position, convertView, parent);

                return (position == 0) ? setTextDisabled((TextView) view) : view;
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
            {
                View view = super.getView(position, convertView, parent);

                return (position == 0) ? setTextDisabled((TextView) view) : view;
            }

            /**
             * Set the text of the input textview to the disabled color
             *
             * @param textView TextView to change the text color
             * @return Input textview with the text color changed
             */
            View setTextDisabled(@NonNull TextView textView)
            {
                textView.setTextColor(getResources().getColor(R.color.textDisabled, getActivity().getTheme()));

                return textView;
            }
        };

        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner3.setAdapter(adapter);
        spinner4.setAdapter(adapter);

        view.findViewById(R.id.enroldisability_confirm).setOnClickListener(v ->
        {
            HashMap<String, String> requestInfo = new HashMap<>();
            requestInfo.put("type", "confirm_disability_registration");
            requestInfo.put("id", String.valueOf(((MainActivity) Objects.requireNonNull(getActivity())).getUserID()));
            requestInfo.put("disability_1", ((TextView) spinner1.getSelectedView()).getText().toString());
            requestInfo.put("disability_2", ((TextView) spinner2.getSelectedView()).getText().toString());
            requestInfo.put("disability_3", ((TextView) spinner3.getSelectedView()).getText().toString());
            requestInfo.put("disability_4", ((TextView) spinner4.getSelectedView()).getText().toString());
            requestInfo.put("disability_comment", ((EditText) view.findViewById(R.id.enroldisability_comment)).getText().toString());
            requestInfo.put("disability_dsa", ((CheckBox) view.findViewById(R.id.enroldisability_dsa)).isChecked() ? "1" : "0");

            new connectMySQL(new WeakReference<>(DashboardEnrolDisabilityFragment.this), FILE_ENROL, requestInfo, "Disability Registration", false).execute();
        });

        view.findViewById(R.id.enroldisability_cancel).setOnClickListener(v -> getActivity().onBackPressed());

        return view;
    }

    @Override
    public void connectionStarted()
    {
        setChildrenEnabled(enrolDisabilityLayout, false);
    }

    @Override
    public void connectionSuccessful(HashMap<String, String> results)
    {
        Toast.makeText(getContext(), "Disability registered", Toast.LENGTH_LONG).show();

        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    @Override
    public void connectionUnsuccessful(Boolean canRetry)
    {
        setChildrenEnabled(enrolDisabilityLayout, true);
        Toast.makeText(getContext(), "Couldn't save data", Toast.LENGTH_LONG).show();
    }
}
