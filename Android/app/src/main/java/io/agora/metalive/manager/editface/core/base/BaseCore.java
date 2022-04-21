package io.agora.metalive.manager.editface.core.base;

import android.content.Context;

import java.util.Arrays;

import io.agora.metalive.manager.RtcManager;
import io.agora.metalive.manager.editface.constant.FilePathFactory;
import io.agora.metalive.manager.editface.core.FUPTARenderer;
import io.agora.metalive.manager.editface.entity.AvatarInfo;
import io.agora.rtc2.Constants;
import io.agora.rtc2.video.AvatarItemType;
import io.agora.rtc2.video.AvatarOptionValue;

/**
 * 场景
 * Created by tujh on 2018/12/18.
 */
public abstract class BaseCore {
    private static final String TAG = BaseCore.class.getSimpleName();

    protected Context mContext;
    protected FUPTARenderer mFUP2ARenderer;
    protected FUItemHandler mFUItemHandler;


    protected float[] landmarksData = new float[150];
    protected AvatarInfo avatarInfo = new AvatarInfo();
    protected float[] faceRectData = new float[4];

    public BaseCore(Context context, FUPTARenderer fuP2ARenderer) {
        this.mContext = context.getApplicationContext();
        this.mFUP2ARenderer = fuP2ARenderer;
        this.mFUItemHandler = fuP2ARenderer.getFUItemHandler();

        avatarInfo.mExpression = new float[57];
        avatarInfo.mRotation = new float[4];
        avatarInfo.mPupilPos = new float[2];
        avatarInfo.mRotationMode = new float[1];
    }


    public abstract void unBind();

    public abstract void bind();

    public abstract void release();


    //******************nama SDK中的人脸信息相关参数*****************************//

    /**
     * landmarks 2D人脸特征点，返回值为75个二维坐标，长度75*2
     */
    public float[] getLandmarksData() {
        Arrays.fill(landmarksData, 0.0f);
        RtcManager.getInstance().GetGeneratorOptions("landmarks", Constants.AvatarValueType.FloatArray, new AvatarOptionValue(landmarksData));
        return landmarksData;
    }

    /**
     * rotation 人脸三维旋转，返回值为旋转四元数，长度4
     */
    public float[] getRotationData() {
        Arrays.fill(avatarInfo.mRotation, 0.0f);
        RtcManager.getInstance().GetGeneratorOptions("rotation", Constants.AvatarValueType.FloatArray, new AvatarOptionValue(avatarInfo.mRotation));
        return avatarInfo.mRotation;
    }

    public float[] getFaceRectData() {
        Arrays.fill(faceRectData, 0.0f);
        RtcManager.getInstance().GetGeneratorOptions("face_rect", Constants.AvatarValueType.FloatArray, new AvatarOptionValue(faceRectData));
        return faceRectData;
    }

    /**
     * expression  表情系数，长度57
     */
    public float[] getExpressionData() {
        Arrays.fill(avatarInfo.mExpression, 0.0f);
        RtcManager.getInstance().GetGeneratorOptions("expression", Constants.AvatarValueType.FloatArray, new AvatarOptionValue(avatarInfo.mExpression));
        return avatarInfo.mExpression;
    }


    /**
     * 相机bundle - 全身
     */
    public void loadWholeBodyCamera() {
        RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_BODY, FilePathFactory.CAMERA_WHOLE_BODY);
    }

    /**
     * 相机bundle - 全身-更小
     */
    public void loadSmallWholeBodyCamera() {
        RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_BODY, FilePathFactory.CAMERA_SMALL_WHOLE_BODY);
    }

    /**
     * 相机bundle - 半身
     */
    public void loadHalfLengthBodyCamera() {
        RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_BODY, FilePathFactory.CAMERA_HALF_LENGTH_BODY);
    }

    /**
     * 相机bundle - 半身-更大
     */
    public void loadBigHalfLengthBodyCamera() {
        RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_BODY, FilePathFactory.CAMERA_BIG_HALF_LENGTH_BODY);
    }

}
