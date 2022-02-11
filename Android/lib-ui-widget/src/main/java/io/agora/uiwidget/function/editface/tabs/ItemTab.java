package io.agora.uiwidget.function.editface.tabs;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.bean.ItemInfo;
import io.agora.uiwidget.function.editface.item.ItemAdapter;
import io.agora.uiwidget.function.editface.item.ItemSelectView;

public class ItemTab extends Tab<ItemTab.ItemTabViewHolder>{


    public ItemTab(String title) {
        super(title, VIEW_TYPE_ITEM);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemTabViewHolder holder, int position) {

    }

    public static class ItemTabViewHolder extends RecyclerView.ViewHolder{
        private ItemSelectView itemSelectView;

        public ItemTabViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_face_select_tab_item, parent, false));
            itemSelectView = itemView.findViewById(R.id.item_recycler);

            itemSelectView.setItemControllerListener(new ItemAdapter.ItemSelectListener() {
                @Override
                public boolean itemSelectListener(int lastPos, int position) {
                    return true;
                }
            });
            itemSelectView.init(Arrays.asList(
                    new ItemInfo(R.drawable.user_profile_image_2),
                    new ItemInfo(R.drawable.user_profile_image_1)
            ), 0);
        }
    }
}
