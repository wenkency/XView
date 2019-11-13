package cn.lven.xview.view;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import cn.lven.xview.adapter.XBaseAdapter;
import cn.lven.xview.adapter.LifecycleCallbacks;


/**
 * 为多个子View定义出来的ViewGroup
 */

public abstract class AdapterLayout extends ViewGroup {
    protected XBaseAdapter mAdapter;
    protected DataSetObserver mObserver;
    private boolean isRegister = false;

    public AdapterLayout(Context context) {
        this(context, null);
    }

    public AdapterLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdapterLayout(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (context instanceof Activity) {
            ((Activity) context).getApplication().registerActivityLifecycleCallbacks(new LifecycleCallbacks() {
                @Override
                public void onActivityDestroyed(Activity activity) {
                    if (activity == context) {
                        // 移除监听
                        if (mAdapter != null && mObserver != null) {
                            mAdapter.unregisterDataSetObserver(mObserver);
                            mAdapter = null;
                            mObserver = null;
                            //mViews = null;
                        }
                        ((Activity) context).getApplication().unregisterActivityLifecycleCallbacks(this);
                    }

                }
            });
        }
    }

    /**
     * 设置Adapter
     */
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

        resetLayout();

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
     * 重新添加布局
     */
    protected void resetLayout() {
        if (mAdapter == null) {
            return;
        }
        removeAllViews();
        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            View view = mAdapter.getView(i, this);
            view.setFocusable(true);
            addView(view);
        }
    }

}
