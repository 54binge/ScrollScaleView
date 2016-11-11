package present.binge.com.scrollscaleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2016/11/15.
 */
public class Pointer extends View {
    private int mPointerLength;
    private int mPointerWidth;
    private Paint mPaint = new Paint();
    private
    @ColorInt
    int mPointerColor = Color.RED;

    public Pointer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public Pointer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mPointerColor);
        mPaint.setStrokeWidth(mPointerWidth);
        drawPointer(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        // 处理宽高都为 wrap_content 的情况
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mPointerWidth, mPointerLength);
        }
        // 处理宽为 wrap_content 的情况
        else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode != MeasureSpec.AT_MOST) {
            setMeasuredDimension(mPointerWidth, heightSpecSize);
        }
        // 处理高为 wrap_content 的情况
        else if (heightSpecMode == MeasureSpec.AT_MOST && widthSpecMode != MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mPointerLength);
        } else {
            setMeasuredDimension(widthSpecSize, heightSpecSize);
        }
    }

    private void drawPointer(Canvas canvas) {
        canvas.drawLine(getWidth() / 2, getHeight(), getWidth() / 2, getHeight() - mPointerLength, mPaint);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ScrollScaleView);
        if (typedArray != null) {
            mPointerLength = typedArray.getDimensionPixelOffset(R.styleable.ScrollScaleView_scaleview_long_line, 50);
            mPointerWidth = typedArray.getDimensionPixelOffset(R.styleable.ScrollScaleView_scaleview_pointer_width, 3);
            typedArray.recycle();
        }
    }

    public void setPointerLength(int pointerLength) {
        this.mPointerLength = pointerLength;
    }

    public void setPointerWidth(int pointerWidth) {
        this.mPointerWidth = pointerWidth;
    }
}
