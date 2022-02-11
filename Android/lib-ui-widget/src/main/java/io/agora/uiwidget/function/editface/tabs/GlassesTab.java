package io.agora.uiwidget.function.editface.tabs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.CustomGlassSwitchView;
import io.agora.uiwidget.function.editface.bean.ItemInfo;
import io.agora.uiwidget.function.editface.color.ColorSelectView;
import io.agora.uiwidget.function.editface.item.ItemAdapter;
import io.agora.uiwidget.function.editface.item.ItemSelectView;

public class GlassesTab extends Tab<GlassesTab.GlassesTabViewHolder>{

    public GlassesTab(String title) {
        super(title, VIEW_TYPE_GLASSES);
    }

    @Override
    public void onBindViewHolder(@NonNull GlassesTabViewHolder holder, int position) {

    }

    public static class GlassesTabViewHolder extends RecyclerView.ViewHolder{
        private CustomGlassSwitchView glassSwitchView;
        private ColorSelectView colorSelectView;
        private ItemSelectView itemSelectView;

        public GlassesTabViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_face_select_tab_glasses, parent, false));
            glassSwitchView = itemView.findViewById(R.id.glass_color_switch);
            colorSelectView = itemView.findViewById(R.id.color_recycler);
            itemSelectView = itemView.findViewById(R.id.glasses_recycler);

            colorSelectView.setVisibility(View.VISIBLE);
            colorSelectView.init(new double[][]{
                    {0f, 0f, 0f},
                    {255f, 0f, 0f},
                    {0f, 255f, 0f},
                    {0f, 0f, 255f},
                    {0f, 0f, 255f},
                    {0f, 0f, 255f},
                    {0f, 0f, 255f},
                    {0f, 0f, 255f},
                    {0f, 0f, 255f},
                    {0f, 0f, 255f},
                    {0f, 0f, 255f},
                    {0f, 0f, 255f},
                    {0f, 0f, 255f},
                    {0f, 0f, 255f},
            }, 0);
            itemSelectView.setItemControllerListener(new ItemAdapter.ItemSelectListener() {
                @Override
                public boolean itemSelectListener(int lastPos, int position) {
                    return true;
                }
            });
            itemSelectView.init(Arrays.asList(
                    new ItemInfo(R.drawable.user_profile_image_2),
                    new ItemInfo(R.drawable.user_profile_image_2),
                    new ItemInfo(R.drawable.user_profile_image_2),
                    new ItemInfo(R.drawable.user_profile_image_2),
                    new ItemInfo(R.drawable.user_profile_image_2),
                    new ItemInfo(R.drawable.user_profile_image_2),
                    new ItemInfo(R.drawable.user_profile_image_2)
            ), 3);
            glassSwitchView.setCheckedChangeListener(new CustomGlassSwitchView.CheckedChangeListener() {
                @Override
                public void onCheckedChangeListener(boolean selectedLeft) {

                }
            });
        }
    }
}
