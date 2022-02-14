package io.agora.uiwidget.function.editface.tabs;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.bean.MultipleItemInfo;
import io.agora.uiwidget.function.editface.bean.MultipleItemPair;
import io.agora.uiwidget.function.editface.item.MultipleItemAdapter;
import io.agora.uiwidget.function.editface.item.MultipleSelectView;

public class MultipleItemTab extends Tab<MultipleItemTab.DecorationTabViewHolder>{

    private final List<MultipleItemInfo> itemList;
    private final Map<Integer, MultipleItemPair> itemTypePairMap;
    private final MultipleItemAdapter.ItemSelectListener itemSelectListener;

    public MultipleItemTab(
            String title,
            List<MultipleItemInfo> itemList,
            Map<Integer, MultipleItemPair> itemTypePairMap,
            MultipleItemAdapter.ItemSelectListener itemSelectListener
    ) {
        super(title, VIEW_TYPE_DECORATION);
        this.itemList = itemList;
        this.itemTypePairMap = itemTypePairMap;
        this.itemSelectListener = itemSelectListener;
    }

    @Override
    public void onBindViewHolder(@NonNull DecorationTabViewHolder holder, int position) {
        holder.multipleSelectView.init(itemList, itemTypePairMap, itemTypePairMap.keySet().size());
        holder.multipleSelectView.setItemControllerListener(itemSelectListener);
    }

    public static class DecorationTabViewHolder extends RecyclerView.ViewHolder{
        private final MultipleSelectView multipleSelectView;

        public DecorationTabViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_face_select_tab_decoration, parent, false));
            multipleSelectView = itemView.findViewById(R.id.decoration_recycler);
        }
    }
}
