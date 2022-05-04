package io.agora.uiwidget.function.editface.tabs;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.bean.ItemInfo;
import io.agora.uiwidget.function.editface.item.ItemAdapter;
import io.agora.uiwidget.function.editface.item.ItemSelectView;

public class ItemTab extends Tab<ItemTab.ItemTabViewHolder>{
    private final List<ItemInfo> itemList;
    private final int selectItem;
    private final ItemAdapter.ItemSelectListener itemSelectListener;
    private final ItemSelectView.ItemImageInterceptListener itemImageInterceptor;

    public ItemTab(
            String title,
            List<ItemInfo> itemList,
            int selectItem,
            ItemAdapter.ItemSelectListener itemSelectListener,
            ItemSelectView.ItemImageInterceptListener interceptor
    ) {
        super(title, VIEW_TYPE_ITEM);
        this.itemList = itemList;
        this.selectItem = selectItem;
        this.itemSelectListener = itemSelectListener;
        this.itemImageInterceptor = interceptor;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemTabViewHolder holder, int position) {
        holder.itemSelectView.setItemControllerListener(itemSelectListener);
        holder.itemSelectView.init(itemList, selectItem, itemImageInterceptor);
    }

    public static class ItemTabViewHolder extends RecyclerView.ViewHolder{
        private final ItemSelectView itemSelectView;

        public ItemTabViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_face_select_tab_item, parent, false));
            itemSelectView = itemView.findViewById(R.id.item_recycler);
        }
    }
}
