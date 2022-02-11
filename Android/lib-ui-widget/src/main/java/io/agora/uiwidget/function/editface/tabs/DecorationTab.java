package io.agora.uiwidget.function.editface.tabs;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.HashMap;

import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.bean.MultipleItemInfo;
import io.agora.uiwidget.function.editface.bean.PairBean;
import io.agora.uiwidget.function.editface.item.MultipleSelectView;

public class DecorationTab extends Tab<DecorationTab.DecorationTabViewHolder>{


    public DecorationTab(String title) {
        super(title, VIEW_TYPE_DECORATION);
    }

    @Override
    public void onBindViewHolder(@NonNull DecorationTabViewHolder holder, int position) {

    }

    public static class DecorationTabViewHolder extends RecyclerView.ViewHolder{
        private MultipleSelectView multipleSelectView;

        public DecorationTabViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_face_select_tab_decoration, parent, false));
            multipleSelectView = itemView.findViewById(R.id.decoration_recycler);

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
        }

    }
}
