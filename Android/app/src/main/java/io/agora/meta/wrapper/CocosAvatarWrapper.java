package io.agora.meta.wrapper;

import static io.agora.base.internal.ContextUtils.getApplicationContext;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.cocos.arts.sdk.AvatarInstance;
import com.cocos.arts.sdk.AvatarSDK;
import com.cocos.arts.sdk.BlendShapeLocation;
import com.cocos.arts.sdk.DressingSession;
import com.cocos.arts.sdk.FaceAnchor;
import com.cocos.arts.sdk.FaceEditorSession;
import com.cocos.arts.sdk.FaceTrackingSession;
import com.cocos.arts.sdk.SessionType;
import com.cocos.arts.sdk.math.Vec3;
import com.cocos.game.CocosGameScreenSession;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.Callable;

import io.agora.base.VideoFrame;
import io.agora.base.internal.CalledByNative;
import io.agora.base.internal.video.EglBase;
import io.agora.base.internal.video.EglBase14;
import io.agora.meta.base.AvatarStatus;
import io.agora.meta.base.FaceDetectInfo;
import io.agora.meta.base.TextureBufferHelper;

public class CocosAvatarWrapper implements IAvatarHandler {
    private final static String TAG = CocosAvatarWrapper.class.getSimpleName();
    private Context mContext;
    private io.agora.meta.base.TextureBufferHelper textureBufferHelper;
    private final Matrix localRenderMatrix = new Matrix();
    private boolean glPrepared = false;
    private boolean mCocosEngineInited = false;
    private Map<Integer, String> mBundleMap = new HashMap<Integer, String>();
    FaceTrackingSession faceTrackingSession;
    FaceEditorSession faceEditorSession;
    DressingSession dressingSession;
    AvatarSDK.ScreenCaptureHandle screenCaptureHandle = null;
    HashMap<String, Double> faceEditorProperties = new HashMap<>();
    HashMap<String, Vector<JSONObject>> dresses = new HashMap<>();
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private Queue<AvatarSDK.FrameCaptured> frameQueue = null;
    private int mInstanceId = 0;
    private Activity mActivity;
    Float[] mBlendShapes = new Float[BlendShapeLocation.COUNT.ordinal()];
    EglBase14.Context mEglContext;
    AvatarInstance _avatarInstance = null;
    private int mTexWidth = 720;
    private int mTexHeight = 1280;

    @CalledByNative
    public static int SetLicense(byte[] license) {
        return AvatarStatus.OK.getNumber();
    }

    void setCapturedFrame(AvatarSDK.FrameCaptured frameCaptureInfo) {
        //Log.d(TAG, "add frame");
        if(frameQueue != null){
            frameQueue.offer(frameCaptureInfo);
        }
    }

    void startCaptureSession(AvatarInstance instance) {
        frameQueue = new LinkedList<AvatarSDK.FrameCaptured>();
        //gurarantee the FrameCaptureListener is executed at main thread
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                Bundle options = new Bundle();
                options.putInt(AvatarSDK.KEY_SCREEN_CAPTURE_OPTIONS_MAX_FRAMES, 0);

                screenCaptureHandle = instance.startScreenCapture(0,
                        options, new AvatarSDK.FrameCaptureListener() {
                            @Override
                            public void onFrameCaptureStart() {
                                Log.d(TAG, "capture onFrameCaptureStart");
                            }

                            @Override
                            public void onFrameCaptured(AvatarSDK.FrameCaptured frameCaptured) {
                                setCapturedFrame(frameCaptured);
                            }

                            @Override
                            public void onFrameCaptureComplete(int i) {
                                Log.d(TAG, "onFrameCaptureComplete errorCode: " + i);
                            }
                        });
            }
        });
    }

    void stopCaptureSession(int instanceId) {
        if (screenCaptureHandle != null) {
            screenCaptureHandle.stopScreenCapture();
            screenCaptureHandle = null;
        }
        frameQueue = null;
    }

    int[] getCaptureFrame() {
        int[] textures = null;
        if (frameQueue.size() > 1) {
            AvatarSDK.FrameCaptured cocosCapturedFrame = frameQueue.poll();
            /*if (cocosCapturedFrame != null) {
                Log.d(TAG, "unlock");
                cocosCapturedFrame.release();
            } else {
                Log.e(TAG, "unlock failed");
            }*/
            if (cocosCapturedFrame != null) {
                int size = cocosCapturedFrame.getImages().length;
                textures = new int[size];
                for (int i = 0; i < size; i++) {
                    if (size > 1)
                        Log.d(TAG, "size is so large: " + size);
                    AvatarSDK.FrameCaptured.CaptureImage frame = cocosCapturedFrame.getImages()[i];
                    Object data = frame.getData();
                    if (CocosGameScreenSession.FRAME_DATA_TEXTURE == frame.getFrameType()) {
                        int texture = ((Integer) data).intValue();
                        textures[i] = texture;
                    }
                }
            } else {
                ;//return null;
            }
        }
        return textures;
    }

    void unlockCaptureFrame() {
        if (frameQueue.size() > 1) {
            AvatarSDK.FrameCaptured cocosCapturedFrame = frameQueue.poll();
            if (cocosCapturedFrame != null) {
                cocosCapturedFrame.release();
            } else {
                Log.e(TAG, "unlock failed");
            }
        }
    }

    public int createInstance(Activity activity) {
        return createCocosInstance(activity, mEglContext);
        //return 0;
    }

    int createCocosInstance(Activity activity, EglBase14.Context eglContext) {
        Bundle options = new Bundle();
        options.putLong(AvatarSDK.KEY_SCREEN_CAPTURE_OPTIONS_GL_CONTEXT, eglContext.getNativeEglContext());
        AvatarSDK.RenderMode mode = AvatarSDK.RenderMode.OFF_SCREEN;
        options.putInt(AvatarSDK.KEY_INSTANCE_RENDER_MODE, mode.ordinal());
        options.putInt(AvatarSDK.KEY_INSTANCE_WIDTH, mTexWidth);
        options.putInt(AvatarSDK.KEY_INSTANCE_HEIGHT, mTexHeight);
        _avatarInstance = AvatarSDK.getInstance().create(activity, options, new AvatarInstance.Listener() {
            @Override
            public void onCreate(AvatarInstance instance) {
                startCaptureSession(_avatarInstance);
            }

            @Override
            public void onStart() {
                _avatarInstance.setAvatar("cjie", new AvatarInstance.FunctionListener() {
                    @Override
                    public void onSuccess(String s, Bundle bundle) {
                        //选角成功，开始后续操作
                        //start facetracking
                        startFaceTracking();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
                //set frame rate for gaming
                setGameFrameRate(_avatarInstance, 30);
            }

            @Override
            public void onLoading(String loadStage, float progress, String tips) {
                //加载回调
            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onError(int i) {
                Log.d(TAG, "AvatarSDK.getInstance().create error=" + i);
            }
        });
        return mInstanceId;
    }

    int startFaceTracking() {
        if (!mCocosEngineInited) {
            return AvatarStatus.NOT_INITED.getNumber();
        }
        faceTrackingSession = (FaceTrackingSession) _avatarInstance.createSession(SessionType.FACE_TRACKING, 0, null);
        faceTrackingSession.start();
        return AvatarStatus.OK.getNumber();
    }

    void stopFaceTracking(int instanceId) {
        if (faceTrackingSession != null) {
            faceTrackingSession.close();
            faceTrackingSession = null;
        }
    }

    Activity getActivityFromContext(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof Application || context instanceof Service) {
            return null;
        }
        Context c = context;
        while (c != null) {
            if (c instanceof ContextWrapper) {
                c = ((ContextWrapper) c).getBaseContext();
                if (c instanceof Activity) {
                    return (Activity) c;
                }
            } else {
                return null;
            }
        }
        return null;
    }

    public int initCocosSDK(String usrID, Context context, EglBase14.Context eglContext) {
        /**/
        AvatarSDK.getInstance().init(usrID, context, new AvatarSDK.Listener() {
            @Override
            public void onInit() {
                mCocosEngineInited = true;
                Log.d(TAG, "sdk listener onInit");
            }

            @Override
            public void onError(int errorCode) {
                Log.d(TAG, "onError, code: " + errorCode);
            }
        });
        /**/
        return AvatarStatus.OK.getNumber();
    }

    @Override
    public int init(String usrId, EglBase14.Context eglContext) {
        if (mCocosEngineInited) {
            return AvatarStatus.OK.getNumber();
        }
        int ret = AvatarStatus.OK.getNumber();
        mEglContext = eglContext;
        mContext = getApplicationContext();
        initGlHelper(mContext, eglContext);
        ret = initCocosSDK(usrId, mContext, eglContext);
        return ret;
    }

    @Override
    public void release() {
        if (!mCocosEngineInited) {
            //return AvatarStatus.OK.getNumber();
        }
        if (textureBufferHelper != null) {
            textureBufferHelper.invoke(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    Log.d(TAG, "release all");
                    return null;
                }
            });
            textureBufferHelper.dispose();
            textureBufferHelper = null;
        }
        stopCaptureSession(mInstanceId);
        stopFaceTracking(mInstanceId);
        stopDressing(mInstanceId);
        glPrepared = false;
        //return AvatarStatus.OK.getNumber();
    }

    @Override
    public int detectFrame(final VideoFrame frame, FaceDetectInfo detectInfo) {
        return AvatarStatus.OK.getNumber();
    }

    @Override
    public VideoFrame renderFrame(final VideoFrame inputFrame, final FaceDetectInfo detectInfo) {
        if (!mCocosEngineInited || !glPrepared) {
            return inputFrame;
        }

        if (faceTrackingSession != null) {
            Arrays.fill(mBlendShapes, 0.0f);
            for (int i = 0; i < BlendShapeLocation.COUNT.ordinal(); i++)
                mBlendShapes[i] = detectInfo.expression[i];
            FaceAnchor anchor = new FaceAnchor(mBlendShapes, new Vec3(0, 0, 0),
                    new Vec3(detectInfo.rotation[0], detectInfo.rotation[1], detectInfo.rotation[2]));
            faceTrackingSession.send(anchor);
        }

        if (screenCaptureHandle == null)
            return inputFrame;
        return textureBufferHelper.invoke(new Callable<VideoFrame>() {
            @Override
            public VideoFrame call() throws Exception {
                int[] textures = null;
                AvatarSDK.FrameCaptured cocosCapturedFrame = frameQueue.peek();
                if (cocosCapturedFrame != null) {
                    int size = cocosCapturedFrame.getImages().length;
                    textures = new int[size];
                    for (int i = 0; i < size; i++) {
                        if (size > 1)
                            Log.d(TAG, "size is so large: " + size);
                        AvatarSDK.FrameCaptured.CaptureImage frame = cocosCapturedFrame.getImages()[i];
                        Object data = frame.getData();
                        if (CocosGameScreenSession.FRAME_DATA_TEXTURE == frame.getFrameType()) {
                            int texture = ((Integer) data).intValue();
                            textures[i] = texture;
                        }
                    }
                    long timestamp = (inputFrame == null) ? 0 : cocosCapturedFrame.getFrameTimeStamp();
                    //timestamp = 0;
                    VideoFrame.TextureBuffer avatarBuffer = textureBufferHelper.wrapTextureBuffer(
                            cocosCapturedFrame.getFrameWidth(), cocosCapturedFrame.getFrameWidth(),
                            VideoFrame.TextureBuffer.Type.RGB, textures[0], new Matrix());
                    VideoFrame videoFrame = new VideoFrame(avatarBuffer, 0, timestamp);
                    if (frameQueue.size() > 1) {
                        AvatarSDK.FrameCaptured frame = frameQueue.poll();
                        if (frame != null)
                            frame.release();
                        else
                            Log.d(TAG, " unlock fail");
                    }
                    return videoFrame;
                } else {
                    return inputFrame;
                }
            }
        });
    }

    @Override
    public int setPropertyObject(Object object) {
        if (object instanceof Activity) {
            createInstance((Activity) object);
        }
        return 0;
    }

    HashMap<String, Double> startFaceEditor(int instanceId) {
        //faceEditorSession = (FaceEditorSession)AvatarSDK.getInstance().createSession(SessionType.FACE_EDITOR, instanceId, 0, null);
        if (faceEditorSession != null) {
            faceEditorSession.start();
            faceEditorSession.request(new FaceEditorSession.ResultListener() {
                @Override
                public void onResult(HashMap<String, Double> hashMap) {
                    faceEditorProperties = (HashMap<String, Double>) hashMap.clone();
                }
            });
            /*
            faceEditorSession.request("100103", new FaceEditorSession.ResultListener() {
                @Override
                public void onResult(HashMap<String, Double> hashMap) {
                    // Iterating entries using a For Each loop
                    hashMap.forEach((k, v)->faceEditorProperties.put(k, v));
                }
            });
            */
        }
        return faceEditorProperties;
    }

    void stopFaceEditor(int instanceId) {
        if (faceEditorSession != null) {
            faceEditorSession.close();
            faceEditorSession = null;
        }
    }

    HashMap<String, Vector<JSONObject>> startDressing(int instanceId) {
        //dressingSession = (DressingSession) AvatarSDK.getInstance().createSession(SessionType.DRESSING, instanceId, 0, null);
        dressingSession.start();
        dressingSession.request(new DressingSession.ResultListener() {
            @Override
            public void onResult(HashMap<String, Vector<JSONObject>> hashMap) {
                dresses = hashMap;
            }
        });
        return dresses;
    }

    void stopDressing(int instanceId) {
        if (dressingSession != null) {
            dressingSession.close();
            dressingSession = null;
        }
    }

    private void initGlHelper(Context context, EglBase.Context eglContext) {
        try {
            if (!glPrepared) {
                glPrepared = prepareGlHelper(eglContext);
                if (glPrepared)
                    Log.d(TAG, "initCocosRenderer prepareGl done");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean prepareGlHelper(EglBase.Context eglContext) {
        textureBufferHelper = TextureBufferHelper.create(CocosAvatarWrapper.class.getSimpleName(), eglContext);
        if (textureBufferHelper == null) {
            return false;
        }
        textureBufferHelper.invoke(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return null;
            }
        });
        return true;
    }

    void setGameFrameRate(AvatarInstance instance, int fps) {
        instance.setFrameRate(fps);
    }
}