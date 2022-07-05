package io.agora.uiwidget.function;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Locale;

import io.agora.uiwidget.R;
import io.agora.uiwidget.basic.BindingViewHolder;
import io.agora.uiwidget.databinding.VideoSettingDialogItemProgressBinding;
import io.agora.uiwidget.databinding.VideoSettingDialogItemTextBinding;
import io.agora.uiwidget.databinding.VideoSettingDialogLayoutBinding;
import io.agora.uiwidget.databinding.VideoSettingListItemTextOnlyBinding;
import io.agora.uiwidget.utils.StatusBarUtil;

public class VideoSettingDialog extends BottomSheetDialog {

    private VideoSettingDialogLayoutBinding mBinding;

    public VideoSettingDialog(@NonNull Context context) {
        this(context, true);
    }

    public VideoSettingDialog(@NonNull Context context, boolean darkText) {
        this(context, R.style.BottomSheetDialog, darkText);
    }

    public VideoSettingDialog(@NonNull Context context, int theme, boolean darkText) {
        super(context, theme);
        init(darkText);
    }

    private void init(boolean darkText) {
        setCanceledOnTouchOutside(true);
        mBinding = VideoSettingDialogLayoutBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(mBinding.getRoot());
        StatusBarUtil.hideStatusBar(getWindow(), darkText);
    }

    public VideoSettingDialog addProgressItem(String title, int min, int max, int defaultValue, String valueFormat, SeekBar.OnSeekBarChangeListener listener) {
        VideoSettingDialogItemProgressBinding progressBinding = VideoSettingDialogItemProgressBinding.inflate(LayoutInflater.from(getContext()), mBinding.itemContainer, true);
        progressBinding.tvTitle.setText(title);
        progressBinding.tvValue.setText(String.format(Locale.US, valueFormat, defaultValue));
        progressBinding.seekbar.setMax(max - min);
        progressBinding.seekbar.setProgress(defaultValue);
        progressBinding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = min + progress;
                progressBinding.tvValue.setText(String.format(Locale.US, valueFormat, value));

                if (listener != null) {
                    listener.onProgressChanged(seekBar, value, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (listener != null) {
                    listener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (listener != null) {
                    listener.onStopTrackingTouch(seekBar);
                }
            }
        });
        return this;
    }

    public VideoSettingDialog addTextItem(String title, List<CharSequence> options, int defaultOptionIndex, OnClickListener clickListener) {
        VideoSettingDialogItemTextBinding textBinding = VideoSettingDialogItemTextBinding.inflate(LayoutInflater.from(getContext()), mBinding.itemContainer, true);
        textBinding.tvTitle.setText(title);
        textBinding.tvValue.setText(options.get(defaultOptionIndex));
        textBinding.getRoot().setOnClickListener(v -> {
            View view = showNextRecycleView(options, (dialog, which) -> {
                mBinding.videoSettingBack.setVisibility(View.GONE);
                textBinding.tvValue.setText(options.get(which));
                if (clickListener != null) {
                    clickListener.onClick(VideoSettingDialog.this, which);
                }
            });
            mBinding.videoSettingBack.setVisibility(View.VISIBLE);
            mBinding.videoSettingBack.setOnClickListener(v1 -> {
                mBinding.videoSettingBack.setVisibility(View.GONE);
                mBinding.nextContainer.removeView(view);
            });
        });
        return this;
    }

    private View showNextRecycleView(List<CharSequence> options, OnClickListener clickListener) {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new RecyclerView.Adapter<BindingViewHolder<VideoSettingListItemTextOnlyBinding>>() {
            @NonNull
            @Override
            public BindingViewHolder<VideoSettingListItemTextOnlyBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new BindingViewHolder<>(VideoSettingListItemTextOnlyBinding.inflate(LayoutInflater.from(parent.getContext())));
            }

            @Override
            public void onBindViewHolder(@NonNull BindingViewHolder<VideoSettingListItemTextOnlyBinding> holder, int position) {
                CharSequence option = options.get(position);
                holder.binding.videoSettingItemText.setText(option);
                holder.binding.videoSettingItemText.setOnClickListener(v -> {
                    mBinding.nextContainer.removeView(recyclerView);
                    if (clickListener != null) {
                        clickListener.onClick(null, position);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return options.size();
            }
        });
        mBinding.nextContainer.addView(recyclerView);
        recyclerView.setBackgroundColor(Color.WHITE);
        recyclerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return recyclerView;
    }


}
