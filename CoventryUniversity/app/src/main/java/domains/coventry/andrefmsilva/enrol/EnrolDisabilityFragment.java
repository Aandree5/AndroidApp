/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 :                                                  :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : EnrolDisabilityFragment                 :
 : Last modified 10 Nov 2018                        :
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

import static domains.coventry.andrefmsilva.utils.Utils.setToolbarText;
import static domains.coventry.andrefmsilva.utils.Utils.setChildrenEnabled;

public class EnrolDisabilityFragment extends Fragment implements MySQLConnector {
    LinearLayout enrolDisabilityLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enroldisability, container, false);

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
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, disabilitiesList) {

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);

                view.setEnabled(isEnabled(position));

                return (position == 0) ? setTextDisabled(view) : resetTextStyle(view);
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);

                return (position == 0) ? setTextDisabled(view) : resetTextStyle(view);
            }

            @Override
            public boolean isEnabled(int position) {
                String value = disabilitiesList.get(position).toString();

                return position == 0 || !value.equals(spinner1.getSelectedItem().toString()) && !value.equals(spinner2.getSelectedItem().toString()) &&
                        !value.equals(spinner3.getSelectedItem().toString()) && !value.equals(spinner4.getSelectedItem().toString());
            }

            @Override
            public int getPosition(@Nullable CharSequence item) {
                return disabilitiesList.indexOf(item);
            }

            @Override
            public int getCount() {
                return disabilitiesList.size();
            }

            /**
             * Set the text of the input textview to the disabled color
             *
             * @param textView TextView to change the text color
             * @return Input textview with the text color changed
             */
            View setTextDisabled(@NonNull TextView textView) {
                textView.setTextColor(getResources().getColor(R.color.textDisabled, getActivity().getTheme()));

                return textView;
            }

            /**
             * Reset the textbox back to normal, because it's reused in the recycler view
             *
             * @param textView TextView to change the text color
             * @return Input textview with the text color changed
             */
            View resetTextStyle(@NonNull TextView textView) {
                textView.setTextColor(getResources().getColorStateList(R.color.co_spinner_text, getActivity().getTheme()));

                return textView;
            }
        };

        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner3.setAdapter(adapter);
        spinner4.setAdapter(adapter);

        view.findViewById(R.id.enroldisability_confirm).setOnClickListener(v -> {
            HashMap<String, String> requestInfo = new HashMap<>();
            requestInfo.put("type", "confirm_disability_registration");
            requestInfo.put("id", String.valueOf(MainActivity.getUserID()));
            requestInfo.put("disability_1", ((TextView) spinner1.getSelectedView()).getText().toString());
            requestInfo.put("disability_2", ((TextView) spinner2.getSelectedView()).getText().toString());
            requestInfo.put("disability_3", ((TextView) spinner3.getSelectedView()).getText().toString());
            requestInfo.put("disability_4", ((TextView) spinner4.getSelectedView()).getText().toString());
            requestInfo.put("disability_comment", ((EditText) view.findViewById(R.id.enroldisability_comment)).getText().toString());
            requestInfo.put("disability_dsa", ((CheckBox) view.findViewById(R.id.enroldisability_dsa)).isChecked() ? "1" : "0");

            new connectMySQL(new WeakReference<>(EnrolDisabilityFragment.this), FILE_ENROL, requestInfo, "Disability Registration", false).execute();
        });

        return view;
    }

    @Override
    public void connectionStarted() {
        setChildrenEnabled(enrolDisabilityLayout, false);
    }

    @Override
    public void connectionSuccessful(HashMap<String, String> results) {
        Toast.makeText(getContext(), "Disability registered", Toast.LENGTH_LONG).show();

        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    @Override
    public void connectionUnsuccessful(Boolean canRetry) {
        setChildrenEnabled(enrolDisabilityLayout, true);
        Toast.makeText(getContext(), "Couldn't save data", Toast.LENGTH_LONG).show();
    }
}
