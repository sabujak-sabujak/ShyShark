package life.sabujak.shyshark

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import life.sabujak.shyshark.ShySharkLayoutManager.Companion.SWIPE_BOTTOM
import life.sabujak.shyshark.ShySharkLayoutManager.Companion.SWIPE_LEFT
import life.sabujak.shyshark.ShySharkLayoutManager.Companion.SWIPE_RIGHT
import life.sabujak.shyshark.ShySharkLayoutManager.Companion.SWIPE_TOP
import life.sabujak.shyshark.listener.OnInternalDragListener
import life.sabujak.shyshark.listener.OnSwipeListener

class ShySharkView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {
    private var onSwipeListener: OnSwipeListener? = null

    private val internalDragListener = object :
        OnInternalDragListener {
        override fun onDrag(v: View, x: Float, y: Float) {
            (layoutManager as? ShySharkLayoutManager)?.run {
                changeChildViewScale(this@ShySharkView, getChildViewHolder(v), x, y)
                changeDragPercent(this@ShySharkView, x, y)
            }
        }

        override fun onDragEnded(v: View, x: Float, y: Float) {
            (layoutManager as ShySharkLayoutManager).endDrag(
                this@ShySharkView,
                getChildViewHolder(v),
                x,
                y
            )
        }
    }

    fun setOnSwipeListener(onSwipeListener: OnSwipeListener) {
        this.onSwipeListener = onSwipeListener
        (layoutManager as? ShySharkLayoutManager)?.setOnSwipeListener(onSwipeListener)
    }

    init {
        layoutManager = ShySharkLayoutManager(internalDragListener)
    }
}