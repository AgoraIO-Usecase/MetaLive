package io.agora.metalive.manager;

import android.content.Context;
import android.opengl.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.core.view.GestureDetectorCompat;

import java.util.List;

import io.agora.metalive.manager.editface.constant.ColorConstant;
import io.agora.metalive.manager.editface.constant.FilePathFactory;
import io.agora.metalive.manager.editface.core.AvatarHandle;
import io.agora.metalive.manager.editface.core.FUPTARenderer;
import io.agora.metalive.manager.editface.core.PTACore;
import io.agora.metalive.manager.editface.core.client.PTAClientWrapper;
import io.agora.metalive.manager.editface.core.driver.ar.AvatarARDriveHandle;
import io.agora.metalive.manager.editface.core.driver.ar.PTAARDriveCore;
import io.agora.metalive.manager.editface.entity.AvatarPTA;
import io.agora.metalive.manager.editface.entity.DBHelper;
import io.agora.metalive.manager.editface.shape.EditParamFactory;


public class EditFaceManager {
    private static final String TAG = "FUDemoManager";
    private static final float[] IdentityMatrix = new float[16];
    static {
        Matrix.setIdentityM(IdentityMatrix, 0);
    }

    private static volatile EditFaceManager INSTANCE;
    private PTAARDriveCore mARDriveCore;
    private AvatarARDriveHandle mARAvatarHandle;

    public static EditFaceManager getInstance(){
        if(INSTANCE == null){
            synchronized (EditFaceManager.class){
                if(INSTANCE == null){
                    INSTANCE = new EditFaceManager();
                }
            }
        }
        return INSTANCE;
    }
    private EditFaceManager(){}

    private volatile boolean isInitialized = false;
    private volatile boolean isStarted = false;
    private Context mContext;
    private FUPTARenderer mFUP2ARenderer;
    private PTACore mP2ACore;
    private AvatarHandle mAvatarHandle;

    private DBHelper mDBHelper;
    private List<AvatarPTA> mAvatarP2As;
    private int mShowIndex;
    private AvatarPTA mShowAvatarP2A;

    private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private int touchMode;

    private boolean isARMode = false;
    private Runnable disableBackgroundColorRun = new Runnable() {
        @Override
        public void run() {
            mAvatarHandle.disableBackgroundColor();
        }
    };


    public void initialize(Context context){
        if(isInitialized){
            return;
        }
        mContext = context.getApplicationContext();

        //初始化 core data 数据---捏脸
        PTAClientWrapper.setupData(mContext);
        PTAClientWrapper.setupStyleData(mContext);

        //风格选择后初始化 P2A client
        ColorConstant.init(mContext);
        EditParamFactory.init(mContext);

        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final int screenWidth = metrics.widthPixels;
        final int screenHeight = metrics.heightPixels;
        mGestureDetector = new GestureDetectorCompat(mContext, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if(mP2ACore != null){
                    mP2ACore.setNextHomeAnimationPosition();
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (touchMode != 1) {
                    touchMode = 1;
                    return false;
                }
                float rotDelta = -distanceX / screenWidth;
                if(mAvatarHandle != null){
                    mAvatarHandle.setRotDelta(rotDelta);
                }
                return distanceX != 0;
            }
        });
        mScaleGestureDetector = new ScaleGestureDetector(mContext, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scale = detector.getScaleFactor() - 1;
                if(mAvatarHandle != null){
                    mAvatarHandle.setScaleDelta(scale);
                }
                return scale != 0;
            }
        });

        mDBHelper = DBHelper.create(mContext);
        mAvatarP2As = mDBHelper.getAllAvatarP2As();
        Log.d(TAG, "mAvatarP2As=" + mAvatarP2As.toString());
        mShowIndex = mAvatarP2As.size() - 1;
        mShowAvatarP2A = mAvatarP2As.get(mShowIndex);

        isInitialized = true;
    }

    public void start() {
        if (isStarted) {
            return;
        }
        mFUP2ARenderer = new FUPTARenderer(mContext);
        mP2ACore = new PTACore(mContext, mFUP2ARenderer);
        mFUP2ARenderer.setFUCore(mP2ACore);
        mAvatarHandle = mP2ACore.createAvatarHandle();

        isStarted = true;
        isARMode = false;
        resetMode();
    }

    public void switchMode(){
        if(isARMode){
            switch2AvatarMode();
        }else{
            switch2ARMode();
        }
    }

    public boolean isARMode() {
        return isARMode;
    }

    public void switch2AvatarMode(){
        if(!isStarted){
            return;
        }
        isARMode = false;
        mFUP2ARenderer.getFUItemHandler().removeCallbacks(disableBackgroundColorRun);
        mAvatarHandle.setBackgroundColor("#AE8EF0");
        if(mARAvatarHandle != null){
            mARDriveCore.release();
            mARDriveCore = null;
            mARAvatarHandle = null;
            mFUP2ARenderer.setFUCore(mP2ACore);
        }

        mP2ACore.loadWholeBodyCamera();
        mAvatarHandle.setFaceCapture(true);
        mAvatarHandle.setNeedTrackFace(true);
        mAvatarHandle.setAvatar(getShowAvatarP2A(), true, true);
        mAvatarHandle.openLight(FilePathFactory.BUNDLE_light);
        mAvatarHandle.setScale(new double[]{0.0, -50f, 300f});

    }

    private void resetMode() {
        if(isARMode){
            switch2ARMode();
        }
        else {
            switch2AvatarMode();
        }
    }

    public void switch2ARMode() {
        if(!isStarted){
            return;
        }
        isARMode = true;
        if(mARAvatarHandle == null){
            mP2ACore.unBind();
            mARDriveCore = new PTAARDriveCore(mContext, mFUP2ARenderer);
            mFUP2ARenderer.setFUCore(mARDriveCore);
            mARAvatarHandle = mARDriveCore.createAvatarARHandle();
        }
        mARAvatarHandle.setARAvatar(getShowAvatarP2A(), true);
        mFUP2ARenderer.getFUItemHandler().removeCallbacks(disableBackgroundColorRun);
        mFUP2ARenderer.getFUItemHandler().postDelayed(disableBackgroundColorRun, 500);
    }

    public void stop(){
        isStarted = false;
        if(mARAvatarHandle != null){
            mARDriveCore.release();
            mARDriveCore = null;
            mARAvatarHandle = null;
        }
        if(mP2ACore != null){
            mP2ACore.unBind();
            mP2ACore.release();
            mAvatarHandle.closeLight();
            mAvatarHandle = null;
            mP2ACore = null;
        }

        if(mFUP2ARenderer != null){
            mFUP2ARenderer.release();
            mFUP2ARenderer = null;
        }
    }

    public AvatarPTA getShowAvatarP2A() {
        return mShowAvatarP2A;
    }

    public List<AvatarPTA> getAvatarP2As() {
        return mAvatarP2As;
    }

    public FUPTARenderer getFUP2ARenderer() {
        return mFUP2ARenderer;
    }

    public PTACore getP2ACore() {
        return mP2ACore;
    }

    public AvatarHandle getAvatarHandle() {
        return mAvatarHandle;
    }

    public Context getContext() {
        return mContext;
    }

    public void handleTouchEvent(MotionEvent event){
        if(!isStarted || !isInitialized){
            return;
        }
        if (event.getPointerCount() == 2) {
            mScaleGestureDetector.onTouchEvent(event);
        } else if (event.getPointerCount() == 1) {
            mGestureDetector.onTouchEvent(event);
        }
    }

}
