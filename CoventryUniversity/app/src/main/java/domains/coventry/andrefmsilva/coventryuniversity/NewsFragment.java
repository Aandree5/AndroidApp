package domains.coventry.andrefmsilva.coventryuniversity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;
import com.twitter.sdk.android.tweetui.TwitterListTimeline;
import com.twitter.sdk.android.tweetui.UserTimeline;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;

//TODO: Add scroll up to refresh news
public class NewsFragment extends Fragment {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ActionBar toolbar;
    BottomNavigationView bottomNav;

    int bottomNavSelectdID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news, container, false);

        recyclerView = view.findViewById(R.id.news_recyclerview);
        progressBar = view.findViewById(R.id.news_progressbar);
        bottomNav = view.findViewById(R.id.news_bottom_nav);

        // As getSupportActionBar can be null a check is made first
        toolbar = ((AppCompatActivity) Objects.requireNonNull(getActivity(), "Activity must not be null.")).getSupportActionBar();


        // Set the recycler view layout to vertical
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // Set listener that gets called when a new item is selected
        // Lambda function
        bottomNav.setOnNavigationItemSelectedListener(menuItem -> {
            recyclerView.removeAllViews();
            bottomNavSelectdID = menuItem.getItemId();

            switch (bottomNavSelectdID) {
                case R.id.news_bottom_nav_moodle:
                    toolbar.setTitle(R.string.news_bottom_nav_moodle);
                    showMoodleNews();
                    break;
                case R.id.news_bottom_nav_twitter:
                    toolbar.setTitle(R.string.news_bottom_nav_twitter);
                    showTwitterNews();
                    break;
                case R.id.news_bottom_nav_faculty:
                    toolbar.setTitle(R.string.news_bottom_nav_faculty);
                    showFacultyNews();
                    break;
            }

            return true;
        });

        if (savedInstanceState == null){
            // Set the action bar title, selected item and show default news
            toolbar.setTitle(R.string.news_bottom_nav_twitter);
            bottomNav.setSelectedItemId(R.id.news_bottom_nav_twitter);
            showTwitterNews();
        }

        return view;
    }


    //TODO: Improve restoring state (TEMP solution, now saving choosen bottom navigation id and selecting it on restore state to trigger a refresh)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            bottomNavSelectdID = savedInstanceState.getInt("bottomNavSelectdID");
            bottomNav.setSelectedItemId(bottomNavSelectdID);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("bottomNavSelectdID", bottomNavSelectdID);
    }


    //TODO: Add moodle news
    public void showMoodleNews() {
        progressBar.setVisibility(View.VISIBLE);

        RSSAdapter adapter = new RSSAdapter();

        // On first data change (the recycler view has data) hide the loading progress bar and unregister observer
        // because no more changes need to be detected
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                progressBar.setVisibility(View.INVISIBLE);

                adapter.unregisterAdapterDataObserver(this);
            }
        });

        // Set the new adapter
        recyclerView.setAdapter(adapter);
    }

    // Show the news for all university twitter accounts (Excluding faculty accounts)
    public void showTwitterNews() {
        progressBar.setVisibility(View.VISIBLE);

        // Set the twitter list for all the university twitter accounts
        TwitterListTimeline timeline = new TwitterListTimeline.Builder()
                .slugWithOwnerScreenName("CovUni", "Aandree5")
                .build();

        attachTimeline(timeline);
    }

    // Show  timeline for faculty accounts
    public void showFacultyNews() {
        progressBar.setVisibility(View.VISIBLE);

        // @covunieec - Faculty of Engenieering, Environment and Computing
        // @CU_HLS - Health and Life Sciences
        // @CovUniFBL - Business and Law
        // @CoventryFAH - Faculty of Arts and Humanities

        // Set the twitter user for faculty account
        UserTimeline timeline = new UserTimeline.Builder().screenName("covunieec").build();

        attachTimeline(timeline);
    }

    // Create adapter and attach it to the recycler view
    public void attachTimeline(@NonNull Timeline<Tweet> timeline) {
        // Creste adapter for recycler view
        TweetTimelineRecyclerViewAdapter adapter = new TweetTimelineRecyclerViewAdapter.Builder(getContext())
                .setTimeline(timeline)
                .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                .build();

        // On first data change (the recycler view has data) hide the loading progress bar and unregister observer
        // because no more changes need to be detected
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                progressBar.setVisibility(View.INVISIBLE);

                adapter.unregisterAdapterDataObserver(this);
            }
        });

        // Set the new adapter
        recyclerView.setAdapter(adapter);
    }

    private class RSSAdapter extends RecyclerView.Adapter<RSSAdapter.RSSViewHolder>  {

        private ArrayList<HashMap<String, String>> rssFeed;
        private RecyclerView rView;
        private readRSS reader; // If user clicks several times on the buttom it just creates a new instance overriding the old one

        RSSAdapter() {
            reader = new readRSS();
            reader.execute(this);
            setHasStableIds(true);
        }

        @Override
        public int getItemCount() {
            if (rssFeed != null)
                return rssFeed.size();
            else
                return 0;
        }

        @NonNull
        @Override
        public RSSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_post, parent, false);

            return new RSSViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RSSViewHolder viewHolder, int position) {
            HashMap<String, String> post = rssFeed.get(position);

            viewHolder.title.setText(post.get("title"));
            viewHolder.date.setText(shortenDate(post.get("pubDate")));
            viewHolder.author.setText(post.get("author"));
            viewHolder.description.setText(post.get("description"));



            if (!Objects.requireNonNull(post.get("image")).isEmpty())
            {
                CompletableFuture<Bitmap> completableFuture = new CompletableFuture<>();

                Executors.newCachedThreadPool().submit(() -> {
                    try {
                        URL url = new URL(post.get("image"));

                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                        completableFuture.complete(bmp);

                    } catch (IOException e) {
                        Log.e(TAG, "onBindViewHolder: " + e.getMessage(), e);
                    }

                    return null;
                });

                try {
                    viewHolder.image.setImageBitmap(completableFuture.get());

                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "onBindViewHolder: " + e.getMessage(), e);
                }
            }
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            rView = recyclerView;
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        private String shortenDate(String date){
            String shortDate = "";

            SimpleDateFormat dFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.UK);

            try {
                Date d = dFormat.parse(date);

                dFormat.applyPattern("dd MMM ''yy");

                shortDate = dFormat.format(d);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return shortDate;
        }


        class RSSViewHolder extends RecyclerView.ViewHolder {
            private TextView title;
            private TextView date;
            private TextView author;
            private TextView description;
            private ImageView image;

            RSSViewHolder(View view) {
                super(view);

                title = view.findViewById(R.id.news_card_title);
                date = view.findViewById(R.id.news_card_date);
                author = view.findViewById(R.id.news_card_author);
                description = view.findViewById(R.id.news_card_description);
                image = view.findViewById(R.id.news_card_image);
            }
        }

    }

    private static class readRSS extends AsyncTask<RSSAdapter, Void, Boolean> implements Html.ImageGetter {
        private String imgSrc; // Link for the first image found when reading moodle rss description
        private WeakReference<RSSAdapter> rssAdapter;

        @Override
        protected Boolean doInBackground(RSSAdapter... params) {
            rssAdapter = new WeakReference<>(params[0]);

            try {
                URL url = new URL("https://cumoodle.coventry.ac.uk/rss/file.php/27/d824761bf8f70ac9f828581198537265/mod_forum/1/rss.xml");

                rssAdapter.get().rssFeed = parseFeed(url.openConnection().getInputStream());

                return true;

            } catch (IOException | XmlPullParserException e) {
                Log.e(TAG, "readRSS: " + e.getMessage(), e);
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            RSSAdapter adapter = rssAdapter.get();
            RecyclerView rView = adapter.rView;

            if (aBoolean && rView != null)
            {
                adapter.notifyDataSetChanged();
                rView.scheduleLayoutAnimation();
            }
        }

        @NonNull
        private ArrayList<HashMap<String, String>> parseFeed(@NonNull InputStream iStream) throws XmlPullParserException, IOException {
            ArrayList<HashMap<String, String>> rss = new ArrayList<>();

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(iStream, "UTF_8");

                int eventType = xpp.getEventType();
                String text = "";
                boolean readingItem = false;
                HashMap<String, String> item = new HashMap<>();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item"))
                            readingItem = true;
                    }
                    else if (eventType == XmlPullParser.TEXT && readingItem)
                    {
                        SpannableStringBuilder spanString = (SpannableStringBuilder) Html.fromHtml(xpp.getText(), Html.FROM_HTML_MODE_COMPACT, this, null);
                        Object[] spanObjects = spanString.getSpans(0, spanString.length(), Object.class);
                        for (Object sObj : spanObjects) {
                            if (sObj instanceof ImageSpan) {
                                ImageSpan imgSpan = (ImageSpan) sObj;
                                spanString.replace(spanString.getSpanStart(imgSpan), spanString.getSpanEnd(imgSpan), "");
                            }
                        }

                        text = spanString.toString();
                    }

                    else if (eventType == XmlPullParser.END_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            readingItem = false;
                            rss.add(new HashMap<>(item)); // Create a new copy, otherwise it will just "point" to item that is cleared after
                            item.clear();
                        }
                        else if (readingItem && !xpp.getName().equalsIgnoreCase("guid"))
                        {
                            if(xpp.getName().equalsIgnoreCase("description"))
                            {
                                String author = text.substring(2, text.indexOf('.')).trim();
                                String des = text.substring(text.indexOf('.') + 4, text.length()).trim();
                                item.put("author", author);
                                item.put(xpp.getName(), des);

                                if (!imgSrc.isEmpty())
                                    item.put("image", imgSrc);
                            }
                            else
                                item.put(xpp.getName(), text);

                        }
                    }

                    eventType = xpp.next();
                }
            } finally {
                iStream.close();
            }

            return rss;
        }


        @Override
        public Drawable getDrawable(String source) {
            imgSrc = source;
            return null;
        }
    }
}
