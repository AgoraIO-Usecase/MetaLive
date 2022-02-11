package io.agora.uiwidget.function.editface.tabs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.HashMap;

import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.bean.MultipleItemInfo;
import io.agora.uiwidget.function.editface.bean.PairBean;
import io.agora.uiwidget.function.editface.color.ColorSelectView;
import io.agora.uiwidget.function.editface.item.MultipleItemAdapter;
import io.agora.uiwidget.function.editface.item.MultipleSelectView;

public class MakeUpTab extends Tab<MakeUpTab.MakeUpTabViewHolder>{


    public MakeUpTab(String title) {
        super(title, VIEW_TYPE_MAKE_UP);
    }

    @Override
    public void onBindViewHolder(@NonNull MakeUpTabViewHolder holder, int position) {

    }

    public static class MakeUpTabViewHolder extends RecyclerView.ViewHolder{
        private TextView tvCheckName;
        private ColorSelectView colorSelectView;
        private MultipleSelectView multipleSelectView;

        public MakeUpTabViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_face_select_tab_make_up, parent, false));
            tvCheckName = itemView.findViewById(R.id.tv_check_name);
            colorSelectView = itemView.findViewById(R.id.color_recycler);
            multipleSelectView = itemView.findViewById(R.id.make_up_recycler);

            colorSelectView.setVisibility(View.VISIBLE);
            colorSelectView.init(new double[][]{
                    {0f, 0f, 0f},
                    {255f, 0f, 0f},
                    {0f, 255f, 0f},
                    {0f, 0f, 255f},
            }, 0);

            HashMap<Integer, PairBean> pairBeanMap = new HashMap<>();
            pairBeanMap.put(1, new PairBean(2, 0));
            pairBeanMap.put(2, new PairBean(2, 0));
            pairBeanMap.put(3, new PairBean(2, 0));
            multipleSelectView.init(Arrays.asList(
                    new MultipleItemInfo(R.drawable.user_profile_image_2, 1, "睫毛"),
                    new MultipleItemInfo(R.drawable.user_profile_image_1, 1, "睫毛"),
                    new MultipleItemInfo(R.drawable.user_profile_image_3, 2, "眼影"),
                    new MultipleItemInfo(R.drawable.user_profile_image_4, 2, "眼影"),
                    new MultipleItemInfo(R.drawable.user_profile_image_5, 3, "脸装"),
                    new MultipleItemInfo(R.drawable.user_profile_image_6, 3, "脸装")
            ), pairBeanMap, 3);
            multipleSelectView.setItemControllerListener(new MultipleItemAdapter.ItemSelectListener() {
                @Override
                public boolean itemSelectListener(int type, int lastPos, boolean isSel, int position, int realPos) {
                    return true;
                }
            });
        }
    }
}
