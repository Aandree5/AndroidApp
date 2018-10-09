package domains.coventry.andrefmsilva.coventryuniversity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;


import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;
import com.twitter.sdk.android.tweetui.TwitterListTimeline;

import static com.twitter.sdk.android.core.TwitterCore.TAG;

public class NewsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news, container, false);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_news);
        final ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.recyclerview_news_pbar);

        progressBar.setVisibility(view.VISIBLE);

        // @covunieec - Faculty of Engenieering, Environment and Computing
        // @CU_HLS - Health and Life Sciences
        // @CovUniFBL - Business and Law
        // @CoventryFAH - Faculty of Arts and Humanities

        // Set the twitter list for all the university twitters
        final TwitterListTimeline timeline = new TwitterListTimeline.Builder()
                .slugWithOwnerScreenName("CovUni", "Aandree5")
                .build();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        final TweetTimelineRecyclerViewAdapter adapter = new TweetTimelineRecyclerViewAdapter.Builder(view.getContext())
                                                                                            .setTimeline(timeline)
                                                                                            .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                                                                                            .build();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                progressBar.setVisibility(view.INVISIBLE);

                adapter.unregisterAdapterDataObserver(this);
            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }


}
