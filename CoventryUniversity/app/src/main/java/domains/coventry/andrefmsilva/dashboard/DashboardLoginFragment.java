package domains.coventry.andrefmsilva.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

import domains.coventry.andrefmsilva.coventryuniversity.MainActivity;
import domains.coventry.andrefmsilva.utils.MySQLConnector;
import domains.coventry.andrefmsilva.coventryuniversity.R;

import static domains.coventry.andrefmsilva.utils.Utils.setChildsEnabled;


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

        ((MainActivity)Objects.requireNonNull(getActivity(), "Activity must not be null.")).setToolbarText(R.string.dashboard_tab_login);

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

            new connectMySQL(new WeakReference<>(DashboardLoginFragment.this), FILE_LOGIN, resquestInfo, "Checking Login Details", false).execute();
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
        setChildsEnabled(layoutLogin, false);
    }

    @Override
    public void connectionSuccessful(HashMap<String, String> results)
    {
        SharedPreferences.Editor sharedPreferences = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE).edit();
        sharedPreferences.putString("status", "logged");
        sharedPreferences.putInt("id", Integer.valueOf(Objects.requireNonNull(results.get("id"))));
        sharedPreferences.putString("name", String.format("%s, %s", results.get("last_name"), results.get("first_name")));
        sharedPreferences.putString("username", results.get("username"));
        sharedPreferences.putString("email", results.get("email"));
        sharedPreferences.apply();

        Toast.makeText(getContext(), R.string.dashboard_login_successful, Toast.LENGTH_SHORT).show();

        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardInfoFragment()).commit();
    }

    @Override
    public void connectionUnsuccessful()
    {
        Toast.makeText(getContext(), R.string.dashboard_incorrect_user_pass, Toast.LENGTH_SHORT).show();

        setChildsEnabled(layoutLogin, true);
    }
}
