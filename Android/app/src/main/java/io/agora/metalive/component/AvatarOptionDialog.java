package io.agora.metalive.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import io.agora.metalive.databinding.AvatarOptionlLayoutBinding;
import io.agora.uiwidget.R;
import io.agora.uiwidget.basic.BindingViewHolder;
import io.agora.uiwidget.databinding.LiveToolItemBinding;
import io.agora.uiwidget.utils.StatusBarUtil;

public class AvatarOptionDialog extends BottomSheetDialog {
    public final static AvatarToolItem TOOL_ITEM_DRESS = new AvatarToolItem(R.string.avatar_option_name_dress, R.drawable.live_tool_icon_setting);
    public final static AvatarToolItem TOOL_ITEM_FACE_EDIT = new AvatarToolItem(R.string.avatar_option_name_face, R.drawable.live_tool_icon_setting);

    private final List<AvatarToolItem> showToolItems = new ArrayList<>();

    public AvatarOptionDialog(@NonNull Context context) {
        this(context, R.style.BottomSheetDialog, false);
    }

    public AvatarOptionDialog(@NonNull Context context, boolean dartText) {
        this(context, R.style.BottomSheetDialog, dartText);
    }

    public AvatarOptionDialog(@NonNull Context context, int theme, boolean dartText) {
        super(context, theme);
        init(dartText);
    }

    private void init(boolean dartText) {
        setCanceledOnTouchOutside(true);
        AvatarOptionlLayoutBinding mBinding = AvatarOptionlLayoutBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(mBinding.getRoot());
        StatusBarUtil.hideStatusBar(getWindow(), dartText);
        mBinding.liveToolRecycler.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mBinding.liveToolRecycler.setAdapter(new ToolsAdapter());
    }

    public AvatarOptionDialog addToolItem(AvatarToolItem item, boolean isActivated, OnItemClickListener listener) {
        showToolItems.add(new AvatarToolItem(item.nameRes, item.iconRes, isActivated, listener));
        return this;
    }

    private class ToolsAdapter extends RecyclerView.Adapter<BindingViewHolder<LiveToolItemBinding>> {

        @NonNull
        @Override
        public BindingViewHolder<LiveToolItemBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BindingViewHolder<>(LiveToolItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull BindingViewHolder<LiveToolItemBinding> holder, int position) {
            AvatarToolItem toolItem = showToolItems.get(position);
            holder.binding.liveToolItemIcon.setImageResource(toolItem.iconRes);
            holder.binding.liveToolItemName.setText(toolItem.nameRes);
            holder.binding.liveToolItemIcon.setActivated(toolItem.activated);
            holder.binding.liveToolItemIcon.setOnClickListener(v -> {
                toolItem.activated = !toolItem.activated;
                holder.binding.liveToolItemIcon.setActivated(toolItem.activated);
                if (toolItem.click != null) {
                    toolItem.click.onItemClicked(v, toolItem);
                }
            });
        }

        @Override
        public int getItemCount() {
            return showToolItems.size();
        }
    }

    public static class AvatarToolItem {
        private final int iconRes;
        private final int nameRes;
        private boolean activated;
        private final OnItemClickListener click;

        public AvatarToolItem(int nameRes, int iconRes) {
            this.iconRes = iconRes;
            this.nameRes = nameRes;
            this.activated = false;
            this.click = null;
        }

        private AvatarToolItem(int nameRes, int iconRes, boolean activated, OnItemClickListener click) {
            this.iconRes = iconRes;
            this.nameRes = nameRes;
            this.activated = activated;
            this.click = click;
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(View view, AvatarToolItem item);
    }
}
