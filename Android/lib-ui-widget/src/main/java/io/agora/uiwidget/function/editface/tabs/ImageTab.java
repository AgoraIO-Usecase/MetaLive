package io.agora.uiwidget.function.editface.tabs;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.OnTabItemSelectedListener;
import io.agora.uiwidget.utils.UIUtil;

public class ImageTab extends Tab<ImageTab.ImageTabViewHolder> {

    private int drawable01;
    private int drawable02;
    private OnTabItemSelectedListener listener;
    private int selectedPosition = 0;

    public ImageTab(String title, int drawable01, int drawable02, OnTabItemSelectedListener listener) {
        super(title, VIEW_TYPE_IMAGE);
        this.drawable01 = drawable01;
        this.drawable02 = drawable02;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageTabViewHolder holder, int position) {
        holder.setImageDrawable(drawable01, drawable02);
        holder.setSelected(selectedPosition);
        holder.setOnTabItemSelectedListener(position1 -> {
            selectedPosition = position1;
            if (listener != null) {
                listener.onTabItemSelected(position1);
            }
        });
    }

    public static class ImageTabViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iv01;
        private final ImageView iv02;

        private OnTabItemSelectedListener listener;

        public ImageTabViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_face_select_tab_image, parent, false));
            iv01 = itemView.findViewById(R.id.iv01);
            iv02 = itemView.findViewById(R.id.iv02);
            iv01.setOnClickListener(v -> {
                setSelected(0);
                if (listener != null) {
                    listener.onTabItemSelected(0);
                }
            });
            iv02.setOnClickListener(v -> {
                setSelected(1);
                if (listener != null) {
                    listener.onTabItemSelected(1);
                }
            });
        }

        public void setImageDrawable(int ivDrawable01, int ivDrawable02) {
            iv01.setImageDrawable(UIUtil.getRoundDrawable(itemView.getContext(), ivDrawable01, itemView.getContext().getResources().getDimension(R.dimen.edit_face_select_item_corner_radius_inner)));
            iv02.setImageDrawable(UIUtil.getRoundDrawable(itemView.getContext(), ivDrawable02, itemView.getContext().getResources().getDimension(R.dimen.edit_face_select_item_corner_radius_inner)));
        }

        public void setSelected(int position) {
            if (position == 0) {
                iv01.setBackgroundResource(R.drawable.edit_face_select_selected_bg);
                iv02.setBackground(null);
            } else {
                iv02.setBackgroundResource(R.drawable.edit_face_select_selected_bg);
                iv01.setBackground(null);
            }
        }

        public void setOnTabItemSelectedListener(OnTabItemSelectedListener listener) {
            this.listener = listener;
        }

    }

}
