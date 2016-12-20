package com.example.fredrik.supersudoku;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by fred on 12/20/16.
 */

public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
