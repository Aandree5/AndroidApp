package domains.coventry.andrefmsilva.coventryuniversity;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class DashboardLoginFragment extends Fragment
{
    EditText editTxt_username;
    EditText editTxt_password;

    TextView test;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dashboard_fragment_login, container, false);

        editTxt_username = view.findViewById(R.id.login_username);
        editTxt_password = view.findViewById(R.id.login_password);
        test = view.findViewById(R.id.test);
        editTxt_username.requestFocus();

        view.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                new test().execute();
            }
        });


        return view;
    }


    private class test extends AsyncTask<Void, Void, Boolean>
    {
        String str;

        @Override
        protected Boolean doInBackground(Void... voids)
        {
            try
            {
                String data = URLEncoder.encode("username", "UTF-8")
                        + "=" + URLEncoder.encode(editTxt_username.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(editTxt_password.getText().toString(), "UTF-8");

                URL url = new URL("http://a202sgi-1819t1a.andrefmsilva.coventry.domains/login.php");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line);
                }

                str = sb.toString();
            }
            catch (IOException e)
            {
                Log.e("login_button.click", e.getMessage(), e);
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);

            if (aBoolean)
            {
                test.setText(str);
                test.setText(test.getText() + "sad");
            }
        }
    }
}
