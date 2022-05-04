package io.agora.metalive.component;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.lang.ref.WeakReference;

import io.agora.metalive.manager.AvatarConfigManager;
import io.agora.metalive.manager.RtcManager;

public class AvatarOptionDialogUtil implements LifecycleObserver {
    private static final String TAG = "AvatarDialog";

    private AvatarOptionDialog optionDialog;
    private AvatarDressDialog dressDialog;
    private AvatarFaceEditDialog faceDialog;

    private WeakReference<AppCompatActivity> activityWeak;

    private final AvatarOptionDialogListener mListener = new AvatarOptionDialogListener() {
        @Override
        public void onDressOptionDialogToggled(boolean showing) {
            int result;
            if (showing) {
                result = RtcManager.getInstance().startDressing();
            } else {
                result = RtcManager.getInstance().stopDressing();
            }

            Log.e(TAG, "onDressOptionDialogToggled, showing:"
                    + showing + ", result " + result);
        }

        @Override
        public void onFaceEditOptionDialogToggled(boolean showing) {
            int result;
            if (showing) {
                result = RtcManager.getInstance().startFaceEdit();
            } else {
                result = RtcManager.getInstance().stopFaceEdit();
            }
            Log.e(TAG, "onFaceEditOptionDialogToggled, showing:"
                    + showing + ", result " + result);
        }

        @Override
        public void onDressTypeChanged(String type) {
            Log.e(TAG, "onDressTypeChanged " + type);
            String format = "{\"type\":\"%s\"}";
            String value = String.format(format, type);
            int result = RtcManager.getInstance().setLocalAvatarOption(
                    AvatarConfigManager.AvatarConfig.DRESS_KEY_SHOW_VIEW, value);
            Log.e(TAG, "onDressTypeChanged " + type + ", result: " + result);
        }

        @Override
        public void onDressItemSelected(String id) {
            String format = "{\"id\":\"%s\"}";
            String value = String.format(format, id);
            int result = RtcManager.getInstance().setLocalAvatarOption(
                    AvatarConfigManager.AvatarConfig.DRESS_KEY_PUT_ON, value);
            Log.e(TAG, "onDressItemSelected " + id + ", result: " + result);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onFaceEditChanged(String id, float value) {
            String format = "{\"%s\":%.2f}";
            String valueString = String.format(format, id, value);
            int result = RtcManager.getInstance().setLocalAvatarOption(
                    AvatarConfigManager.AvatarConfig.FACE_EDIT_KEY_SEND, valueString);
            if (result == 0) {
                AvatarConfigManager.getInstance().refreshFaceEditConfig(id, value);
            }
            Log.e(TAG, "onFaceEditChanged " + id + ", " + valueString + ", result: " + result);
        }
    };

    public synchronized void show(AppCompatActivity activity) {
        if (optionDialog != null && optionDialog.isShowing() ||
            dressDialog != null && dressDialog.isShowing() ||
            faceDialog != null && faceDialog.isShowing()) {
            return;
        }

        activityWeak = new WeakReference<>(activity);
        if (activityWeak.get() != null) {
            activityWeak.get().getLifecycle().addObserver(this);
        }

        runOnUiThread(() -> {
            optionDialog = new AvatarOptionDialog(activity);
            optionDialog.setOnDismissListener(dialog -> optionDialog = null);
            optionDialog.addToolItem(AvatarOptionDialog.TOOL_ITEM_DRESS, true,
                    (view, item) -> {
                        optionDialog.dismiss();
                        showDressDialog();
                    });
            optionDialog.addToolItem(AvatarOptionDialog.TOOL_ITEM_FACE_EDIT, true,
                    (view, item) -> {
                        optionDialog.dismiss();
                        showFaceEditDialog();
                    });
            optionDialog.show();
        });
    }

    private void showDressDialog() {
        if (dressDialog != null && dressDialog.isShowing()) {
            return;
        }

        runOnUiThread(() -> {
            AppCompatActivity activity = getActivityRef();
            if (activity != null) {
                dressDialog = new AvatarDressDialog(activity);
                dressDialog.setAvatarOptionDialogListener(mListener);
                dressDialog.setOnShowListener(dialogInterface -> {
                    mListener.onDressOptionDialogToggled(true);
                });
                dressDialog.setOnDismissListener(dialog -> {
                    mListener.onDressOptionDialogToggled(false);
                    dressDialog = null;
                    show(activity);
                });
                dressDialog.show();
            }
        });
    }

    private void showFaceEditDialog() {
        if (faceDialog != null && faceDialog.isShowing()) {
            return;
        }

        runOnUiThread(() -> {
            AppCompatActivity activity = getActivityRef();
            if (activity != null) {
                faceDialog = new AvatarFaceEditDialog(activity);
                faceDialog.setAvatarOptionDialogListener(mListener);
                faceDialog.setOnShowListener(dialogInterface -> {
                    mListener.onFaceEditOptionDialogToggled(true);
                });
                faceDialog.setOnDismissListener(dialog -> {
                    mListener.onFaceEditOptionDialogToggled(false);
                    faceDialog = null;
                    show(activity);
                });
                faceDialog.show();
            }
        });
    }

    private void runOnUiThread(Runnable runnable) {
        AppCompatActivity act = getActivityRef();
        if (act != null) {
            act.runOnUiThread(runnable);
        }
    }

    public synchronized void dismissAll() {
        if (faceDialog != null && faceDialog.isShowing()) {
            faceDialog.dismiss();
            faceDialog = null;
        }

        if (dressDialog != null && dressDialog.isShowing()) {
            dressDialog.dismiss();
            dressDialog = null;
        }

        if (optionDialog != null && optionDialog.isShowing()) {
            optionDialog.dismiss();
            optionDialog = null;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        dismissAll();

        AppCompatActivity activity = getActivityRef();
        if (activity != null) {
            activity.getLifecycle().removeObserver(this);
        }
    }

    private AppCompatActivity getActivityRef() {
        if (activityWeak != null && activityWeak.get() != null) {
            return activityWeak.get();
        } else {
            return null;
        }
    }
}

interface AvatarOptionDialogListener {
    void onDressOptionDialogToggled(boolean showing);

    void onFaceEditOptionDialogToggled(boolean showing);

    void onDressTypeChanged(String type);

    void onDressItemSelected(String id);

    void onFaceEditChanged(String id, float value);
}
