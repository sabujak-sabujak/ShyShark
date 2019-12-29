package life.sabujak.shyshark

import android.content.Context
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

    fun nextView() {
        (layoutManager as? ShySharkLayoutManager)?.nextView()
    }

    fun previousView() {
        (layoutManager as? ShySharkLayoutManager)?.previousView()
    }

    fun performSwipe(direction: Int) {
        (layoutManager as? ShySharkLayoutManager)?.performSwipe(this, direction)
    }

    init {
        layoutManager = ShySharkLayoutManager(internalDragListener)
            .apply {
                context.theme?.obtainStyledAttributes(
                    attrs,
                    R.styleable.ShySharkView,
                    0, 0
                )?.let {
                    if (it.hasValue(R.styleable.ShySharkView_swipeableFlag))
                        swipeableFlag = it.getInteger(
                            R.styleable.ShySharkView_swipeableFlag,
                            SWIPE_LEFT or SWIPE_RIGHT or SWIPE_TOP or SWIPE_BOTTOM
                        )

                    if (it.hasValue(R.styleable.ShySharkView_preloadCount))
                        preloadCount = it.getInteger(R.styleable.ShySharkView_preloadCount, 3)

                    if (it.hasValue(R.styleable.ShySharkView_scaleGap))
                        scaleGap = it.getFloat(R.styleable.ShySharkView_scaleGap, 0.1f)

                    if (it.hasValue(R.styleable.ShySharkView_dragThrashold))
                        dragThrashold = it.getFloat(R.styleable.ShySharkView_dragThrashold, 0.4f)

                    if (it.hasValue(R.styleable.ShySharkView_defaultElevation))
                        defaultElevation =
                            it.getDimension(R.styleable.ShySharkView_defaultElevation, 0f)

                    if (it.hasValue(R.styleable.ShySharkView_restoreScaleAnimationDuration))
                        restoreScaleAnimationDuration = it.getInteger(
                            R.styleable.ShySharkView_restoreScaleAnimationDuration,
                            200
                        ).toLong()

                    if (it.hasValue(R.styleable.ShySharkView_autoDraggingAnimationDuration))
                        autoDraggingAnimationDuration = it.getInteger(
                            R.styleable.ShySharkView_autoDraggingAnimationDuration,
                            200
                        ).toLong()
                }
            }
    }
}