package io.agora.metalive.manager.editface.core;

import android.content.Context;
import android.os.HandlerThread;

import androidx.annotation.NonNull;

import io.agora.metalive.manager.editface.core.base.BaseCore;
import io.agora.metalive.manager.editface.core.base.FUItemHandler;

/**
 * 一个基于Faceunity Nama SDK的简单封装，方便简单集成，理论上简单需求的步骤：
 * <p>
 * 1.通过OnEffectSelectedListener在UI上进行交互
 * 2.合理调用FURenderer构造函数
 * 3.对应的时机调用onSurfaceCreated和onSurfaceDestroyed
 * 4.处理图像时调用onDrawFrame
 * <p>
 * 如果您有更高级的定制需求，Nama API文档请参考http://www.faceunity.com/technical/android-api.html
 */
public class FUPTARenderer {
    private static final String TAG = FUPTARenderer.class.getSimpleName();

    private Context mContext;

    //用于和异步加载道具的线程交互
    private HandlerThread mFUItemHandlerThread;
    private FUItemHandler mFUItemHandler;

    private BaseCore mFUCore;

    /**
     * FURenderer构造函数
     */
    public FUPTARenderer(Context context) {
        mContext = context.getApplicationContext();

        mFUItemHandlerThread = new HandlerThread("FUItemHandlerThread");
        mFUItemHandlerThread.start();
        mFUItemHandler = new FUItemHandler(mFUItemHandlerThread.getLooper(), mContext);
    }

    public FUItemHandler getFUItemHandler() {
        return mFUItemHandler;
    }

    public void setFUCore(@NonNull BaseCore core) {
        this.mFUCore = core;
    }

    public void release() {
        if (mFUItemHandlerThread != null) {
            mFUItemHandlerThread.quitSafely();
            mFUItemHandlerThread = null;
            mFUItemHandler = null;
        }

    }

}
