package io.agora.uiwidget.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

public class ImageUtil {

    public static void setDrawableRound(Context context, ImageView imageView, int drawableResId, float radius){
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), BitmapFactory.decodeResource(context.getResources(), drawableResId));
        drawable.setCornerRadius(radius);
        imageView.setImageDrawable(drawable);
    }

}
