package io.agora.uiwidget.function.editface.item;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.bean.MultipleItemInfo;
import io.agora.uiwidget.function.editface.bean.MultipleItemPair;


public class MultipleItemAdapter extends RecyclerView.Adapter<MultipleItemAdapter.ItemHolder> {

    private final int totalType;
    private Context mContext;
    private List<MultipleItemInfo> itemList;
    private Map<Integer, MultipleItemPair> pairMap;

    private ItemSelectListener itemSelectListener;

    public MultipleItemAdapter(Context context, List<MultipleItemInfo> itemList, Map<Integer, MultipleItemPair> pairBeanMap, int totalType) {
        mContext = context;
        this.itemList = itemList;
        this.pairMap = pairBeanMap;
        this.totalType = totalType;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(mContext).inflate(R.layout.edit_face_select_item_multiple, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int pos) {
        final int position = holder.getLayoutPosition();
        holder.mItemImg.setImageResource(getRes(position));
        MultipleItemInfo makeUpBundleRes = itemList.get(position);
        MultipleItemPair multipleItemPair = pairMap.get(makeUpBundleRes.getType());

        boolean isSel = multipleItemPair != null && multipleItemPair.getSelectItemPos() == position;
        holder.mSelect.setVisibility(isSel ? View.VISIBLE : View.GONE);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.rl_item.getLayoutParams();
        layoutParams.width = isSel ?
                mContext.getResources().getDimensionPixelOffset(R.dimen.edit_face_select_item_content_inner_size) :
                mContext.getResources().getDimensionPixelOffset(R.dimen.edit_face_select_item_content_size);
        layoutParams.height = isSel ?
                mContext.getResources().getDimensionPixelOffset(R.dimen.edit_face_select_item_content_inner_size) :
                mContext.getResources().getDimensionPixelOffset(R.dimen.edit_face_select_item_content_size);
        holder.rl_item.setLayoutParams(layoutParams);
        holder.tv_type_name.setVisibility(position == 0 || TextUtils.isEmpty(makeUpBundleRes.getName()) ? View.GONE : View.VISIBLE);

        holder.tv_type_name.setText(makeUpBundleRes.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemSelectListener != null) {
                    itemSelectListener.itemSelectListener(makeUpBundleRes.getType(), 0, isPosSel(position), position, position - multipleItemPair.getFrontLength());
                    setSelectPosition(position);
                }
            }
        });
    }

    public int getRes(int pos) {
        return itemList.get(pos).resId;
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        ImageView mItemImg;
        View mSelect;
        TextView tv_type_name;
        RelativeLayout rl_item;

        public ItemHolder(View itemView) {
            super(itemView);
            mItemImg = itemView.findViewById(R.id.bottom_item_img);
            mSelect = itemView.findViewById(R.id.bottom_item_img_select);
            tv_type_name = itemView.findViewById(R.id.tv_type_name);
            rl_item = itemView.findViewById(R.id.rl_item);
        }
    }

    public void setSelectPosition(int selectPos) {
        if (selectPos > itemList.size() - 1) {
            return;
        }
        MultipleItemInfo makeUpBundleRes = itemList.get(selectPos);
        MultipleItemPair multipleItemPair = pairMap.get(makeUpBundleRes.getType());
        if(multipleItemPair == null){
            multipleItemPair = new MultipleItemPair(0, 0);
        }
        if (multipleItemPair.getSelectItemPos() == selectPos) {
            if (selectPos > 0) {
                multipleItemPair.setSelectItemPos(0);
                pairMap.put(makeUpBundleRes.getType(), multipleItemPair);
                notifyItemChanged(selectPos);
            }
            if (!hasSelectMakeUp()) {
                MultipleItemPair makeBean = pairMap.get(totalType);
                if(makeBean == null){
                    makeBean = new MultipleItemPair(0, 0);
                }
                if (makeBean.getSelectItemPos() != 0) {
                    makeBean.setSelectItemPos(0);
                    pairMap.put(totalType, makeBean);
                    notifyItemChanged(0);
                }
            }
            return;
        }
        int oldSelectId = multipleItemPair.getSelectItemPos();
        multipleItemPair.setSelectItemPos(selectPos);
        pairMap.put(makeUpBundleRes.getType(), multipleItemPair);
        notifyItemChanged(selectPos);
        if (selectPos == 0) {
            initData();
            notifyDataSetChanged();
            return;
        }
        if (oldSelectId > 0) {
            notifyItemChanged(oldSelectId);
        }
        if (hasSelectMakeUp()) {
            MultipleItemPair makeBean = pairMap.get(totalType);
            if(makeBean == null){
                makeBean = new MultipleItemPair(0, 0);
            }
            if (makeBean.getSelectItemPos() == 0) {
                makeBean.setSelectItemPos(-1);
                pairMap.put(totalType, makeBean);
                notifyItemChanged(0);
            }
        }
    }

    private boolean hasSelectMakeUp() {
        boolean hasSelect = false;
        for (Integer key : pairMap.keySet()) {
            MultipleItemPair multipleItemPair = pairMap.get(key);
            if (multipleItemPair != null && multipleItemPair.getSelectItemPos() > 0) {
                hasSelect = true;
                break;
            }
        }
        return hasSelect;
    }

    private void initData() {
        for (Integer key : pairMap.keySet()) {
            MultipleItemPair multipleItemPair = pairMap.get(key);
            if (multipleItemPair != null && multipleItemPair.getSelectItemPos() > 0) {
                multipleItemPair.setSelectItemPos(0);
                pairMap.put(key, multipleItemPair);
            }
        }
    }

    private boolean isPosSel(int pos) {
        MultipleItemInfo makeUpBundleRes = itemList.get(pos);
        MultipleItemPair multipleItemPair = pairMap.get(makeUpBundleRes.getType());
        return multipleItemPair != null && multipleItemPair.getSelectItemPos() != pos;
    }

    public int getLastPos(int pos) {
        MultipleItemInfo makeUpBundleRes = itemList.get(pos);
        MultipleItemPair multipleItemPair = pairMap.get(makeUpBundleRes.getType());
        if (multipleItemPair != null && multipleItemPair.getSelectItemPos() == pos) return -1;
        int oldSelectId = multipleItemPair != null ? multipleItemPair.getSelectItemPos() : -1;
        return oldSelectId;
    }

    public void setItemSelectListener(ItemSelectListener itemSelectListener) {
        this.itemSelectListener = itemSelectListener;
    }

    public interface ItemSelectListener {
        void itemSelectListener(int type, int lastPos, boolean isSel, int position, int realPos);
    }
}