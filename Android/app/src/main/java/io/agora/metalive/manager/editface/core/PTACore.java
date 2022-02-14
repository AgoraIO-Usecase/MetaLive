package io.agora.metalive.manager.editface.core;

import android.content.Context;

import com.faceunity.wrapper.faceunity;

import java.util.Arrays;

import io.agora.metalive.manager.RtcManager;
import io.agora.metalive.manager.editface.constant.FilePathFactory;
import io.agora.metalive.manager.editface.core.base.BaseCore;
import io.agora.rtc2.video.AvatarItemType;
import io.agora.rtc2.video.AvatarOptionValue;

/**
 * Created by tujh on 2018/12/17.
 */
public class PTACore extends BaseCore {
    private static final String TAG = PTACore.class.getSimpleName();

    private AvatarHandle avatarHandle;

    private boolean isNeedTrackFace = false;
    // 设置即将要播放的动画位置
    private int currentHomeAnimationPosition = -1;
    // 是否可以再次设置下一个播放动画
    private boolean canResetHomeAnimationPosition = true;

    public PTACore(PTACore core) {
        super(core.mContext, core.mFUP2ARenderer);
        avatarHandle = core.avatarHandle;
    }

    public PTACore(Context context, FUPTARenderer fuP2ARenderer) {
        super(context, fuP2ARenderer);

        Arrays.fill(avatarInfo.mRotation, 0.0f);
        Arrays.fill(avatarInfo.mExpression, 0.0f);
        Arrays.fill(avatarInfo.mPupilPos, 0.0f);
        Arrays.fill(avatarInfo.mRotationMode, 0.0f);
    }


    public AvatarHandle createAvatarHandle() {
        return avatarHandle = new AvatarHandle(this, mFUItemHandler, null);
    }

    public void setCurrentInstancceId(int id) {
        if (avatarHandle != null)
            avatarHandle.setCurrentInstancceId(id);
    }


    /**
     * 解绑之前的2D背景，并绑定上默认背景
     */
    public void unBindAndBindDefault() {
        unBind();
        bindDefault();
    }

    public void unBindDefault() {
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_BACKGROUND);
    }

    public void bindDefault() {
        RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_BACKGROUND, FilePathFactory.BUNDLE_default_bg);
    }

    @Override
    public void unBind() {
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_BODY);
        if (avatarHandle != null) {
            avatarHandle.unBindAll();
        }
    }

    @Override
    public void bind() {
        RtcManager.getInstance().setGeneratorOptions("target_position", new AvatarOptionValue(new double[]{0.0, 0.0f, 0.0f}));
        RtcManager.getInstance().setGeneratorOptions("target_angle", new AvatarOptionValue(0));
        RtcManager.getInstance().setGeneratorOptions("reset_all", new AvatarOptionValue(3));
        RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_BODY, FilePathFactory.CAMERA_WHOLE_BODY);
        if (avatarHandle != null) {
            avatarHandle.bindAll();
        }
    }

    @Override
    public void release() {
        canResetHomeAnimationPosition = true;
        currentHomeAnimationPosition = -1;
        avatarHandle.release();
    }

    public void setNeedTrackFace(boolean needTrackFace) {
        isNeedTrackFace = needTrackFace;
        avatarHandle.setCNNTrackFace(isNeedTrackFace);
    }


    public interface AniLoadCompletedListener {
        /**
         * 当前的动画已经播放了一遍，也可能是多遍，主要根据loadCount来判断
         *
         * @param loadCount             当前动画播放了多少遍
         * @param nextAnimationPosition 需要播放的下一个动画所在FilePathFactory.getHomeSwitchAnimation()集合中的位置
         * @param haveNextAni           是否有下一个动画，如果没有下一个动画，nextAnimationPosition的值为当前动画
         *                              所在FilePathFactory.getHomeSwitchAnimation()集合中的位置
         */
        void loadCompleted(int loadCount, int nextAnimationPosition, boolean haveNextAni);
    }

    private AniLoadCompletedListener aniLoadCompletedListener;

    public void setAniLoadCompletedListener(AniLoadCompletedListener aniLoadCompletedListener) {
        this.aniLoadCompletedListener = aniLoadCompletedListener;
    }

    public interface AniRefreshNowListener {
        /**
         * 动画需要立即播放
         *
         * @param currentHomeAnimationPosition 动画所在FilePathFactory.getHomeSwitchAnimation()集合中的位置
         */
        void refreshNow(int currentHomeAnimationPosition);
    }

    private AniRefreshNowListener aniRefreshNowListener;

    public void setAniRefreshNowListener(AniRefreshNowListener aniRefreshNowListener) {
        this.aniRefreshNowListener = aniRefreshNowListener;
    }

    public void setNextHomeAnimationPosition() {
        int size = FilePathFactory.getHomeSwitchAnimation().size();
        if (size == 0) {
            return;
        }
        if (canResetHomeAnimationPosition) {
            currentHomeAnimationPosition = ++currentHomeAnimationPosition % size;
            if (size == 1) {
                // 如果只有一个动画，则需要改动avatarHandle中的loadCount，否则会导致动画的异常结束
                // 因为我们是通过loadCount - progress 去判断动画是否执行完毕，如果重复的执行同一个动画，
                // progress是不会重置为0 的，所以我们需要增加loadCount
                avatarHandle.setCurrentAniLoadCount(avatarHandle.getLoadCount() + 1);
            }
            canResetHomeAnimationPosition = false;
        }
        if (avatarHandle.getLoadCount() == Integer.MAX_VALUE) {
            // 当前为idle动画，可以立即刷新动画
            if (aniRefreshNowListener != null) {
                aniRefreshNowListener.refreshNow(currentHomeAnimationPosition);
                canResetHomeAnimationPosition = true;
            }
        }
    }

    @Override
    public void loadWholeBodyCamera() {
        avatarHandle.resetAllFront();
        super.loadWholeBodyCamera();
    }

    @Override
    public void loadSmallWholeBodyCamera() {
        avatarHandle.resetAllFront();
        super.loadSmallWholeBodyCamera();
    }

    @Override
    public void loadHalfLengthBodyCamera() {
        avatarHandle.resetAllFront();
        super.loadHalfLengthBodyCamera();
    }

    @Override
    public void loadBigHalfLengthBodyCamera() {
        avatarHandle.resetAllFront();
        super.loadBigHalfLengthBodyCamera();
    }

    @Override
    public float[] getLandmarksData() {
        Arrays.fill(landmarksData, 0.0f);
        if (isNeedTrackFace )
            faceunity.fuGetFaceInfo(0, "landmarks", landmarksData);
        return landmarksData;
    }
}
