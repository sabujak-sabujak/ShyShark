package life.sabujak.shyshark

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import life.sabujak.shyshark.ShySharkLayoutManager.Companion.SWIPE_BOTTOM
import life.sabujak.shyshark.ShySharkLayoutManager.Companion.SWIPE_LEFT
import life.sabujak.shyshark.ShySharkLayoutManager.Companion.SWIPE_RIGHT
import life.sabujak.shyshark.ShySharkLayoutManager.Companion.SWIPE_TOP
import life.sabujak.shyshark.listener.OnInternalDragListener
import life.sabujak.shyshark.listener.OnSwipeListener

class ShySharkView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {

    private var onSwipeListener: OnSwipeListener? = null
    private val shySharkLayoutManager: ShySharkLayoutManager

    init {
        shySharkLayoutManager = ShySharkLayoutManager(getInternalDragListener())
            .apply {
                context.theme?.obtainStyledAttributes(
                    attrs,
                    R.styleable.ShySharkView,
                    0, 0
                )?.let { initAttribute(this, it) }
            }.also {
                layoutManager = it
            }
    }

    fun setOnSwipeListener(onSwipeListener: OnSwipeListener) {
        this.onSwipeListener = onSwipeListener
        shySharkLayoutManager.setOnSwipeListener(onSwipeListener)
    }

    fun nextView() {
        shySharkLayoutManager.nextView()
    }

    fun previousView() {
        shySharkLayoutManager.previousView()
    }

    fun performSwipe(direction: Int) {
        shySharkLayoutManager.performSwipe(this, direction)
    }

    private fun getInternalDragListener() = object :
        OnInternalDragListener {
        override fun onDrag(v: View, x: Float, y: Float) {
            shySharkLayoutManager.run {
                changeChildViewScale(this@ShySharkView, getChildViewHolder(v), x, y)
                changeDragPercent(this@ShySharkView, x, y)
            }
        }

        override fun onDragEnded(v: View, x: Float, y: Float) {
            shySharkLayoutManager.onDragEnded(this@ShySharkView, getChildViewHolder(v), x, y)
        }
    }

    private fun initAttribute(
        shySharkLayoutManager: ShySharkLayoutManager,
        shySharkAttribute: TypedArray
    ) {
        shySharkLayoutManager.apply {
            if (shySharkAttribute.hasValue(R.styleable.ShySharkView_swipeableFlag))
                swipeableFlag = shySharkAttribute.getInteger(
                    R.styleable.ShySharkView_swipeableFlag,
                    SWIPE_LEFT or SWIPE_RIGHT or SWIPE_TOP or SWIPE_BOTTOM
                )

            if (shySharkAttribute.hasValue(R.styleable.ShySharkView_preloadCount))
                preloadCount =
                    shySharkAttribute.getInteger(R.styleable.ShySharkView_preloadCount, 3)

            if (shySharkAttribute.hasValue(R.styleable.ShySharkView_scaleGap))
                scaleGap = shySharkAttribute.getFloat(R.styleable.ShySharkView_scaleGap, 0.1f)

            if (shySharkAttribute.hasValue(R.styleable.ShySharkView_dragThrashold))
                dragThrashold =
                    shySharkAttribute.getFloat(R.styleable.ShySharkView_dragThrashold, 0.2f)

            if (shySharkAttribute.hasValue(R.styleable.ShySharkView_defaultElevation))
                defaultElevation =
                    shySharkAttribute.getDimension(R.styleable.ShySharkView_defaultElevation, 0f)

            if (shySharkAttribute.hasValue(R.styleable.ShySharkView_restoreScaleAnimationDuration))
                restoreScaleAnimationDuration = shySharkAttribute.getInteger(
                    R.styleable.ShySharkView_restoreScaleAnimationDuration,
                    200
                ).toLong()

            if (shySharkAttribute.hasValue(R.styleable.ShySharkView_autoDraggingAnimationDuration))
                autoDraggingAnimationDuration = shySharkAttribute.getInteger(
                    R.styleable.ShySharkView_autoDraggingAnimationDuration,
                    200
                ).toLong()
        }
    }
}