package cn.lven.xview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import cn.lven.xview.R;


/**
 * QQ空间类似的网格布局
 */

public class XGridLayout extends AdapterLayout {
    /**
     * 列数
     */
    private int mSpan = 1;
    /**
     * Item 水平之间的间距
     */
    private int mHorizontalSpace = 0;
    /**
     * Item 垂直之间的间距
     */
    private int mVerticalSpace = 0;
    /**
     * 最大的Item数量
     */
    private int mMaxItem = 9;
    /**
     * 条目的宽高是否一样
     */
    private boolean isSquare;

    public XGridLayout(Context context) {
        this(context, null);
    }

    public XGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 获取自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.XGridLayout);
        mSpan = array.getInteger(R.styleable.XGridLayout_gridSpan, mSpan);
        mHorizontalSpace = (int) array.getDimension(R.styleable.XGridLayout_gridHorizontalSpace, mHorizontalSpace);
        mVerticalSpace = (int) array.getDimension(R.styleable.XGridLayout_gridVerticalSpace, mVerticalSpace);
        mMaxItem = array.getInteger(R.styleable.XGridLayout_gridMaxItem, mMaxItem);
        isSquare = array.getBoolean(R.styleable.XGridLayout_gridIsSquare, false);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取控件的宽度
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        // 计算单个子View的宽度
        int itemWidth = (width - getPaddingLeft() - getPaddingRight() - mHorizontalSpace * (mSpan - 1)) / mSpan;
        // 测量子View的宽高
        int childCount = getChildCount();
        // 计算一下最大的条目数量
        childCount = Math.min(childCount, mMaxItem);
        if (childCount <= 0) {
            setMeasuredDimension(0, 0);
            return;
        }
        int height = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int itemSpec = View.MeasureSpec.makeMeasureSpec(itemWidth, View.MeasureSpec.EXACTLY);
            // 判断条目宽高是否一样
            if (isSquare) {
                measureChild(child, itemSpec, itemSpec);
            } else {
                measureChild(child, itemSpec, heightMeasureSpec);

                if (i != 0) {
                    // 换行就加多一个高度
                    if ((i) % mSpan == 0) {
                        height += child.getMeasuredHeight() + mVerticalSpace;
                    }
                } else {
                    height += child.getMeasuredHeight();
                }
            }
        }
        // 判断条目宽高是否一样
        if (isSquare) {
            height = itemWidth * (childCount % mSpan == 0 ? (childCount / mSpan) : (childCount / mSpan + 1))
                    + mVerticalSpace * ((childCount - 1) / mSpan);
        }
        height += getPaddingTop() + getPaddingBottom();
        // 指定自己的宽高
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        // 计算一下最大的条目数量
        childCount = Math.min(childCount, mMaxItem);
        if (childCount <= 0) {
            return;
        }
        int cl = getPaddingLeft();
        int ct = getPaddingTop();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            child.layout(cl, ct, cl + width, ct + height);
            // 累加宽度
            cl += width + mHorizontalSpace;
            // 如果是换行
            if ((i + 1) % mSpan == 0) {
                // 重置左边的位置
                cl = getPaddingLeft();
                // 叠加高度
                ct += height + mVerticalSpace;
            }
        }
    }

    /**
     * 重新添加布局
     */
    protected void resetLayout() {
        if (mAdapter == null) {
            return;
        }
        removeAllViews();
        int count = mAdapter.getCount();
        count = Math.min(count, mMaxItem);
        for (int i = 0; i < count; i++) {
            View view = mAdapter.getView(i, this);
            addView(view);
        }
    }

    /**
     * 设置最大显示个数
     *
     * @param maxItem
     */
    public void setMaxItem(int maxItem) {
        mMaxItem = maxItem;
        resetLayout();
    }

    /**
     * @param mSpanx
     */
    public void setGridSpan(int mSpanx) {
        mSpan = mSpanx;
        invalidate();
    }


}
