package io.agora.uiwidget.function.editface.tabs;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.bean.ItemInfo;
import io.agora.uiwidget.function.editface.color.ColorAdapter;
import io.agora.uiwidget.function.editface.color.ColorSelectView;
import io.agora.uiwidget.function.editface.item.ItemAdapter;
import io.agora.uiwidget.function.editface.item.ItemSelectView;

public class ColorItemTab extends Tab<ColorItemTab.ColorItemTabViewHolder> {

    private final List<ItemInfo> itemList;
    private final int selectItem;
    private final ItemAdapter.ItemSelectListener itemSelectListener;
    private final double[][] colorList;
    private final int selectColor;
    private final ColorAdapter.ColorSelectListener colorSelectListener;

    public ColorItemTab(
            String title,
            List<ItemInfo> itemList,
            int selectItem,
            ItemAdapter.ItemSelectListener itemSelectListener,

            double[][] colorList,
            int selectColor,
            ColorAdapter.ColorSelectListener colorSelectListener
    ) {
        super(title, VIEW_TYPE_COLOR_ITEM);
        this.itemList = itemList;
        this.selectItem = selectItem;
        this.itemSelectListener = itemSelectListener;
        this.colorList = colorList;
        this.selectColor = selectColor;
        this.colorSelectListener = colorSelectListener;
    }

    @Override
    public void onBindViewHolder(@NonNull ColorItemTab.ColorItemTabViewHolder holder, int position) {
        holder.itemSelectView.setItemControllerListener(itemSelectListener);
        holder.itemSelectView.init(itemList, selectItem);
        holder.colorSelectView.setColorSelectListener(colorSelectListener);
        holder.colorSelectView.init(colorList, selectColor);
    }

    public static class ColorItemTabViewHolder extends RecyclerView.ViewHolder {

        private final ColorSelectView colorSelectView;
        private final ItemSelectView itemSelectView;

        public ColorItemTabViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_face_select_tab_color_item, parent, false));
            colorSelectView = itemView.findViewById(R.id.color_recycler);
            itemSelectView = itemView.findViewById(R.id.item_select_view);
        }
    }
}
