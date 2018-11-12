/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 :                                                  :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : DashboardFragment                                :
 : Last modified 10 Nov 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.coventryuniversity;

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

import domains.coventry.andrefmsilva.dashboard.DashboardEnrolFragment;
import domains.coventry.andrefmsilva.dashboard.DashboardEnrolLoginFragment;
import domains.coventry.andrefmsilva.dashboard.DashboardInfoFragment;
import domains.coventry.andrefmsilva.dashboard.DashboardLoginFragment;

import static domains.coventry.andrefmsilva.utils.Utils.setToolbarText;

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
                        fragmentManager
                                .beginTransaction()
                                .replace(R.id.dashboard_framelayout, new DashboardLoginFragment())
                                .commit();
                        break;

                    case 1:
                        fragmentManager
                                .beginTransaction()
                                .replace(R.id.dashboard_framelayout, new DashboardEnrolLoginFragment())
                                .commit();
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

            switch (((MainActivity) getActivity()).getStatus())
            {
                case LOGGED:
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.dashboard_framelayout, new DashboardInfoFragment())
                            .commit();

                    tablayout.setVisibility(View.GONE);
                    break;

                case ENROLMENT:
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.dashboard_framelayout, new DashboardEnrolFragment())
                            .addToBackStack("root")
                            .commit();

                    tablayout.setVisibility(View.GONE);
                    break;

                case NONE:
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.dashboard_framelayout, new DashboardLoginFragment())
                            .commit();

                    tablayout.setVisibility(View.VISIBLE);
                    break;
            }
        }

        return view;
    }
}
