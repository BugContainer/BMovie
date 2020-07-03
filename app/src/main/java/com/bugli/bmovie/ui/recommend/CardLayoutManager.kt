package com.bugli.bmovie.ui.recommend

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

class CardLayoutManager : RecyclerView.LayoutManager() {
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    /**
     * 自定义LayoutManager核心是摆放控件，所以onLayoutChildren方法是我们要改写的核心
     *
     * @param recycler
     * @param state
     */
    override fun onLayoutChildren(
        recycler: Recycler,
        state: RecyclerView.State
    ) {
        //缓存
        detachAndScrapAttachedViews(recycler)

        //获取所有item(包括不可见的)个数
        val count = itemCount
        //由于我们是倒序摆放，所以初始索引从后面开始
        var initIndex = count - CardConfig.SHOW_MAX_COUNT
        if (initIndex < 0) {
            initIndex = 0
        }
        for (i in initIndex until count) {
            //从缓存中获取view
            val view = recycler.getViewForPosition(i)
            //添加到recyclerView
            addView(view)
            //测量一下view
            measureChild(view, 0, 0)

            //居中摆放，getDecoratedMeasuredWidth方法是获取带分割线的宽度，比直接使用view.getWidth()精确
            val realWidth = getDecoratedMeasuredWidth(view)
            val realHeight = getDecoratedMeasuredHeight(view)
            val widthPadding = ((width - realWidth) / 2f).toInt()
            val heightPadding = ((height - realHeight) / 2f).toInt()

            //摆放child
            layoutDecorated(
                view, widthPadding, heightPadding,
                widthPadding + realWidth, heightPadding + realHeight
            )
            //根据索引，来位移和缩放child
            var level = count - i - 1
            //level范围（CardConfig.SHOW_MAX_COUNT-1）- 0
            // 最下层的不动和最后第二层重叠
            if (level == CardConfig.SHOW_MAX_COUNT - 1) {
                level--
            }
            view.translationY = level * CardConfig.TRANSLATION_Y
            view.scaleX = 1 - level * CardConfig.SCALE
            view.scaleY = 1 - level * CardConfig.SCALE
        }
    }
}