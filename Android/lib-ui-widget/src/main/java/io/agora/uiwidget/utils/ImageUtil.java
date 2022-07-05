package io.agora.uiwidget.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.IntegerRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ImageUtil {
    public static void setImageUrl(Context context, ImageView imageView, String url) {
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageView);
    }

    @SuppressLint("ResourceType")
    public static void setImageResource(Context context, ImageView imageView, @IntegerRes int res) {
        Glide.with(context).load(res).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
    }
}
