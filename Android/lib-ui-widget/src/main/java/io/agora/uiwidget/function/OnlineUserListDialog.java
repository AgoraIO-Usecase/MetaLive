package io.agora.uiwidget.function;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import io.agora.uiwidget.R;
import io.agora.uiwidget.basic.BindingSingleAdapter;
import io.agora.uiwidget.basic.BindingViewHolder;
import io.agora.uiwidget.databinding.OnlineUserListDialogItemBinding;
import io.agora.uiwidget.databinding.OnlineUserListDialogLayoutBinding;
import io.agora.uiwidget.utils.StatusBarUtil;

public class OnlineUserListDialog extends BottomSheetDialog {
    private OnlineUserListDialogLayoutBinding mBinding;

    public OnlineUserListDialog(@NonNull Context context) {
        this(context, false);
    }

    public OnlineUserListDialog(@NonNull Context context, boolean darkText) {
        this(context, R.style.BottomSheetDialog, darkText);
    }

    public OnlineUserListDialog(@NonNull Context context, int theme, boolean darkText) {
        super(context, theme);
        init(darkText);
    }

    private void init(boolean darkText) {
        setCanceledOnTouchOutside(true);
        mBinding = OnlineUserListDialogLayoutBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(mBinding.getRoot());
        StatusBarUtil.hideStatusBar(getWindow(), darkText);

        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(
                getContext(), 1));
    }

    public OnlineUserListDialog setListAdapter(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        mBinding.recyclerView.setAdapter(adapter);
        return this;
    }

    public OnlineUserListDialog setListTitle(CharSequence title) {
        mBinding.tvTitle.setText(title);
        return this;
    }

    @Override
    public void show() {
        super.show();
    }

    public static abstract class DefaultListItemAdapter<T> extends AbsListItemAdapter<T, OnlineUserListDialogItemBinding> { }

    public static abstract class AbsListItemAdapter<T, B extends ViewBinding> extends BindingSingleAdapter<T, B> {

        @Override
        public final void onBindViewHolder(@NonNull BindingViewHolder<B> holder, int position) {
            T item = getItem(position);
            onItemUpdate(holder, position, item);
        }

        public AbsListItemAdapter<T, B> resetAll(List<T> list) {
            removeAll();
            insertAll(list);
            return this;
        }

        protected abstract void onItemUpdate(BindingViewHolder<B> holder, int position, T item);
    }
}
