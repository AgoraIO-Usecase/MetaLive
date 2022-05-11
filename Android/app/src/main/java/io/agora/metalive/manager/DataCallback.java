package io.agora.metalive.manager;

import androidx.annotation.NonNull;

public interface DataCallback<T> {
    void onSuccess(@NonNull T data);
}
