package io.agora.uiwidget.function.editface.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.agora.uiwidget.R;

public abstract class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

    private Context mContext;
    private int mLayoutId;

    protected int mSelectPosition = -1;

    private ItemSelectListener itemSelectListener;

    public ItemAdapter(Context context, @LayoutRes int layoutId) {
        mContext = context;
        mLayoutId = layoutId;
    }

    public ItemAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(mContext).inflate(mLayoutId == 0 ? R.layout.edit_face_select_item : mLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int pos) {
        final int position = holder.getLayoutPosition();
        holder.mItemImg.setImageResource(getRes(position));
        holder.mSelect.setVisibility(mSelectPosition == position ? View.VISIBLE : View.GONE);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.mItemImg.getLayoutParams();
        layoutParams.width = mSelectPosition == position ?
                mContext.getResources().getDimensionPixelOffset(R.dimen.edit_face_select_item_content_inner_size) :
                mContext.getResources().getDimensionPixelOffset(R.dimen.edit_face_select_item_content_size);
        layoutParams.height = mSelectPosition == position ?
                mContext.getResources().getDimensionPixelOffset(R.dimen.edit_face_select_item_content_inner_size) :
                mContext.getResources().getDimensionPixelOffset(R.dimen.edit_face_select_item_content_size);
        holder.mItemImg.setLayoutParams(layoutParams);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemSelectListener != null && itemSelectListener.itemSelectListener(mSelectPosition, position)) {
                    setSelectPosition(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return getSize();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        ImageView mItemImg;
        View mSelect;

        public ItemHolder(View itemView) {
            super(itemView);
            mItemImg = itemView.findViewById(R.id.bottom_item_img);
            mSelect = itemView.findViewById(R.id.bottom_item_img_select);
        }
    }

    public void setSelectPosition(int selectPos) {
        if (mSelectPosition == selectPos) return;
        int oldSelectId = mSelectPosition;
        mSelectPosition = selectPos;
        notifyItemChanged(mSelectPosition);
        notifyItemChanged(oldSelectId);
    }

    public void setItemSelectListener(ItemSelectListener itemSelectListener) {
        this.itemSelectListener = itemSelectListener;
    }

    public interface ItemSelectListener {

        boolean itemSelectListener(int lastPos, int position);
    }

    public abstract int getRes(int pos);

    public abstract int getSize();

}