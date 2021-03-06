package present.binge.com.scrollscaleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

import java.util.List;

/**
 * Created by Administrator on 2016/11/8.
 */

public class ScrollScaleView extends View {
    private static final String TAG = "ScalePickView";
    private float DEFAULT_WIDTH;
    private float DEFAULT_HEIGHT;


    private int mOrientation;
    private float mLongLineLength;
    private float mShortLineLength;
    private float mLineMargin;
    private int mMinValue;
    private int mMaxValue;
    private int mMultiple;
    private float mTextScaleMargin;
    private float mTextSize;
    private int mStepUnit;
    private float mLineWidth;
    private float mDefaultStrokeWidth;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private Paint mPaint = new Paint();
    private
    @ColorInt
    int mScaleColor = Color.BLACK;
    private
    @ColorInt
    int mTextColor = Color.BLACK;

    private Typeface mTypeface;
    private boolean mNeedBottomLine;

    private int mCurrenValuePosition;
    private List mRangeDataList;

    private int mScrollLastX;
    private int mScrollLastY;
    private float mDefaultOffset;//初始距原点距离(基于scroller计算，左正右负)
    private float tempOffset = 0f;
    private String lastValue;
    private boolean isOnTouch;

    private boolean isScrollFinishToCallback;

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
        mDefaultStrokeWidth = mPaint.getStrokeWidth();
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ScrollScaleView);
        if (typedArray != null) {
            mOrientation = typedArray.getInteger(R.styleable.ScrollScaleView_scaleview_orientation, ScalePickView.HORIZONTAL);
            mLongLineLength = typedArray.getDimension(R.styleable.ScrollScaleView_scaleview_long_line, 50f);
            mShortLineLength = typedArray.getDimension(R.styleable.ScrollScaleView_scaleview_short_line, 25f);
            mLineMargin = typedArray.getDimension(R.styleable.ScrollScaleView_scaleview_line_margin, 20f);
            mMinValue = typedArray.getInteger(R.styleable.ScrollScaleView_scaleview_min_value, 0);
            mMaxValue = typedArray.getInteger(R.styleable.ScrollScaleView_scaleview_max_value, mMinValue);
            mMultiple = typedArray.getInteger(R.styleable.ScrollScaleView_scaleview_multiple, 3);
            mTextScaleMargin = typedArray.getDimension(R.styleable.ScrollScaleView_scaleview_text_scale_margin, 20f);
            mTextSize = typedArray.getDimension(R.styleable.ScrollScaleView_scaleview_text_size, 30f);
            mNeedBottomLine = typedArray.getBoolean(R.styleable.ScrollScaleView_scaleview_bottom_line, false);
            mStepUnit = typedArray.getInt(R.styleable.ScrollScaleView_scaleview_step_unit, 10);
            mLineWidth = typedArray.getDimension(R.styleable.ScrollScaleView_scaleview_line_width, 4);
            mTextColor = typedArray.getColor(R.styleable.ScrollScaleView_scaleview_text_color, Color.BLACK);
            mScaleColor = typedArray.getColor(R.styleable.ScrollScaleView_scaleview_line_color, mTextColor);
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
        if (mOrientation == ScalePickView.HORIZONTAL) {
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
        mPaint.setStrokeWidth(mLineWidth);
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
        if (mOrientation == ScalePickView.HORIZONTAL) {
            canvas.drawLine(0, getHeight(), DEFAULT_WIDTH, getHeight(), mPaint);
        } else {
            canvas.drawLine(0, 0, 0, DEFAULT_HEIGHT, mPaint);
        }
    }

    private void drawScale(Canvas canvas) {
        if (mOrientation == ScalePickView.HORIZONTAL) {
            if (mRangeDataList != null && !mRangeDataList.isEmpty()) {
                for (int i = 0; i < mRangeDataList.size(); i++) {
                    float x1 = i * mMultiple * mLineMargin;
                    mPaint.setColor(mScaleColor);
                    canvas.drawLine(x1, getHeight(), x1, getHeight() - mLongLineLength, mPaint);

                    mPaint.setColor(mTextColor);
                    mPaint.setTextSize(mTextSize);
                    mPaint.setStrokeWidth(mDefaultStrokeWidth);
                    canvas.drawText(String.valueOf(mRangeDataList.get(i)), x1, getHeight() - mLongLineLength - mTextScaleMargin, mPaint);

                    if (i == mRangeDataList.size() - 1) {
                        return;
                    }
                    for (int j = 1; j < mMultiple; j++) {
                        float x2 = (i * mMultiple + j) * mLineMargin;
                        mPaint.setColor(mScaleColor);
                        mPaint.setStrokeWidth(mLineWidth);
                        canvas.drawLine(x2, getHeight(), x2, getHeight() - mShortLineLength, mPaint);
                    }
                }
            } else if (mMaxValue != mMinValue && mMaxValue > mMinValue) {
                for (int i = 0, k = mMinValue; i < mMaxValue - mMinValue; i++) {
                    float x1 = i * mMultiple * mLineMargin;
                    mPaint.setColor(mScaleColor);
                    canvas.drawLine(x1, getHeight(), x1, getHeight() - mLongLineLength, mPaint);

                    mPaint.setColor(mTextColor);
                    mPaint.setTextSize(mTextSize);
                    canvas.drawText(String.valueOf(k), x1, getHeight() - mLongLineLength - mTextScaleMargin, mPaint);

                    if (k == mMaxValue) {
                        return;
                    }

                    for (int j = 1; j < mMultiple; j++) {
                        float x2 = (i * mMultiple + j) * mLineMargin;
                        mPaint.setColor(mScaleColor);
                        canvas.drawLine(x2, getHeight(), x2, getHeight() - mShortLineLength, mPaint);
                    }

                    k += mStepUnit;
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

                    Paint.FontMetrics fm = mPaint.getFontMetrics();
                    float baseLine = y1 + (fm.bottom - fm.top) / 2 - 3 * fm.bottom + 2 * fm.descent;
                    canvas.drawText(String.valueOf(mRangeDataList.get(i)), mLongLineLength + mTextScaleMargin, baseLine, mPaint);
                    if (i == mRangeDataList.size() - 1) {
                        return;
                    }
                    for (int j = 1; j < mMultiple; j++) {
                        float y2 = (i * mMultiple + j) * mLineMargin;
                        mPaint.setColor(mScaleColor);
                        canvas.drawLine(0, y2, mShortLineLength, y2, mPaint);
                    }
                }
            } else if (mMaxValue != mMinValue && mMaxValue > mMinValue) {
                for (int i = 0, k = mMinValue; i <= mMaxValue - mMinValue; i++) {
                    float y1 = i * mMultiple * mLineMargin;
                    mPaint.setColor(mScaleColor);
                    canvas.drawLine(0, y1, mLongLineLength, y1, mPaint);

                    mPaint.setColor(mTextColor);
                    mPaint.setTextSize(mTextSize);
                    Paint.FontMetrics fm = mPaint.getFontMetrics();
                    float baseLine = y1 + (fm.bottom - fm.top) / 2 - 3 * fm.bottom + 2 * fm.descent;
                    canvas.drawText(String.valueOf(k), mLongLineLength + mTextScaleMargin, baseLine, mPaint);

                    if (k == mMaxValue) {
                        return;
                    }

                    for (int j = 1; j < mMultiple; j++) {
                        float y2 = (i * mMultiple + j) * mLineMargin;
                        mPaint.setColor(mScaleColor);
                        canvas.drawLine(0, y2, mShortLineLength, y2, mPaint);
                    }

                    k += mStepUnit;
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

        if (mScroller.isFinished() && isScrollFinishToCallback) {
            lastValue = getCurrentValue();
            mOnScrollListener.onScrollCompleted(lastValue);
            isScrollFinishToCallback = false;
        }
    }

    public void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        obtainVelocityTracker(event);

        if (mOrientation == ScalePickView.VERTICAL) {
            return handleVerticalTouch(event);
        } else {
            return handleHorizontalTouch(event);
        }
    }

    private boolean handleHorizontalTouch(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isOnTouch = true;
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mScrollLastX = x;
                isScrollFinishToCallback = false;
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
                mVelocityTracker.computeCurrentVelocity(1000);
                int initialVelocity = (int) mVelocityTracker.getXVelocity();
                if (Math.abs(initialVelocity) > 800) {
                    mScroller.forceFinished(true);
                    mScroller.fling(mScroller.getCurrX(), mScroller.getCurrY(),
                            -initialVelocity, 0,
                            -getWidth() / 2, (int) (DEFAULT_WIDTH - getWidth() / 2), 0, 0);
                }
                releaseVelocityTracker();

                float deltaOffset = mDefaultOffset - mScroller.getFinalX();
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

                mScroller.setFinalX((int) (mDefaultOffset - deltaOffset));
                ViewCompat.postInvalidateOnAnimation(this);

                int mCurrenValuePositionTmp = (int) (mCurrenValuePosition - (deltaOffset - tempOffset) / (mMultiple * mLineMargin));

                if (mOnScrollListener != null && mCurrenValuePositionTmp != mCurrenValuePosition) {
                    mCurrenValuePosition = mCurrenValuePositionTmp;
                    isScrollFinishToCallback = true;
                }
                mCurrenValuePosition = mCurrenValuePositionTmp;
                tempOffset = deltaOffset;

                isOnTouch = false;
                return true;
        }
        return true;
    }

    private boolean handleVerticalTouch(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isOnTouch = true;
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mScrollLastY = y;
                isScrollFinishToCallback = false;
                return true;
            case MotionEvent.ACTION_MOVE:
                float deltaY = mScrollLastY - y;

                if ((deltaY + mScroller.getFinalY()) < -getHeight() / 2) {
                    deltaY = -getHeight() / 2 - mScroller.getFinalY();
                }
                if ((deltaY + mScroller.getFinalY() - DEFAULT_HEIGHT) > -getHeight() / 2) {
                    deltaY = -getHeight() / 2 - mScroller.getFinalY() + DEFAULT_HEIGHT;
                }
                smoothScrollBy(0, (int) deltaY);
                mScrollLastY = y;

                postInvalidate();
                return true;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                int initialVelocity = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(initialVelocity) > 800) {
                    mScroller.forceFinished(true);
                    mScroller.fling(mScroller.getCurrX(), mScroller.getCurrY(),
                            0, -initialVelocity, 0, 0,
                            -getHeight() / 2, (int) (DEFAULT_HEIGHT - getHeight() / 2));
                }
                releaseVelocityTracker();

                float deltaOffset = mDefaultOffset - mScroller.getFinalY();
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

                mScroller.setFinalY((int) (mDefaultOffset - deltaOffset));
                ViewCompat.postInvalidateOnAnimation(this);

                int mCurrenValuePositionTmp = (int) (mCurrenValuePosition - (deltaOffset - tempOffset) / (mMultiple * mLineMargin));

                if (mOnScrollListener != null && mCurrenValuePositionTmp != mCurrenValuePosition) {
                    mCurrenValuePosition = mCurrenValuePositionTmp;
                    isScrollFinishToCallback = true;
                }

                mCurrenValuePosition = mCurrenValuePositionTmp;
                tempOffset = deltaOffset;

                isOnTouch = false;
                return true;
        }
        return true;
    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /*-----------------set---------------------*/

    public void setOrientation(@ScalePickView.ORIENTATION int mOrientation) {
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
                float deltaOffset = 0;
                if ((mRangeDataList != null && mRangeDataList.size() > mCurrenValuePosition && mCurrenValuePosition >= 0) || (mMaxValue != mMinValue && mMaxValue > mMinValue)) {
                    if (mOrientation == ScalePickView.HORIZONTAL) {
                        deltaOffset = mCurrenValuePosition * mMultiple * mLineMargin - getWidth() / 2;
                        mScroller.setFinalX((int) deltaOffset);
                    } else {
                        deltaOffset = mCurrenValuePosition * mMultiple * mLineMargin - getHeight() / 2;
                        mScroller.setFinalY((int) deltaOffset);
                    }
                }

                tempOffset = 0f;
                postInvalidate();
                mDefaultOffset = deltaOffset;
            }
        });
    }

    public void setCurrentValue(final String defaultValue) {
        if (mRangeDataList != null && mRangeDataList.contains(defaultValue)) {
            mCurrenValuePosition = mRangeDataList.indexOf(defaultValue);
        } else if (mMaxValue != mMinValue && mMaxValue > mMinValue) {
            mCurrenValuePosition = (int) (Float.valueOf(defaultValue) - mMinValue) / mStepUnit;
        }
        setCurrentValuePosition(mCurrenValuePosition);
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
        if (isOnTouch) {
            return;
        }
        if (mRangeDataList.contains(lastValue)) {
            mCurrenValuePosition = mRangeDataList.indexOf(lastValue);
        } else if (this.mRangeDataList != null) {
            int length = (this.mRangeDataList.size() - 1) / 2;
            if (mCurrenValuePosition > length) {
                mCurrenValuePosition = mRangeDataList.size() - 1;
            } else if (mCurrenValuePosition < length) {
                mCurrenValuePosition = 0;
            } else {
                mCurrenValuePosition = (mRangeDataList.size() - 1) / 2;
            }
        } else {
            mCurrenValuePosition = 0;
        }

        this.mRangeDataList = mRangeDataList;
        init();
        tempOffset = 0f;

        setCurrentValuePosition(mCurrenValuePosition);
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
        } else if (mMaxValue != mMinValue && mMaxValue > mMinValue) {
            return String.valueOf(mMinValue + mCurrenValuePosition * mStepUnit);
        } else {
            Log.d(TAG, "---------出错了----------");
        }
        return "";
    }

    public void setStepUnit(int stepUnit) {
        if (stepUnit <= 0 || stepUnit > mMaxValue || mMaxValue % stepUnit != 0) {
            throw new RuntimeException("stepUnit must in (0," + mMaxValue + "], and " + mMaxValue + "%stepUnit==0 is true");
        } else {
            mStepUnit = stepUnit;
        }
    }

    public void setMultiple(int multiple) {
        this.mMultiple = multiple;
    }

}
