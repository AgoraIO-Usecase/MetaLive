package io.agora.uiwidget.function.editface.tabs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.bean.MultipleItemInfo;
import io.agora.uiwidget.function.editface.bean.MultipleItemPair;
import io.agora.uiwidget.function.editface.color.ColorAdapter;
import io.agora.uiwidget.function.editface.color.ColorSelectView;
import io.agora.uiwidget.function.editface.item.MultipleItemAdapter;
import io.agora.uiwidget.function.editface.item.MultipleSelectView;

public class ColorMultipleItemTab extends Tab<ColorMultipleItemTab.ColorMultipleItemTabViewHolder>{

    private final List<MultipleItemInfo> itemList;
    private final Map<Integer, MultipleItemPair> itemTypePairMap;
    private final MultipleItemAdapter.ItemSelectListener itemSelectListener;

    private final double[][] colorList;
    private final int selectColor;
    private final ColorAdapter.ColorSelectListener colorSelectListener;


    public ColorMultipleItemTab(
            String title,
            List<MultipleItemInfo> itemList,
            Map<Integer, MultipleItemPair> itemTypePairMap,
            MultipleItemAdapter.ItemSelectListener itemSelectListener,

            double[][] colorList,
            int selectColor,
            ColorAdapter.ColorSelectListener colorSelectListener
    ) {
        super(title, VIEW_TYPE_COLOR_MULTIPLE_ITEM);
        this.itemList = itemList;
        this.itemTypePairMap = itemTypePairMap;
        this.itemSelectListener = itemSelectListener;

        this.colorList = colorList;
        this.selectColor = selectColor;
        this.colorSelectListener = colorSelectListener;
    }

    @Override
    public void onBindViewHolder(@NonNull ColorMultipleItemTabViewHolder holder, int position) {
        holder.colorSelectView.init(colorList, selectColor);
        holder.colorSelectView.setColorSelectListener(colorSelectListener);

        holder.multipleSelectView.init(itemList, itemTypePairMap, itemTypePairMap.keySet().size());
        holder.multipleSelectView.setItemControllerListener(new MultipleItemAdapter.ItemSelectListener() {
            @Override
            public void itemSelectListener(int type, int lastPos, boolean isSel, int position, int realPos) {
                MultipleItemInfo multipleItemInfo = itemList.get(position);
                MultipleItemPair multipleItemPair = itemTypePairMap.get(multipleItemInfo.type);
                if(multipleItemPair != null && multipleItemPair.getSelectColorPos() >= 0){
                    holder.colorLayout.setVisibility(View.VISIBLE);
                    holder.tvCheckName.setText(multipleItemInfo.name);
                    holder.colorSelectView.setColorItem(multipleItemPair.getSelectColorPos());
                }else{
                    holder.colorLayout.setVisibility(View.GONE);
                }
                if(itemSelectListener != null){
                    itemSelectListener.itemSelectListener(type, lastPos, isSel, position, realPos);
                }
            }
        });
    }

    public static class ColorMultipleItemTabViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvCheckName;
        private final ColorSelectView colorSelectView;
        private final MultipleSelectView multipleSelectView;
        private final LinearLayout colorLayout;

        public ColorMultipleItemTabViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_face_select_tab_make_up, parent, false));
            colorLayout = itemView.findViewById(R.id.color_layout);
            tvCheckName = itemView.findViewById(R.id.tv_check_name);
            colorSelectView = itemView.findViewById(R.id.color_recycler);
            multipleSelectView = itemView.findViewById(R.id.make_up_recycler);
        }
    }
}
