package present.binge.com.scrollscaleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.List;

/**
 * Created by Administrator on 2016/11/10.
 */
public class ScalePickView extends FrameLayout {
    private static final String TAG = "ScalePickView";

    private Paint mPaint = new Paint();
    private
    @ColorInt
    int mPointerColor = Color.RED;
    private ScrollScaleView mScrollScaleView;

    private int mPointerLength;

    public void setRangeDataList(List mRangeDataList) {
        mScrollScaleView.setRangeDataList(mRangeDataList);
    }

    public ScalePickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initUI(context, attrs);
    }

    public ScalePickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        mScrollScaleView = new ScrollScaleView(context, attrs);
        addView(mScrollScaleView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mPointerColor);
        drawPointer(canvas);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ScrollScaleView);
        if (typedArray != null) {
            mPointerLength = typedArray.getDimensionPixelOffset(R.styleable.ScrollScaleView_scaleview_long_line, 50);
            typedArray.recycle();
        }
    }

    private void drawPointer(Canvas canvas) {
        canvas.drawLine(getWidth() / 2, getHeight(), getWidth() / 2, getHeight() - mPointerLength, mPaint);
    }

    public void setCurrenValuePosition(int mCurrenValuePosition) {
        mScrollScaleView.setCurrenValuePosition(mCurrenValuePosition);
    }

    private void setOnScrollListener(ScrollScaleView.OnScrollListener onScrollListener){
        mScrollScaleView.setOnScrollListener(onScrollListener);
    }
}
