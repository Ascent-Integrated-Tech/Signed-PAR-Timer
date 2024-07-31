package com.aitech.atak.AnyaPlugin.plugin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;

import com.atakmap.android.gui.PanListPreference;
import com.atakmap.android.preference.PluginPreferenceFragment;

public class AnyaPluginPreferenceFragment extends PluginPreferenceFragment {

    private static Context staticPluginContext;

    private Handler handler;
    public static final String TAG = "AnyaPluginPreferenceFragment";

    /**
     * Only will be called after this has been instantiated with the 1-arg constructor.
     * Fragments must has a zero arg constructor.
     */
    public AnyaPluginPreferenceFragment() {
        super(staticPluginContext, R.xml.preferences);
        this.handler = new Handler(Looper.getMainLooper());
    }

    @SuppressLint("ValidFragment")
    public AnyaPluginPreferenceFragment(final Context pluginContext) {
        super(pluginContext, R.xml.preferences);
        staticPluginContext = pluginContext;
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "AnyaPluginPreferenceFragment onCreate");

        // Set a listener for the "timer_length" preference
        findPreference("timer_length").setOnPreferenceChangeListener((preference, newValue) -> {
            String newTimerLength = (String) newValue;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(staticPluginContext);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("timer_length", newTimerLength);
            editor.apply();

            Log.d(TAG, "Timer length preference changed to: " + newTimerLength);
            return true;
        });
    }

    public void show(PreferenceFragment pf) {
        super.showScreen(new AnyaPluginPreferenceFragment());
    }

}
