package com.vcom.smart.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Banlap on 2021/1/21
 */
public class DefaultItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ItemTouchHelperAdapter itemTouchHelperAdapter;
    private Activity activity;
    private int mCurrentScrollX = 0;

    public DefaultItemTouchHelperCallback(ItemTouchHelperAdapter adapter, Activity activity){
        this.itemTouchHelperAdapter = adapter;
        this.activity = activity;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.LEFT;
        return makeMovementFlags(0, swipeFlags);
    }


    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        itemTouchHelperAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //如果dX小于等于删除方块的宽度，那么我们把该方块滑出来
            //viewHolder.itemView.scrollTo((int) -dX,0);
           /* if(Math.abs(dX) <= getSlideLimitation(viewHolder)){
                //viewHolder.itemView.setScrollX(0);
                viewHolder.itemView.setScrollX((int) -dX);
                mCurrentScrollX = (int) dX;
            } else {
                viewHolder.itemView.setScrollX(-mCurrentScrollX);
            }*/

            //int value = (int)(Math.abs(dX)/getSlideLimitation(viewHolder)) ;
            WindowManager wm = activity.getWindowManager();
            float width = wm.getDefaultDisplay().getWidth();
            float getDeleteWidth = getSlideLimitation(viewHolder);
            double move =  getDeleteWidth/width * Math.abs(dX);
            viewHolder.itemView.setScrollX((int)move);
            Log.d("SWIPE", "getScrollX: " + viewHolder.itemView.getScrollX() + " dX: " + dX +
                        "width: " + getSlideLimitation(viewHolder) + "dfWidth:" + width
                        + "move: " + move);
            if((int)move == getSlideLimitation(viewHolder)){
                clearView(recyclerView, viewHolder);
            }


        }
        //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    //拖动完成之后调用，所操作的viewHolder即为目标位置的项目
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

    }

    /**
     * 获取删除方块的宽度
     */
    public int getSlideLimitation(RecyclerView.ViewHolder viewHolder){
        ViewGroup viewGroup = (ViewGroup) viewHolder.itemView;
        return viewGroup.getChildAt(1).getLayoutParams().width;
    }


}
