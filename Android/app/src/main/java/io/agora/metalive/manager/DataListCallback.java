package io.agora.metalive.manager;

import androidx.annotation.NonNull;

import java.util.List;

public interface DataListCallback<T> {
    void onSuccess(@NonNull List<T> dataList);
}
