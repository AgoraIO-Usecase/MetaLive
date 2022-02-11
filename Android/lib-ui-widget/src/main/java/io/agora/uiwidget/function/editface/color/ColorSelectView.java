package io.agora.uiwidget.function.editface.color;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import io.agora.uiwidget.R;

public class ColorSelectView extends RecyclerView {
    public static final String TAG = ColorSelectView.class.getSimpleName();

    private LinearLayoutManager mLinearLayoutManager;
    private ColorAdapter mColorAdapter;
    private ColorAdapter.ColorSelectListener mColorSelectListener;

    private double[][] mColorList;
    private int mDefaultSelectColor;

    public ColorSelectView(@NonNull Context context) {
        this(context, null);
    }

    public ColorSelectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorSelectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(double[][] colorList, int defaultSelectColor) {
        mColorAdapter = new ColorAdapter(getContext(), this.mColorList = colorList);
        mColorAdapter.setSelectPosition(this.mDefaultSelectColor = defaultSelectColor);

        setLayoutManager(mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        setAdapter(mColorAdapter);

        final int l30 = getResources().getDimensionPixelSize(R.dimen.edit_face_select_color_space);
        addItemDecoration(new ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                int size = mColorList.length;
                int index = parent.getChildAdapterPosition(view);
                int left = l30;
                int right = index == size - 1 ? l30 : 0;
                outRect.set(left, 0, right, 0);
            }
        });

        ((SimpleItemAnimator) getItemAnimator()).setSupportsChangeAnimations(false);
        mColorAdapter.setColorSelectListener(new ColorAdapter.ColorSelectListener() {
            @Override
            public void colorSelectListener(int position) {
                if (mColorSelectListener != null) {
                    mColorSelectListener.colorSelectListener(position);
                }
                scrollToPosition(position);
            }
        });

        scrollToPosition(mDefaultSelectColor);
    }

    public void setColorList(double[][] colorList, int selectColorPosition) {
        mColorAdapter.setmColorList(this.mColorList = colorList);
        setColorItem(selectColorPosition);
        mColorAdapter.notifyDataSetChanged();
    }

    public void scrollToPosition(final int pos) {
        post(new Runnable() {
            @Override
            public void run() {
                final int l30 = getResources().getDimensionPixelSize(R.dimen.edit_face_select_color_space);
                final int itemW = getResources().getDimensionPixelOffset(R.dimen.edit_face_select_color_width);
                final int first = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (first < 0) return;
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int dx = (int) ((0.5 + pos) * (itemW + l30) - screenWidth / 2
                        - (first * (itemW + l30) - mLinearLayoutManager.findViewByPosition(first).getLeft()));
                smoothScrollBy(dx, 0);
            }
        });
    }

    public void setColorItem(int position) {
        mColorAdapter.setSelectPosition(position);
        mColorAdapter.notifyItemChanged(position);
        mColorAdapter.notifyItemChanged(this.mDefaultSelectColor);
        this.mDefaultSelectColor = position;
        scrollToPosition(position);
    }

    public void setColorSelectListener(ColorAdapter.ColorSelectListener colorSelectListener) {
        mColorSelectListener = colorSelectListener;
    }
}