/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 :                                                  :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : SettingsFragment                                 :
 : Last modified 10 Nov 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.coventryuniversity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.widget.Button;

import java.util.Objects;

import domains.coventry.andrefmsilva.utils.InfoDialog;

public class SettingsFragment extends PreferenceFragmentCompat
{
    ListPreference lstFacChoice;

    @Override
    public void onCreatePreferences(Bundle bundle, String s)
    {
        setPreferencesFromResource(R.xml.settings, s);


        SwitchPreference swtchFacCheck = (SwitchPreference) findPreference("settingsFacultyAuto");
        lstFacChoice = (ListPreference) findPreference("settingsFacultyTwitter");
        lstFacChoice.setEnabled(!swtchFacCheck.isChecked());

        swtchFacCheck.setOnPreferenceChangeListener((preference, o) ->
        {
            lstFacChoice.setEnabled(((SwitchPreference) preference).isChecked());

            return true;
        });

        Preference btn_log_out = findPreference("btn_log_out");

        btn_log_out.setEnabled(MainActivity.getStatus() == MainActivity.UserStatus.LOGGED);

        btn_log_out.setOnPreferenceClickListener(preference ->
        {
            Intent intent = Objects.requireNonNull(getActivity()).getIntent();
            intent.putExtra("logout", true);

            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();

            return true;
        });

        findPreference("btn_about").setOnPreferenceClickListener(preference ->
        {
            InfoDialog.newIntance(InfoDialog.Type.OK, "About", "App developed by Andre Silva\n\nVersion 0.1 (Beta)\nCopyright © 2018 Andre Silva", R.mipmap.ic_launcher_round)
                    .showNow(Objects.requireNonNull(getFragmentManager()), null);

            return true;
        });
    }
}
