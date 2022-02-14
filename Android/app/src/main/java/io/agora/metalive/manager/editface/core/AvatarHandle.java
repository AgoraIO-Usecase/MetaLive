package io.agora.metalive.manager.editface.core;

import android.graphics.Color;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.agora.metalive.manager.RtcManager;
import io.agora.metalive.manager.editface.constant.FilePathFactory;
import io.agora.metalive.manager.editface.core.base.BaseCore;
import io.agora.metalive.manager.editface.core.base.BasePTAHandle;
import io.agora.metalive.manager.editface.core.base.FUItemHandler;
import io.agora.metalive.manager.editface.entity.AvatarPTA;
import io.agora.metalive.manager.editface.entity.BundleRes;
import io.agora.rtc2.Constants;
import io.agora.rtc2.video.AvatarItemType;
import io.agora.rtc2.video.AvatarOptionValue;

/**
 * Avatar Controller
 * Created by tujh on 2018/12/17.
 */
public class AvatarHandle extends BasePTAHandle {
    private static final String TAG = AvatarHandle.class.getSimpleName();

    private boolean mIsNeedTrack;
    private boolean mIsNeedFacePUP;
    private boolean isPose;//是否是静止动画
    private boolean mIsNeedIdle;// 是否需要idle动画
    // 当前是否为合影界面
    private boolean isGroupPhoto = false;

    public int eyebrowItemTypeId;
    public int eyelashItemTypeId;
    public int eyeshadowItemTypeId;
    public int lipglossItemTypeId;
    public int[] otherItem = new int[]{
            RtcManager.AVATAR_ITEM_TYPE_OTHER_01,
            RtcManager.AVATAR_ITEM_TYPE_OTHER_02,
            RtcManager.AVATAR_ITEM_TYPE_OTHER_03,
            RtcManager.AVATAR_ITEM_TYPE_OTHER_04,
            RtcManager.AVATAR_ITEM_TYPE_OTHER_05
    };

    public AvatarHandle(BaseCore baseCore, FUItemHandler FUItemHandler, final Runnable prepare) {
        super(baseCore, FUItemHandler);
        isPose = false;
        openHairFollowing(true);
        FUItemHandler.post(() -> {
            if (prepare != null)
                prepare.run();
        });
    }

    public AvatarHandle(BaseCore baseCore, FUItemHandler FUItemHandler) {
        super(baseCore, FUItemHandler);
        isPose = false;
    }

    public void setAvatar(AvatarPTA avatar) {
        setAvatar(avatar, false, false);
    }

    public void setAvatar(AvatarPTA avatar, boolean mustLoadHead) {
        setAvatar(avatar, mustLoadHead, false);
    }

    public void setAvatar(final AvatarPTA avatar, final boolean mustLoadHead, final boolean mistLoadHair) {
        mFUItemHandler.post(new Runnable() {
            @Override
            public void run() {
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_HEAD, avatar.getHeadFile(), mustLoadHead);
                // 当前的帽子都是帽子头发道具，所以就不需要原先的头发道具了
                if (TextUtils.isEmpty(avatar.getHatFile())) {
                    RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_HAIR, avatar.getHairFile(), mistLoadHair);
                    RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_HAT, avatar.getHatFile());
                } else {
                    RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_HAIR);
                    RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_HAT, avatar.getHatFile());
                }
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_GLASS, avatar.getGlassesFile());
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_BEARD, avatar.getBeardFile());

                eyebrowItemTypeId = AvatarItemType.AvatarItemType_BROW;
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_BROW, avatar.getEyebrowFile());
                eyelashItemTypeId = AvatarItemType.AvatarItemType_ELASH;
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_ELASH, avatar.getEyelashFile());

                RtcManager.getInstance().enableAvatarGeneratorItems(RtcManager.AVATAR_ITEM_TYPE_BODY, FilePathFactory.bodyBundle(avatar.getClothesGender(), avatar.getBodyLevel()));
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_CLOTHES_SUIT, avatar.getClothesFile());

                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_CLOTHES_UPPER, avatar.getClothesUpperFile());
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_CLOTHES_LOWER, avatar.getClothesLowerFile());
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_SHOES, avatar.getShoeFile());

                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_TOUSHI, avatar.getHeadDecorationsFile());
                RtcManager.getInstance().enableAvatarGeneratorItems(RtcManager.AVATAR_ITEM_TYPE_ER_SHI, avatar.getEarDecorationsFile());
                RtcManager.getInstance().enableAvatarGeneratorItems(RtcManager.AVATAR_ITEM_TYPE_JIAO_SHI, avatar.getFootDecorationsFile());
                RtcManager.getInstance().enableAvatarGeneratorItems(RtcManager.AVATAR_ITEM_TYPE_SHOU_SHI, avatar.getHandDecorationsFile());
                RtcManager.getInstance().enableAvatarGeneratorItems(RtcManager.AVATAR_ITEM_TYPE_BO_SHI, avatar.getNeckDecorationsFile());

                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_EYE_LINER, avatar.getEyelinerFile());
                eyeshadowItemTypeId = AvatarItemType.AvatarItemType_EYE_SHADOW;
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_EYE_SHADOW, avatar.getEyeshadowFile());
                lipglossItemTypeId = AvatarItemType.AvatarItemType_LIP_GLOSS;
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_LIP_GLOSS, avatar.getLipglossFile());
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_PUPIL, avatar.getPupilFile());

                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_MAKEUP, avatar.getFacemakeupFile());

                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_ANIMATION, loadExpressionBundle(avatar));
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_BACKGROUND, isGroupPhoto ? "" : avatar.getBackgroundFile());

                // 其他？
                String[] others = avatar.getOtherFile();
                for (int i = 0; i < otherItem.length; i++) {
                    if (others != null && i < others.length) {
                        RtcManager.getInstance().enableAvatarGeneratorItems(otherItem[i], others[i]);
                    } else {
                        RtcManager.getInstance().disableAvatarGeneratorItems(otherItem[i]);
                    }
                }
                commitItem(avatar);
            }
        });
    }

    private String loadExpressionBundle(AvatarPTA avatar) {
        String bundlePath = null;
        if (TextUtils.isEmpty(avatar.getExpressionFile())) {
            if (isPose) {
                bundlePath = FilePathFactory.bundlePose(avatar.getGender());
            } else if (mIsNeedTrack || mIsNeedFacePUP || mIsNeedIdle) {
                bundlePath = FilePathFactory.bundleIdle(avatar.getGender());
            } else {
                bundlePath = FilePathFactory.bundleAnim(avatar.getGender());
            }
        } else {
            bundlePath = avatar.getExpressionFile();
        }
        return bundlePath;
    }

    /**
     * 设置当前controller控制的人物id（默认：0）
     *
     * @param id
     */
    public void setCurrentInstancceId(int id) {
        RtcManager.getInstance().setGeneratorOptions("current_instance_id", new AvatarOptionValue(id));
        RtcManager.getInstance().setGeneratorOptions("target_position", new AvatarOptionValue(new double[]{0, 0, 0}));
        RtcManager.getInstance().setGeneratorOptions("reset_all", new AvatarOptionValue(1.0f));
    }

    @Override
    protected void bindAll() {
        setAvatarColor();
    }

    @Override
    protected void unBindAll() {
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_HEAD);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_HAIR);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_HAT);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_GLASS);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_BEARD);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_BROW);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_ELASH);

        RtcManager.getInstance().disableAvatarGeneratorItems(RtcManager.AVATAR_ITEM_TYPE_BODY);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_CLOTHES_SUIT);

        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_CLOTHES_UPPER);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_CLOTHES_LOWER);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_SHOES);

        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_TOUSHI);
        RtcManager.getInstance().disableAvatarGeneratorItems(RtcManager.AVATAR_ITEM_TYPE_ER_SHI);
        RtcManager.getInstance().disableAvatarGeneratorItems(RtcManager.AVATAR_ITEM_TYPE_JIAO_SHI);
        RtcManager.getInstance().disableAvatarGeneratorItems(RtcManager.AVATAR_ITEM_TYPE_SHOU_SHI);
        RtcManager.getInstance().disableAvatarGeneratorItems(RtcManager.AVATAR_ITEM_TYPE_BO_SHI);

        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_EYE_LINER);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_EYE_SHADOW);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_LIP_GLOSS);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_PUPIL);

        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_MAKEUP);

        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_ANIMATION);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_BACKGROUND);

        // 其他？
        for (int i = 0; i < otherItem.length; i++) {
            RtcManager.getInstance().disableAvatarGeneratorItems(otherItem[i]);
        }
    }

    @Override
    public void release() {
        unBindAll();
        releaseAll(true);
    }

    public void setMakeupHandleId() {
        eyebrowHandleId = eyebrowItemTypeId;
        eyeshadowHandleId = eyeshadowItemTypeId;
        lipglossHandleId = lipglossItemTypeId;
        eyelashHandleId = eyelashItemTypeId;
    }


    public void releaseAll(boolean isControllerRelease) {
    }

    /**
     * avatar水平方向旋转角度
     *
     * @param rotDelta 水平方向旋转角度增量
     */
    public void setRotDelta(final float rotDelta) {
        RtcManager.getInstance().setGeneratorOptions("rot_delta", new AvatarOptionValue(rotDelta));
    }

    /**
     * avatar所在位置高度
     *
     * @param translateDelta avatar所在位置高度增量
     */
    public void setTranslateDelta(final float translateDelta) {
        RtcManager.getInstance().setGeneratorOptions("translate_delta", new AvatarOptionValue(translateDelta));
    }

    /**
     * avatar缩放比例
     *
     * @param scaleDelta avatar缩放比例增量
     */
    public void setScaleDelta(final float scaleDelta) {
        RtcManager.getInstance().setGeneratorOptions("scale_delta", new AvatarOptionValue(scaleDelta));
    }

    /**
     * avatar缩放比例
     *
     * @param scaleDelta avatar缩放比例增量
     */
    public void setScaleDelta(final float scaleDelta, double maxScale, double minScale) {
        double[] lastScale = getCurrent_position();
        if ((lastScale[2] >= maxScale && scaleDelta > 0) || (lastScale[2] <= minScale && scaleDelta < 0)) {
            return;
        }
        if (scaleDelta > 0 && lastScale[2] + scaleDelta > maxScale) {
            RtcManager.getInstance().setGeneratorOptions("scale_delta", new AvatarOptionValue(maxScale - lastScale[2]));
        } else if (scaleDelta < 0 && lastScale[2] + scaleDelta < minScale) {
            RtcManager.getInstance().setGeneratorOptions("scale_delta", new AvatarOptionValue(minScale - lastScale[2]));
        } else {
            RtcManager.getInstance().setGeneratorOptions("scale_delta", new AvatarOptionValue(scaleDelta));
        }
    }

    /**
     * 设置缩放
     *
     * @param xyz
     */
    public void setScale(double[] xyz) {
        RtcManager.getInstance().setGeneratorOptions("target_position", new AvatarOptionValue(new double[]{xyz[0], xyz[1], xyz[2]}));
        RtcManager.getInstance().setGeneratorOptions("reset_all", new AvatarOptionValue(1));
    }

    /**
     * 获取当前位置
     *
     * @return
     */
    public double[] getCurrent_position() {
        AvatarOptionValue outValue = new AvatarOptionValue(new double[]{0, 0, 0});
        RtcManager.getInstance().GetGeneratorOptions("current_position", Constants.AvatarValueType.DoubleArray, outValue);
        return (double[]) outValue.value;
    }

    /**
     * 该方法只做对模型的旋转
     */
    public void resetAllFront() {
        RtcManager.getInstance().setGeneratorOptions("target_position", new AvatarOptionValue(new double[]{0, 0, 0}));
        RtcManager.getInstance().setGeneratorOptions("target_angle", new AvatarOptionValue(0));
        RtcManager.getInstance().setGeneratorOptions("reset_all", new AvatarOptionValue(3));
    }

    /**
     * 该方法只做对模型的旋转
     */
    public void resetAllSide() {
        RtcManager.getInstance().setGeneratorOptions("target_position", new AvatarOptionValue(new double[]{0, 0, 0}));
        RtcManager.getInstance().setGeneratorOptions("target_angle", new AvatarOptionValue(0.125));
        RtcManager.getInstance().setGeneratorOptions("reset_all", new AvatarOptionValue(3));
    }


    public void setNeedTrackFace(boolean needTrackFace) {
        mIsNeedTrack = needTrackFace;
        RtcManager.getInstance().setGeneratorOptions(mIsNeedTrack ? "enter_track_rotation_mode" : "quit_track_rotation_mode", new AvatarOptionValue(1));
    }

    /**
     * CNN 面部追踪
     *
     * @param needTrackFace
     */
    public void setCNNTrackFace(boolean needTrackFace) {
        mIsNeedTrack = needTrackFace;
        setFaceCapture(needTrackFace);
    }

    public void setFaceCapture(boolean isOpen) {
        RtcManager.getInstance().setGeneratorOptions("enable_face_processor", new AvatarOptionValue(isOpen ? 1.0 : 0.0));
    }

    public void setPose(boolean isPose) {
        this.isPose = isPose;
    }

    public void setGroupPhoto(boolean groupPhoto) {
        isGroupPhoto = groupPhoto;
    }

    //--------------------------------------动画----------------------------------------

    //从头播放句柄为anim_id的动画（循环）
    public void seekToAnimBegin(final int anim_id) {
        RtcManager.getInstance().setGeneratorOptions("play_animation", new AvatarOptionValue(anim_id));
    }

    /**
     * @param state   1：播放 2：暂停 3：停止
     * @param role_id 角色id
     */
    public void setAnimState(final int state, int role_id) {
        RtcManager.getInstance().setGeneratorOptions(
                "current_instance_id", new AvatarOptionValue(role_id));
        switch (state) {
            case 1:
                RtcManager.getInstance().setGeneratorOptions(
                        "start_animation", new AvatarOptionValue(role_id));
                break;
            case 2:
                RtcManager.getInstance().setGeneratorOptions(
                        "pause_animation", new AvatarOptionValue(role_id));
                break;
            case 3:
                RtcManager.getInstance().setGeneratorOptions(
                        "stop_animation", new AvatarOptionValue(role_id));
                break;
        }
    }

    /**
     * 获取某个动画的播放进度
     * 进度0-0.9999为第一次循环，1-1.9999为第二次循环，以此类推
     * 即使play_animation_once,进度也会突破1.0，照常运行
     *
     * @param anim_id 当前动画的句柄
     * @return
     */
    public float getAnimateProgress(final int anim_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", "get_animation_progress");
            jsonObject.put("anim_id", anim_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AvatarOptionValue outValue = new AvatarOptionValue();
        RtcManager.getInstance().GetGeneratorOptions(jsonObject.toString(), Constants.AvatarValueType.Float, outValue);
        return (float) outValue.value;
    }
    //--------------------------------------捏脸----------------------------------------

    public void setNeedFacePUP(boolean needFacePUP) {
        mIsNeedFacePUP = needFacePUP;
        RtcManager.getInstance().setGeneratorOptions(
                mIsNeedFacePUP ? "enter_facepup_mode" : "quit_facepup_mode", new AvatarOptionValue(1));
    }

    public void fuItemSetParamFaceShape(final String key, final double values) {
        if (values < 0 || values > 1) {
            Log.e(TAG, "fuItemSetParamFaceShape error key " + key + " values " + values);
            return;
        }
        RtcManager.getInstance().setGeneratorOptions(
                "{\"name\":\"facepup\",\"param\":\"" + key + "\"}", new AvatarOptionValue(values));
    }

    /**
     * 隐藏脖子
     */
    public void hide_neck() {
        RtcManager.getInstance().setGeneratorOptions(
                "hide_neck", new AvatarOptionValue(1.0));
    }

    public float fuItemGetParamShape(final String key) {
        AvatarOptionValue outValue = new AvatarOptionValue(0f);
        RtcManager.getInstance().GetGeneratorOptions("{\"name\":\"facepup\",\"param\":\"" + key + "\"}", Constants.AvatarValueType.Float, outValue);
        return (float) outValue.value;
    }

    private float[] expressions;

    public float[] fuItemGetParamFaceShape() {
        expressions = null;
        AvatarOptionValue outValue = new AvatarOptionValue(new float[0]);
        RtcManager.getInstance().GetGeneratorOptions("facepup_expression", Constants.AvatarValueType.FloatArray, outValue);
        expressions = (float[]) outValue.value;
        return expressions;
    }

    public Point getPointByIndex(int index) {
        RtcManager.getInstance().setGeneratorOptions("query_vert", new AvatarOptionValue(index));

        AvatarOptionValue xOutValue = new AvatarOptionValue();
        RtcManager.getInstance().GetGeneratorOptions("query_vert_x", Constants.AvatarValueType.UInt64, xOutValue);
        int x = (int)((long)xOutValue.value);

        AvatarOptionValue yOutValue = new AvatarOptionValue();
        RtcManager.getInstance().GetGeneratorOptions("query_vert_y", Constants.AvatarValueType.UInt64, yOutValue);
        int y = (int)((long)yOutValue.value);
        return new Point(x, y);
    }

    /**
     * 是否开启头发跟随
     *
     * @param isOpen true 开启
     *               false 关闭
     */
    public void openHairFollowing(boolean isOpen) {
        RtcManager.getInstance().setGeneratorOptions("modelmat_to_bone", new AvatarOptionValue(isOpen ? 1.0 : 0.0));
    }

    /**
     * 关闭加载的头发物理动效
     */
    public void setDynamicBone(boolean isOpen) {
        RtcManager.getInstance().setGeneratorOptions("enable_dynamicbone", new AvatarOptionValue(isOpen ? 1.0 : 0.0));
    }


    /**
     * 设置模型动画
     *
     * @param mShowAvatarP2A
     * @param bundleRes
     */
    public void setExpression(AvatarPTA mShowAvatarP2A, BundleRes bundleRes, int loadCount) {
        setExpression(mShowAvatarP2A, bundleRes, true, loadCount);
    }

    /**
     * 设置模型动画
     *
     * @param mShowAvatarP2A
     * @param bundleRes
     */
    public void setExpression(AvatarPTA mShowAvatarP2A, BundleRes bundleRes, boolean needResetAvatar, int loadCount) {
        mShowAvatarP2A.setExpression(bundleRes);
        setCurrentAniLoadCount(loadCount);
        needResetAvatar(mShowAvatarP2A, needResetAvatar);
    }


    /**
     * 取消模型动画
     *
     * @param mShowAvatarP2A
     */
    public void clearExpression(AvatarPTA mShowAvatarP2A, boolean needResetAvatar) {
        if (!TextUtils.isEmpty(mShowAvatarP2A.getExpressionFile())) {
            setCurrentAniLoadCount(Integer.MAX_VALUE);
            mShowAvatarP2A.setExpression(new BundleRes(""));
            needResetAvatar(mShowAvatarP2A, needResetAvatar);
        }
    }

    private void needResetAvatar(AvatarPTA mShowAvatarP2A, boolean needResetAvatar) {
        if (needResetAvatar) {
            setAvatar(mShowAvatarP2A);
        }
    }

    public void setCurrentAniLoadCount(int loadCount) {
        this.loadCount = loadCount;
    }

    public int getLoadCount() {
        return loadCount;
    }

    private int loadCount = Integer.MAX_VALUE;

    /**
     * 开启光照
     */
    public void openLight(String lightPath) {
        RtcManager.getInstance().enableAvatarGeneratorItems(RtcManager.AVATAR_ITEM_TYPE_LIGHT, lightPath);
    }

    /**
     * 关闭光照
     */
    public void closeLight() {
        RtcManager.getInstance().disableAvatarGeneratorItems(RtcManager.AVATAR_ITEM_TYPE_LIGHT);
    }

    /**
     * 关闭光照
     */
    public void setBackgroundColor(String color) {
        int colorValue = Color.parseColor(color);
        int r = Color.red(colorValue);
        int g = Color.green(colorValue);
        int b = Color.blue(colorValue);
        int a = Color.alpha(colorValue);
        RtcManager.getInstance().setGeneratorOptions("enable_background_color", new AvatarOptionValue(1.0));
        RtcManager.getInstance().setGeneratorOptions("set_background_color", new AvatarOptionValue(new double[]{r, g, b, a}));
    }

    public void disableBackgroundColor() {
        RtcManager.getInstance().setGeneratorOptions("enable_background_color", new AvatarOptionValue(0.0));
    }

    public void setmIsNeedIdle(boolean mIsNeedIdle) {
        this.mIsNeedIdle = mIsNeedIdle;
    }
}
