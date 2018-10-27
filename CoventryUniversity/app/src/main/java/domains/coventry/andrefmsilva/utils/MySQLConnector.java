package domains.coventry.andrefmsilva.utils;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

import domains.coventry.andrefmsilva.coventryuniversity.R;

import static android.view.View.GONE;
import static domains.coventry.andrefmsilva.coventryuniversity.MainActivity.getLoadginView;

public interface MySQLConnector
{
    String link = "http://a202sgi-1819t1a.andrefmsilva.coventry.domains/";

    String FILE_ENROL = "enrol";
    String FILE_LOGIN = "login";

    default void connectWithRetry()
    {
    }

    void connectionStarted();
    void connectionSuccessful(HashMap<String, String> results);
    void connectionUnsuccessful();

    class connectMySQL extends AsyncTask<Void, Void, HashMap<String, String>>
    {
        WeakReference<MySQLConnector> mySQLConnector;
        String mySQLLink;
        HashMap<String, String> requestInfo;
        String text;
        Boolean canRetry;

        @SuppressWarnings("unused")
        public connectMySQL(WeakReference<MySQLConnector> mySQLConnector, String fileToConnect, HashMap<String, String> requestInfo)
        {
            this.mySQLConnector = mySQLConnector;
            this.mySQLLink = String.format("%s%s.php", link, fileToConnect);
            this.requestInfo = requestInfo;
            this.canRetry = true;
            this.text = null;
        }

        @SuppressWarnings("unused")
        public connectMySQL(WeakReference<MySQLConnector> mySQLConnector, String fileToConnect, HashMap<String, String> requestInfo, Boolean canRetry)
        {
            this.mySQLConnector = mySQLConnector;
            this.mySQLLink = String.format("%s%s.php", link, fileToConnect);
            this.requestInfo = requestInfo;
            this.canRetry = canRetry;
            this.text = null;
        }

        @SuppressWarnings("unused")
        public connectMySQL(WeakReference<MySQLConnector> mySQLConnector, String fileToConnect, HashMap<String, String> requestInfo, String text)
        {
            this.mySQLConnector = mySQLConnector;
            this.mySQLLink = String.format("%s%s.php", link, fileToConnect);
            this.requestInfo = requestInfo;
            this.text = text;
            this.canRetry = true;
        }

        @SuppressWarnings("unused")
        public connectMySQL(WeakReference<MySQLConnector> mySQLConnector, String fileToConnect, HashMap<String, String> requestInfo, String text, Boolean canRetry)
        {
            this.mySQLConnector = mySQLConnector;
            this.mySQLLink = String.format("%s%s.php", link, fileToConnect);
            this.requestInfo = requestInfo;
            this.text = text;
            this.canRetry = canRetry;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            mySQLConnector.get().connectionStarted();

            getLoadginView().setVisibility(View.VISIBLE);
            getLoadginView().setText(text);
        }

        @Override
        protected HashMap<String, String> doInBackground(Void... voids)
        {
            HashMap<String, String> results = new HashMap<>();
            try
            {
                StringBuilder data = new StringBuilder();
                for(String rI : requestInfo.keySet())
                    data.append(URLEncoder.encode(rI, "UTF-8")).append("=").append(URLEncoder.encode(requestInfo.get(rI), "UTF-8")).append("&");

                URL url = new URL(mySQLLink);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter outStreamWriter = new OutputStreamWriter(conn.getOutputStream());
                outStreamWriter.write(data.toString());
                outStreamWriter.flush();

                BufferedReader buffReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = buffReader.readLine()) != null)
                    response.append(line);

                if (response.length() < 1)
                    return new HashMap<>();

                JSONObject jsonObject = new JSONObject(response.toString());

                Iterator<String> it = jsonObject.keys();
                while (it.hasNext())
                {
                    String key = it.next();
                    results.put(key, jsonObject.getString(key));
                }
            }
            catch (JSONException | IOException e)
            {
                Log.e("connectMySQL", e.getMessage(), e);
                return new HashMap<>();
            }

            return results;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> results)
        {
            super.onPostExecute(results);

            if (results.size() > 0)
            {
                mySQLConnector.get().connectionSuccessful(results);
                getLoadginView().setVisibility(GONE);
            }
            else
            {
                if (canRetry)
                {
                    getLoadginView().findViewById(R.id.loadingview_retry).setOnClickListener(v ->
                    {
                        getLoadginView().findViewById(R.id.loadingview_retry).setVisibility(GONE);
                        getLoadginView().findViewById(R.id.loadingview_progressbar).setVisibility(View.VISIBLE);

                        mySQLConnector.get().connectWithRetry();
                    });

                    getLoadginView().findViewById(R.id.loadingview_progressbar).setVisibility(GONE);
                    getLoadginView().findViewById(R.id.loadingview_retry).setVisibility(View.VISIBLE);
                }

                mySQLConnector.get().connectionUnsuccessful();

                if(!canRetry)
                    getLoadginView().setVisibility(GONE);
            }

        }
    }
}
