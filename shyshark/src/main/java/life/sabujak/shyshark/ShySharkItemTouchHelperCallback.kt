package life.sabujak.shyshark

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ShySharkItemTouchHelperCallback : ItemTouchHelper.Callback() {

    var onInternalSwipeListener: OnInternalSwipeListener? = null

    private fun getAllowedDirectionsMovementFlags(holder: RecyclerView.ViewHolder?): Int {
        return getAllowedDirectionsMovementFlags()
    }

    private fun getAllowedDirectionsMovementFlags(): Int {
        return ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlags =
            ItemTouchHelper.START or ItemTouchHelper.END or ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(
            0,
            swipeFlags
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        println("onSwiped")
        onInternalSwipeListener?.onSwiped()
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.5f
    }

    override fun getAnimationDuration(
        recyclerView: RecyclerView,
        animationType: Int,
        animateDx: Float,
        animateDy: Float
    ): Long {
        return 100
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
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val swipValue = Math.sqrt(dX * dX + dY * dY.toDouble())
        var fraction: Double = swipValue / getThreshold(viewHolder)
        fraction = Math.min(1.0, fraction)
        val childCount = recyclerView.childCount
        val layoutManager = recyclerView.layoutManager as ShySharkLayoutManager
        for (i in 0 until childCount) {
            val child = recyclerView.getChildAt(i)
            val level: Int = childCount - i - 1
            if (level > 0) {
                val scale = Math.max(
                    0f, Math.min(
                        1f,
                        (1 - layoutManager.scaleGap * level
                                + fraction * layoutManager.scaleGap).toFloat()
                    )
                )
                child.scaleX = scale
                if (level < layoutManager.preloadCount - 1) {
                    child.scaleY = scale
                    child.translationY = Math.max(
                        0f, (layoutManager.transYGap * level
                                - fraction * layoutManager.transYGap).toFloat()
                    )
                }
            }
        }
    }


    private fun getThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return viewHolder.itemView.width * 0.9f
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.rotation = 0f
        viewHolder.itemView.scaleX = 1f
        viewHolder.itemView.scaleY = 1f
    }

    override fun isLongPressDragEnabled(): Boolean = false
}