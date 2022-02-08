package io.agora.example.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;


/**
 * On Jetpack navigation
 * Fragments enter/exit represent onCreateView/onDestroyView
 * Thus we should detach all reference to the VIEW on onDestroyView
 */
public abstract class BaseFragment<B extends ViewBinding> extends Fragment {
    public B mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = getViewBindingByReflect(inflater, container);
        if (mBinding == null)
            return null;
        return mBinding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    public void showLoading() {
        getParentActivity().showLoading(true);
    }

    public void showLoading(boolean cancelable) {
        getParentActivity().showLoading(cancelable);
    }

    public void dismissLoading() {
        getParentActivity().dismissLoading();
    }

    @NonNull
    public BaseActivity<?> getParentActivity() {
        return (BaseActivity<?>) requireActivity();
    }


    private B getViewBindingByReflect(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        try {
            Class<B> c = BaseUtil.getGenericClass(getClass(), 0);
            return (B) BaseUtil.getViewBinding(c, inflater, container);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected final void setStatusBarStyle(boolean isLight) {
        Window window = getParentActivity().getWindow();
        if (isLight) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


}