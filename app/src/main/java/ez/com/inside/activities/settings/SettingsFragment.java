package ez.com.inside.activities.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import ez.com.inside.R;

/**
 * Created by Charly on 29/11/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private final String KEY_BEGINNING_FORFAIT = "beginning_forfait";
    private final String KEY_AMOUNT_FORFAIT = "amount_forfait";

    /**
     * Important step to do here : set the summaries with the current values.
     * If we don't, the summaries will be set to the default text in the xml.
     *
     * Also, every edittextpreference that needs validation such as inputs accepting only
     * integers, need to have a changepreferencelistener set here.
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        /******** Validation listeners ********/
        EditTextPreference beginningForfait = (EditTextPreference) findPreference(KEY_BEGINNING_FORFAIT);
        beginningForfait.setOnPreferenceChangeListener(new ValidationNumberPreferenceChangeListener());

        EditTextPreference amountForfait = (EditTextPreference) findPreference(KEY_AMOUNT_FORFAIT);
        amountForfait.setOnPreferenceChangeListener(new ValidationNumberPreferenceChangeListener());

        /******** Setting the right summaries *********/
        updateBeginningForfaitSummary(sharedPref);
        updateAmountForfaitSummary(sharedPref);
    }

    @Override
    public void onResume() {
        super.onResume();
        //unregister the preferenceChange listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        switch(key)
        {
            case KEY_BEGINNING_FORFAIT :
            {
                updateBeginningForfaitSummary(sharedPreferences);
                break;
            }
            case KEY_AMOUNT_FORFAIT :
            {
                updateAmountForfaitSummary(sharedPreferences);
                break;
            }
        }
    }

    private void updateBeginningForfaitSummary(SharedPreferences sharedPreferences)
    {
        Preference beginningForfait = findPreference(KEY_BEGINNING_FORFAIT);

        int currentValueForfait;
        currentValueForfait = Integer.parseInt(sharedPreferences.getString(KEY_BEGINNING_FORFAIT, ""));
        beginningForfait.setSummary("Tout les " + currentValueForfait + " du mois.");
    }

    private void updateAmountForfaitSummary(SharedPreferences sharedPreferences)
    {
        Preference amountForfait = findPreference(KEY_AMOUNT_FORFAIT);

        String currentAmount = sharedPreferences.getString(KEY_AMOUNT_FORFAIT, "");

        amountForfait.setSummary(currentAmount);
    }


    /******************** Useful listeners ********************/

    private class ValidationNumberPreferenceChangeListener implements Preference.OnPreferenceChangeListener
    {
        /**
         * @return true : the sharedpreference file will be updated. false : it won't.
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue)
        {
            String value = (String) newValue;

            if(value.charAt(0) == '-')
                return false;
            try
            {
                Integer.parseInt(value);
                return true;
            }
            catch(NumberFormatException e)
            {
                return false;
            }
        }
    }
}
