package com.partime.user.pushnotificaton;

import android.app.Activity;
import android.app.Application;

/**
 * Created by techugo on 30/03/19.
 */

public class BApp extends Application {
    static BApp instance;

    public BApp() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = new BApp();
    }

    public static BApp getInstance() {
        if (instance != null)
            return instance;
        else
            return instance = new BApp();
    }

    private Activity mCurrentActivity = null;
    private boolean activityVisible;

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    public boolean isVisible() {
        return activityVisible;
    }

    public void setVisible(Boolean activityVisible) {
        this.activityVisible = activityVisible;
    }
}
