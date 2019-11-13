package cn.lven.xview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import cn.lven.xview.R;


/**
 * 访简书赞赏平放的布局
 */

public class LineLayout extends AdapterLayout {
    /**
     * 两个View之间距的比例
     */
    private float mViewMarginRate = 0.5f;
    /**
     * 是不是从后面向前摆放
     */
    private boolean mIsReverse = true;


    public LineLayout(Context context) {
        this(context, null);
    }

    public LineLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LineLayout);
        // 默认是在一半的位置
        mViewMarginRate = array.getFloat(R.styleable.LineLayout_lineViewMarginRate, 0.5f);
        // 默认第一个在上面
        mIsReverse = array.getBoolean(R.styleable.LineLayout_lineIsReverse, mIsReverse);
        array.recycle();
        // 设置充许改变绘制顺序
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 1.测量控件的宽高
        // 获取自已的测量模式
        int modeWidth = View.MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = View.MeasureSpec.getMode(heightMeasureSpec);
        // 获取自已的宽高
        int sizeWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = View.MeasureSpec.getSize(heightMeasureSpec);

        int count = getChildCount();
        int height = 0;
        int width = 0;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            int measuredWidth = child.getMeasuredWidth();
            // 计算控件的宽度
            if (i == 0) {
                width = measuredWidth;
            } else {
                width += (int) (mViewMarginRate * width + 0.5f);
            }
            int measuredHeight = child.getMeasuredHeight();
            // 高度取最大的子View的高度
            height = Math.max(height, measuredHeight);
        }
        width += getPaddingLeft() + getPaddingRight();
        height += getPaddingTop() + getPaddingBottom();
        // 设置自己的宽高
        setMeasuredDimension
                (
                        modeWidth == View.MeasureSpec.EXACTLY ? sizeWidth : width,
                        modeHeight == View.MeasureSpec.EXACTLY ? sizeHeight : height
                );


    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int count = getChildCount();
        int cl = getPaddingLeft();
        int ct = getPaddingTop();
        // 摆放
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            if (i > 0) {
                // 计算第二个后面子View左边的位置
                cl += (int) (mViewMarginRate * width + 0.5f);
            }
            // 摆放子View
            child.layout(cl, ct, cl + width, ct + height);
        }

    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        // 确定View的绘制优先级
        if (!mIsReverse) {
            return i;
        }
        return childCount - 1 - i;
    }


}
