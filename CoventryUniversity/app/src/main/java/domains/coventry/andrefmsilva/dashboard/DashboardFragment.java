package domains.coventry.andrefmsilva.dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import domains.coventry.andrefmsilva.coventryuniversity.MainActivity;
import domains.coventry.andrefmsilva.coventryuniversity.R;

import static domains.coventry.andrefmsilva.coventryuniversity.MainActivity.setToolbarText;

public class DashboardFragment extends Fragment
{
    TabLayout tablayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.nav_dashboard, R.string.app_name);

        tablayout = view.findViewById(R.id.dashboard_tab_layout);

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

                switch (tab.getPosition())
                {
                    case 0:
                        fragmentManager.beginTransaction().replace(R.id.dashboard_framelayout, new DashboardLoginFragment()).commit();
                        break;
                    case 1:
                        fragmentManager.beginTransaction().replace(R.id.dashboard_framelayout, new DashboardEnrolLoginFragment()).commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });

        if (savedInstanceState == null)
        {
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

            if (((MainActivity) getActivity()).getStatus() == MainActivity.UserStatus.LOGGED)
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new DashboardInfoFragment()).commit();

            else if (((MainActivity) getActivity()).getStatus() == MainActivity.UserStatus.ENROLMENT)
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new DashboardEnrolFragment()).commit();

            else
                fragmentManager.beginTransaction().replace(R.id.dashboard_framelayout, new DashboardLoginFragment()).commit();
        }

        return view;
    }

}
