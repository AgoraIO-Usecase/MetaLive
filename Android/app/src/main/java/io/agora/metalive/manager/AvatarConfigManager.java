package io.agora.metalive.manager;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.ClosedFileSystemException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AvatarConfigManager extends RtcManager.BaseAvatarEventHandler {
    private static final String TAG = "AvatarConfigManager";

    private static AvatarConfigManager instance;

    private final Map<String, String> dressIdToNameMap = new HashMap<>();
    private final Map<String, DressConfigItemSet> dressConfigs = new HashMap<>();


    private final Map<String, String> feIdToNameMap = new HashMap<>();
    private final Set<String> feSupportIdSet = new HashSet<>();

    private final Map<String, FaceEditConfigGroup> feConfigGroups = new HashMap<>();
    private final Map<String, String> feGroupNames = new HashMap<>();

    private AvatarConfigManager() {
        initDressSetIdMap();
        initFaceEditMaps();
    }

    public static AvatarConfigManager getInstance() {
        if (instance == null) {
            synchronized (AvatarConfigManager.class) {
                if (instance == null) {
                    instance = new AvatarConfigManager();
                }
            }
        }
        return instance;
    }

    public void parseDressConfig(String configString) {
        dressConfigs.clear();
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, List<DressConfigItem>>>(){}.getType();


        Map<String, List<DressConfigItem>> map = gson.fromJson(configString, type);
        for (Map.Entry<String, List<DressConfigItem>> entry : map.entrySet()) {
            String id = entry.getKey();
            if (dressIdToNameMap.containsKey(id)) {
                DressConfigItemSet set = new DressConfigItemSet();
                set.id = id;
                set.name = dressIdToNameMap.get(id);
                try {
                    set.no = Integer.parseInt(set.id);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    set.no = 0;
                }

                set.items = entry.getValue();
                dressConfigs.put(id, set);
            }
        }
    }

    public List<DressConfigItemSet> getCurDressConfigSets() {
        List<DressConfigItemSet> list = new ArrayList<>(dressConfigs.values());
        Collections.sort(list, (first, second) -> {
            return Integer.compare(first.no, second.no);
        });
        return list;
    }

    public void parseFaceEditConfig(String configString) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Float>>(){}.getType();

        Map<String, Float> map = gson.fromJson(configString, type);
        for (Map.Entry<String, Float> entry : map.entrySet()) {
            String id = entry.getKey();
            if (!feIdToNameMap.containsKey(id)) {
                Log.e(TAG, "Face edit id " + id + " is not supported yet");
                continue;
            }

            float value = entry.getValue();
            String groupId = getGroupIdFromFeId(id);
            String groupName = feGroupNames.get(groupId);
            String feWholeName = feIdToNameMap.get(id);
            String subName = getFeSubNameFromWholeName(groupName, feWholeName);
            long no = Long.parseLong(id);

            if (feIdToNameMap.containsKey(id)) {
                FaceEditConfigItem item = new FaceEditConfigItem();
                item.id = id;
                item.name = subName;
                item.no = no;
                item.value = value;

                item.dump();

                if (feConfigGroups.get(groupId) != null) {
                    feConfigGroups.get(groupId).items.add(item);
                }
            }
        }
    }

    private String getGroupIdFromFeId(String feId) {
        if (feId.length() < 5) {
            return "";
        } else {
            return feId.substring(0, 5);
        }
    }

    private String getFeSubNameFromWholeName(String groupName, String feName) {
        if (feName.startsWith(groupName)) {
            return feName.substring(groupName.length());
        } else {
            return "";
        }
    }

    public List<FaceEditConfigGroup> getCurFaceEditConfigs() {
        List<FaceEditConfigGroup> list = new ArrayList<>(feConfigGroups.values());
        Collections.sort(list, (first, second) -> {
            return Long.compare(first.no, second.no);
        });
        return list;
    }

    public void refreshFaceEditConfig(String id, float value) {
        String groupId = getGroupIdFromFeId(id);
        if (feConfigGroups.get(groupId) != null) {
            for (FaceEditConfigItem item : feConfigGroups.get(groupId).items) {
                if (item.id.equals(id)) {
                    item.value = value;
                    break;
                }
            }
        }
    }

    @Override
    public void onLocalUserAvatarEvent(String s, String s1) {
        Log.e(TAG, "onLocalUserAvatarEvent " + s + " " + s1);
        if (AvatarConfig.DRESS_KEY_REQUEST_FULL_LIST.equals(s)) {
            AvatarConfigManager.getInstance().parseDressConfig(s1);
        } else if (AvatarConfig.FACE_EDIT_KEY_REQUEST_FULL_LIST.equals(s)) {
            AvatarConfigManager.getInstance().parseFaceEditConfig(s1);
        }
    }

    @Override
    public void onLocalUserAvatarStarted(boolean b, int i, String s) {
        Log.e(TAG, "onLocalUserAvatarStarted " + b + " " + i + " " + s);
    }

    @Override
    public void onLocalUserAvatarError(int i, String s) {
        Log.e(TAG, "onLocalUserAvatarError " + i + " " + s);
    }

    String dump() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, DressConfigItemSet> entry : dressConfigs.entrySet()) {
            DressConfigItemSet set = entry.getValue();
            builder.append(set.id).append(" ").append(set.name).append(" ").append(set.items);
            builder.append('\n');
        }
        return builder.toString();
    }

    public static class AvatarConfig {
        public static final String DRESS_KEY_START = "start_dress";
        public static final String DRESS_KEY_STOP = "stop_dress";
        public static final String DRESS_KEY_REQUEST_FULL_LIST = "request_dresslist";
        public static final String DRESS_KEY_SHOW_VIEW = "send_showview";
        public static final String DRESS_KEY_PUT_ON = "send_dress";
        public static final String DRESS_KEY_TAKE_OFF = "send_takeoff";
        public static final String DRESS_KEY_SET_COLOR = "send_setcolor";

        public static final String FACE_EDIT_KEY_START = "start_faceedit";
        public static final String FACE_EDIT_KEY_STOP = "stop_faceedit";
        public static final String FACE_EDIT_KEY_REQUEST_FULL_LIST = "request_felist";
        public static final String FACE_EDIT_KEY_SEND = "send_felist";

        public static final String KEY_AVATAR_QUALITY = "set_quality";
    }

    public static class DressConfigItemSet {
        public String name;
        public String id;
        public int no;

        public List<DressConfigItem> items;
    }

    public static class DressConfigItem {
        public String id;
        public String mame;
        public String icon;
        public int version;
        public int tag;
        public int status;
        public int isUsing;
        public int zOrder;

        @NonNull
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    public static class FaceEditConfigGroup {
        public String id;
        public long no;
        public String name;
        public List<FaceEditConfigItem> items = new ArrayList<>();

        public void dump() {
            System.out.println("group id:" + id + ", name:" + name + "-------------------------------------");
            System.out.println();
            for (FaceEditConfigItem item : items) {
                item.dump();
                System.out.println();
            }
        }
    }

    public static class FaceEditConfigItem {
        public String id;
        public long no;
        public String name;
        public float value;

        public void dump() {
            System.out.println("id:" + id + ", name:" + name + ", val:" + value);
        }
    }

    private void initDressSetIdMap() {
        dressIdToNameMap.put("50", "脸型");
        dressIdToNameMap.put("51", "眼型");
        dressIdToNameMap.put("52", "嘴型");
        dressIdToNameMap.put("53", "瞳孔");
        dressIdToNameMap.put("54", "睫毛");
        dressIdToNameMap.put("55", "眉毛");
        dressIdToNameMap.put("60", "腮红");
        dressIdToNameMap.put("61", "口红");
        dressIdToNameMap.put("62", "眼影");
        dressIdToNameMap.put("63", "眼线");
        dressIdToNameMap.put("64", "胡须");
        dressIdToNameMap.put("65", "面部彩绘");
        dressIdToNameMap.put("66", "肤色");
        dressIdToNameMap.put("70", "发型");
        dressIdToNameMap.put("71", "上装");
        dressIdToNameMap.put("72", "下装");
        dressIdToNameMap.put("73", "连衣裙");
        dressIdToNameMap.put("74", "鞋子");
        dressIdToNameMap.put("75", "帽子");
        dressIdToNameMap.put("76", "眼镜");
        dressIdToNameMap.put("77", "配饰");
    }

    private void initFaceEditMaps() {
        feGroupNames.put("10020", "颧骨");
        feGroupNames.put("10030", "苹果肌");
        feGroupNames.put("10040", "下颚角");
        feGroupNames.put("10060", "下颚");
        feGroupNames.put("10070", "下唇肌");
        feGroupNames.put("10080", "下巴");

        feGroupNames.put("11010", "眼睛整体");
        feGroupNames.put("11020", "内上眼皮");
        feGroupNames.put("11030", "外上眼皮");
        feGroupNames.put("11050", "内眼角");
        feGroupNames.put("11060", "外眼角");
        feGroupNames.put("11070", "瞳孔");

        feGroupNames.put("12010", "眉心");
        feGroupNames.put("12020", "眉头");
        feGroupNames.put("12030", "眉中");
        feGroupNames.put("12040", "眉尾");

        feGroupNames.put("13020", "鼻梁");
        feGroupNames.put("13040", "鼻头");

        feGroupNames.put("14010", "嘴巴整体");
        feGroupNames.put("14020", "嘴角");
        feGroupNames.put("14030", "上唇");
        feGroupNames.put("14040", "唇珠");

        feIdToNameMap.put("100201", "颧骨上下");
        feIdToNameMap.put("100202", "颧骨前后");
        feIdToNameMap.put("100204", "颧骨宽度");

        feIdToNameMap.put("100301", "苹果肌上下");
        feIdToNameMap.put("100302", "苹果肌前后");
        feIdToNameMap.put("100304", "苹果肌宽度");

        feIdToNameMap.put("100402", "下颚角上下");
        feIdToNameMap.put("100403", "下颚角前后");
        feIdToNameMap.put("100407", "下颚角宽度");

        feIdToNameMap.put("100602", "下颚上下");
        feIdToNameMap.put("100603", "下颚前后");
        feIdToNameMap.put("100607", "下颚宽度");

        feIdToNameMap.put("100702", "下唇肌上下");
        feIdToNameMap.put("100703", "下唇肌前后");
        feIdToNameMap.put("100707", "下唇肌宽度");

        feIdToNameMap.put("100801", "下巴上下");
        feIdToNameMap.put("100802", "下巴前后");
        feIdToNameMap.put("100804", "下巴宽度");

        feIdToNameMap.put("110101", "眼睛整体左右");
        feIdToNameMap.put("110102", "眼睛整体上下");
        feIdToNameMap.put("110103", "眼睛整体前后");
        feIdToNameMap.put("110104", "眼睛整体角度");

        feIdToNameMap.put("110201", "内上眼皮左右");
        feIdToNameMap.put("110202", "内上眼皮上下");
        feIdToNameMap.put("110203", "内上眼皮前后");
        feIdToNameMap.put("110204", "内上眼皮角度");
        feIdToNameMap.put("110205", "内上眼皮高低");
        feIdToNameMap.put("110206", "内上眼皮倾斜");
        feIdToNameMap.put("110207", "内上眼皮长度");
        feIdToNameMap.put("110208", "内上眼皮宽度");
        feIdToNameMap.put("110209", "内上眼皮饱满");

        feIdToNameMap.put("110301", "外上眼皮左右");
        feIdToNameMap.put("110302", "外上眼皮上下");
        feIdToNameMap.put("110303", "外上眼皮前后");
        feIdToNameMap.put("110304", "外上眼皮角度");
        feIdToNameMap.put("110305", "外上眼皮高低");
        feIdToNameMap.put("110306", "外上眼皮倾斜");
        feIdToNameMap.put("110307", "外上眼皮长度");
        feIdToNameMap.put("110308", "外上眼皮宽度");
        feIdToNameMap.put("110309", "外上眼皮饱满");

        feIdToNameMap.put("110501", "内眼角左右");
        feIdToNameMap.put("110502", "内眼角上下");

        feIdToNameMap.put("110601", "外眼角左右");
        feIdToNameMap.put("110602", "外眼角上下");

        feIdToNameMap.put("110701", "瞳孔大小");

        feIdToNameMap.put("120101", "眉心上下");
        feIdToNameMap.put("120103", "眉心角度");

        feIdToNameMap.put("120202", "眉头上下");
        feIdToNameMap.put("120204", "眉头角度");

        feIdToNameMap.put("120302", "眉中上下");
        feIdToNameMap.put("120304", "眉中角度");

        feIdToNameMap.put("120402", "眉尾上下");
        feIdToNameMap.put("120404", "眉尾角度");

        feIdToNameMap.put("130202", "鼻梁前后");

        feIdToNameMap.put("130401", "鼻头上下");
        feIdToNameMap.put("130402", "鼻头前后");

        feIdToNameMap.put("140101", "嘴巴整体上下");
        feIdToNameMap.put("140102", "嘴巴整体前后");

        feIdToNameMap.put("140201", "嘴角左右");
        feIdToNameMap.put("140202", "嘴角上下");
        feIdToNameMap.put("140203", "嘴角前后");
        feIdToNameMap.put("140204", "嘴角角度");
        feIdToNameMap.put("140205", "嘴角倾斜");
        feIdToNameMap.put("140206", "嘴角外翻");
        feIdToNameMap.put("140207", "嘴角宽度");
        feIdToNameMap.put("140208", "嘴角厚度");
        feIdToNameMap.put("140209", "嘴角饱满");

        feIdToNameMap.put("140301", "上嘴唇两侧左右");
        feIdToNameMap.put("140302", "上嘴唇两侧上下");
        feIdToNameMap.put("140303", "上嘴唇两侧前后");
        feIdToNameMap.put("140304", "上嘴唇两侧角度");
        feIdToNameMap.put("140305", "上嘴唇两侧倾斜");
        feIdToNameMap.put("140306", "上嘴唇两侧外翻");
        feIdToNameMap.put("140307", "上嘴唇两侧宽度");
        feIdToNameMap.put("140308", "上嘴唇两侧厚度");
        feIdToNameMap.put("140309", "上嘴唇两侧饱满");

        feIdToNameMap.put("140401", "唇珠上下");
        feIdToNameMap.put("140402", "唇珠前后");
        feIdToNameMap.put("140403", "唇珠角度");
        feIdToNameMap.put("140404", "唇珠宽度");
        feIdToNameMap.put("140405", "唇珠厚度");
        feIdToNameMap.put("140406", "唇珠饱满");

        feSupportIdSet.add("100204");
        feSupportIdSet.add("100304");
        feSupportIdSet.add("100402");
        feSupportIdSet.add("100407");

        feSupportIdSet.add("100602");
        feSupportIdSet.add("110102");
        feSupportIdSet.add("110202");
        feSupportIdSet.add("110203");

        feSupportIdSet.add("110204");
        feSupportIdSet.add("110304");
        feSupportIdSet.add("110602");
        feSupportIdSet.add("120302");

        feSupportIdSet.add("120304");
        feSupportIdSet.add("140101");
        feSupportIdSet.add("140201");
        feSupportIdSet.add("140301");

        for (Map.Entry<String, String> entry : feGroupNames.entrySet()) {
            FaceEditConfigGroup group = new FaceEditConfigGroup();
            group.id = entry.getKey();
            group.name = entry.getValue();
            try {
                group.no = Long.parseLong(group.id);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                group.no = 0;
            }

            feConfigGroups.put(group.id, group);
        }
    }
}

