package io.agora.metalive;

import android.app.Application;

import io.agora.metalive.manager.RtcManager;

public class MetaLiveApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RtcManager.getInstance().destroy();
    }
}
