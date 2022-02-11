package io.agora.uiwidget.function.editface.bean;

public class PairBean {
    private int frontLength;  //当前道具之前的长度
    private int selectItemPos;//选择的道具位置

    public PairBean(int frontLength, int selectItemPos) {
        this.frontLength = frontLength;
        this.selectItemPos = selectItemPos;
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

}
