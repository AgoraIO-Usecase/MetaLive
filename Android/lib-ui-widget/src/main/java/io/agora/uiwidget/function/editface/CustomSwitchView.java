package io.agora.uiwidget.function.editface;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import io.agora.uiwidget.R;


public class CustomSwitchView extends View {

    private Paint borderPaint;
    private Paint textPaint;
    private Paint viewPaint;
    private int mWidth;
    private int mHeight;
    private int centerOvalWidth;
    private int radius;
    private int borderWidth;
    private int centerOvalHeight;
    private int centerViewColor;
    private int checkedTextColor;
    private int unCheckedTextColor;
    private int backgroundColor;
    private int borderColor;
    private int textsize;
    private RectF borderRect;
    private int currentSelectedX = 0;

    String leftText = "镜框";
    String rightText = "镜片";
    private float baseline;

    boolean animRunning = false;

    private boolean leftChecked;
    private AnimatorSet animatorSet;

    public CustomSwitchView(Context context) {
        this(context, null);
    }

    public CustomSwitchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditFaceCustomGlassSwitchView);
        backgroundColor = typedArray.getColor(R.styleable.EditFaceCustomGlassSwitchView_backgroundColor,
                                              ContextCompat.getColor(getContext(), R.color.edit_face_select_customer_switch_background));
        borderColor = typedArray.getColor(R.styleable.EditFaceCustomGlassSwitchView_borderColor,
                                          ContextCompat.getColor(getContext(), R.color.edit_face_select_customer_switch_border));
        checkedTextColor = typedArray.getColor(R.styleable.EditFaceCustomGlassSwitchView_checkedTextColor,
                                               ContextCompat.getColor(getContext(), R.color.edit_face_select_customer_switch_checked_text_color));
        unCheckedTextColor = typedArray.getColor(R.styleable.EditFaceCustomGlassSwitchView_unCheckedTextColor,
                                                 ContextCompat.getColor(getContext(), R.color.edit_face_select_customer_switch_unchecked_text_color));

        centerViewColor = typedArray.getColor(R.styleable.EditFaceCustomGlassSwitchView_centerViewBackGroundColor,
                                              ContextCompat.getColor(getContext(), R.color.edit_face_select_customer_switch_center_view));
        borderWidth = (int) typedArray.getDimension(R.styleable.EditFaceCustomGlassSwitchView_borderWidth, 2);

        textsize = typedArray.getDimensionPixelSize(R.styleable.EditFaceCustomGlassSwitchView_textSize, 50);

        leftText = typedArray.getString(R.styleable.EditFaceCustomGlassSwitchView_leftText);
        rightText = typedArray.getString(R.styleable.EditFaceCustomGlassSwitchView_rightText);

        leftChecked = typedArray.getBoolean(R.styleable.EditFaceCustomGlassSwitchView_leftChecked, true);
        typedArray.recycle();
        init();

    }

    private void init() {
        mWidth = getResources().getDimensionPixelSize(R.dimen.edit_face_select_custom_glass_switch_width);
        mHeight = getResources().getDimensionPixelSize(R.dimen.edit_face_select_custom_glass_switch_height);

        borderPaint = initPaint();
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth);
        textPaint = initPaint();
        textPaint.setTextSize(textsize);
        textPaint.setTextAlign(Paint.Align.CENTER);

        viewPaint = initPaint();
        viewPaint.setStyle(Paint.Style.FILL);


    }

    public void setLeftText(String leftText) {
        this.leftText = leftText;
        invalidate();
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
        invalidate();
    }

    public void setLeftChecked(boolean leftChecked) {
        this.leftChecked = leftChecked;
        invalidate();
    }

    private Paint initPaint() {
        Paint paint = new Paint();
        // 设置抗锯齿
        paint.setAntiAlias(true);
        // 设置防抖动
        paint.setDither(true);
        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else {
            width = mWidth;
        }
        size = MeasureSpec.getSize(heightMeasureSpec);
        mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            height = mHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerOvalWidth = w / 2 - borderWidth;
        centerOvalHeight = h - borderWidth * 2;
        radius = h / 2;
        currentSelectedX = leftChecked ? borderWidth : centerOvalWidth + borderWidth;
        borderRect = new RectF(0, 0, w, h);

        //计算baseline
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        baseline = centerOvalHeight / 2 + borderWidth + distance;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        viewPaint.setColor(backgroundColor);
        canvas.drawRoundRect(borderRect, radius, radius, viewPaint);
        canvas.drawRoundRect(borderRect, radius, radius, borderPaint);
        viewPaint.setColor(centerViewColor);
        canvas.drawRoundRect(currentSelectedX, borderWidth, currentSelectedX + centerOvalWidth, borderWidth + centerOvalHeight, radius, radius, viewPaint);
        textPaint.setColor(checkedTextColor);
        canvas.drawText(leftText, borderRect.centerX() / 2, baseline, textPaint);
        textPaint.setColor(unCheckedTextColor);
        canvas.drawText(rightText, borderRect.centerX() / 2 * 3, baseline, textPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            determineTheClickPosition((int) event.getX(), (int) event.getY());
        }
        return true;
    }

    private void determineTheClickPosition(int x, int y) {
        if (x < 0 || x > getMeasuredWidth() || y < 0 || y > getMeasuredWidth() || animRunning) {
            return;
        }
        if (x < getMeasuredWidth() / 2 && !leftChecked) {
            // 表示选中的是左边
            startAnim(currentSelectedX, borderWidth);

        } else if (x > getMeasuredWidth() / 2 && leftChecked) {
            startAnim(currentSelectedX, centerOvalWidth + borderWidth);
        }
    }

    private void startAnim(int start, final int end) {
        animRunning = true;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentSelectedX = (int) animation.getAnimatedValue();
                invalidate();
                if (currentSelectedX == end) {
                    leftChecked = !leftChecked;
                    animRunning = false;
                    if (checkedChangeListener != null) {
                        checkedChangeListener.onCheckedChangeListener(leftChecked);
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        // 如果是低版本的话，那就直接交换颜色
                        int temp = unCheckedTextColor;
                        unCheckedTextColor = checkedTextColor;
                        checkedTextColor = temp;
                    }
                }
            }
        });

        animatorSet = new AnimatorSet();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ValueAnimator toUnSelected = ValueAnimator.ofArgb(checkedTextColor, unCheckedTextColor);
            toUnSelected.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    checkedTextColor = (int) animation.getAnimatedValue();
                }
            });
            ValueAnimator toSelected = ValueAnimator.ofArgb(unCheckedTextColor, checkedTextColor);
            toSelected.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    unCheckedTextColor = (int) animation.getAnimatedValue();
                }
            });
            animatorSet.play(valueAnimator)
                    .with(toUnSelected)
                    .with(toSelected);
        } else {
            animatorSet.play(valueAnimator);
        }

        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animatorSet != null) {
            animatorSet.cancel();
        }
    }

    public interface CheckedChangeListener {

        /**
         * 左边这个是否被选中
         *
         * @param selectedLeft
         */
        void onCheckedChangeListener(boolean selectedLeft);
    }


    private CheckedChangeListener checkedChangeListener;

    public void setCheckedChangeListener(CheckedChangeListener checkedChangeListener) {
        this.checkedChangeListener = checkedChangeListener;
    }

    public boolean isLeftChecked() {
        return leftChecked;
    }
}
