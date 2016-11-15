package present.binge.com.scrollscaleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by Administrator on 2016/11/8.
 */

public class ScrollScaleView extends View {
    private static final String TAG = "ScrollScaleView";
    private float DEFAULT_WIDTH;
    private float DEFAULT_HEIGHT;

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private int mOrientation;
    private float mLongLineLength;
    private float mShortLineLength;
    private float mLineMargin;
    private int mMinValue;
    private int mMaxValue;
    private int mMultiple;
    private float mTextScaleMargin;
    private float mTextSize;

    private Scroller mScroller;

    private Paint mPaint = new Paint();
    private
    @ColorInt
    int mScaleColor = Color.BLACK;
    private
    @ColorInt
    int mTextColor = Color.BLACK;

    private Typeface mTypeface;
    private boolean mNeedBottomLine;

    private int mCurrenValuePosition = 0;
    private List mRangeDataList;

    private int mScrollLastX;
    private int mScrollLastY;
    private float mLeftOffset;
    private float tempOffset = 0f;


    @IntDef({HORIZONTAL, VERTICAL})

    @Retention(RetentionPolicy.SOURCE)
    public @interface ORIENTATION {
    }

    public ScrollScaleView(Context context) {
        super(context);
    }

    public ScrollScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public ScrollScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ScrollScaleView);
        if (typedArray != null) {
            mOrientation = typedArray.getInteger(R.styleable.ScrollScaleView_scaleview_orientation, HORIZONTAL);
            mLongLineLength = typedArray.getDimension(R.styleable.ScrollScaleView_scaleview_long_line, 50f);
            mShortLineLength = typedArray.getDimension(R.styleable.ScrollScaleView_scaleview_short_line, 25f);
            mLineMargin = typedArray.getDimension(R.styleable.ScrollScaleView_scaleview_line_margin, 20f);
            mMinValue = typedArray.getInteger(R.styleable.ScrollScaleView_scaleview_min_value, 0);
            mMaxValue = typedArray.getInteger(R.styleable.ScrollScaleView_scaleview_max_value, 100);
            mMultiple = typedArray.getInteger(R.styleable.ScrollScaleView_scaleview_multiple, 3);
            mTextScaleMargin = typedArray.getDimension(R.styleable.ScrollScaleView_scaleview_text_scale_margin, 10f);
            mTextSize = typedArray.getDimension(R.styleable.ScrollScaleView_scaleview_text_size, 30f);
            mNeedBottomLine = typedArray.getBoolean(R.styleable.ScrollScaleView_scaleview_bottom_line, false);
            typedArray.recycle();
        }

        mScroller = new Scroller(getContext());

        setCurrentValuePosition(mCurrenValuePosition);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        init();

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        // 处理宽高都为 wrap_content 的情况
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension((int) DEFAULT_WIDTH, (int) DEFAULT_HEIGHT);
        }
        // 处理宽为 wrap_content 的情况
        else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode != MeasureSpec.AT_MOST) {
            setMeasuredDimension((int) DEFAULT_WIDTH, heightSpecSize);
        }
        // 处理高为 wrap_content 的情况
        else if (heightSpecMode == MeasureSpec.AT_MOST && widthSpecMode != MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, (int) DEFAULT_HEIGHT);
        } else {
            setMeasuredDimension(widthSpecSize, heightSpecSize);
        }

    }

    private void init() {
        if (mOrientation == HORIZONTAL) {
            if (mRangeDataList == null || mRangeDataList.isEmpty()) {
                DEFAULT_WIDTH = (mMaxValue - mMinValue) * mLineMargin;
            } else {
                DEFAULT_WIDTH = (mRangeDataList.size() - 1) * mMultiple * mLineMargin;
            }
            DEFAULT_HEIGHT = mLongLineLength + mTextScaleMargin + mTextSize;
        } else {
            if (mRangeDataList == null || mRangeDataList.isEmpty()) {
                DEFAULT_HEIGHT = (mMaxValue - mMinValue) * mLineMargin;
            } else {
                DEFAULT_HEIGHT = (mRangeDataList.size() - 1) * mMultiple * mLineMargin;
            }
            DEFAULT_WIDTH = mLongLineLength + mTextScaleMargin + mTextSize;
        }

    }

    /*-------------onDraw-------------*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        if (mTypeface != null) {
            mPaint.setTypeface(mTypeface);
        }

        if (mNeedBottomLine) {
            drawLine(canvas);
        }
        drawScale(canvas);
    }

    private void drawLine(Canvas canvas) {
        mPaint.setColor(mScaleColor);
        if (mOrientation == HORIZONTAL) {
            canvas.drawLine(0, getHeight(), DEFAULT_WIDTH, getHeight(), mPaint);
        } else {
            canvas.drawLine(0, 0, 0, DEFAULT_HEIGHT, mPaint);
        }
    }

    private void drawScale(Canvas canvas) {
        if (mOrientation == HORIZONTAL) {
            if (mRangeDataList != null && !mRangeDataList.isEmpty()) {
                for (int i = 0; i < mRangeDataList.size(); i++) {
                    float x1 = i * mMultiple * mLineMargin;
                    mPaint.setColor(mScaleColor);
                    canvas.drawLine(x1, getHeight(), x1, getHeight() - mLongLineLength, mPaint);

                    mPaint.setColor(mTextColor);
                    mPaint.setTextSize(mTextSize);
                    canvas.drawText(String.valueOf(mRangeDataList.get(i)), x1, getHeight() - mLongLineLength - mTextScaleMargin, mPaint);

                    if (i == mRangeDataList.size() - 1) {
                        return;
                    }
                    for (int j = 1; j < mMultiple; j++) {
                        float x2 = (i * mMultiple + j) * mLineMargin;
                        mPaint.setColor(mScaleColor);
                        canvas.drawLine(x2, getHeight(), x2, getHeight() - mShortLineLength, mPaint);
                    }
                }
            }
        } else {
            if (mRangeDataList != null && !mRangeDataList.isEmpty()) {
                for (int i = 0; i < mRangeDataList.size(); i++) {
                    float y1 = i * mMultiple * mLineMargin;
                    mPaint.setColor(mScaleColor);
                    canvas.drawLine(0, y1, mLongLineLength, y1, mPaint);

                    mPaint.setColor(mTextColor);
                    mPaint.setTextSize(mTextSize);
                    canvas.drawText(String.valueOf(mRangeDataList.get(i)), mLongLineLength + mTextScaleMargin + mTextSize / 2, y1 + mTextSize / 3, mPaint);// TODO: 2016/11/9 垂直文字居中不能简单的/3

                    if (i == mRangeDataList.size() - 1) {
                        return;
                    }
                    for (int j = 1; j < mMultiple; j++) {
                        float y2 = (i * mMultiple + j) * mLineMargin;
                        mPaint.setColor(mScaleColor);
                        canvas.drawLine(0, y2, mShortLineLength, y2, mPaint);
                    }
                }
            }
        }

    }

    /*------------scroll----------------*/
    @Override
    public void computeScroll() {
        super.computeScroll();
        // 判断Scroller是否执行完毕
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            // 通过重绘来不断调用computeScroll
            invalidate();
        }
    }

    public void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mScrollLastX = x;
                return true;
            case MotionEvent.ACTION_MOVE:
                float deltaX = mScrollLastX - x;

                if ((deltaX + mScroller.getFinalX()) < -getWidth() / 2) {
                    deltaX = -getWidth() / 2 - mScroller.getFinalX();
                }

                if ((deltaX + mScroller.getFinalX() - DEFAULT_WIDTH) > -getWidth() / 2) {
                    deltaX = -getWidth() / 2 - mScroller.getFinalX() + DEFAULT_WIDTH;
                }

                smoothScrollBy((int) deltaX, 0);
                mScrollLastX = x;
                postInvalidate();
                return true;
            case MotionEvent.ACTION_UP:
                float deltaOffset = mLeftOffset - mScroller.getFinalX();
                float tempOffsetSign = deltaOffset;
                float abs = Math.abs(deltaOffset % (mMultiple * mLineMargin));
                if (deltaOffset > 0) {
                    deltaOffset -= abs;
                } else {
                    deltaOffset += abs;
                }

                if (abs > (mMultiple * mLineMargin) / 2) {
                    //补全
                    deltaOffset += Math.copySign((mMultiple * mLineMargin), tempOffsetSign);
                }

                mScroller.setFinalX((int) (mLeftOffset - deltaOffset));
                postInvalidate();

                mCurrenValuePosition = (int) (mCurrenValuePosition - (deltaOffset - tempOffset) / (mMultiple * mLineMargin));

                tempOffset = deltaOffset;

                if (mOnScrollListener != null) {
                    if (mRangeDataList != null && mRangeDataList.size() > mCurrenValuePosition && mCurrenValuePosition >= 0) {
                        mOnScrollListener.onScrollCompleted(String.valueOf(mRangeDataList.get(mCurrenValuePosition)));
                    }
                }

                return true;
        }
        return super.onTouchEvent(event);
    }

    /*-----------------set---------------------*/

    public void setOrientation(@ORIENTATION int mOrientation) {
        this.mOrientation = mOrientation;
    }

    public void setMinValue(int mMinValue) {
        this.mMinValue = mMinValue;
    }

    public void setMaxValue(int mMaxValue) {
        this.mMaxValue = mMaxValue;
    }

    public void setScaleColor(@ColorInt int mScaleColor) {
        this.mScaleColor = mScaleColor;
    }

    public void setCurrentValuePosition(final int mCurrenValuePosition) {
        this.mCurrenValuePosition = mCurrenValuePosition;
        post(new Runnable() {
            @Override
            public void run() {
                float deltaX = mCurrenValuePosition * mMultiple * mLineMargin - getWidth() / 2;
                mScroller.setFinalX((int) deltaX);
                postInvalidate();
                mLeftOffset = deltaX;
            }
        });
    }

    public void setCurrentValue(String defaultValue) {
        if (mRangeDataList != null && mRangeDataList.contains(defaultValue)) {
            mCurrenValuePosition = mRangeDataList.indexOf(defaultValue);
            setCurrentValuePosition(mCurrenValuePosition);
        }
    }

    public void setLongLineLength(int mLongLineLength) {
        this.mLongLineLength = mLongLineLength;
    }

    public void setShortLineLength(int mShortLineLength) {
        this.mShortLineLength = mShortLineLength;
    }

    public void setLineMargin(int mLineMargin) {
        this.mLineMargin = mLineMargin;
    }

    public void setTextScaleMargin(int mTextScaleMargin) {
        this.mTextScaleMargin = mTextScaleMargin;
    }

    public void setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public void setTypeface(Typeface mTypeface) {
        this.mTypeface = mTypeface;
    }

    public void needBottomLine(boolean needBottomLine) {
        this.mNeedBottomLine = needBottomLine;
    }

    public void setRangeDataList(List mRangeDataList) {
        this.mRangeDataList = mRangeDataList;
        postInvalidate();
    }

    private OnScrollListener mOnScrollListener;

    public interface OnScrollListener {
        void onScrollCompleted(String value);
    }

    public void setOnScrollListener(OnScrollListener mOnScrollListener) {
        this.mOnScrollListener = mOnScrollListener;
    }

    public String getCurrentValue() {
        if (mRangeDataList != null && mRangeDataList.size() > mCurrenValuePosition && mCurrenValuePosition >= 0) {
            return String.valueOf(mRangeDataList.get(mCurrenValuePosition));
        }
        return "";
    }
}
