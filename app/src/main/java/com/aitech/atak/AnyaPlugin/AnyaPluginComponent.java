package com.aitech.atak.AnyaPlugin;

import com.aitech.atak.AnyaPlugin.plugin.AnyaPluginPreferenceFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;

import gov.tak.api.plugin.IServiceController;
import gov.tak.api.ui.IHostUIService;
import gov.tak.api.ui.ToolbarItem;
import gov.tak.api.ui.ToolbarItemAdapter;
import gov.tak.platform.marshal.MarshalManager;

import com.atakmap.android.emergency.tool.EmergencyManager;
import com.atakmap.android.emergency.tool.EmergencyType;
import com.atakmap.android.dropdown.DropDownMapComponent;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.widgets.LayoutHelper;
import com.atakmap.android.widgets.RootLayoutWidget;
import com.atakmap.app.preferences.ToolsPreferenceFragment;
import com.aitech.atak.AnyaPlugin.plugin.R;

import java.util.List;
import java.util.Locale;

public class AnyaPluginComponent extends DropDownMapComponent  {

    private static final String TAG = "AnyaPluginComponent";
    public static final String TOGGLE_OVERLAY_VIEW = "com.aitech.atak.AnyaPlugin.TOGGLE_OVERLAY_VIEW";

    private Context pluginContext;
    private View overlayView;
    private RelativeLayout mapParent;
    private TextView timerTextView;
    private Handler handler;
    private Runnable runnable;
    private long startTime;
    private long elapsedTime;
    private boolean isRunning;
    private MapView mapView;
    private final BroadcastReceiver broadcastReceiver;
    private AnyaPluginPreferenceFragment preferenceFragment;

    private BroadcastReceiver openPreferencesReceiver;
    private SharedPreferences sharedPref;
    private ToolbarItem toolbarItem;
    private IHostUIService uiService;
    private IServiceController serviceController;
    private int timerLength;

    public AnyaPluginComponent(IServiceController serviceController) {
        if (serviceController == null) {
            throw new IllegalArgumentException("IServiceController cannot be null");
        }
        this.serviceController = serviceController;
        //this.handler = new Handler(Looper.getMainLooper()); // Ensure Handler is created on main thread
        initializeHandler();
        
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received broadcast to toggle overlay");
                toggleOverlay();
            }
        };
    }

    private void initializeHandler() {
        try {
            Log.d(TAG, "Initializing Handler");
            if (Looper.myLooper() == null) {
                Log.d(TAG, "Looper not prepared, preparing now");
                Looper.prepare();
            }
            this.handler = new Handler(Looper.getMainLooper());
            Log.d(TAG, "Handler initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Handler", e);
        }
    }

    @Override
    public void onCreate(final Context context, final Intent intent, final MapView mapView) {
        super.onCreate(context, intent, mapView);
        Log.d(TAG, "onCreate called");
        this.pluginContext = context;
        this.mapView = mapView;

        mapParent = mapView.getRootView().findViewById(com.atakmap.app.R.id.map_parent);
        if (mapParent == null) {
            Log.e(TAG, "mapParent is null");
            return;
        }

        overlayView = LayoutInflater.from(context).inflate(R.layout.main_layout, mapParent, false);
        if (overlayView == null) {
            Log.e(TAG, "overlayView is null after inflation");
            return;
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.setMargins(30, 50, 0, 0); // Adjust the margins to position the overlay

        mapParent.addView(overlayView, layoutParams);
        overlayView.setVisibility(View.INVISIBLE); // Hide the overlay initially
        Log.d(TAG, "Overlay view added to map parent and set to INVISIBLE");

        timerTextView = overlayView.findViewById(R.id.timerTextView);

        // Initialize the toolbar button for the plugin
        toolbarItem = new ToolbarItem.Builder(
                pluginContext.getString(R.string.app_name),
                MarshalManager.marshal(
                        pluginContext.getResources().getDrawable(R.drawable.oxygen_mask),
                        android.graphics.drawable.Drawable.class,
                        gov.tak.api.commons.graphics.Bitmap.class))
                .setListener(new ToolbarItemAdapter() {
                    @Override
                    public void onClick(ToolbarItem item) {
                        toggleOverlay();
                    }
                })
                .build();

        // Get timer length from preferences
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        timerLength = Integer.parseInt(preferences.getString("timer_length", "10"));
//        Log.d(TAG, "Timer length set to: " + timerLength + " minutes");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        timerLength = Integer.parseInt(preferences.getString("timer_length", "10"));
        Log.d(TAG, "Timer length set to: " + timerLength + " minutes");

        // Register preference change listener
        preferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals("timer_length")) {
                timerLength = Integer.parseInt(sharedPreferences.getString(key, "10"));
                Log.d(TAG, "Timer length updated to: " + timerLength + " minutes");
            }
        });

        runnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    long updatedElapsedElapsedTime = System.currentTimeMillis() - startTime + elapsedTime;
                    int minutes = (int) (updatedElapsedElapsedTime / 1000 / 60);
                    int seconds = (int) ((updatedElapsedElapsedTime / 1000) % 60);
                    String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                    timerTextView.setText(time);

                    Log.d(TAG, "Timer running: " + time + " / Target: " + timerLength + " minutes");
                    // Check if the timer length has passed
                    if (updatedElapsedElapsedTime >= timerLength * 60 * 1000 && (updatedElapsedElapsedTime / 1000) % (timerLength * 60) == 0) {
                        triggerReminderAlert("Check on your units!");
                        Log.d(TAG, "Reminder alert triggered at: " + time);
                    }

                    handler.postDelayed(this, 1000);
                }
            }
        };

        Button startButton = overlayView.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        Button pauseButton = overlayView.findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
            }
        });

        Button stopButton = overlayView.findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });

        Button resetButton = overlayView.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        IntentFilter filter = new IntentFilter(TOGGLE_OVERLAY_VIEW);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        context.registerReceiver(broadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        Log.d(TAG, "Receiver registered and initial setup complete");

        // Register the preference fragment
        ToolsPreferenceFragment.register(
                new ToolsPreferenceFragment.ToolPreference(
                        context.getString(R.string.plugin_preferences_title),
                        "Manage plugin settings",
                        "pluginPreference",
                        context.getResources().getDrawable(R.drawable.oxygen_mask),
                        new AnyaPluginPreferenceFragment(context)
                )
        );
    }

    private void triggerReminderAlert(String message) {
        Log.d(TAG, "Triggering Reminder Alert");

        EmergencyType customType = EmergencyType.Custom;
        EmergencyManager.getInstance().setEmergencyType(customType);
        EmergencyManager.getInstance().initiateRepeat(customType, false);
        EmergencyManager.getInstance().setEmergencyOn(true);
    }

    public void toggleOverlay() {
        if (overlayView == null) {
            Log.e(TAG, "Overlay view is null in toggleOverlay");
            return;
        }

        Log.d(TAG, "Toggling overlay visibility");
        if (overlayView.getVisibility() == View.VISIBLE) {
            overlayView.setVisibility(View.INVISIBLE);
            Log.d(TAG, "Overlay made INVISIBLE");
        } else {
            overlayView.setVisibility(View.VISIBLE);
            Log.d(TAG, "Overlay made VISIBLE");
        }
    }

    private void startTimer() {
        if (!isRunning) {
            startTime = System.currentTimeMillis();
            handler.post(runnable);
            isRunning = true;
        }
    }

    private void pauseTimer() {
        if (isRunning) {
            handler.removeCallbacks(runnable);
            elapsedTime += System.currentTimeMillis() - startTime;
            isRunning = false;
        }
    }

    private void stopTimer() {
        if (isRunning) {
            handler.removeCallbacks(runnable);
            isRunning = false;
        }
        elapsedTime = 0;
        timerTextView.setText("00:00");
    }

    private void resetTimer() {
        elapsedTime = 0;
        timerTextView.setText("00:00");
        if (isRunning) {
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onStart(Context context, MapView view) {
        super.onStart(context, view);
        // Add the toolbar item when the plugin starts
        uiService = serviceController.getService(IHostUIService.class);
        if (uiService != null) {
            uiService.addToolbarItem(toolbarItem);
        }
    }

    @Override
    public void onStop(Context context, MapView view) {
        super.onStop(context, view);
        // Remove the toolbar item when the plugin stops
        if (uiService != null) {
            uiService.removeToolbarItem(toolbarItem);
        }
    }

//    @Override
//    protected void onDestroyImpl(Context context, MapView view) {
//        Log.d(TAG, "onDestroyImpl called");
//        if (mapParent != null && overlayView != null) {
//            mapParent.removeView(overlayView);
//        }
//        if (handler != null) {
//            handler.removeCallbacks(runnable);
//        }
//        context.unregisterReceiver(broadcastReceiver);
//    }
    @Override
    protected void onDestroyImpl(Context context, MapView view) {
        Log.d(TAG, "onDestroyImpl called");
        if (mapParent != null && overlayView != null) {
            mapParent.removeView(overlayView);
        }
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        context.unregisterReceiver(broadcastReceiver);

        // Unregister preference change listener
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.unregisterOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals("timer_length")) {
                timerLength = Integer.parseInt(sharedPreferences.getString(key, "10"));
                Log.d(TAG, "Timer length updated to: " + timerLength + " minutes");
            }
        });
    }

}