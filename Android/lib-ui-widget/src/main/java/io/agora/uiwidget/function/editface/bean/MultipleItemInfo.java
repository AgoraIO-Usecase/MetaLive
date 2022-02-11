package io.agora.uiwidget.function.editface.bean;

public class MultipleItemInfo extends ItemInfo {
    public int type;
    public String name;

    public MultipleItemInfo(int resId, int type, String name) {
        this.resId = resId;
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
