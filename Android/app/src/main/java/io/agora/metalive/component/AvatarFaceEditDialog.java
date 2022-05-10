package io.agora.metalive.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import io.agora.metalive.databinding.FaceEditDialogLayoutBinding;
import io.agora.metalive.databinding.FaceEditListItemBinding;
import io.agora.metalive.manager.AvatarManager;
import io.agora.metalive.manager.DataListCallback;
import io.agora.uiwidget.R;
import io.agora.uiwidget.utils.StatusBarUtil;

public class AvatarFaceEditDialog extends BottomSheetDialog {
    private FaceEditDialogLayoutBinding mViewBindings;

    private AvatarManager.FaceEditConfigItem curFeItem;

    private List<AvatarManager.FaceEditConfigGroup> configList;

    public AvatarFaceEditDialog(@NonNull Context context) {
        this(context, R.style.BottomSheetDialog, true);
    }

    public AvatarFaceEditDialog(@NonNull Context context, boolean dartText) {
        this(context, R.style.BottomSheetDialog, dartText);
    }

    public AvatarFaceEditDialog(@NonNull Context context, int theme, boolean dartText) {
        super(context, theme);
        init(dartText);
    }

    private void init(boolean dartText) {
        setCanceledOnTouchOutside(true);
        mViewBindings = FaceEditDialogLayoutBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(mViewBindings.getRoot());
        StatusBarUtil.hideStatusBar(getWindow(), dartText);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AvatarManager.getInstance().startFaceEdit();
    }

    @Override
    public void show() {
        super.show();
        initTabs();
    }

    @Override
    protected void onStop() {
        super.onStop();
        AvatarManager.getInstance().stopFaceEdit();
    }

    private void initTabs() {
        TabLayout tabLayout = mViewBindings.faceEditDialogTabs;
        tabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        onFaceEditGroupTabSelected(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                }
        );

        mViewBindings.faceEditItemSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (curFeItem != null) {
                    float value = seekBar.getProgress() / 100f;
                    mViewBindings.faceEditItemValue.setText(value + "");
                    AvatarManager.getInstance().changeFaceEdit(curFeItem.id, value);
                }
            }
        });

        AvatarManager.getInstance().getCurFaceEditConfigsSafely(new DataListCallback<AvatarManager.FaceEditConfigGroup>() {
            @Override
            public void onSuccess(@NonNull List<AvatarManager.FaceEditConfigGroup> dataList) {
                if(!isShowing()){
                    return;
                }
                mViewBindings.progressBar.setVisibility(View.GONE);
                mViewBindings.contentLayout.setVisibility(View.VISIBLE);
                configList = dataList;
                if (configList.size() > 0) {
                    for (AvatarManager.FaceEditConfigGroup group : configList) {
                        addTab(group.name);
                    }

                    showTab(0);
                }
            }
        });
    }

    private void addTab(String title) {
        TabLayout tabLayout = mViewBindings.faceEditDialogTabs;
        TabLayout.Tab _tab = tabLayout.newTab();
        _tab.setText(title);
        tabLayout.addTab(_tab);
    }

    @SuppressLint("SetTextI18n")
    private void showTab(int position) {
        TabLayout tabLayout = mViewBindings.faceEditDialogTabs;
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        tabLayout.selectTab(tab);

        AvatarManager.FaceEditConfigGroup group = configList.get(position);
        if (group != null && group.items.size() > 0) {
            AvatarManager.FaceEditConfigItem item = group.items.get(0);
            refreshCurFaceEditItemInfo(group.name, item);
        }
    }

    @SuppressLint("SetTextI18n")
    private void onFaceEditGroupTabSelected(int position) {
        mViewBindings.faceEditProgressLayout.setVisibility(View.VISIBLE);
        mViewBindings.faceEditSubItemRecycler.setVisibility(View.VISIBLE);
        FaceEditItemAdapter adapter = new FaceEditItemAdapter(position);
        adapter.setSelected(0);
        mViewBindings.faceEditSubItemRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mViewBindings.faceEditSubItemRecycler.setAdapter(adapter);

        AvatarManager.FaceEditConfigGroup group = configList.get(position);
        if (group != null && group.items.size() > 0) {
            AvatarManager.FaceEditConfigItem item = group.items.get(0);

            refreshCurFaceEditItemInfo(group.name, item);
        }
    }

    @SuppressLint("SetTextI18n")
    private void refreshCurFaceEditItemInfo(String groupName, AvatarManager.FaceEditConfigItem item) {
        curFeItem = item;
        mViewBindings.faceEditItemValue.setText(item.value + "");
        mViewBindings.faceEditItemName.setText(groupName + item.name);
        mViewBindings.faceEditItemSeekBar.setProgress((int) (item.value * 100));
    }

    private class FaceEditItemAdapter extends RecyclerView.Adapter<FaceEditItemHolder> {
        private final int groupNo;
        private int selected = -1;

        private final int textColorDefault;
        private final int bgColorDefault;
        private final int textColorSelected;
        private final int bgColorSelected;

        FaceEditItemAdapter(int groupNo) {
            this.groupNo = groupNo;

            textColorDefault = Color.parseColor("#9e9e9e");
            textColorSelected = Color.parseColor("#5e5e5e");
            bgColorDefault = Color.TRANSPARENT;
            bgColorSelected = Color.parseColor("#efefef");
        }

        void setSelected(int position) {
            int lastPos = selected;
            selected = position;
            if (0 <= lastPos && lastPos < getItemCount()) {
                notifyItemChanged(lastPos);
            }
            notifyItemChanged(selected);
        }

        @NonNull
        @Override
        public FaceEditItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            FaceEditListItemBinding binding = FaceEditListItemBinding
                    .inflate(LayoutInflater.from(parent.getContext()));
            return new FaceEditItemHolder(binding, binding.getRoot());
        }

        @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
        @Override
        public void onBindViewHolder(@NonNull FaceEditItemHolder holder, int position) {
            AvatarManager.FaceEditConfigGroup group = configList.get(groupNo);
            if (group != null) {
                int pos = holder.getAdapterPosition();
                AvatarManager.FaceEditConfigItem config = group.items.get(pos);
                holder.title.setText(config.name);
                if (pos == selected) {
                    holder.itemView.setBackgroundColor(bgColorSelected);
                    holder.title.setTextColor(textColorSelected);
                } else {
                    holder.itemView.setBackgroundColor(bgColorDefault);
                    holder.title.setTextColor(textColorDefault);
                }

                holder.itemView.setOnClickListener(view -> {
                    selected = holder.getAdapterPosition();
                    refreshCurFaceEditItemInfo(group.name, config);
                    notifyDataSetChanged();
                });
            }
        }

        @Override
        public int getItemCount() {
            AvatarManager.FaceEditConfigGroup group = configList.get(groupNo);
            return group != null ? configList.get(groupNo).items.size() : 0;
        }
    }

    private static class FaceEditItemHolder extends RecyclerView.ViewHolder {
        AppCompatTextView title;

        public FaceEditItemHolder(@NonNull FaceEditListItemBinding binding,
                                  @NonNull View itemView) {
            super(itemView);
            title = binding.faceEditListItemText;
        }
    }

}
