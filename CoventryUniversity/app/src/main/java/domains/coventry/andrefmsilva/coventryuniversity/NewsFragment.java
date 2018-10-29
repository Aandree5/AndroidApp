package domains.coventry.andrefmsilva.coventryuniversity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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

import java.io.FileOutputStream;
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

import domains.coventry.andrefmsilva.utils.ZoomImageActivity;

import static domains.coventry.andrefmsilva.coventryuniversity.MainActivity.setToolbarText;

//TODO: Add scroll up to refresh news
//TODO: Change loading state to a InfoDialog, with retry
public class NewsFragment extends Fragment
{
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TabLayout tabLayout;
    Integer tabSelectedPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_news, container, false);

        recyclerView = view.findViewById(R.id.news_recyclerview);
        progressBar = view.findViewById(R.id.news_progressbar);
        tabLayout = view.findViewById(R.id.news_tab_layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                tabSelectedPosition = tab.getPosition();

                switch (tabSelectedPosition)
                {
                    case 0:
                        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.news_tab_moodle);
                        showMoodleNews();
                        break;
                    case 1:
                        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.news_tab_twitter);
                        showTwitterNews();
                        break;
                    case 2:
                        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.news_tab_faculty);
                        showFacultyNews();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
                recyclerView.removeAllViews();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });

        // Show defaults
        if (savedInstanceState == null)
        {
            setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.news_tab_twitter);
            Objects.requireNonNull(tabLayout.getTabAt(1)).select();
            showTwitterNews();
        }

        return view;
    }


    //TODO: Improve restoring state (TEMP solution, now saving choosen bottom navigation id and selecting it on restore state to trigger a refresh)
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null)
        {
            tabSelectedPosition = savedInstanceState.getInt("tabSelectedPosition");
            Objects.requireNonNull(tabLayout.getTabAt(tabSelectedPosition)).select();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putInt("tabSelectedPosition", tabSelectedPosition);
    }


    /**
     * Show the news from moodle
     */
    public void showMoodleNews()
    {
        progressBar.setVisibility(View.VISIBLE);

        RSSAdapter adapter = new RSSAdapter();

        // On first data change (the recycler view has data) hide the loading progress bar and unregister observer
        // because no more changes need to be detected
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onChanged()
            {
                super.onChanged();

                progressBar.setVisibility(View.INVISIBLE);

                adapter.unregisterAdapterDataObserver(this);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    /**
     * Show the news for all university twitter accounts (Excluding faculty accounts)
     */
    public void showTwitterNews()
    {
        progressBar.setVisibility(View.VISIBLE);

        // Set the twitter list for all the university twitter accounts
        TwitterListTimeline timeline = new TwitterListTimeline.Builder()
                .slugWithOwnerScreenName("CovUni", "Aandree5")
                .build();

        attachTimeline(timeline);
    }

    /**
     * Show  timeline for faculty accounts
     */
    public void showFacultyNews()
    {
        progressBar.setVisibility(View.VISIBLE);

        // @covunieec - Faculty of Engenieering, Environment and Computing
        // @CU_HLS - Health and Life Sciences
        // @CovUniFBL - Business and Law
        // @CoventryFAH - Faculty of Arts and Humanities

        // Set the twitter user for faculty account
        UserTimeline timeline = new UserTimeline.Builder().screenName("covunieec").build();

        attachTimeline(timeline);
    }

    /**
     * Create adapter and attach it to the recycler view
     *
     * @param timeline Tweeter timeline to get the tweets from
     */
    public void attachTimeline(@NonNull Timeline<Tweet> timeline)
    {
        TweetTimelineRecyclerViewAdapter adapter = new TweetTimelineRecyclerViewAdapter.Builder(getContext())
                .setTimeline(timeline)
                .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                .build();

        // On first data change (the recycler view has data) hide the loading progress bar and unregister observer
        // because no more changes need to be detected
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onChanged()
            {
                super.onChanged();

                progressBar.setVisibility(View.INVISIBLE);

                adapter.unregisterAdapterDataObserver(this);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    /**
     * Read and parse the moodle rss feed, extends AsyncTask to run in background while getting the data, doesn't hold the main thread
     */
    private static class ReadRSS extends AsyncTask<Void, Void, Boolean> implements Html.ImageGetter
    {
        // Link for the first image found when reading moodle rss description
        private String imgSrc;
        private WeakReference<RSSAdapter> rssAdapter;


        //TODO: Restructure class to not change rssFeed directly

        /**
         * Constructor, get reference of adapter to notify the data changed and animations play accordingly, alter adapter rss feed array directly
         *
         * @param rssAdapter Adapter to be notified the data changed, and access recycler view and rssFeed array
         */
        ReadRSS(WeakReference<RSSAdapter> rssAdapter)
        {
            this.rssAdapter = rssAdapter;
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            try
            {
                URL url = new URL("https://cumoodle.coventry.ac.uk/rss/file.php/27/d824761bf8f70ac9f828581198537265/mod_forum/1/rss.xml");


                rssAdapter.get().rssFeed = parseFeed(url.openConnection().getInputStream());

                return true;

            }
            catch (IOException | XmlPullParserException e)
            {
                Log.e("ReadRSS", e.getMessage(), e);
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);
            RSSAdapter adapter = rssAdapter.get();
            RecyclerView rView = adapter.rView;

            // Notify adapter and recycler view that data was loaded
            if (aBoolean && rView != null)
            {
                adapter.notifyDataSetChanged();
                rView.scheduleLayoutAnimation();
            }
        }


        /**
         * Parses the rss feed from the input stream to return an array of hashmaps for each news
         *
         * @param iStream Input Stream to read the rss feed
         * @return Array of hasmap<String, String> of each news
         * @throws XmlPullParserException Error parsing xml file
         * @throws IOException            Error reading from iStream
         */
        @NonNull
        private ArrayList<HashMap<String, String>> parseFeed(@NonNull InputStream iStream) throws XmlPullParserException, IOException
        {
            ArrayList<HashMap<String, String>> rss = new ArrayList<>();

            try
            {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(iStream, "UTF_8");

                int eventType = xpp.getEventType();
                String text = "";
                boolean readingItem = false;
                HashMap<String, String> item = new HashMap<>();

                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    if (eventType == XmlPullParser.START_TAG)
                    {
                        if (xpp.getName().equalsIgnoreCase("item"))
                            readingItem = true;
                    }
                    else if (eventType == XmlPullParser.TEXT && readingItem)
                    {
                        SpannableStringBuilder spanString = (SpannableStringBuilder) Html.fromHtml(xpp.getText(), Html.FROM_HTML_MODE_COMPACT, this, null);

                        // Remove obj character that stays inside the string when an <img> tag is removed
                        Object[] spanObjects = spanString.getSpans(0, spanString.length(), Object.class);
                        for (Object sObj : spanObjects)
                        {
                            if (sObj instanceof ImageSpan)
                            {
                                ImageSpan imgSpan = (ImageSpan) sObj;
                                spanString.replace(spanString.getSpanStart(imgSpan), spanString.getSpanEnd(imgSpan), "");
                            }
                        }

                        text = spanString.toString();
                    }

                    else if (eventType == XmlPullParser.END_TAG)
                    {
                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            readingItem = false;
                            rss.add(new HashMap<>(item)); // Create a new copy, otherwise it will just "point" to item that is cleared after
                            item.clear();
                        }
                        else if (readingItem && !xpp.getName().equalsIgnoreCase("guid"))
                        {
                            if (xpp.getName().equalsIgnoreCase("description"))
                            {
                                // Separate the author from the description
                                String author = text.substring(2, text.indexOf('.')).trim();
                                String des = text.substring(text.indexOf('.') + 4, text.length()).trim();

                                item.put("author", author);
                                item.put(xpp.getName(), des);

                                // If there was an image inside the description, store it in the array
                                if (imgSrc != null && !imgSrc.isEmpty())
                                    item.put("image", imgSrc);
                            }
                            else
                                item.put(xpp.getName(), text);

                        }
                    }

                    eventType = xpp.next();
                }
            }
            finally
            {
                iStream.close();
            }

            return rss;
        }


        @Override
        public Drawable getDrawable(String source)
        {
            imgSrc = source;
            return null;
        }
    }

    /**
     * Load image from the internet to the image view, extends AsyncTask to run in background while getting the data, doesn't hold the main thread
     */
    private static class LoadImageURL extends AsyncTask<Void, Void, Boolean>
    {
        private WeakReference<ImageView> imageView;
        private String urlLink;
        private Bitmap bmp;

        /**
         * Constructor to set imageview reference and the url
         *
         * @param imgView Reference for the imageView
         * @param url     Url of the image to load
         */
        private LoadImageURL(WeakReference<ImageView> imgView, String url)
        {
            imageView = imgView;
            urlLink = url;
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            if (imageView.get() == null || urlLink.isEmpty())
                cancel(true);

            InputStream inpStream = null;
            try
            {
                URL url = new URL(urlLink);
                inpStream = url.openConnection().getInputStream();

                // First get the real image width and height to after now how much to sample it for
                // reducing the image size for lighter download from the internet and memory usage
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
                int maxSize = 256; // Image max size (Width or Height whichever comes first)

                options.inSampleSize = 1;
                if (options.outHeight > maxSize || options.outWidth > maxSize)
                {

                    final int halfHeight = options.outHeight / 2;
                    final int halfWidth = options.outWidth / 2;

                    // Check the biggest sample value that keeps the image just above the max size defined
                    while ((halfHeight / options.inSampleSize) >= maxSize && (halfWidth / options.inSampleSize) >= maxSize)
                        options.inSampleSize += 1;
                }

                options.inJustDecodeBounds = false;

                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
            }
            catch (IOException e)
            {
                Log.e("LoadImageURL", e.getMessage(), e);
                return false;
            }
            finally
            {
                try
                {
                    if (inpStream != null)
                        inpStream.close();
                }
                catch (IOException e)
                {
                    Log.e("LoadImageURL", e.getMessage(), e);
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);
            ImageView iView = imageView.get();

            if (aBoolean && iView != null && bmp != null)
                iView.setImageBitmap(bmp);
        }
    }

    /**
     * Adapter for recycler view, to use the rss data
     */
    private class RSSAdapter extends RecyclerView.Adapter<RSSAdapter.RSSViewHolder>
    {

        private ArrayList<HashMap<String, String>> rssFeed; // Fed data by the reader
        private RecyclerView rView; // Accessed by the reader
        private ReadRSS reader; //If user reselects the same bottom navigation option it just creates a new instance overriding the old one
        private LoadImageURL imageLoader;

        /**
         * Constructor, create and execure reader
         */
        RSSAdapter()
        {
            reader = new ReadRSS(new WeakReference<>(this));
            reader.execute();

            // Let's reycler view play animations for each view inside (They need an id)
            setHasStableIds(true);
        }

        @Override
        public int getItemCount()
        {
            if (rssFeed != null)
                return rssFeed.size();
            else
                return 0;
        }

        @NonNull
        @Override
        public RSSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_post, parent, false);

            return new RSSViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RSSViewHolder viewHolder, int position)
        {
            HashMap<String, String> post = rssFeed.get(position);

            viewHolder.title.setText(post.get("title"));
            viewHolder.author.setText(post.get("author"));
            viewHolder.description.setText(post.get("description"));

            if (post.get("pubDate") != null)
            {
                SimpleDateFormat dFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.UK);

                try
                {
                    Date d = dFormat.parse(post.get("pubDate"));

                    dFormat.applyPattern("dd MMM ''yy");

                    viewHolder.date.setText(dFormat.format(d));

                }
                catch (ParseException e)
                {
                    Log.e("onBindViewHolder", e.getMessage(), e);
                }
            }


            if (post.containsKey("image") && !Objects.requireNonNull(post.get("image"), "rssFeed  post is null.").isEmpty())
            {
                //TODO: Show temporary color on imageview background while loading image
                imageLoader = new LoadImageURL(new WeakReference<>(viewHolder.image), post.get("image"));
                imageLoader.execute();

                //TODO: Cash images to file when downloaded in the background task (Fast on click, and not running on main thread)
                viewHolder.image.setOnClickListener(new View.OnClickListener()
                {
                    // Don't allow clicking twice and starting two activities
                    boolean processingClick = false;

                    @Override
                    public void onClick(View v)
                    {
                        if (!processingClick)
                        {
                            processingClick = true;
                            FileOutputStream fOutStream = null;

                            try
                            {
                                // Save image to file to be loaded on the next activity
                                String fileName = "bitmap.png";
                                Bitmap bmp = ((BitmapDrawable) viewHolder.image.getDrawable()).getBitmap();
                                fOutStream = Objects.requireNonNull(getContext(), "Context is null.").openFileOutput(fileName, Context.MODE_PRIVATE);
                                bmp.compress(Bitmap.CompressFormat.PNG, 100, fOutStream);

                                final Intent intent = new Intent(getContext(), ZoomImageActivity.class);
                                intent.putExtra("fileName", fileName);

                                getContext().startActivity(intent);
                            }
                            catch (Exception e)
                            {
                                Log.e("viewHolder.image.setOnClickListener", e.getMessage(), e);
                            }
                            finally
                            {
                                if (fOutStream != null)
                                {
                                    try
                                    {
                                        fOutStream.close();
                                    }
                                    catch (IOException e)
                                    {
                                        Log.e("viewHolder.image.setOnClickListener.closingStream", e.getMessage(), e);
                                    }
                                }

                                processingClick = false;
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
        {
            super.onAttachedToRecyclerView(recyclerView);
            rView = recyclerView;
        }

        @Override
        public long getItemId(int position)
        {
            return super.getItemId(position);
        }


        /**
         * Specific data for each created view inside the recycler view
         */
        class RSSViewHolder extends RecyclerView.ViewHolder
        {
            private TextView title;
            private TextView date;
            private TextView author;
            private TextView description;
            private ImageView image;

            RSSViewHolder(View view)
            {
                super(view);

                title = view.findViewById(R.id.news_card_title);
                date = view.findViewById(R.id.news_card_date);
                author = view.findViewById(R.id.news_card_author);
                description = view.findViewById(R.id.news_card_description);
                image = view.findViewById(R.id.news_card_image);
            }
        }

    }
}
