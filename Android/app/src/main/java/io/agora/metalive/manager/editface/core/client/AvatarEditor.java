package io.agora.metalive.manager.editface.core.client;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import io.agora.metalive.manager.editface.constant.Constant;
import io.agora.metalive.manager.editface.constant.FilePathFactory;
import io.agora.metalive.manager.editface.entity.AvatarPTA;
import io.agora.metalive.manager.editface.entity.BundleRes;
import io.agora.metalive.manager.editface.entity.DBHelper;
import io.agora.metalive.manager.editface.shape.EditFaceParameter;
import io.agora.metalive.manager.editface.utils.DateUtil;
import io.agora.metalive.manager.editface.utils.FileUtil;

/**
 * 捏脸
 * Created by tujh on 2019/2/22.
 */
public class AvatarEditor {

    private Context mContext;

    public AvatarEditor(Context context) {
        mContext = context;
    }

    public void saveAvatar(final AvatarPTA avatarP2A, final EditFaceParameter editFaceParameter, final SaveAvatarListener listener) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AvatarPTA newAvatarP2A;
                DBHelper dbHelper = DBHelper.create(mContext);
                File dirFile = null;
                byte[] head = null;
                boolean isCreateAvatar = avatarP2A.isCreateAvatar();
                if (isCreateAvatar) {
                    newAvatarP2A = avatarP2A;
                } else {
                    String dir = Constant.filePath + DateUtil.getCurrentDate() + File.separator;
                    FileUtil.createFile(dir);
                    dirFile = new File(dir);

                    newAvatarP2A = avatarP2A.clone();
                    newAvatarP2A.setBundleDir(dir);
                    newAvatarP2A.setCreateAvatar(true);
                }

                List<BundleRes> hairBundles = FilePathFactory.hairBundleRes();
                try {
                    if (editFaceParameter.isShapeChangeValues()) {
                        head = PTAClientWrapper.deformAvatarHead(isCreateAvatar ? new FileInputStream(new File(avatarP2A.getHeadFile())) : mContext.getAssets().open(avatarP2A.getHeadFile()), newAvatarP2A.getHeadFile(), editFaceParameter.getEditFaceParameters());
                    } else if (!isCreateAvatar) {
                        FileUtil.copyFileTo(mContext.getAssets().open(avatarP2A.getHeadFile()), new File(newAvatarP2A.getHeadFile()));
                    }

                    if (!isCreateAvatar) {
                        FileUtil.copyFileTo(mContext.getResources().openRawResource(avatarP2A.getOriginPhotoRes()), new File(newAvatarP2A.getOriginPhoto()));
                    }

                    BundleRes hairRes = hairBundles.get(avatarP2A.getHairIndex());
                    if (!TextUtils.isEmpty(hairRes.path)) {
                        String hair = avatarP2A.getBundleDir() + hairRes.name;
                        String hairNew = newAvatarP2A.getBundleDir() + hairRes.name;
//                        if (editFaceParameter.isHeadShapeChangeValues() && Constant.style == Constant.style_new) {
//                            PTAClientWrapper.deformHairByHead(head, mContext.getAssets().open(hairRes.path), hairNew);
//                        } else
                        if (!isCreateAvatar) {
                            FileUtil.copyFileTo(mContext.getAssets().open(hair), new File(hairNew));
                        }
                    }

                    List<BundleRes> hatBundles = FilePathFactory.hatBundleRes();
                    BundleRes hatRes = hatBundles.get(avatarP2A.getHatIndex());
                    if (!TextUtils.isEmpty(hatRes.path)) {
                        String hat = avatarP2A.getBundleDir() + hatRes.name;
                        String hatNew = newAvatarP2A.getBundleDir() + hatRes.name;
                        if (!isCreateAvatar) {
                            FileUtil.copyFileTo(mContext.getAssets().open(hat), new File(hatNew));
                        }
                    }

                    if (isCreateAvatar) {
                        dbHelper.updateHistory(newAvatarP2A);
                    } else {
                        dbHelper.insertHistory(newAvatarP2A);
                    }
                    listener.saveComplete(newAvatarP2A);

                } catch (Exception e) {
                    e.printStackTrace();
                    if (dirFile != null) {
                        dbHelper.deleteHistoryByDir(dirFile.getAbsolutePath());
                        if (dirFile.exists()) {
                            dirFile.delete();
                        }
                    }
                    listener.saveFailure();
                }
            }
        });
    }

    public interface SaveAvatarListener {
        void saveComplete(AvatarPTA avatarP2A);

        void saveFailure();
    }
}
