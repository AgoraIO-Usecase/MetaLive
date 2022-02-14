package io.agora.uiwidget.function.editface.tabs;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.CustomSwitchView;
import io.agora.uiwidget.function.editface.bean.ItemInfo;
import io.agora.uiwidget.function.editface.color.ColorAdapter;
import io.agora.uiwidget.function.editface.color.ColorSelectView;
import io.agora.uiwidget.function.editface.item.ItemAdapter;
import io.agora.uiwidget.function.editface.item.ItemSelectView;

public class SwitchColorItemTab extends Tab<SwitchColorItemTab.SwitchColorItemTabViewHolder>{

    private final List<ItemInfo> itemList;
    private final int selectItem;
    private final ItemAdapter.ItemSelectListener itemSelectListener;
    private final String color1Name;
    private final double[][] color1List;
    private final int color1Select;
    private final ColorAdapter.ColorSelectListener color1SelectListener;
    private final String color2Name;
    private final double[][] color2List;
    private final int color2Select;
    private final ColorAdapter.ColorSelectListener color2SelectListener;

    public SwitchColorItemTab(
            String title,

            List<ItemInfo> itemList,
            int selectItem,
            ItemAdapter.ItemSelectListener itemSelectListener,

            String color1Name,
            double[][] color1List,
            int color1Select,
            ColorAdapter.ColorSelectListener color1SelectListener,

            String color2Name,
            double[][] color2List,
            int color2Select,
            ColorAdapter.ColorSelectListener color2SelectListener
    ) {
        super(title, VIEW_TYPE_SWiTCH_COLOR_ITEM);
        this.itemList = itemList;
        this.selectItem = selectItem;
        this.itemSelectListener = itemSelectListener;
        this.color1Name = color1Name;
        this.color1List = color1List;
        this.color1Select = color1Select;
        this.color1SelectListener = color1SelectListener;
        this.color2Name = color2Name;
        this.color2List = color2List;
        this.color2Select = color2Select;
        this.color2SelectListener = color2SelectListener;
    }

    @Override
    public void onBindViewHolder(@NonNull SwitchColorItemTabViewHolder holder, int position) {
        holder.colorSwitchView.setLeftText(color1Name);
        holder.colorSwitchView.setRightText(color2Name);
        holder.colorSwitchView.setCheckedChangeListener(new CustomSwitchView.CheckedChangeListener() {
            @Override
            public void onCheckedChangeListener(boolean selectedLeft) {
                if(selectedLeft){
                    holder.colorSelectView.init(color1List, color1Select);
                }else{
                    holder.colorSelectView.init(color2List, color2Select);
                }
            }
        });
        holder.colorSwitchView.setLeftChecked(true);
        holder.colorSelectView.init(color1List, color1Select);

        holder.itemSelectView.setItemControllerListener(itemSelectListener);
        holder.itemSelectView.init(itemList, selectItem);
    }

    public static class SwitchColorItemTabViewHolder extends RecyclerView.ViewHolder{
        private final CustomSwitchView colorSwitchView;
        private final ColorSelectView colorSelectView;
        private final ItemSelectView itemSelectView;

        public SwitchColorItemTabViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_face_select_tab_glasses, parent, false));
            colorSwitchView = itemView.findViewById(R.id.color_switch);
            colorSelectView = itemView.findViewById(R.id.color_recycler);
            itemSelectView = itemView.findViewById(R.id.glasses_recycler);
        }
    }
}
