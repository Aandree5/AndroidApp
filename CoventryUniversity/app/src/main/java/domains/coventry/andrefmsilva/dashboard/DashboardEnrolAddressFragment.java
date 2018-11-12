/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 :                                                  :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : DashboardEnrolAddressFragment                    :
 : Last modified 10 Nov 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

import domains.coventry.andrefmsilva.coventryuniversity.MainActivity;
import domains.coventry.andrefmsilva.coventryuniversity.R;
import domains.coventry.andrefmsilva.utils.MySQLConnector;

import static domains.coventry.andrefmsilva.utils.Utils.setToolbarText;
import static domains.coventry.andrefmsilva.utils.Utils.clearChildren;
import static domains.coventry.andrefmsilva.utils.Utils.setChildrenEnabled;

public class DashboardEnrolAddressFragment extends Fragment implements MySQLConnector
{
    LinearLayout enrolAddressLayout;
    LinearLayout sameOptionsLayout;
    LinearLayout formLayout;
    TabLayout tabLayoutOptions;
    HashMap<String, String> address;

    EditText editTxtFlat;
    EditText editTxtHouse;
    EditText editTxtStreet;
    EditText editTxtCity;
    EditText editTxtRegion;
    EditText editTxtPostCode;
    EditText editTxtCountry;
    EditText editTxtPhone;
    EditText editTxtMobile;

    public DashboardEnrolAddressFragment()
    {
        address = new HashMap<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dashboard_fragment_enroladdress, container, false);

        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.enrol_address, R.string.app_name);

        enrolAddressLayout = view.findViewById(R.id.enroladress_layout);
        sameOptionsLayout = view.findViewById(R.id.enroladdress_same_options);
        formLayout = view.findViewById(R.id.enroladdress_form);
        tabLayoutOptions = view.findViewById(R.id.enroladdress_tablayout);
        CheckBox chckBxSameHome = view.findViewById(R.id.enroladdress_same_home);
        CheckBox chckBxSameTerm = view.findViewById(R.id.enroladdress_same_term);

        editTxtFlat = view.findViewById(R.id.enroladdress_flat);
        editTxtHouse = view.findViewById(R.id.enroladdress_house);
        editTxtStreet = view.findViewById(R.id.enroladdress_street);
        editTxtCity = view.findViewById(R.id.enroladdress_city);
        editTxtRegion = view.findViewById(R.id.enroladdress_region);
        editTxtPostCode = view.findViewById(R.id.enroladdress_postcode);
        editTxtCountry = view.findViewById(R.id.enroladdress_country);
        editTxtPhone = view.findViewById(R.id.enroladdress_phone);
        editTxtMobile = view.findViewById(R.id.enroladdress_mobile);

        tabLayoutOptions.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                // Disable unwanted options, all are enabled on unselect tab
                switch (tab.getPosition())
                {
                    case 0: // Home
                        setChildrenEnabled(sameOptionsLayout, false);
                        break;

                    case 1: // Term
                        chckBxSameTerm.setEnabled(false);
                        break;
                }

                String selectedTab = Objects.requireNonNull(tab.getText()).toString();

                // Check if is to be copied from other option, and set checkbox
                if (address.containsKey(String.format("%s%s", selectedTab, "Copy")))
                {
                    selectedTab = address.get(String.format("%s%s", selectedTab, "Copy"));
                    setChildrenEnabled(formLayout, false);

                    switch (Objects.requireNonNull(selectedTab))
                    {
                        case "Home":
                            chckBxSameHome.setChecked(true);
                            break;
                        case "Term":
                            chckBxSameTerm.setChecked(true);
                            break;
                    }
                }

                readAddressMap(selectedTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
                saveAddressMap(Objects.requireNonNull(tab.getText()).toString());

                setChildrenEnabled(sameOptionsLayout, true);
                setChildrenEnabled(formLayout, true);
                clearChildren(sameOptionsLayout);
                clearChildren(formLayout);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });

        View.OnClickListener listener = v ->
        {
            CheckBox chkBox = (CheckBox) v;

            String selectedTab = Objects.requireNonNull(Objects.requireNonNull(tabLayoutOptions.getTabAt(tabLayoutOptions.getSelectedTabPosition())).getText()).toString();

            if (chkBox.isChecked())
            {
                clearChildren(formLayout);
                (chkBox.getId() == R.id.enroladdress_same_home ? chckBxSameTerm : chckBxSameHome).setChecked(false);

                setChildrenEnabled(formLayout, false);

                address.remove(String.format("%s%s", selectedTab, "Flat"));
                address.remove(String.format("%s%s", selectedTab, "House"));
                address.remove(String.format("%s%s", selectedTab, "Street"));
                address.remove(String.format("%s%s", selectedTab, "City"));
                address.remove(String.format("%s%s", selectedTab, "Region"));
                address.remove(String.format("%s%s", selectedTab, "PostCode"));
                address.remove(String.format("%s%s", selectedTab, "Country"));
                address.remove(String.format("%s%s", selectedTab, "Phone"));
                address.remove(String.format("%s%s", selectedTab, "Mobile"));


                String copyFrom = (chkBox.getId() == R.id.enroladdress_same_home ? "Home" : "Term");

                address.put(String.format("%s%s", selectedTab, "Copy"), copyFrom);

                readAddressMap(copyFrom);
            }
            else
            {
                clearChildren(formLayout);
                setChildrenEnabled(formLayout, true);
                address.remove(String.format("%s%s", selectedTab, "Copy"));
            }
        };

        chckBxSameHome.setOnClickListener(listener);
        chckBxSameTerm.setOnClickListener(listener);

        view.findViewById(R.id.enroladdress_confirm).setOnClickListener(v ->
        {
            saveAddressMap(Objects.requireNonNull(Objects.requireNonNull(tabLayoutOptions.getTabAt(tabLayoutOptions.getSelectedTabPosition())).getText()).toString());

            if (!isAddressSet("Home"))
            {
                Toast.makeText(getContext(), "Home address details missing", Toast.LENGTH_LONG).show();
                return;
            }

            if (!isAddressSet("Term"))
            {
                Toast.makeText(getContext(), "Term address details missing", Toast.LENGTH_LONG).show();
                return;
            }

            if (!isAddressSet("Contact"))
            {
                Toast.makeText(getContext(), "Contact address details missing", Toast.LENGTH_LONG).show();
                return;
            }

            HashMap<String, String> requestInfo = new HashMap<>();
            requestInfo.put("type", "confirm_address_registration");
            requestInfo.put("id", String.valueOf(((MainActivity) getActivity()).getUserID()));
            requestInfo.put("home_flat", address.get("HomeFlat"));
            requestInfo.put("home_house", address.get("HomeHouse"));
            requestInfo.put("home_street", address.get("HomeStreet"));
            requestInfo.put("home_city", address.get("HomeCity"));
            requestInfo.put("home_region", address.get("HomeRegion"));
            requestInfo.put("home_postcode", address.get("HomePostCode"));
            requestInfo.put("home_country", address.get("HomeCountry"));
            requestInfo.put("home_phone", address.get("HomePhone"));
            requestInfo.put("home_mobile", address.get("HomeMobile"));

            if (address.containsKey("TermCopy"))
                requestInfo.put("term_copy", address.get("TermCopy"));
            else
            {
                requestInfo.put("term_flat", address.get("TermFlat"));
                requestInfo.put("term_house", address.get("TermHouse"));
                requestInfo.put("term_street", address.get("TermStreet"));
                requestInfo.put("term_city", address.get("TermCity"));
                requestInfo.put("term_region", address.get("TermRegion"));
                requestInfo.put("term_postcode", address.get("TermPostCode"));
                requestInfo.put("term_country", address.get("TermCountry"));
                requestInfo.put("term_phone", address.get("TermPhone"));
                requestInfo.put("term_mobile", address.get("TermMobile"));
            }

            if (address.containsKey("ContactCopy"))
                requestInfo.put("contact_copy", address.get("ContactCopy"));
            else
            {
                requestInfo.put("contact_flat", address.get("ContactFlat"));
                requestInfo.put("contact_house", address.get("ContactHouse"));
                requestInfo.put("contact_street", address.get("ContactStreet"));
                requestInfo.put("contact_city", address.get("ContactCity"));
                requestInfo.put("contact_region", address.get("ContactRegion"));
                requestInfo.put("contact_postcode", address.get("ContactPostCode"));
                requestInfo.put("contact_country", address.get("ContactCountry"));
                requestInfo.put("contact_phone", address.get("ContactPhone"));
                requestInfo.put("contact_mobile", address.get("ContactMobile"));
            }

            new connectMySQL(new WeakReference<>(this), FILE_ENROL, requestInfo, "Registering Address", false).execute();
        });

        view.findViewById(R.id.enroladdress_cancel).setOnClickListener(v -> getActivity().onBackPressed());

        return view;
    }

    /**
     * Read the adresses from the Hashmap and set the editboxes text
     *
     * @param name Address name to look for the data
     */
    void readAddressMap(String name)
    {
        if (address.containsKey(String.format("%s%s", name, "Copy")))
            name = address.get(String.format("%s%s", name, "Copy"));

        editTxtFlat.setText(address.get(String.format("%s%s", name, "Flat")));
        editTxtHouse.setText(address.get(String.format("%s%s", name, "House")));
        editTxtStreet.setText(address.get(String.format("%s%s", name, "Street")));
        editTxtCity.setText(address.get(String.format("%s%s", name, "City")));
        editTxtRegion.setText(address.get(String.format("%s%s", name, "Region")));
        editTxtPostCode.setText(address.get(String.format("%s%s", name, "PostCode")));
        editTxtCountry.setText(address.get(String.format("%s%s", name, "Country")));
        editTxtPhone.setText(address.get(String.format("%s%s", name, "Phone")));
        editTxtMobile.setText(address.get(String.format("%s%s", name, "Mobile")));
    }


    /**
     * Save the current data on screen to the given address name, only if is not copying
     *
     * @param name Address name to store the data
     */
    void saveAddressMap(String name)
    {
        // If not copying, save the written data
        if (!address.containsKey(String.format("%s%s", name, "Copy")))
        {
            address.put(String.format("%s%s", name, "Flat"), editTxtFlat.getText().toString());
            address.put(String.format("%s%s", name, "House"), editTxtHouse.getText().toString());
            address.put(String.format("%s%s", name, "Street"), editTxtStreet.getText().toString());
            address.put(String.format("%s%s", name, "City"), editTxtCity.getText().toString());
            address.put(String.format("%s%s", name, "Region"), editTxtRegion.getText().toString());
            address.put(String.format("%s%s", name, "PostCode"), editTxtPostCode.getText().toString());
            address.put(String.format("%s%s", name, "Country"), editTxtCountry.getText().toString());
            address.put(String.format("%s%s", name, "Phone"), editTxtPhone.getText().toString());
            address.put(String.format("%s%s", name, "Mobile"), editTxtMobile.getText().toString());
        }
    }

    /**
     * Check the address hashmap for the details of the given address name
     *
     * @param name Address name to check for the details
     * @return True if all details are set, false otherwise
     */
    Boolean isAddressSet(String name)
    {
        if (address.containsKey(String.format("%s%s", name, "Copy")))
            return true;

        if (address.containsKey(String.format("%s%s", name, "Flat")) && address.containsKey(String.format("%s%s", name, "House")) &&
                address.containsKey(String.format("%s%s", name, "Street")) && address.containsKey(String.format("%s%s", name, "City")) &&
                address.containsKey(String.format("%s%s", name, "Region")) && address.containsKey(String.format("%s%s", name, "PostCode")) &&
                address.containsKey(String.format("%s%s", name, "Country")) && address.containsKey(String.format("%s%s", name, "Phone")) &&
                address.containsKey(String.format("%s%s", name, "Mobile")))
            return (!Objects.equals(address.get(String.format("%s%s", name, "Flat")), "") && !Objects.equals(address.get(String.format("%s%s", name, "House")), "") &&
                    !Objects.equals(address.get(String.format("%s%s", name, "Street")), "") && !Objects.equals(address.get(String.format("%s%s", name, "City")), "") &&
                    !Objects.equals(address.get(String.format("%s%s", name, "Region")), "") && !Objects.equals(address.get(String.format("%s%s", name, "PostCode")), "") &&
                    !Objects.equals(address.get(String.format("%s%s", name, "Country")), "") && !Objects.equals(address.get(String.format("%s%s", name, "Phone")), "") &&
                    !Objects.equals(address.get(String.format("%s%s", name, "Mobile")), ""));

        return false;
    }

    @Override
    public void connectionStarted()
    {
        setChildrenEnabled(enrolAddressLayout, false);

        // Select Home to be easier to enable everything if connection fails
        Objects.requireNonNull(tabLayoutOptions.getTabAt(0)).select();
    }

    @Override
    public void connectionSuccessful(HashMap<String, String> results)
    {
        Toast.makeText(getContext(), "Addresses registered", Toast.LENGTH_LONG).show();
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    @Override
    public void connectionUnsuccessful(Boolean canRetry)
    {
        Toast.makeText(getContext(), "Connection failed", Toast.LENGTH_LONG).show();

        setChildrenEnabled(enrolAddressLayout, true);
        setChildrenEnabled(sameOptionsLayout, false);
    }
}
