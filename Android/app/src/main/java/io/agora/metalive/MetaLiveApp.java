package io.agora.metalive;

import android.app.Application;
import android.text.TextUtils;

import com.tencent.bugly.crashreport.CrashReport;

import io.agora.metalive.manager.RtcManager;

public class MetaLiveApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String buglyAppId = getString(R.string.bugly_app_id);
        if(!TextUtils.isEmpty(buglyAppId)){
            CrashReport.initCrashReport(getApplicationContext(), buglyAppId, BuildConfig.DEBUG);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RtcManager.getInstance().destroy();
    }
}
