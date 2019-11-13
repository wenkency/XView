package cn.lven.xview.adapter;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;


/**
 * RecyclerView的通用适配器---》》XQuickViewHolder
 */

public class XQuickViewHolder {
    private SparseArray<WeakReference<View>> mViews;
    private int mLayoutId;
    private View itemView;

    public XQuickViewHolder(View itemView) {
        this(itemView, -1);
    }

    public XQuickViewHolder(View itemView, int layoutId) {
        this.itemView = itemView;
        mViews = new SparseArray<>();
        this.mLayoutId = layoutId;

    }

    public int getLayoutId() {
        return mLayoutId;
    }

    /**
     * 设置条目的点击事件
     */
    public XQuickViewHolder setOnClickListener(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
        return this;
    }

    /**
     * 设置条目的长按事件
     */
    public XQuickViewHolder setOnLongClickListener(View.OnLongClickListener listener) {
        itemView.setOnLongClickListener(listener);
        return this;
    }

    /**
     * 设置View的点击事件
     *
     * @return
     */
    public XQuickViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnLongClickListener(listener);
        }
        return this;
    }

    /**
     * 设置View的点击事件
     *
     * @return
     */
    public XQuickViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
        return this;
    }

    /**
     * 获取条目的View
     */
    public View getView() {
        return itemView;
    }

    /**
     * 根据ID获取条目里面的View
     */
    public <T extends View> T getView(int viewId) {
        WeakReference<View> viewWeakReference = mViews.get(viewId);
        View view = null;
        if (viewWeakReference == null) {
            view = itemView.findViewById(viewId);
            if (view != null) {
                mViews.put(viewId, new WeakReference<>(view));
            }
        } else {
            view = viewWeakReference.get();
        }
        return (T) view;
    }

    public XQuickViewHolder setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        if (tv != null && !TextUtils.isEmpty(text)) {
            tv.setText(text);
        }
        return this;
    }

    /**
     * 设置加粗
     */
    public XQuickViewHolder setBold(TextView tv, boolean isBold) {
        if (tv != null) {
            if (isBold) {
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
        }
        return this;
    }

    /**
     * 设置加粗
     */
    public XQuickViewHolder setBold(int viewId, boolean isBold) {
        TextView tv = getView(viewId);
        if (tv != null) {
            if (isBold) {
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
        }
        return this;
    }

    /**
     * 设置下划线
     */
    public XQuickViewHolder setUnderLine(TextView tv, boolean isUnderLine) {
        if (tv != null) {
            if (isUnderLine) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            } else {
                tv.setPaintFlags(tv.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            }
        }
        return this;
    }

    /**
     * 设置下划线
     */
    public XQuickViewHolder setUnderLine(int viewId, boolean isUnderLine) {
        TextView tv = getView(viewId);
        if (tv != null) {
            if (isUnderLine) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            } else {
                tv.setPaintFlags(tv.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            }
        }
        return this;
    }

    /**
     * 设置删除线
     */
    public XQuickViewHolder setDeleteLine(int viewId, boolean isDeleteLine) {
        TextView tv = getView(viewId);
        if (tv != null) {
            if (isDeleteLine) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
        return this;
    }

    /**
     * 设置删除线
     */
    public XQuickViewHolder setDeleteLine(TextView tv, boolean isDeleteLine) {
        if (tv != null) {
            if (isDeleteLine) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
        return this;
    }

    /**
     * 设置图片背景颜色
     */
    public XQuickViewHolder setTextColor(int viewId, int color) {
        TextView view = getView(viewId);
        if (view != null) {
            view.setTextColor(color);
        }
        return this;
    }

    /**
     * 设置控件是否可见
     */
    public XQuickViewHolder setVisible(int viewId, int visible) {
        View view = getView(viewId);
        view.setVisibility(visible);
        return this;
    }

    /**
     * 设置控件是否可见
     */
    public XQuickViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        if (visible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }

        return this;
    }

    /**
     * 设置控件选中
     */
    public XQuickViewHolder setChecked(int viewId, boolean checked) {
        Checkable view = getView(viewId);
        view.setChecked(checked);
        return this;
    }

    /**
     * 设置控件背景
     */
    public XQuickViewHolder setBackgroundRes(int viewId, int backgroundRes) {
        View view = getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    /**
     * 设置图片
     */
    public XQuickViewHolder setImageResource(int viewId, int imageResId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(imageResId);
        return this;
    }

    /**
     * 设置图片
     */
    public XQuickViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView imageView = getView(viewId);
        imageView.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 根据URL加载图片
     */
    public XQuickViewHolder displayImage(int viewId, String url, int errorResId) {
        ImageView imageView = getView(viewId);
        // TODO 用自己的图片加载
        return this;
    }

    /**
     * 根据URL加载图片
     */
    public XQuickViewHolder displayImage(int viewId, String url) {
        ImageView imageView = getView(viewId);
        // TODO 用自己的图片加载
        return this;
    }

    /**
     * 根据本地资源ID加载图片
     */
    public XQuickViewHolder displayImage(int viewId, int resId) {
        ImageView imageView = getView(viewId);
        // TODO 用自己的图片加载
        return this;
    }

    /**
     * 根据URL加载圆形图片
     */
    public XQuickViewHolder displayCircleImage(int viewId, String url, int errorResId) {
        ImageView imageView = getView(viewId);
        // TODO 用自己的图片加载
        return this;
    }

    /**
     * 根据URL加载圆角图片
     */
    public XQuickViewHolder displayRadiusImage(int viewId, String url, int errorResId, int radius) {
        ImageView imageView = getView(viewId);
        // TODO 用自己的图片加载
        return this;
    }


}
