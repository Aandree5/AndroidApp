package domains.coventry.andrefmsilva.dashboard;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

import domains.coventry.andrefmsilva.CustomViews.TitledTextView;
import domains.coventry.andrefmsilva.coventryuniversity.MainActivity;
import domains.coventry.andrefmsilva.coventryuniversity.R;
import domains.coventry.andrefmsilva.utils.MySQLConnector;

import static domains.coventry.andrefmsilva.coventryuniversity.MainActivity.setToolbarText;
import static domains.coventry.andrefmsilva.utils.Utils.setChildrenEnabled;

public class DashboardInfoFragment extends Fragment implements MySQLConnector
{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dashboard_fragment_info, container, false);

        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.nav_dashboard, R.string.app_name);

        // Test
        view.findViewById(R.id.dashboard_enrolment).setOnClickListener(v -> Objects.requireNonNull(getFragmentManager()).beginTransaction().replace(R.id.fragment_container, new DashboardEnrolFragment()).addToBackStack(null).commit());
        view.findViewById(R.id.dashboard_enrolment_reset).setOnClickListener(v ->
        {
            HashMap<String, String> requestInfo = new HashMap<>();
            requestInfo.put("id", String.valueOf(((MainActivity) Objects.requireNonNull(getActivity())).getUserID()));

            new MySQLConnector.connectMySQL(new WeakReference<>(this), FILE_RESETENROL, requestInfo, "Reseting...", false).execute();
        });

        MainActivity activity = (MainActivity) getActivity();
        if (activity != null)
        {
            ((TitledTextView) view.findViewById(R.id.info_name)).setText(activity.getName());
            ((TitledTextView) view.findViewById(R.id.info_id)).setText(String.valueOf(activity.getUserID()));
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (sharedPreferences != null)
        {
            ((TitledTextView) view.findViewById(R.id.info_year)).setText(sharedPreferences.getString("year", null));
            ((TitledTextView) view.findViewById(R.id.info_course)).setText(String.format("%s %s (%s)",
                    sharedPreferences.getString("courseType", null),
                    sharedPreferences.getString("courseName", null),
                    sharedPreferences.getString("courseCode", null)));

            String photo = sharedPreferences.getString("photo", null);
            if (photo != null)
            {
                FileInputStream fInpStream = null;

                try
                {
                    fInpStream = Objects.requireNonNull(getContext()).openFileInput(photo);

                    ((ImageView) view.findViewById(R.id.info_photo)).setImageBitmap(BitmapFactory.decodeStream(fInpStream));
                }
                catch (Exception e)
                {
                    Log.e("infoLoadingPhoto", e.getMessage(), e);
                }
                finally
                {
                    if (fInpStream != null)
                    {
                        try
                        {
                            fInpStream.close();
                        }
                        catch (IOException e)
                        {
                            Log.e("infoLoadingPhoto", e.getMessage(), e);
                        }
                    }
                }
            }
            else
                ((ImageView) view.findViewById(R.id.info_photo)).setImageResource(R.drawable.ic_user_photo);
        }

        return view;
    }

    @Override
    public void connectionStarted()
    {
    }

    @Override
    public void connectionSuccessful(HashMap<String, String> results)
    {
        Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void connectionUnsuccessful(Boolean canRetry)
    {
        Toast.makeText(getContext(), "Error connecting to database", Toast.LENGTH_LONG).show();
    }
}
