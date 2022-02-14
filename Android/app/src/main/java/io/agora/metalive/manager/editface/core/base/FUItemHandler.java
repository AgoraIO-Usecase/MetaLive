package io.agora.metalive.manager.editface.core.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * 异步消息的处理
 * <p>
 * Created by tujh on 2018/12/17.
 */
public class FUItemHandler extends Handler {
    private static final String TAG = FUItemHandler.class.getSimpleName();

    private static int what_index = 1;
    private static final int what_space_constant = 100;

    public static int generateWhatIndex() {
        return what_index++ * what_space_constant;
    }

    public FUItemHandler(Looper looper, Context mContext) {
        super(looper);
    }

}
