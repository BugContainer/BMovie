package com.bugli.bmovie.ui.recommend

import android.graphics.Canvas
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bugli.bmovie.data.model.Movie
import kotlin.math.hypot

class ItemTouchHelperCallback(
    private val mDatas: MutableList<Movie>,
    private val mAdapter: RVMyAdapter
) : ItemTouchHelper.Callback() {

    //真实被划走的item的position
    private var truePos = 0
    private var leftOrRight = 0
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        //决定是否能否左右上下滑动
        return makeMovementFlags(
            0, ItemTouchHelper.LEFT  //                ItemTouchHelper.LEFT
            //                        | ItemTouchHelper.RIGHT
            //                | ItemTouchHelper.DOWN
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        viewHolder1: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if (leftOrRight == 0) {
            //将滑动的item从List的末尾移动到开头
            val s = mDatas.removeAt(viewHolder.layoutPosition)
            mDatas.add(0, s)
            //滑动结束
            if (truePos == 0) {
                truePos = viewHolder.layoutPosition
            } else {
                truePos--
            }
        }/*else if (leftOrRight == 1) {
            val s = mDatas.removeAt(0)
            mDatas.add(s)
            if (truePos == viewHolder.layoutPosition) {
                truePos = 0
            } else {
                truePos ++
            }

        }*/
        Log.e("bugli", truePos.toString() + "刚移除的pos ")
        //同步更新 adapter的数据
        mAdapter.updateData(mDatas)
        mAdapter.notifyDataSetChanged()
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        //计算移动距离
        val distance =
            hypot(dX.toDouble(), dY.toDouble()).toFloat()
        if (dX > 0) {
            leftOrRight = 1
            Log.e("bugli", "向右")
        } else if (dX < 0) {
            leftOrRight = 0
            Log.e("bugli", "向左")
        }
        //        Log.e("bugli", "dX=" + dX + "dY = " + dY + "distance = " + distance);
        val maxDistance = recyclerView.width / 2f

        //比例
        var fraction = distance / maxDistance
        if (fraction > 1) {
            fraction = 1f
        }
        //为每个child执行动画
        val count = recyclerView.childCount
        for (i in 0 until count) {
            //获取的view从下层到上层
            val view = recyclerView.getChildAt(i)
            val level = CardConfig.SHOW_MAX_COUNT - i - 1
            //level范围（CardConfig.SHOW_MAX_COUNT-1）-0，每个child最大只移动一个CardConfig.TRANSLATION_Y和放大CardConfig.SCALE
            if (level == CardConfig.SHOW_MAX_COUNT - 1) { // 最下层的不动和最后第二层重叠
                view.translationY = CardConfig.TRANSLATION_Y * (level - 1)
                view.scaleX = 1 - CardConfig.SCALE * (level - 1)
                view.scaleY = 1 - CardConfig.SCALE * (level - 1)
            } else if (level > 0) {
                view.translationY =
                    level * CardConfig.TRANSLATION_Y - fraction * CardConfig.TRANSLATION_Y
                val scaleY =
                    1 - level * CardConfig.SCALE + fraction * CardConfig.SCALE
                view.scaleX = scaleY
                view.scaleY = scaleY
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.3f
    }

}