package domains.coventry.andrefmsilva.coventryuniversity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DashboardFragment extends Fragment {
    TabLayout tablayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tablayout = view.findViewById(R.id.dashboard_tab_layout);

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_framelayout, new DashboardLoginFragment()).commit();
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
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_framelayout, new DashboardLoginFragment()).commit();

        return view;
    }
}
