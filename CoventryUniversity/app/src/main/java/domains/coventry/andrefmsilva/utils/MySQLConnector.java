/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 :                                                  :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : MySQLConnector                                   :
 : Last modified 10 Nov 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

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
import java.util.Objects;

import domains.coventry.andrefmsilva.coventryuniversity.R;


public interface MySQLConnector
{
    String link = "http://a202sgi-1819t1a.andrefmsilva.coventry.domains/";

    String FILE_ENROL = "enrol";
    String FILE_LOGIN = "login";
    String FILE_RESETENROL = "resetenrol";

    /**
     * Gets called when retry button is pressed, used to write the connecting function to the database
     */
    default void connectWithRetry()
    {
    }

    /**
     * Gets called before the async task begins
     */
    void connectionStarted();

    /**
     * Called on successful connection, with the results from the connection
     *
     * @param results Hasmap with the data retreived from the databse, will be empty if succeded but no data was to be retreived
     */
    void connectionSuccessful(HashMap<String, String> results);

    /**
     * Called on unsuccessfull connection, with the information if was a "retry" type connection or not
     *
     * @param canRetry True is was a "retry" type connection, when true shows automatic retry dialog, if false doesn't show anything
     */
    default void connectionUnsuccessful(Boolean canRetry)
    {
    }


    class connectMySQL extends AsyncTask<Void, Void, HashMap<String, String>>
    {
        WeakReference<MySQLConnector> mySQLConnector;
        String mySQLLink;
        HashMap<String, String> requestInfo;
        String title;
        Boolean canRetry;

        private InfoDialog infoDialog;

        /**
         * Constructor with retry automatically set to true
         *
         * @param mySQLConnector Class that called the connections, implementing mySQLCOnnector
         * @param fileToConnect  Type of file to connect to
         * @param requestInfo    Hashmap of information to send to database
         * @param title          Title to show on InfoDialog
         */
        public connectMySQL(@NonNull WeakReference<MySQLConnector> mySQLConnector, @NonNull String fileToConnect, @NonNull HashMap<String, String> requestInfo, @NonNull String title)
        {
            this.mySQLConnector = mySQLConnector;
            this.mySQLLink = String.format("%s%s.php", link, fileToConnect);
            this.requestInfo = requestInfo;
            this.title = title;
            this.canRetry = true;
        }

        /**
         * Constructor
         *
         * @param mySQLConnector Class that called the connections, implementing mySQLCOnnector
         * @param fileToConnect  Type of file to connect to
         * @param requestInfo    Hashmap of information to send to database
         * @param title          Title to show on InfoDialog
         * @param canRetry       True if the user can have the option to retry the connection
         */
        public connectMySQL(@NonNull WeakReference<MySQLConnector> mySQLConnector, @NonNull String fileToConnect, @NonNull HashMap<String, String> requestInfo, @NonNull String title, @NonNull Boolean canRetry)
        {
            this.mySQLConnector = mySQLConnector;
            this.mySQLLink = String.format("%s%s.php", link, fileToConnect);
            this.requestInfo = requestInfo;
            this.title = title;
            this.canRetry = canRetry;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            mySQLConnector.get().connectionStarted();

            // A reference of the dialog is kept, so it can be canceled when task finishes
            infoDialog = InfoDialog.newIntance(InfoDialog.Type.NO_ACTION, title);
            infoDialog.show(Objects.requireNonNull(((Fragment) mySQLConnector.get()).getFragmentManager()), null);
        }

        @Override
        protected HashMap<String, String> doInBackground(Void... voids)
        {
            HashMap<String, String> results = new HashMap<>();
            try
            {
                StringBuilder data = new StringBuilder();
                for (String rI : requestInfo.keySet())
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


                outStreamWriter.close();
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

            infoDialog.dismiss();

            // If got information form the connection but wasn't a status response
            if (results.size() > 0 && results.get("status") == null)
                mySQLConnector.get().connectionSuccessful(results);

                // If was a status return and was confirmed (successfull), send an empty hashmap because there was no information to be read
            else if (Objects.equals(results.get("status"), "confirmed"))
                mySQLConnector.get().connectionSuccessful(new HashMap<>());

            else
            {
                if (canRetry)
                {
                    infoDialog = InfoDialog.newIntance(InfoDialog.Type.RETRY, title, "Connection failed, please try again.");
                    infoDialog.showNow(Objects.requireNonNull(((Fragment) mySQLConnector.get()).getFragmentManager()), null);

                    Objects.requireNonNull(infoDialog.getDialog().getWindow()).findViewById(R.id.dialog_confirm).setOnClickListener(v ->
                    {
                        infoDialog.dismiss();

                        mySQLConnector.get().connectWithRetry();
                    });
                }

                mySQLConnector.get().connectionUnsuccessful(canRetry);
            }

        }
    }
}
