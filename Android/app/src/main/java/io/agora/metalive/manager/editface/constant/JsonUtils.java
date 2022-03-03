package io.agora.metalive.manager.editface.constant;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.agora.metalive.manager.EditFaceManager;
import io.agora.metalive.manager.editface.entity.BundleRes;
import io.agora.metalive.manager.editface.entity.SpecialBundleRes;

public class JsonUtils {
    /**
     * 解析的是普通类型的配置文件
     */
    public static final int TYPE_NORMAL = 1;
    /**
     * 解析的是配饰类型的配置文件
     */
    public static final int TYPE_DECORATION = 2;

    private List<BundleRes> jsonList = new ArrayList<>();
    private List<SpecialBundleRes> jsonDecorationList = new ArrayList<>();
    private Context context;

    public JsonUtils() {
        this.context = EditFaceManager.getInstance().getContext();
    }


    public void readJson(String path) {
        readJson(path, TYPE_NORMAL, 0);
    }

    /**
     * 解析传递进来的json文件
     *
     * @param path       json文件路径
     * @param type       解析的类型（1：正常类型 2：配饰类型）
     * @param bundleType 配饰类型中的类型（配饰-手、配饰-脚、配饰-脖子、配饰-头、配饰-耳朵）
     */
    public void readJson(String path, int type, int bundleType) {
        if (type == TYPE_NORMAL) {
            jsonList.clear();
        } else {
            jsonDecorationList.clear();
        }
        try {
            InputStream inputStream = context.getAssets().open(path);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            String jsonStr = new String(data);
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = (JSONArray) (jsonObject.opt(jsonObject.keys().next()));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                resolveConfigJson(jsonObject1, type, bundleType);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException", e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", e.getMessage());
        }
    }

    private void resolveConfigJson(JSONObject jsonObject, int type, int bundleType) {
        int gender = 0;
        String bundle = "";
        int resId = 0;
        Integer[] label = new Integer[]{};
        boolean isSupport = true;
        int bodyLevel = 0;
        try {
            if (jsonObject.has("bundle")) {
                bundle = jsonObject.getString("bundle");
            }

            if (jsonObject.has("icon")) {
                resId = context.getResources().getIdentifier(jsonObject.getString("icon"), "drawable", context.getPackageName());
            }

            if (jsonObject.has("gender")) {
                gender = jsonObject.getInt("gender");
            }

            if (jsonObject.has("label")) {
                JSONArray labelJA = jsonObject.getJSONArray("label");
                if (labelJA != null && labelJA.length() > 0) {
                    label = new Integer[labelJA.length()];
                }
                for (int i = 0; i < labelJA.length(); i++) {
                    label[i] = labelJA.getInt(i);
                }
            }

            if (jsonObject.has("body_match_level")) {
                bodyLevel = jsonObject.getInt("body_match_level");
            }
            if (jsonObject.has("body_level")) {
                bodyLevel = jsonObject.getInt("body_level");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (type == TYPE_NORMAL) {
            jsonList.add(new BundleRes(gender, bundle, resId, label, isSupport, bodyLevel));
        } else {
            jsonDecorationList.add(new SpecialBundleRes(resId, bundleType, bundle));
        }
    }

    public List<BundleRes> getBundleResList() {
        return jsonList;
    }

    /**
     * 获取配饰配置文件解析出来的数据
     *
     * @return
     */
    public List<SpecialBundleRes> getDecorationBundleResList() {
        return jsonDecorationList;
    }

    //读取捏脸点位个数及对应的名称
    public String[] readFacePupJson(String path) {
        List<String> facePupList = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open(path);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            String jsonStr = new String(data);
            JSONObject jsonObject = new JSONObject(jsonStr);
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                facePupList.add(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException", e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", e.getMessage());
        }
        String[] facePup = new String[facePupList.size()];
        facePupList.toArray(facePup);
        return facePup;
    }

}
