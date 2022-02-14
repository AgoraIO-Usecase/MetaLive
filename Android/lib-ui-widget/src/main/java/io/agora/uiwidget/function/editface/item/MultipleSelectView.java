package io.agora.uiwidget.function.editface.item;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.List;
import java.util.Map;

import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.bean.MultipleItemInfo;
import io.agora.uiwidget.function.editface.bean.MultipleItemPair;

public class MultipleSelectView extends RecyclerView {
    public static final String TAG = MultipleSelectView.class.getSimpleName();

    private static final int spanCount = 5;

    private MultipleItemAdapter mItemAdapter;
    private GridLayoutManager mGridLayoutManager;
    private MultipleItemAdapter.ItemSelectListener mItemSelectListener;
    private ItemDecoration mItemDecoration;

    public MultipleSelectView(@NonNull Context context) {
        this(context, null);
    }

    public MultipleSelectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleSelectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(List<MultipleItemInfo> itemList, Map<Integer, MultipleItemPair> pairBeanMap, int totalType) {
        mItemAdapter = new MultipleItemAdapter(getContext(), itemList, pairBeanMap, totalType);
        init();
    }

    private void init() {
        setLayoutManager(mGridLayoutManager = new GridLayoutManager(getContext(), spanCount, GridLayoutManager.VERTICAL, false));
        setAdapter(mItemAdapter);
        final int wL = getResources().getDimensionPixelSize(R.dimen.edit_face_select_item_space_h);
        final int hL = getResources().getDimensionPixelSize(R.dimen.edit_face_select_item_space_v);
        final int topNormalL = getResources().getDimensionPixelSize(R.dimen.edit_face_select_item_space_h);
        if(mItemDecoration != null){
            removeItemDecoration(mItemDecoration);
        }
        mItemDecoration = new ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                int index = parent.getChildAdapterPosition(view);
                int left = wL;
                int right = wL;
                int top = index < spanCount ? hL : topNormalL;
                int bottom = index < spanCount ? 0 : topNormalL;
                outRect.set(left, top, right, bottom);
            }
        };
        addItemDecoration(mItemDecoration);

        ((SimpleItemAnimator) getItemAnimator()).setSupportsChangeAnimations(false);
        mItemAdapter.setItemSelectListener(new MultipleItemAdapter.ItemSelectListener() {
            @Override
            public void itemSelectListener(int type, int lastPos, boolean isSel, int position, int realPos) {
                scrollToPosition(position);
                if (mItemSelectListener != null) {
                    mItemSelectListener.itemSelectListener(type, lastPos, isSel, position, realPos);
                }
            }
        });
    }

    public void scrollToPosition(final int pos) {
        post(new Runnable() {
            @Override
            public void run() {
                final int topNormalL = getResources().getDimensionPixelSize(R.dimen.edit_face_select_item_space_h);
                final int itemW = getResources().getDimensionPixelOffset(R.dimen.edit_face_select_item_content_size);
                final int first = mGridLayoutManager.findFirstVisibleItemPosition();
                if (first < 0) return;
                int dy = (int) ((0.5 + pos / spanCount) * (itemW + topNormalL) - getHeight() / 2
                        - (first / spanCount * (itemW + topNormalL) - mGridLayoutManager.findViewByPosition(first).getTop()));
                smoothScrollBy(0, dy);
            }
        });
    }

    public void setItemControllerListener(MultipleItemAdapter.ItemSelectListener itemSelectListener) {
        mItemSelectListener = itemSelectListener;
    }

    public void setItem(int position) {
        mItemAdapter.setSelectPosition(position);
    }
}
