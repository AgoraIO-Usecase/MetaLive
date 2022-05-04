package io.agora.metalive.component;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import io.agora.metalive.databinding.AvatarDressToolLayoutBinding;
import io.agora.metalive.manager.AvatarConfigManager;
import io.agora.uiwidget.R;
import io.agora.uiwidget.function.editface.bean.ItemInfo;
import io.agora.uiwidget.function.editface.bean.UrlItemInfo;
import io.agora.uiwidget.function.editface.tabs.ItemTab;
import io.agora.uiwidget.utils.ImageUtil;
import io.agora.uiwidget.utils.StatusBarUtil;

public class AvatarDressDialog extends BottomSheetDialog {
    private AvatarDressToolLayoutBinding mBinding;

    private AvatarOptionDialogListener mListener;

    public AvatarDressDialog(@NonNull Context context) {
        this(context, R.style.BottomSheetDialog, false);
    }

    public AvatarDressDialog(@NonNull Context context, boolean dartText) {
        this(context, R.style.BottomSheetDialog, dartText);
    }

    public AvatarDressDialog(@NonNull Context context, int theme, boolean dartText) {
        super(context, theme);
        init(dartText);
    }

    private void init(boolean dartText) {
        setCanceledOnTouchOutside(true);
        mBinding = AvatarDressToolLayoutBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(mBinding.getRoot());
        StatusBarUtil.hideStatusBar(getWindow(), dartText);
        initTabs(getContext());
    }

    private void initTabs(Context context) {
        List<AvatarConfigManager.DressConfigItemSet> list =
                AvatarConfigManager.getInstance().getCurDressConfigSets();

        mBinding.avatarDressToolSelectView.setOnTabSelectListener(
                position -> {
                    if (mListener != null) {
                        AvatarConfigManager.DressConfigItemSet set = list.get(position);
                        if (set != null) {
                            mListener.onDressTypeChanged(set.id);
                        }
                    }
                }
        );

        for (AvatarConfigManager.DressConfigItemSet set : list) {
            int index = -1;
            int active = -1;

            List<ItemInfo> infoList = new ArrayList<>();
            for (AvatarConfigManager.DressConfigItem item : set.items) {
                index++;
                UrlItemInfo info = new UrlItemInfo(item.icon);
                infoList.add(info);

                if (item.isUsing == 1) {
                    active = index;
                }
            }
            mBinding.avatarDressToolSelectView.addTab(
                    new ItemTab(
                            set.name,
                            infoList,
                            active,
                            (lastPos, position) -> {
                                if (mListener != null) {
                                    AvatarConfigManager.DressConfigItem config = set.items.get(position);
                                    if (config != null) {
                                        mListener.onDressItemSelected(config.id);
                                    }
                                }
                                return true;
                            },
                            (imageView, position) -> {
                                String url = set.items.get(position).icon;
                                ImageUtil.setImageUrl(context, imageView, url);
                            }
                    )
            );
        }
    }

    public void setAvatarOptionDialogListener(AvatarOptionDialogListener listener) {
        this.mListener = listener;
    }
}
