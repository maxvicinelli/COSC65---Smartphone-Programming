package edu.dartmouth.cs.myruns1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import edu.dartmouth.cs.myruns1.utils.Preference;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Settings");
        setUpActionBar();

        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        PrefsFragment mPrefsFragment = new PrefsFragment();
        mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
        mFragmentTransaction.commit();
    }


    public static class PrefsFragment extends PreferenceFragmentCompat {

        private edu.dartmouth.cs.myruns1.utils.Preference preference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.settings_preferences);
            preference = new edu.dartmouth.cs.myruns1.utils.Preference(this.getContext());

            ListPreference unitSelection = (ListPreference) findPreference("unit_preference");

            if (preference.getUnits() == null) {
                unitSelection.setValue("Metric");
            } else {
                unitSelection.setValue(preference.getUnits());
            }
            unitSelection.setOnPreferenceChangeListener(new androidx.preference.Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(androidx.preference.Preference p, Object newValue) {
                    preference.setUnits(newValue.toString());
                    return true;
                }
            });
        }
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
