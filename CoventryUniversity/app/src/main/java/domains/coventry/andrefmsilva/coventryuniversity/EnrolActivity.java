package domains.coventry.andrefmsilva.coventryuniversity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import domains.coventry.andrefmsilva.enrol.EnrolFragment;

import static domains.coventry.andrefmsilva.utils.Utils.setToolbarText;
import static domains.coventry.andrefmsilva.utils.Utils.showToolbarBack;

public class EnrolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrol);

        setToolbarText(this, R.string.enrolement, R.string.app_name);
        showToolbarBack(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.enrol_fragment_container, new EnrolFragment())
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
