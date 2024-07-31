package com.aitech.atak.AnyaPlugin;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.maps.MapView;
import com.atakmap.coremap.log.Log;
import com.atakmap.android.dropdown.DropDown.OnStateListener;

import com.aitech.atak.AnyaPlugin.plugin.R;

public class AnyaPluginDropDownReceiver extends DropDownReceiver implements OnStateListener {

    private static final String TAG = "AnyaPluginDropDownRcvr";
    public static final String SHOW_LAYOUT = "com.aitech.atak.AnyaPlugin.SHOW_LAYOUT";

    private final View AltView;

    public AnyaPluginDropDownReceiver(MapView mapView, Context context) {
        super(mapView);
        Log.d(TAG, "AnyaPluginDropDownReceiver ctr");
        AltView = PluginLayoutInflater.inflate(context, R.layout.main_layout, null);
    }

    @Override
    protected void disposeImpl() {
        // Clean up resources if needed
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d(TAG, "onReceive: " + action);
        if (action == null)
            return;

        // Show drop-down
        switch (action) {
            case SHOW_LAYOUT:
                showDropDown(AltView, 0.4, FULL_HEIGHT, 0.5, HALF_HEIGHT, false, this);
                setAssociationKey("AnyaPluginPreferences");
        }
    }

    @Override
    public void onDropDownSelectionRemoved() {
        // Handle removal of drop-down selection if needed
    }

    @Override
    public void onDropDownClose() {
        // Handle drop-down close if needed
    }

    @Override
    public void onDropDownSizeChanged(double v, double v1) {
        // Handle drop-down size change if needed
    }

    @Override
    public void onDropDownVisible(boolean b) {
        // Handle drop-down visibility change if needed
    }
}
