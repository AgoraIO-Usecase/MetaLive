package io.agora.metalive.manager;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public abstract  class WeakRunnable<T> implements Runnable{

    private WeakReference<T> data;

    public WeakRunnable(T data){
        this.data = new WeakReference<T>(data);
    }

    @Override
    public final void run() {
        if(data != null){
            T t = data.get();
            if(t != null){
                runSafe(t);
            }
        }
    }

    protected abstract void runSafe(@NonNull T data);
}
