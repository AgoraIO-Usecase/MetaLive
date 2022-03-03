package io.agora.uiwidget.function;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import io.agora.uiwidget.R;
import io.agora.uiwidget.databinding.TextInputDialogLayoutBinding;
import io.agora.uiwidget.utils.StatusBarUtil;

public class TextInputDialog extends BottomSheetDialog {

    private TextInputDialogLayoutBinding mBinding;
    private OnSendClickListener onSendClickListener;

    public TextInputDialog(@NonNull Context context) {
        this(context, false);
    }

    public TextInputDialog(@NonNull Context context, boolean dartText) {
        this(context, R.style.BottomSheetDialog, dartText);
    }

    public TextInputDialog(@NonNull Context context, int theme, boolean dartText) {
        super(context, theme);
        init(dartText);
    }

    private void init(boolean dartText){
        setCanceledOnTouchOutside(true);
        mBinding = TextInputDialogLayoutBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(mBinding.getRoot());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        StatusBarUtil.hideStatusBar(getWindow(), dartText);

        mBinding.editText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEND){
                if(onSendClickListener != null && !TextUtils.isEmpty(v.getText())){
                    onSendClickListener.onSendClicked(TextInputDialog.this, v.getText().toString());
                }
                return true;
            }
            return false;
        });
        setOnShowListener(dialog -> mBinding.editText.requestFocus());
    }

    public TextInputDialog setOnSendClickListener(OnSendClickListener listener){
        onSendClickListener = listener;
        return this;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

    public interface OnSendClickListener {
        void onSendClicked(Dialog dialog, String text);
    }
}
