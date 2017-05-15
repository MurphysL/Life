package com.murphysl.life;

import android.app.Application;

import com.orhanobut.logger.Logger;

/**
 * App
 *
 * @author: MurphySL
 * @time: 2017/5/15 17:14
 */


public class App extends Application {
    private static final String TAG = "MurphySL";

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init(TAG).methodCount(3);
    }
}
