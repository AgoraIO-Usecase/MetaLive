package io.agora.meta.wrapper;

import android.content.Context;
import android.util.Log;

import com.benben.faceapi.BBFaceHelper;
import com.benben.faceapi.MessinfoListener;

import org.json.JSONObject;

import java.util.HashMap;

import io.agora.base.NV21Buffer;
import io.agora.base.VideoFrame;
import io.agora.meta.base.FaceDetectInfo;

public class BBFaceDetection implements IFaceDetectBase {
    private static final String TAG = BBFaceDetection.class.getSimpleName();

    private int mFrameWidth;
    private int mFrameHeight;
    private boolean mInited = false;

    @Override
    public int create(Context context) {
        if (!mInited) {
            BBFaceHelper.getInstance().initBBFaceEngine(context);
            BBFaceHelper.getInstance().setSdkForAppIdAndKey(AvatarConstant.APPID, AvatarConstant.SDKKEY);
            BBFaceHelper.getInstance().setMessinfoListener(new MessinfoListener() {
                @Override
                public void reBackMess(int code, String mess) {
                    if (code == 102) {
                        BBFaceHelper.getInstance().starFaceCapture();
                        mInited = true;
                    }
                    Log.d(TAG, "BBFaceHelper callback, code: " + code + ", msg: " + mess);
                }
            });
            BBFaceHelper.getInstance().launchSDKInfo();
        }
        return 0;
    }

    @Override
    public int release() {
        BBFaceHelper.getInstance().freeFaceCapture();
        return 0;
    }

    @Override
    public int detectFrame(VideoFrame frame, FaceDetectInfo detectInfo) {
        if (!mInited || detectInfo == null)
            return -1;

        int ret = 0;
        if (mFrameWidth != frame.getRotatedWidth() || mFrameHeight != frame.getRotatedHeight()) {
            mFrameWidth = frame.getRotatedWidth();
            mFrameHeight = frame.getRotatedHeight();
            BBFaceHelper.getInstance().setCameraResolution(mFrameWidth, mFrameHeight);
        }
        if (mFrameWidth > 0 && mFrameHeight > 0) {
            long t0 = System.currentTimeMillis();
            VideoFrame.I420Buffer i420Buffer;
            VideoFrame.Buffer tempBuffer = frame.getBuffer();
            if (frame.getBuffer() instanceof  VideoFrame.TextureBuffer) {
                tempBuffer = ((VideoFrame.TextureBuffer)frame.getBuffer()).rotate(frame.getRotation());
            }
            if (!(frame.getBuffer() instanceof VideoFrame.I420Buffer)) {
                i420Buffer = ((VideoFrame.TextureBuffer)tempBuffer).toI420();
                tempBuffer.release();
            } else {
                i420Buffer = (VideoFrame.I420Buffer) tempBuffer;
            }

            NV21Buffer nv21Buffer =  NV21Buffer.createNV21BufferWithLibYUV(i420Buffer);
            i420Buffer.release();

            long t1 = System.currentTimeMillis();
//            Log.d(TAG, "frame convert time: " + (t1 - t0));
            String bbData = BBFaceHelper.getInstance().setImageData(nv21Buffer.getData(), nv21Buffer.getWidth(), nv21Buffer.getHeight(), false, 0);
            long t2 = System.currentTimeMillis();
//            Log.d(TAG, "face detect time: " + (System.currentTimeMillis() - t1));
            Log.d(TAG, "face result: " + bbData);
            ret = parseFaceInfo(bbData, detectInfo);
            long t3 = System.currentTimeMillis();
//            Log.d(TAG, "parse time: " + (t3 - t2));
        }

        return ret;
    }

    @Override
    public void enableDetection(boolean enable) {
        if (!mInited)
            return;
        if (!enable)
            BBFaceHelper.getInstance().pauseFaceCapture();
        else
            BBFaceHelper.getInstance().starFaceCapture();
    }

    public static final HashMap<Integer, String> benbenBSMap = new HashMap<Integer, String>(){{
        //eye left
        put(BlendShapeEnum.eyeBlinkLeft.ordinal(), "eyeBlink_R");
        put(BlendShapeEnum.eyeLookDownLeft.ordinal(), "eyeLookDown_R");
        put(BlendShapeEnum.eyeLookInLeft.ordinal(), "eyeLookIn_R");
        put(BlendShapeEnum.eyeLookOutLeft.ordinal(), "eyeLookOut_R");
        put(BlendShapeEnum.eyeLookUpLeft.ordinal(), "eyeLookUp_R");
        put(BlendShapeEnum.eyeSquintLeft.ordinal(), "eyeSquint_R");
        put(BlendShapeEnum.eyeWideLeft.ordinal(), "eyeWide_R");
        //eye right
        put(BlendShapeEnum.eyeBlinkRight.ordinal(), "eyeBlink_L");
        put(BlendShapeEnum.eyeLookDownRight.ordinal(), "eyeLookDown_L");
        put(BlendShapeEnum.eyeLookInRight.ordinal(), "eyeLookIn_L");
        put(BlendShapeEnum.eyeLookOutRight.ordinal(), "eyeLookOut_L");
        put(BlendShapeEnum.eyeLookUpRight.ordinal(), "eyeLookUp_L");
        put(BlendShapeEnum.eyeSquintRight.ordinal(),"eyeSquint_L");
        put(BlendShapeEnum.eyeWideRight.ordinal(),"eyeWide_L");
        //jaw
        put(BlendShapeEnum.jawForward.ordinal(), "jawForward");
        put(BlendShapeEnum.jawLeft.ordinal(), "jawRight");
        put(BlendShapeEnum.jawRight.ordinal(), "jawLeft");
        put(BlendShapeEnum.jawOpen.ordinal(), "jawOpen");
        //mouth todo
        put(BlendShapeEnum.mouthClose.ordinal(), "mouthClose");
        put(BlendShapeEnum.mouthFunnel.ordinal(), "mouthFunnel");
        put(BlendShapeEnum.mouthPucker.ordinal(), "mouthPucker");
        put(BlendShapeEnum.mouthLeft.ordinal(), "mouthRight");
        put(BlendShapeEnum.mouthRight.ordinal(), "mouthLeft");
        put(BlendShapeEnum.mouthSmileLeft.ordinal(), "mouthSmile_R");
        put(BlendShapeEnum.mouthSmileRight.ordinal(), "mouthSmile_L");
        put(BlendShapeEnum.mouthFrownLeft.ordinal(), "mouthFrown_R");
        put(BlendShapeEnum.mouthFrownRight.ordinal(), "mouthFrown_L");
        put(BlendShapeEnum.mouthDimpleLeft.ordinal(), "mouthDimple_R");
        put(BlendShapeEnum.mouthDimpleRight.ordinal(), "mouthDimple_L");
        put(BlendShapeEnum.mouthStretchLeft.ordinal(), "mouthStretch_R");
        put(BlendShapeEnum.mouthStretchRight.ordinal(), "mouthStretch_L");
        put(BlendShapeEnum.mouthRollLower.ordinal(), "mouthRollLower");
        put(BlendShapeEnum.mouthRollUpper.ordinal(), "mouthRollUpper");
        put(BlendShapeEnum.mouthShrugLower.ordinal(), "mouthShrugLower");
        put(BlendShapeEnum.mouthShrugUpper.ordinal(), "mouthShrugUpper");
        put(BlendShapeEnum.mouthPressLeft.ordinal(), "mouthPress_R");
        put(BlendShapeEnum.mouthPressRight.ordinal(), "mouthPress_L");
        put(BlendShapeEnum.mouthLowerDownLeft.ordinal(), "mouthLowerDown_R");
        put(BlendShapeEnum.mouthLowerDownRight.ordinal(), "mouthLowerDown_L");
        put(BlendShapeEnum.mouthUpperUpLeft.ordinal(), "mouthUpperUp_R");
        put(BlendShapeEnum.mouthUpperUpRight.ordinal(), "mouthUpperUp_L");
        //brow
        put(BlendShapeEnum.browDownLeft.ordinal(), "browDown_R");
        put(BlendShapeEnum.browDownRight.ordinal(), "browDown_L");
        put(BlendShapeEnum.browInnerUp.ordinal(), "browInnerUp");
        put(BlendShapeEnum.browOuterUpLeft.ordinal(), "browOuterUp_R");
        put(BlendShapeEnum.browOuterUpRight.ordinal(), "browOuterUp_L");
        //cheek
        put(BlendShapeEnum.cheekPuff.ordinal(), "cheekPuff");
        put(BlendShapeEnum.cheekSquintLeft.ordinal(), "cheekSquint_R");
        put(BlendShapeEnum.cheekSquintRight.ordinal(), "cheekSquint_L");
        //nose
        put(BlendShapeEnum.noseSneerLeft.ordinal(), "noseSneer_R");
        put(BlendShapeEnum.noseSneerRight.ordinal(), "noseSneer_L");
        //tongue
        put(BlendShapeEnum.tongueOut.ordinal(), "tongueOut");
    }};

    private int parseFaceInfo(String data, FaceDetectInfo faceDetectInfo) {
        if (faceDetectInfo == null) {
            return -1;
        }

        if (BlendShapeEnum.COUNT.ordinal() != faceDetectInfo.expression.length) {
            Log.e(TAG, "blend shape enum illegal or expression length wrong");
            return -1;
        }
        try {
            JSONObject jsonObject = new JSONObject(data);
            Log.d(TAG,  "" + jsonObject.getJSONArray("FaceDetected").getDouble(1));
            for (int i = 0; i < BlendShapeEnum.COUNT.ordinal(); i++) {
                if (benbenBSMap.get(i) != "invalidIdx") {
                    float value = (float) jsonObject.getJSONArray(benbenBSMap.get(i)).getDouble(1) / 100;
                    faceDetectInfo.expression[i] = clips3(value, 0.0f, 1.0f);
                }
            }

            faceDetectInfo.rotation[0] = (float) jsonObject.getJSONArray("HeadPose").getDouble(3); //pitch
            faceDetectInfo.rotation[1] = (float) jsonObject.getJSONArray("HeadPose").getDouble(4); //roll
            faceDetectInfo.rotation[2] = (float) jsonObject.getJSONArray("HeadPose").getDouble(5);  //yaw
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    float clips3(float iX, float iY, float iZ) {
        //CLIP3(iX, iY, iZ) ((iX) <= (iY) ? (iY) : ((iX) > (iZ) ? (iZ) : (iX)))
        float value = ((iX) <= (iY) ? (iY) : ((iX) > (iZ) ? (iZ) : (iX)));
        return value;
    }
}
