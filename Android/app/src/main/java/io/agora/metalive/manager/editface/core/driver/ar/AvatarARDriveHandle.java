package io.agora.metalive.manager.editface.core.driver.ar;

import android.text.TextUtils;

import io.agora.metalive.manager.RtcManager;
import io.agora.metalive.manager.editface.constant.FilePathFactory;
import io.agora.metalive.manager.editface.core.base.BaseCore;
import io.agora.metalive.manager.editface.core.base.BasePTAHandle;
import io.agora.metalive.manager.editface.core.base.FUItemHandler;
import io.agora.metalive.manager.editface.entity.AvatarPTA;
import io.agora.rtc2.video.AvatarItemType;
import io.agora.rtc2.video.AvatarOptionValue;

/**
 * AR Controller
 * Created by tujh on 2018/12/17.
 */
public class AvatarARDriveHandle extends BasePTAHandle {

    public int eyebrowItem = 0;
    public int eyelashItem = 0;
    public int eyeshadowItem = 0;
    public int lipglossItem = 0;

    public AvatarARDriveHandle(BaseCore baseCore, FUItemHandler FUItemHandler) {
        super(baseCore, FUItemHandler);
        setModelmat(false);
        enterArMode();
    }

    public void setARAvatar(final AvatarPTA avatar, boolean needDestory) {
        mFUItemHandler.post(new Runnable() {
            @Override
            public void run() {
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_HEAD, avatar.getHeadFile(), needDestory);
                // 当前的帽子都是帽子头发道具，所以就不需要原先的头发道具了
                if (TextUtils.isEmpty(avatar.getHatFile())) {
                    RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_HAIR, avatar.getHairFile(), needDestory);
                    RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_HAT, avatar.getHatFile(), needDestory);
                } else {
                    RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_HAIR, "", needDestory);
                    RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_HAT, avatar.getHatFile(), needDestory);
                }
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_GLASS, avatar.getGlassesFile(), needDestory);
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_BEARD, avatar.getBeardFile(), needDestory);
                eyebrowItem = AvatarItemType.AvatarItemType_EYE_SHADOW;
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_EYE_SHADOW, avatar.getEyebrowFile(), needDestory);
                eyelashItem = AvatarItemType.AvatarItemType_ELASH;
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_ELASH, avatar.getEyelashFile(), needDestory);

                RtcManager.getInstance().enableAvatarGeneratorItems(RtcManager.AVATAR_ITEM_TYPE_ER_SHI, avatar.getEarDecorationsFile());
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_TOUSHI, avatar.getHeadDecorationsFile());

                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_EYE_LINER, avatar.getEyelinerFile());
                eyeshadowItem = AvatarItemType.AvatarItemType_EYE_SHADOW;
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_EYE_SHADOW, avatar.getEyeshadowFile());
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_MAKEUP, avatar.getFacemakeupFile());
                lipglossItem = AvatarItemType.AvatarItemType_LIP_GLOSS;
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_LIP_GLOSS, avatar.getLipglossFile());
                RtcManager.getInstance().enableAvatarGeneratorItems(AvatarItemType.AvatarItemType_PUPIL, avatar.getPupilFile());

                commitItem(avatar);
            }
        });
    }

    @Override
    protected void bindAll() {

    }

    @Override
    protected void unBindAll() {
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_HEAD);
        // 当前的帽子都是帽子头发道具，所以就不需要原先的头发道具了
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_HAIR);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_HAT);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_GLASS);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_BEARD);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_EYE_SHADOW);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_ELASH);

        RtcManager.getInstance().disableAvatarGeneratorItems(RtcManager.AVATAR_ITEM_TYPE_ER_SHI);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_TOUSHI);

        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_EYE_LINER);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_EYE_SHADOW);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_MAKEUP);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_LIP_GLOSS);
        RtcManager.getInstance().disableAvatarGeneratorItems(AvatarItemType.AvatarItemType_PUPIL);
    }

    /**
     * 退出ar模式
     */
    public void quitArMode() {
        RtcManager.getInstance().disableAvatarGeneratorItems(RtcManager.AVATAR_ITEM_TYPE_HAIR_MASK);
        RtcManager.getInstance().setGeneratorOptions("quit_ar_mode", new AvatarOptionValue(1));
        RtcManager.getInstance().setGeneratorOptions("enable_face_processor", new AvatarOptionValue(0.0));
    }

    /**
     * 进入ar模式
     */
    public void enterArMode() {
        RtcManager.getInstance().setGeneratorOptions("enter_ar_mode", new AvatarOptionValue(1));
        RtcManager.getInstance().enableAvatarGeneratorItems(RtcManager.AVATAR_ITEM_TYPE_HAIR_MASK, FilePathFactory.BUNDLE_hair_mask);
        RtcManager.getInstance().setGeneratorOptions("enable_face_processor", new AvatarOptionValue(1.0));
    }

    @Override
    public void release() {
        quitArMode();
        unBindAll();
    }

    @Override
    public void setMakeupHandleId() {
        eyebrowHandleId = eyebrowItem;
        eyeshadowHandleId = eyeshadowItem;
        lipglossHandleId = lipglossItem;
        eyelashHandleId = eyelashItem;
    }



    /**
     * 设置头发物理动效
     *
     * @param startModelmatbone 是否开启
     */
    public void setModelmat(final boolean startModelmatbone) {
        /**
         * 1 为开启，0 为关闭，开启的时候移动角色的值会被设进骨骼系统，这时候带DynamicBone的模型会有相关效果
         * 如果添加了没有骨骼的模型，请关闭这个值，否则无法移动模型
         * 默认开启
         * 每个角色的这个值都是独立的
         */
        RtcManager.getInstance().setGeneratorOptions("modelmat_to_bone", new AvatarOptionValue(startModelmatbone ? 1 : 0));
    }


    /**
     * 1为开启，0为关闭，开启的时候已加载的物理会生效，
     * 同时加载新的带物理的bundle也会生效，关闭的时候已加载的物理会停止生效
     * ，但不会清除缓存（这时候再次开启物理会在此生效），这时加载带物理的bundle不会生效，
     * 且不会产生缓存，即关闭后加载的带物理的bundle，即时再次开启，物理也不会生效，需要重新加载
     */
    public void setEnableDynamicbone(boolean open) {
        RtcManager.getInstance().setGeneratorOptions("enable_dynamicbone", new AvatarOptionValue(open ? 1.0 : 0.0));
    }

    public void resetAll() {
        RtcManager.getInstance().setGeneratorOptions("target_position", new AvatarOptionValue(new double[]{0.0, 0, 0}));
        RtcManager.getInstance().setGeneratorOptions("target_angle", new AvatarOptionValue(0));
        RtcManager.getInstance().setGeneratorOptions("reset_all", new AvatarOptionValue(6));
    }
}
