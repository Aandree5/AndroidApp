/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : SettingsActivity.java                            :
 : Last modified 06 Dec 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.coventryuniversity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import static domains.coventry.andrefmsilva.utils.Utils.setToolbarText;
import static domains.coventry.andrefmsilva.utils.Utils.showToolbarBack;

public class SettingsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setToolbarText(this, R.string.settings, R.string.app_name);
        showToolbarBack(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sttings_fragment_container, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
