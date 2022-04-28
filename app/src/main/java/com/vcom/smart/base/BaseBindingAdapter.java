package com.vcom.smart.base;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Lzz
 * @Date 2020/10/30 15:49
 */
public abstract class BaseBindingAdapter<M, VDB extends ViewDataBinding> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context mContext;
    protected List<M> items;

    public BaseBindingAdapter(Context mContext) {
        this.mContext = mContext;
        this.items = new ArrayList<>();
    }

    public void setItems(List<M> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VDB vdb = DataBindingUtil.inflate(LayoutInflater.from(mContext), getLayoutId(viewType), parent, false);
        return new BaseBindingViewHolder(vdb.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VDB vdb = DataBindingUtil.getBinding(holder.itemView);
        this.onBindItem(vdb, this.items.get(position), position);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @LayoutRes
    protected abstract int getLayoutId(int layoutId);

    protected abstract void onBindItem(VDB vdb, M item, int i);

    public static class BaseBindingViewHolder extends RecyclerView.ViewHolder {

        public BaseBindingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
