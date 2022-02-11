package io.agora.uiwidget.function.editface.tabs;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.bean.ItemInfo;
import io.agora.uiwidget.function.editface.color.ColorSelectView;
import io.agora.uiwidget.function.editface.item.ItemAdapter;
import io.agora.uiwidget.function.editface.item.ItemSelectView;

public class ColorItemTab extends Tab<ColorItemTab.ColorItemTabViewHolder> {

    public ColorItemTab(String title) {
        super(title, VIEW_TYPE_COLOR_ITEM);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorItemTab.ColorItemTabViewHolder holder, int position) {

    }

    public static class ColorItemTabViewHolder extends RecyclerView.ViewHolder {

        private ColorSelectView colorSelectView;
        private ItemSelectView itemSelectView;

        public ColorItemTabViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_face_select_tab_color_item, parent, false));
            colorSelectView = itemView.findViewById(R.id.color_recycler);
            itemSelectView = itemView.findViewById(R.id.item_select_view);
            colorSelectView.init(new double[][]{
                    {0f, 0f, 0f},
                    {255f, 0f, 0f},
                    {0f, 255f, 0f},
                    {0f, 0f, 255f},
            }, 0);
            itemSelectView.setItemControllerListener(new ItemAdapter.ItemSelectListener() {
                @Override
                public boolean itemSelectListener(int lastPos, int position) {
                    return true;
                }
            });
            itemSelectView.init(
                    Arrays.asList(
                            new ItemInfo(R.drawable.user_profile_image_1),
                            new ItemInfo(R.drawable.user_profile_image_2)
                    ),
                    0);
        }
    }
}
