package com.aitech.atak.AnyaPlugin;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.atak.plugins.impl.AbstractPluginTool;
import com.aitech.atak.AnyaPlugin.plugin.R;

public class AnyaPluginTool extends AbstractPluginTool {

    public AnyaPluginTool(final Context context) {
        super(
                context,
                context.getString(R.string.app_name),
                context.getString(R.string.app_name),
                getDrawable(context, R.drawable.oxygen_mask),
                AnyaPluginDropDownReceiver.SHOW_LAYOUT
        );
    }

    private static Drawable getDrawable(Context context, int drawableId) {
        return context.getResources().getDrawable(drawableId, context.getTheme());
    }
}
