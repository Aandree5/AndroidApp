package domains.coventry.andrefmsilva.coventryuniversity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import static domains.coventry.andrefmsilva.coventryuniversity.MainActivity.setToolbarText;

public class SettingsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setToolbarText(this, R.string.settings, R.string.app_name);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sttings_fragment_container, new SettingsFragment())
                .commit();
    }
}
