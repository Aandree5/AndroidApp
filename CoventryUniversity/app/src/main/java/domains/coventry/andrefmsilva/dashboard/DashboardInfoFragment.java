package domains.coventry.andrefmsilva.dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import domains.coventry.andrefmsilva.coventryuniversity.R;

import static domains.coventry.andrefmsilva.coventryuniversity.MainActivity.setToolbarText;

public class DashboardInfoFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dashboard_fragment_info, container, false);

        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.nav_dashboard, R.string.app_name);

        // Test
        view.findViewById(R.id.dashboard_enrolment).setOnClickListener(v -> Objects.requireNonNull(getFragmentManager()).beginTransaction().replace(R.id.fragment_container, new DashboardEnrolFragment()).commit());

        return view;
    }
}
