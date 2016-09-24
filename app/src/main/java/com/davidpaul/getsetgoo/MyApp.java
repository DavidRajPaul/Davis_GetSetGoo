package com.davidpaul.getsetgoo;

import android.app.Application;
import android.content.Context;

/**
 * Created by David Paul on 8/15/2016.
 */
public class MyApp extends Application {
    public static Context sContext;

    @Override
    public void onCreate() {
        sContext = getApplicationContext();
        FontsOverride.setDefaultFont(this, "DEFAULT", "Roboto-LightItalic.ttf");
//        FontsOverride.setDefaultFont(this, "MONOSPACE", "MyFontAsset2.ttf");
//        FontsOverride.setDefaultFont(this, "SERIF", "MyFontAsset3.ttf");
//        FontsOverride.setDefaultFont(this, "SANS_SERIF", "MyFontAsset4.ttf");
        super.onCreate();
    }

    public static Context getsContext() {
        return sContext;
    }
}
