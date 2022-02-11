package io.agora.uiwidget.function.editface.tabs;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class Tab<T extends RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_IMAGE = 0;
    public static final int VIEW_TYPE_COLOR_ITEM = 1;
    public static final int VIEW_TYPE_SHAPE = 2;
    public static final int VIEW_TYPE_ITEM = 3;
    public static final int VIEW_TYPE_MAKE_UP = 4;
    public static final int VIEW_TYPE_DECORATION = 5;
    public static final int VIEW_TYPE_GLASSES = 6;


    public final int viewType;
    public final String title;

    public Tab(String title, int viewType) {
        this.title = title;
        this.viewType = viewType;
    }

    public abstract void onBindViewHolder(@NonNull T holder, int position);
}