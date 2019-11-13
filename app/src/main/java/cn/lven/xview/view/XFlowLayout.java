package cn.lven.xview.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.lven.xview.adapter.XBaseAdapter;


/**
 * 流式布局
 */

public class XFlowLayout extends ViewGroup {
    private XBaseAdapter mAdapter;
    private DataSetObserver mObserver;
    private boolean isRegister = false;

    public XFlowLayout(Context context) {
        this(context, null);
    }

    public XFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    // 记录每行的View
    private List<List<View>> mViews = new ArrayList<>();
    // 记录每行的高度
    private List<Integer> mHeights = new ArrayList<>();
    /**
     * 是否平分
     */
    private boolean isSquare;

    private boolean isShowOneLine;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViews.clear();
        mHeights.clear();
        // 1.测量控件的宽高
        // 获取自已的测量模式
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        // 获取自已的宽高
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        // 最后要设置的宽高
        int width = 0;
        int height = 0;
        // 行的宽高
        int lineWidth = 0;
        int lineHeight = 0;

        int count = getChildCount();
        List<View> lineViews = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            // 测量子View
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            // 子View占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            // 子View占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            // 如果行宽 + 子View的宽度 > 测量的宽度
            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
                // 如果只显示一行
                if (isShowOneLine && mViews.size() > 0) {
                    continue;
                }
                // 换行
                // 取最宽
                width = Math.max(width, lineWidth);
                height += lineHeight;

                // 添加到第行的记录
                mViews.add(lineViews);
                mHeights.add(lineHeight);

                // 记录重置（换行后可能就是下一行的宽高）
                lineWidth = childWidth;
                lineHeight = childHeight;
                lineViews = new ArrayList<>();
                lineViews.add(child);
            } else {
                // 叠加宽度
                lineWidth += childWidth;
                // 取高度的最大值
                lineHeight = Math.max(childHeight, lineHeight);
                // 添加到行的View里面
                lineViews.add(child);
            }
            // 最后一个：取巧了
            if (i == count - 1) {
                if (isShowOneLine && mViews.size() > 0) {
                    continue;
                }
                // 取最宽
                width = Math.max(width, lineWidth);
                height += lineHeight;
                mViews.add(lineViews);
                mHeights.add(lineHeight);
            }
        }
        width += getPaddingLeft() + getPaddingRight();
        height += getPaddingTop() + getPaddingBottom();

        // 2.设置控件的宽高
        setMeasuredDimension
                (
                        modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width,
                        modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height
                );
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lineNum = mHeights.size();
        // 子View的起始位置
        int left = getPaddingLeft();
        int top = getPaddingTop();
        // 控件的宽
        int measuredWidth = getMeasuredWidth();
        if (isShowOneLine) {
            lineNum = 1;
        }
        for (int i = 0; i < lineNum && mViews.size() > 0; i++) {
            List<View> views = mViews.get(i);
            // 行宽
            int lineMargin = 0;
            // 如果平分
            if (isSquare && views.size() > 1) {
                int lineWidth = getLineWidth(views);
                lineMargin = (measuredWidth - lineWidth) / (views.size() - 1);
            }
            Integer lineHeight = mHeights.get(i);
            for (View view : views) {
                if (view.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams) view.getLayoutParams();
                int lc = left + lp.leftMargin;
                int rc = lc + view.getMeasuredWidth() + lp.rightMargin;
                int tc = top + lp.topMargin;
                int bc = tc + view.getMeasuredHeight() + lp.bottomMargin;
                view.layout(lc, tc, rc, bc);
                // 叠加左边的位置
                left += lp.leftMargin + view.getMeasuredWidth() + lp.rightMargin + lineMargin;
            }
            // 下一行的宽度和高度
            left = getPaddingLeft();
            top += lineHeight;
        }
    }

    private int getLineWidth(List<View> views) {
        int width = 0;
        for (View view : views) {
            MarginLayoutParams lp = (MarginLayoutParams) view.getLayoutParams();
            width += lp.leftMargin + view.getMeasuredWidth() + lp.rightMargin;
        }
        return width;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    public void setAdapter(XBaseAdapter adapter) {
        // 移除监听
        unRegisterAdapter();
        if (adapter == null) {
            throw new NullPointerException("FlowBaseAdapter is null");
        }
        mAdapter = adapter;
        mObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                resetLayout();
            }
        };
        // 注册监听
        registerAdapter();
        // 添加布局
        resetLayout();
    }

    /**
     * 重新添加布局
     */
    private void resetLayout() {
        if (mAdapter == null) {
            return;
        }
        removeAllViews();
        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            View view = mAdapter.getView(i, this);
            addView(view);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        unRegisterAdapter();
        super.onDetachedFromWindow();
    }

    private void unRegisterAdapter() {
        // 移除监听
        if (mAdapter != null && mObserver != null && isRegister) {
            isRegister = false;
            mAdapter.unregisterDataSetObserver(mObserver);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerAdapter();
    }

    private void registerAdapter() {
        // 添加监听
        if (mAdapter != null && mObserver != null && !isRegister) {
            mAdapter.registerDataSetObserver(mObserver);
            isRegister = true;
        }
    }

    /**
     * 获取一行的个数
     *
     * @return
     */
    public int getFirstLineSize() {
        try {
            return mViews.get(0).size();
        } catch (Throwable e) {
        }
        return 0;
    }

    /**
     * 间距是否平分
     * 默认不平分
     */
    public void setSquare(boolean square) {
        isSquare = square;
    }

    public void setShowOneLine(boolean showOneLine) {
        this.isShowOneLine = showOneLine;
    }

    public void change() {
        this.isShowOneLine = !this.isShowOneLine;
        resetLayout();
    }

    public XBaseAdapter getAdapter() {
        return mAdapter;
    }

}
