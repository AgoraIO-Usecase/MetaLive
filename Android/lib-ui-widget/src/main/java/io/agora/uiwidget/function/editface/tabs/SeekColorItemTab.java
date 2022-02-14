package io.agora.uiwidget.function.editface.tabs;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.OnColorValuesChangeListener;
import io.agora.uiwidget.function.editface.bean.ItemInfo;
import io.agora.uiwidget.function.editface.item.ItemAdapter;
import io.agora.uiwidget.function.editface.item.ItemSelectView;
import io.agora.uiwidget.function.editface.seekbar.DiscreteSeekBar;

public class SeekColorItemTab extends Tab<SeekColorItemTab.SeekColorTabViewHolder>{

    private final List<ItemInfo> itemList;
    private final int selectItem;
    private final ItemAdapter.ItemSelectListener itemSelectListener;

    private final int[] colorList;
    private final int selectColor;
    private final OnColorValuesChangeListener onColorValuesChangeListener;

    public SeekColorItemTab(
            String title,
            List<ItemInfo> itemList,
            int selectItem,
            ItemAdapter.ItemSelectListener itemSelectListener,

            int[] colorList,
            int selectColor,
            OnColorValuesChangeListener onColorValuesChangeListener
    ) {
        super(title, VIEW_TYPE_SEEK_COLOR_ITEM);

        this.itemList = itemList;
        this.selectItem = selectItem;
        this.itemSelectListener = itemSelectListener;

        this.colorList = colorList;
        this.selectColor = selectColor;
        this.onColorValuesChangeListener = onColorValuesChangeListener;
    }

    @Override
    public void onBindViewHolder(@NonNull SeekColorTabViewHolder holder, int position) {
        holder.itemSelectView.setItemControllerListener(itemSelectListener);
        holder.itemSelectView.init(itemList, selectItem);
        holder.colorSeekBar.setTrackColor(colorList);
    }

    public static class SeekColorTabViewHolder extends RecyclerView.ViewHolder {
        private final DiscreteSeekBar colorSeekBar;
        private final ItemSelectView itemSelectView;


        public SeekColorTabViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_face_select_tab_shape, parent, false));
            colorSeekBar = itemView.findViewById(R.id.color_seek_bar);
            itemSelectView = itemView.findViewById(R.id.shape_item_recycler);
        }
    }
}
