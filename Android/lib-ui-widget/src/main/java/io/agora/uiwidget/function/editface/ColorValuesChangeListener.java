package io.agora.uiwidget.function.editface;

public interface ColorValuesChangeListener {
    void colorValuesChangeStart(int id);

    void colorValuesChangeListener(int id, int index, double values);

    void colorValuesForSeekBarListener(int id, int index, float radio, double[] values);

    void colorValuesChangeEnd(int id);
}
