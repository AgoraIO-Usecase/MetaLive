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

import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.bean.ItemInfo;

public class ItemSelectView extends RecyclerView {
    public static final String TAG = ItemSelectView.class.getSimpleName();

    private static final int spanCount = 5;

    private ItemAdapter mItemAdapter;
    private GridLayoutManager mGridLayoutManager;
    private ItemAdapter.ItemSelectListener mItemSelectListener;

    private int size;
    private int mDefaultSelectItem;
    private ItemDecoration mItemDecoration;

    public ItemSelectView(@NonNull Context context) {
        this(context, null);
    }

    public ItemSelectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemSelectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public <T extends ItemInfo> void init(final List<T> itemList, int defaultSelectItem) {
        size = itemList.size();
        mItemAdapter = new ItemAdapter(getContext()) {
            @Override
            public int getRes(int pos) {
                return itemList.get(pos).resId;
            }

            @Override
            public int getSize() {
                return size;
            }
        };
        mItemAdapter.setSelectPosition(this.mDefaultSelectItem = defaultSelectItem);

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
        mItemAdapter.setItemSelectListener(new ItemAdapter.ItemSelectListener() {
            @Override
            public boolean itemSelectListener(int lastPos, int position) {
                scrollToPosition(position);
                return mItemSelectListener != null && mItemSelectListener.itemSelectListener(lastPos, position);
            }
        });

        scrollToPosition(mDefaultSelectItem);
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

    public void setSelectPosition(int selectPos) {
        mItemAdapter.setSelectPosition(selectPos);
    }

    public void setItemControllerListener(ItemAdapter.ItemSelectListener itemSelectListener) {
        mItemSelectListener = itemSelectListener;
    }

    public void setItem(int position) {
        mItemAdapter.setSelectPosition(position);
    }

    public int getSelectItem() {
        return mItemAdapter.mSelectPosition;
    }
}
