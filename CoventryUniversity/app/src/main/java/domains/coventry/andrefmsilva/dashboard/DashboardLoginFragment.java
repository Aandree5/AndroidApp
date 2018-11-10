/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 :                                                  :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : DashboardLoginFragment                           :
 : Last modified 10 Nov 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

import domains.coventry.andrefmsilva.utils.MySQLConnector;
import domains.coventry.andrefmsilva.coventryuniversity.R;

import static domains.coventry.andrefmsilva.coventryuniversity.MainActivity.setToolbarText;
import static domains.coventry.andrefmsilva.utils.Utils.setChildrenEnabled;


public class DashboardLoginFragment extends Fragment implements MySQLConnector
{
    ViewGroup layoutLogin;
    EditText editTxtUsername;
    EditText editTxtPassword;
    Button btnLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dashboard_fragment_login, container, false);

        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.dashboard_tab_login, R.string.app_name);

        layoutLogin = view.findViewById(R.id.login_layout);
        editTxtUsername = view.findViewById(R.id.login_username);
        editTxtPassword = view.findViewById(R.id.login_password);
        btnLogin = view.findViewById(R.id.login_button);
        editTxtUsername.requestFocus();

        btnLogin.setOnClickListener(v ->
        {
            HashMap<String, String> resquestInfo = new HashMap<>();
            resquestInfo.put("username", editTxtUsername.getText().toString());
            resquestInfo.put("password", editTxtPassword.getText().toString());

            new connectMySQL(new WeakReference<>(DashboardLoginFragment.this), FILE_LOGIN, resquestInfo, "Checking login", false).execute();
        });

        editTxtPassword.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_DONE)
                btnLogin.performClick();

            // Return false so system will handle action after and close the soft keyboard
            return false;
        });

        return view;
    }

    @Override
    public void connectionStarted()
    {
        // Disable all layout children
        setChildrenEnabled(layoutLogin, false);
    }

    @Override
    public void connectionSuccessful(HashMap<String, String> results)
    {
        SharedPreferences.Editor sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext())).edit();
        sharedPreferences.putString("status", "logged");
        sharedPreferences.putInt("id", Integer.valueOf(Objects.requireNonNull(results.get("id"))));
        sharedPreferences.putString("name", String.format("%s, %s", results.get("last_name"), results.get("first_name")));
        sharedPreferences.putString("username", results.get("username"));
        sharedPreferences.putString("email", results.get("email"));
        sharedPreferences.putString("facultyTwitter", results.get("twitter"));
        sharedPreferences.putString("year", results.get("course_year"));
        sharedPreferences.putString("courseCode", results.get("code"));
        sharedPreferences.putString("courseName", results.get("name"));
        sharedPreferences.putString("courseType", results.get("type"));

        if (results.get("photo") != null)
        {
            byte[] img = Base64.decode(results.get("photo"), Base64.DEFAULT);

            Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);

            String fileName = "photo.jpg";

            FileOutputStream fOutStream = null;
            try
            {
                // Save image to file to be loaded on the next activity
                fOutStream = Objects.requireNonNull(getContext()).openFileOutput(fileName, Context.MODE_PRIVATE);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOutStream);
            }
            catch (Exception e)
            {
                Log.e("DashboardLoginFragment", e.getMessage(), e);
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
                        Log.e("DashboardLoginFragment", e.getMessage(), e);
                    }
                }
            }

            sharedPreferences.putString("photo", fileName);
        }

        sharedPreferences.apply();

        Toast.makeText(getContext(), R.string.dashboard_login_successful, Toast.LENGTH_SHORT).show();

        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardInfoFragment()).commit();
    }

    @Override
    public void connectionUnsuccessful(Boolean canRetry)
    {
        Toast.makeText(getContext(), R.string.dashboard_incorrect_user_pass, Toast.LENGTH_SHORT).show();

        // Enable all layout children
        setChildrenEnabled(layoutLogin, true);
    }
}
