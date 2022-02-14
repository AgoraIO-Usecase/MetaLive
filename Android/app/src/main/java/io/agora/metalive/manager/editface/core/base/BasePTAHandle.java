package io.agora.metalive.manager.editface.core.base;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.agora.metalive.manager.RtcManager;
import io.agora.metalive.manager.editface.constant.ColorConstant;
import io.agora.metalive.manager.editface.entity.AvatarPTA;
import io.agora.rtc2.Constants;
import io.agora.rtc2.video.AvatarOptionValue;

/**
 * 基础的P2A Controller
 * Created by tujh on 2018/12/18.
 */
public abstract class BasePTAHandle extends BaseHandle {
    private static final String TAG = BasePTAHandle.class.getSimpleName();
    protected final int FUItemHandler_what_controller = FUItemHandler.generateWhatIndex() + 1;

    /**
     * 美妆bundle
     */
    public int eyebrowHandleId, eyeshadowHandleId, lipglossHandleId,
            eyelashHandleId;

    public BasePTAHandle(BaseCore baseCore, FUItemHandler FUItemHandler) {
        super(baseCore, FUItemHandler);
    }

    protected abstract void bindAll();

    protected abstract void unBindAll();

    public abstract void release();

    private AvatarPTA mAvatarP2A;

    public void setAvatarP2A(AvatarPTA avatarP2A) {
        this.mAvatarP2A = avatarP2A;
    }

    protected void commitItem(AvatarPTA avatar) {
        mAvatarP2A = avatar;
        setMakeupHandleId();
        setAvatarColor();
    }

    protected void setAvatarColor() {
        if (mAvatarP2A.getSkinColorValue() >= 0) {
            fuItemSetParam(PARAM_KEY_skin_color, ColorConstant.getRadioColor(mAvatarP2A.getSkinColorValue()));
        }
        if (mAvatarP2A.getLipColorValue() >= 0) {
            fuItemSetParam(PARAM_KEY_lip_color, ColorConstant.getColor(ColorConstant.lip_color, mAvatarP2A.getLipColorValue()));
        }
        fuItemSetParam(PARAM_KEY_iris_color, ColorConstant.getColor(ColorConstant.iris_color, mAvatarP2A.getIrisColorValue()));
        fuItemSetParam(PARAM_KEY_hair_color, ColorConstant.getColor(ColorConstant.hair_color, mAvatarP2A.getHairColorValue()));
        fuItemSetParam(PARAM_KEY_hair_color_intensity, ColorConstant.getColor(ColorConstant.hair_color, mAvatarP2A.getHairColorValue())[3]);
        fuItemSetParam(PARAM_KEY_glass_color, ColorConstant.getColor(ColorConstant.glass_color, mAvatarP2A.getGlassesColorValue()));
        fuItemSetParam(PARAM_KEY_glass_frame_color, ColorConstant.getColor(ColorConstant.glass_frame_color, mAvatarP2A.getGlassesFrameColorValue()));
        fuItemSetParam(PARAM_KEY_beard_color, ColorConstant.getColor(ColorConstant.beard_color, mAvatarP2A.getBeardColorValue()));
        fuItemSetParam(PARAM_KEY_hat_color, ColorConstant.getColor(ColorConstant.hat_color, mAvatarP2A.getHatColorValue()));


        /**
         * 美妆色卡相关
         */
        if (eyebrowHandleId > 0) {
            setMakeupColor(eyebrowHandleId, ColorConstant.getMakeupColor(ColorConstant.makeup_color, mAvatarP2A.getEyebrowColorValue()));
        }
        if (eyeshadowHandleId > 0) {
            setMakeupColor(eyeshadowHandleId, ColorConstant.getMakeupColor(ColorConstant.makeup_color, mAvatarP2A.getEyeshadowColorValue()));
        }
        if (lipglossHandleId > 0) {
            setMakeupColor(lipglossHandleId, ColorConstant.getMakeupColor(ColorConstant.lip_color, mAvatarP2A.getLipglossColorValue()));
        }
        if (eyelashHandleId > 0) {
            setMakeupColor(eyelashHandleId, ColorConstant.getMakeupColor(ColorConstant.makeup_color, mAvatarP2A.getEyelashColorValue()));
        }
    }

    public abstract void setMakeupHandleId();

    public void fuItemSetParamFuItemHandler(final String key, final double[] values) {
        mFUItemHandler.post(new Runnable() {
            @Override
            public void run() {
                fuItemSetParam(key, values);
                Log.i(TAG, "fuItemSetParamFuItemHandler key " + key + " values " + Arrays.toString(values));
            }
        });
    }

    public void fuItemSetParam(final String key, final double[] values) {
        RtcManager.getInstance().setGeneratorOptions(key, new AvatarOptionValue(values));
    }

    public void fuItemSetParamFuItemHandler(final String key, final double values) {
        mFUItemHandler.post(new Runnable() {
            @Override
            public void run() {
                fuItemSetParam(key, values);
                Log.i(TAG, "fuItemSetParamFuItemHandler key " + key + " values " + values);
            }
        });
    }

    public void fuItemSetParam(final String key, final double values) {
        RtcManager.getInstance().setGeneratorOptions(key, new AvatarOptionValue(values));
    }

    /**
     * 设置美妆颜色
     *
     * @param color
     */
    public void setMakeupColor(int makeupHandleId, double[] color) {
        //设置美妆的颜色
        //美妆参数名为json结构，
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", "global");
            jsonObject.put("type", "face_detail");
            jsonObject.put("param", "blend_color");
            jsonObject.put("UUID", "{#type#"+makeupHandleId+"#}");//需要修改的美妆道具bundle handle id
        } catch (JSONException e) {
            e.printStackTrace();
        }
        double[] makeupColor = new double[color.length];
        for (int i = 0; i < color.length; i++) {
            makeupColor[i] = color[i] * 1.0 / 255;
        }
        //美妆参数值为0-1之间的RGB设置，美妆颜色原始为RGB色值(sRGB空间)，RGB/255得到传给controller的值
        //例如要替换的美妆颜色为[255,0,0], 传给controller的值为[1,0,0]
        RtcManager.getInstance().setGeneratorOptions(
                "fmt#"+jsonObject.toString().replace("\"{#", "{#").replace("#}\"", "#}"),
                new AvatarOptionValue(makeupColor));
    }


    public int fuItemGetParamSkinColorIndex() {
        AvatarOptionValue value = new AvatarOptionValue(0);
        RtcManager.getInstance().GetGeneratorOptions("skin_color_index", Constants.AvatarValueType.UInt64, value);
        return (int)((long) value.value);
    }

    public int fuItemGetParamLipColorIndex() {
        AvatarOptionValue value = new AvatarOptionValue(0);
        RtcManager.getInstance().GetGeneratorOptions("lip_color_index", Constants.AvatarValueType.UInt64, value);
        return (int)((long) value.value);
    }

    public static final String PARAM_KEY_skin_color = "skin_color";
    public static final String PARAM_KEY_hair_color = "hair_color";
    public static final String PARAM_KEY_hair_color_intensity = "hair_color_intensity";
    public static final String PARAM_KEY_beard_color = "beard_color";
    public static final String PARAM_KEY_hat_color = "hat_color";
    public static final String PARAM_KEY_iris_color = "iris_color";
    public static final String PARAM_KEY_lip_color = "lip_color";
    public static final String PARAM_KEY_glass_color = "glass_color";
    public static final String PARAM_KEY_glass_frame_color = "glass_frame_color";

}
