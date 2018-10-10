package domains.coventry.andrefmsilva.coventryuniversity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;


import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;
import com.twitter.sdk.android.tweetui.TwitterListTimeline;
import com.twitter.sdk.android.tweetui.UserTimeline;

import static android.view.View.VISIBLE;

public class NewsFragment extends Fragment {
    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news, container, false);

        recyclerView = view.findViewById(R.id.news_recyclerview);
        progressBar = view.findViewById(R.id.news_progressbar);
        BottomNavigationView bottomNav = view.findViewById(R.id.news_bottom_nav);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.news_bottom_nav_twitter);
        bottomNav.setSelectedItemId(R.id.news_bottom_nav_twitter);

        showTwitterNews();

        bottomNav.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.news_bottom_nav_moodle:
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.news_bottom_nav_moodle);
                    break;
                case R.id.news_bottom_nav_twitter:
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.news_bottom_nav_twitter);
                    showTwitterNews();
                    break;
                case R.id.news_bottom_nav_faculty:
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.news_bottom_nav_faculty);
                    showFacultyNews();
                    break;
            }

            return true;
        });

        return view;
    }

    //TODO: Add moodle news
    public void showMoodleNews(){

    }

    public void showTwitterNews(){
        progressBar.setVisibility(View.VISIBLE);

        // Set the twitter list for all the university twitter accounts
        TwitterListTimeline timeline = new TwitterListTimeline.Builder()
                .slugWithOwnerScreenName("CovUni", "Aandree5")
                .build();

        TweetTimelineRecyclerViewAdapter adapter = new TweetTimelineRecyclerViewAdapter.Builder(getContext())
                .setTimeline(timeline)
                .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                .build();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                progressBar.setVisibility(View.INVISIBLE);

                adapter.unregisterAdapterDataObserver(this);
            }
        });

        recyclerView.swapAdapter(adapter, true);
    }

    public void showFacultyNews(){
        progressBar.setVisibility(View.VISIBLE);

        // @covunieec - Faculty of Engenieering, Environment and Computing
        // @CU_HLS - Health and Life Sciences
        // @CovUniFBL - Business and Law
        // @CoventryFAH - Faculty of Arts and Humanities

        // Set the twitter user for faculty account
        UserTimeline timeline = new UserTimeline.Builder().screenName("covunieec").build();

        TweetTimelineRecyclerViewAdapter adapter = new TweetTimelineRecyclerViewAdapter.Builder(getContext())
                .setTimeline(timeline)
                .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                .build();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                progressBar.setVisibility(View.INVISIBLE);

                adapter.unregisterAdapterDataObserver(this);
            }
        });

        recyclerView.swapAdapter(adapter, true);
    }


}
