package com.aitech.atak.AnyaPlugin.plugin;

import android.util.Log;
import android.content.Context;
import com.aitech.atak.AnyaPlugin.AnyaPluginComponent;
import com.aitech.atak.AnyaPlugin.AnyaPluginTool;
import com.atak.plugins.impl.AbstractPlugin;
import com.atak.plugins.impl.PluginContextProvider;

import gov.tak.api.plugin.IServiceController;

public class PluginTemplateLifecycle extends AbstractPlugin {

    public PluginTemplateLifecycle() {
        // Call the main constructor with null
        this(null);
    }

    public PluginTemplateLifecycle(IServiceController serviceController) {
        super(serviceController,
                // Uncomment this line if you want to add another plugin to the toolbar, you will need to change code in the PluginTool
                // new AnyaPluginTool(serviceController.getService(PluginContextProvider.class).getPluginContext()),
                new AnyaPluginComponent(serviceController));

        if (serviceController != null) {
            Log.d("AnyaStuff", "init 1");
            PluginContextProvider contextProvider = serviceController.getService(PluginContextProvider.class);
            try {
                if (contextProvider != null) {
                    Log.d("AnyaStuff", "init 2");
                    Context pluginContext = contextProvider.getPluginContext();
                    Log.d("AnyaStuff", "Plugin context obtained: " + pluginContext);
                    AnyaPluginPreferenceFragment preferenceFragment = new AnyaPluginPreferenceFragment(pluginContext);
                    Log.d("AnyaStuff", "AnyaPluginPreferenceFragment created: " + preferenceFragment);
                    PluginNativeLoader.init(pluginContext);
                    Log.d("AnyaStuff", "PluginNativeLoader initialized");
                } else {
                    Log.e("AnyaStuff", "not good");
                    throw new IllegalArgumentException("PluginContextProvider cannot be null");
                }
            } catch (Exception e) {
                Log.e("AnyaStuff", "bad!", e);
                throw new IllegalArgumentException("PluginContextProvider cannot be null", e);
            }
        } else {
            Log.e("AnyaStuff", "bad");
            throw new IllegalArgumentException("IServiceController cannot be null");
        }
    }
}
