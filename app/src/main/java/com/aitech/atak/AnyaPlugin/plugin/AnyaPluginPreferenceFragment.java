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

    //private Handler handler;
    public static final String TAG = "AnyaStuff";

    /**
     * Only will be called after this has been instantiated with the 1-arg constructor.
     * Fragments must has a zero arg constructor.
     */
    public AnyaPluginPreferenceFragment() {
        super(staticPluginContext, R.xml.preferences);
        Log.d(TAG, "Zero-arg constructor called");
        //initializeHandler();
    }

    @SuppressLint("ValidFragment")
    public AnyaPluginPreferenceFragment(final Context pluginContext) {
        super(pluginContext, R.xml.preferences);
        Log.d(TAG, "One-arg constructor called with pluginContext: " + pluginContext);
        staticPluginContext = pluginContext;
        //initializeHandler();
    }

    // private void initializeHandler() {
    //     try {
    //         Log.d(TAG, "Initializing Handler");
    //         if (Looper.myLooper() == null) {
    //             Log.d(TAG, "Looper not prepared, preparing now");
    //             Looper.prepare();
    //         }
    //         this.handler = new Handler(Looper.getMainLooper());
    //         Log.d(TAG, "Handler initialized successfully");
    //     } catch (Exception e) {
    //         Log.e(TAG, "Error initializing Handler", e);
    //     }
    // }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        // Set a listener for the "timer_length" preference
        try {
            findPreference("timer_length").setOnPreferenceChangeListener((preference, newValue) -> {
                Log.d(TAG, "Preference change detected for timer_length");
                String newTimerLength = (String) newValue;
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(staticPluginContext);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("timer_length", newTimerLength);
                editor.apply();

                Log.d(TAG, "Timer length preference changed to: " + newTimerLength);
                return true;
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting preference change listener", e);
        }
    }

    public void show(PreferenceFragment pf) {
        Log.d(TAG, "show called with PreferenceFragment: " + pf);
        try {
            super.showScreen(new AnyaPluginPreferenceFragment());
            Log.d(TAG, "showScreen executed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error showing screen", e);
        }
    }
}

