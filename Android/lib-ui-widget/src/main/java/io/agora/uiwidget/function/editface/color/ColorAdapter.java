package io.agora.uiwidget.function.editface.color;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.CircleImageView;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorHolder> {

    private Context mContext;
    private double[][] mColorList;
    private int[] mOtherResList;
    private int mOtherCount;
    private int mSelectPosition = 0;

    private ColorSelectListener mColorSelectListener;

    public ColorAdapter(@NonNull Context context, @NonNull double[][] colorList) {
        this.mContext = context;
        this.mColorList = colorList;
        mOtherCount = 0;
    }

    public ColorAdapter(@NonNull Context context, @NonNull double[][] colorList, @NonNull int[] otherResList) {
        this.mContext = context;
        this.mColorList = colorList;
        this.mOtherResList = otherResList;
        mOtherCount = otherResList.length;
    }

    @Override
    public ColorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ColorHolder(LayoutInflater.from(mContext).inflate(R.layout.edit_face_select_color, parent, false));
    }

    @Override
    public void onBindViewHolder(ColorHolder holder, int pos) {
        final int position = holder.getLayoutPosition();
        if (mOtherCount > position) {
            holder.mItemImg.setImageResource(mOtherResList[position]);
            holder.mSelect.setVisibility(View.GONE);
        } else {
            double[] rgb = mColorList[position - mOtherCount];
            holder.mItemImg.setImageDrawable(new ColorDrawable(Color.argb(255, (int) rgb[0], (int) rgb[1], (int) rgb[2])));
            holder.mSelect.setVisibility(mSelectPosition == position ? View.VISIBLE : View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectPosition == position) return;
                if (position >= mOtherCount) {
                    setSelectPosition(position);
                }
                if (mColorSelectListener != null) {
                    mColorSelectListener.colorSelectListener(position);
                }

            }
        });
    }

    public void setSelectPosition(int selectPos) {
        if (mSelectPosition == selectPos) return;
        int oldSelectId = mSelectPosition;
        mSelectPosition = selectPos;
        notifyItemChanged(mSelectPosition);
        notifyItemChanged(oldSelectId);
    }

    @Override
    public int getItemCount() {
        return mColorList.length + mOtherCount;
    }

    class ColorHolder extends RecyclerView.ViewHolder {

        CircleImageView mItemImg;
        View mSelect;

        public ColorHolder(View itemView) {
            super(itemView);
            mItemImg = itemView.findViewById(R.id.bottom_item_img);
            mSelect = itemView.findViewById(R.id.bottom_item_img_select);
        }
    }

    public void setColorSelectListener(ColorSelectListener colorSelectListener) {
        this.mColorSelectListener = colorSelectListener;
    }

    public interface ColorSelectListener {

        void colorSelectListener(int position);
    }

    public void setmColorList(double[][] mColorList) {
        this.mColorList = mColorList;
    }
}
