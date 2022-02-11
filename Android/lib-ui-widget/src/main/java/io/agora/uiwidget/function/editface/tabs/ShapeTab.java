package io.agora.uiwidget.function.editface.tabs;

import android.graphics.Color;
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
import io.agora.uiwidget.function.editface.seekbar.DiscreteSeekBar;

public class ShapeTab extends Tab<ShapeTab.ShapeTabViewHolder>{


    public ShapeTab(String title) {
        super(title, VIEW_TYPE_SHAPE);
    }

    @Override
    public void onBindViewHolder(@NonNull ShapeTabViewHolder holder, int position) {

    }

    public static class ShapeTabViewHolder extends RecyclerView.ViewHolder {
        private DiscreteSeekBar colorSeekBar;
        private ColorSelectView colorSelectView;
        private ItemSelectView itemSelectView;


        public ShapeTabViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_face_select_tab_shape, parent, false));
            colorSeekBar = itemView.findViewById(R.id.color_seek_bar);
            colorSelectView = itemView.findViewById(R.id.color_recycler);
            itemSelectView = itemView.findViewById(R.id.shape_item_recycler);

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

            colorSeekBar.setTrackColor(new int[]{
                    Color.parseColor("#FFFF0000"),
                    Color.parseColor("#FF00FF00"),
                    Color.parseColor("#FF0000FF"),
            });
            colorSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                @Override
                public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

                }

                @Override
                public void onDown() {

                }

                @Override
                public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

                }
            });
        }


    }
}
