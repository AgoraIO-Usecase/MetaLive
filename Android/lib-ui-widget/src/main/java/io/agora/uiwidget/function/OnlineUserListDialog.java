package io.agora.uiwidget.function;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import io.agora.uiwidget.R;
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

        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));
    }

    public OnlineUserListDialog setListAdapter(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        mBinding.recyclerView.setAdapter(adapter);
        return this;
    }

    @Override
    public void show() {
        super.show();
    }

    public static abstract class DefaultListItemAdapter<T> extends AbsListItemAdapter<T, OnlineUserListDialogItemBinding> {
        @Override
        protected OnlineUserListDialogItemBinding onCreateViewBinding(LayoutInflater inflater, ViewGroup parent) {
            return OnlineUserListDialogItemBinding.inflate(inflater, parent, false);
        }
    }

    public static abstract class AbsListItemAdapter<T, B extends ViewBinding> extends RecyclerView.Adapter<BindingViewHolder<B>> {
        private final List<T> mList = new ArrayList<>();

        @NonNull
        @Override
        public final BindingViewHolder<B> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BindingViewHolder<B>(onCreateViewBinding(LayoutInflater.from(parent.getContext()), parent));
        }

        @Override
        public final void onBindViewHolder(@NonNull BindingViewHolder<B> holder, int position) {
            T item = mList.get(position);
            onItemUpdate(holder, position, item);
        }

        @Override
        public final int getItemCount() {
            return mList.size();
        }

        public AbsListItemAdapter<T, B> resetAll(List<T> list) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
            return this;
        }

        protected abstract B onCreateViewBinding(LayoutInflater inflater, ViewGroup parent);

        protected abstract void onItemUpdate(BindingViewHolder<B> holder, int position, T item);
    }
}
