package com.aitech.atak.AnyaPlugin.plugin;

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
            PluginContextProvider contextProvider = serviceController.getService(PluginContextProvider.class);
            if (contextProvider != null) {
                AnyaPluginPreferenceFragment preferenceFragment = new AnyaPluginPreferenceFragment(contextProvider.getPluginContext());
                PluginNativeLoader.init(contextProvider.getPluginContext());
            } else {
                throw new IllegalArgumentException("PluginContextProvider cannot be null");
            }
        } else {
            throw new IllegalArgumentException("IServiceController cannot be null");
        }
    }
}
