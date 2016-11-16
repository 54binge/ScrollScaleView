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
    private float mPointerLength;
    private float mPointerWidth;
    private Paint mPaint = new Paint();
    private
    @ColorInt
    int mPointerColor = Color.RED;
    private int mOrientation;

    public Pointer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public Pointer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ScrollScaleView);
        if (typedArray != null) {
            mOrientation = typedArray.getInteger(R.styleable.ScrollScaleView_scaleview_orientation, ScalePickView.HORIZONTAL);
            mPointerLength = typedArray.getDimension(R.styleable.ScrollScaleView_scaleview_long_line, 50f);
            mPointerWidth = typedArray.getDimension(R.styleable.ScrollScaleView_scaleview_pointer_width, 3f);
            typedArray.recycle();
        }
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
            setMeasuredDimension((int) mPointerWidth, (int) mPointerLength);
        }
        // 处理宽为 wrap_content 的情况
        else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode != MeasureSpec.AT_MOST) {
            setMeasuredDimension((int) mPointerWidth, heightSpecSize);
        }
        // 处理高为 wrap_content 的情况
        else if (heightSpecMode == MeasureSpec.AT_MOST && widthSpecMode != MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, (int) mPointerLength);
        } else {
            setMeasuredDimension(widthSpecSize, heightSpecSize);
        }
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

    private void drawPointer(Canvas canvas) {
        if (mOrientation == ScalePickView.HORIZONTAL) {
            canvas.drawLine(getWidth() / 2, getHeight(), getWidth() / 2, getHeight() - mPointerLength, mPaint);
        } else {
            canvas.drawLine(0, getHeight() / 2, mPointerLength, getHeight() / 2, mPaint);
        }
    }

    public void setPointerLength(int pointerLength) {
        this.mPointerLength = pointerLength;
    }

    public void setPointerWidth(int pointerWidth) {
        this.mPointerWidth = pointerWidth;
    }

    public void setOrientation(@ScalePickView.ORIENTATION int orientation) {
        mOrientation = orientation;
    }
}
