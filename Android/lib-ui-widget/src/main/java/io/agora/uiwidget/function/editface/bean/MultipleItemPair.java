package io.agora.uiwidget.function.editface.bean;

public class MultipleItemPair {
    private int frontLength;  //当前道具之前的长度
    private int selectItemPos;//选择的道具位置
    private int selectColorPos;//选择的道具颜色位置

    public MultipleItemPair(int frontLength, int selectItemPos) {
        this(frontLength, selectItemPos, -1);
    }

    public MultipleItemPair(int frontLength, int selectItemPos, int selectColorPos) {
        this.frontLength = frontLength;
        this.selectItemPos = selectItemPos;
        this.selectColorPos = selectColorPos;
    }

    public int getFrontLength() {
        return frontLength;
    }

    public void setFrontLength(int frontLength) {
        this.frontLength = frontLength;
    }

    public int getSelectItemPos() {
        return selectItemPos;
    }

    public void setSelectItemPos(int selectItemPos) {
        this.selectItemPos = selectItemPos;
    }

    public int getSelectColorPos() {
        return selectColorPos;
    }

    public void setSelectColorPos(int selectColorPos) {
        this.selectColorPos = selectColorPos;
    }
}
