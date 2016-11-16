package present.binge.com.scrollscaleview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by Administrator on 2016/11/10.
 */
public class ScalePickView extends FrameLayout {
    private static final String TAG = "ScalePickView";
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    @IntDef({HORIZONTAL, VERTICAL})

    @Retention(RetentionPolicy.SOURCE)
    public @interface ORIENTATION {
    }


    private ScrollScaleView mScrollScaleView;
    private Pointer mPointer;
    private FrameLayout mMaskLayout;

    public void setRangeDataList(List mRangeDataList) {
        mScrollScaleView.setRangeDataList(mRangeDataList);
    }

    public ScalePickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context, attrs);
    }

    public ScalePickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs) {
        mScrollScaleView = new ScrollScaleView(context, attrs);
        mPointer = new Pointer(context, attrs);
        mMaskLayout = new FrameLayout(context, attrs);
        addView(mScrollScaleView);
        addView(mMaskLayout);
        addView(mPointer);
    }

    public void setCurrentValuePosition(int mCurrenValuePosition) {
        mScrollScaleView.setCurrentValuePosition(mCurrenValuePosition);
    }

    public void setCurrentValue(String defaultValue) {
        mScrollScaleView.setCurrentValue(defaultValue);
    }

    public void setOnScrollListener(ScrollScaleView.OnScrollListener onScrollListener) {
        mScrollScaleView.setOnScrollListener(onScrollListener);
    }

    public String getCurrentValue() {
        return mScrollScaleView.getCurrentValue();
    }

    public void setOrientation(@ORIENTATION int orientation) {
        mScrollScaleView.setOrientation(orientation);
        if (mPointer != null) {
            mPointer.setOrientation(orientation);
        }
    }

    public void setMinValue(int minValue) {
        mScrollScaleView.setMinValue(minValue);
    }

    public void setMaxValue(int maxValue) {
        mScrollScaleView.setMaxValue(maxValue);
    }

    public void setScaleColor(@ColorInt int scaleColor) {
        mScrollScaleView.setScaleColor(scaleColor);
    }

    public void setLongLineLength(int longLineLength) {
        mScrollScaleView.setLongLineLength(longLineLength);
        setPointerLength(longLineLength);
    }

    public void setShortLineLength(int shortLineLength) {
        mScrollScaleView.setShortLineLength(shortLineLength);
    }

    public void setLineMargin(int lineMargin) {
        mScrollScaleView.setLineMargin(lineMargin);
    }

    public void setTextScaleMargin(int textScaleMargin) {
        mScrollScaleView.setTextScaleMargin(textScaleMargin);
    }

    public void setTextSize(int textSize) {
        mScrollScaleView.setTextSize(textSize);
    }

    public void setTextColor(int textColor) {
        mScrollScaleView.setTextColor(textColor);
    }

    public void setTypeface(Typeface typeface) {
        mScrollScaleView.setTypeface(typeface);
    }

    public void needBottomLine(boolean needBottomLine) {
        mScrollScaleView.needBottomLine(needBottomLine);
    }

    public void setPointerLength(int pointerLength) {
        mPointer.setPointerLength(pointerLength);
    }

    public void setPointerWidth(int pointerWidth) {
        mPointer.setPointerWidth(pointerWidth);
    }

    public void setMask(View view) {
        mMaskLayout.addView(view);
    }

    public void setMask(@DrawableRes int resId) {
        mMaskLayout.setBackgroundResource(resId);
    }
}
