package com.example.fredrik.supersudoku;

import android.preference.PreferenceActivity;

import java.util.List;

/**
 * Created by fred on 12/20/16.
 */

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target)
    {
        loadHeadersFromResource(R.xml.headers_preference, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return SettingsFragment.class.getName().equals(fragmentName);
    }
}
