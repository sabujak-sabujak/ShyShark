package life.sabujak.shyshark

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import life.sabujak.shyshark.listener.OnInternalDragListener
import life.sabujak.shyshark.listener.OnSwipeListener
import kotlin.math.abs


class ShySharkLayoutManager(private val internalDragListener: OnInternalDragListener) :
    RecyclerView.LayoutManager() {

    companion object {
        const val SWIPE_NONE = 0
        const val SWIPE_LEFT = 1
        const val SWIPE_RIGHT = 1 shl 1
        const val SWIPE_TOP = 1 shl 2
        const val SWIPE_BOTTOM = 1 shl 3
    }

    private var onSwipeListener: OnSwipeListener? = null
    private var startXPosition: Float? = null
    private var startYPosition: Float? = null
    private var topPosition = 0

    var swipeableFlag = SWIPE_LEFT or SWIPE_RIGHT or SWIPE_TOP or SWIPE_BOTTOM
    var preloadCount = 3
    var scaleGap = 0.1f
    var dragThrashold = .4f
    var defaultElevation = 0f
    var restoreScaleAnimationDuration = 200L
    var autoDraggingAnimationDuration = 200L

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)
        val itemCount = itemCount
        if (itemCount < 1) {
            return
        }

        val startPosition = (itemCount - 1).coerceAtMost(topPosition + preloadCount - 1)

        for (position in startPosition downTo topPosition) {
            val drawPosition = position - topPosition
            val view = recycler.getViewForPosition(position)

            defaultElevation = defaultElevation.coerceAtLeast(ViewCompat.getElevation(view))

            if (position == topPosition) {
                var rawX = 0f
                var rawY = 0f
                view.setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            rawX = v.x - event.rawX
                            rawY = v.y - event.rawY
                        }
                        MotionEvent.ACTION_UP -> {
                            internalDragListener.onDragEnded(
                                v,
                                rawX + event.rawX,
                                rawY + event.rawY
                            )
                        }
                        MotionEvent.ACTION_MOVE -> {
                            internalDragListener.onDrag(v, rawX + event.rawX, rawY + event.rawY)
                            transitionAnimation(
                                v,
                                rawX + event.rawX, rawY + event.rawY
                            )
                        }
                    }
                    true
                }
            }

            if (position == topPosition) {
                ViewCompat.setElevation(view, defaultElevation)
            } else {
                ViewCompat.setElevation(view, defaultElevation - 1f * drawPosition)
            }

            measureChildWithMargins(view, 0, 0)
            val widthSpace = width - getDecoratedMeasuredWidth(view)
            val heightSpace = height - getDecoratedMeasuredHeight(view)

            layoutDecorated(
                view, widthSpace / 2, heightSpace / 2,
                widthSpace / 2 + getDecoratedMeasuredWidth(view),
                heightSpace / 2 + getDecoratedMeasuredHeight(view)
            )

            if (drawPosition > 0) {
                view.scaleX = validateScale(1 - scaleGap * drawPosition)
                view.scaleY = validateScale(1 - scaleGap * drawPosition)
            } else {
                view.scaleX = validateScale(1f)
                view.scaleY = validateScale(1f)
            }

            if (startXPosition == null || startYPosition == null) {
                startXPosition = view.x
                startYPosition = view.y
            }

            addView(view)
        }
    }

    fun nextView() {
        topPosition++
        requestLayout()
    }

    fun previousView() {
        if (topPosition - 1 < 0) topPosition = 0
        else topPosition--
        requestLayout()
    }

    fun setOnSwipeListener(onSwipeListener: OnSwipeListener) {
        this.onSwipeListener = onSwipeListener
    }

    private fun getThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return viewHolder.itemView.width * 0.9f
    }

    /**
     * Change the child view's scale value by dragging the parent view.
     */
    fun changeChildViewScale(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float
    ) {
        val swipeValue = Math.sqrt(dX * dX + dY * dY.toDouble())
        var fraction: Double = swipeValue / getThreshold(viewHolder)
        fraction = Math.min(1.0, fraction)

        val childCount = recyclerView.childCount
        for (i in 0 until childCount) {
            val child = recyclerView.getChildAt(i)
            val level: Int = childCount - i - 1
            if (level > 0) {
                val scale = 0f.coerceAtLeast(
                    1f.coerceAtMost(
                        (1 - scaleGap * level
                                + fraction * scaleGap).toFloat()
                    )
                )
                child.scaleX = scale
                if (level < preloadCount - 1) {
                    child.scaleY = scale
                }
            }
        }
    }

    fun changeDragPercent(
        recyclerView: RecyclerView,
        dX: Float,
        dY: Float
    ) {
        val swipeWidthPercent = 1f.coerceAtMost(dX / recyclerView.measuredWidth)
        val swipeHeightPercent = 1f.coerceAtMost(dY / recyclerView.measuredHeight)

        if (isEnableDirection(judgeLeftOrRight(swipeWidthPercent))) {
            onSwipeListener?.changeHorizontalDrag(
                judgeLeftOrRight(swipeWidthPercent),
                abs(swipeWidthPercent)
            )
        }
        if (isEnableDirection(judgeTopOrBottom(swipeHeightPercent))) {
            onSwipeListener?.changeHorizontalDrag(
                judgeTopOrBottom(swipeHeightPercent),
                abs(swipeHeightPercent)
            )
        }

    }


    fun performSwipe(
        recyclerView: RecyclerView,
        direction: Int
    ) {
        val topView = getTopView(recyclerView)
        if (isEnableDirection(direction) && topView != null) {
            onSwiped(topView, getNextView(recyclerView), direction)
        }
    }

    private fun isSwiped(swipedPercent: Float): Boolean {
        return swipedPercent > dragThrashold
    }

    fun endDrag(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float
    ) {
        val swipeWidthPercent = 1f.coerceAtMost(dX / recyclerView.measuredWidth)
        val swipeHeightPercent = 1f.coerceAtMost(dY / recyclerView.measuredHeight)

        getCandidateDirection(
            swipeWidthPercent,
            swipeHeightPercent
        ).firstOrNull { isEnableDirection(it) }
            ?.let {
                onSwiped(
                    viewHolder.itemView,
                    getNextView(recyclerView),
                    it
                )
            }
            ?: kotlin.run {
                transitionAnimation(
                    viewHolder.itemView,
                    startXPosition ?: 0f,
                    startYPosition ?: 0f,
                    restoreScaleAnimationDuration
                )
            }
    }

    private fun judgeTopOrBottom(percent: Float): Int =
        if (percent > 0) SWIPE_BOTTOM
        else SWIPE_TOP


    private fun judgeLeftOrRight(percent: Float): Int {
        return if (percent > 0) {
            SWIPE_RIGHT
        } else {
            SWIPE_LEFT
        }
    }

    private fun onSwiped(
        swipedView: View,
        nextView: View?,
        direction: Int
    ) {

        fun swipedAnimation(x: Float, y: Float) {
            transitionAnimation(swipedView, x, y, autoDraggingAnimationDuration) {
                clearView(swipedView)
                topPosition++
                requestLayout()
                onSwipeListener?.swiped(direction)
            }
        }

        when (direction) {
            SWIPE_TOP -> {
                swipedAnimation(swipedView.x, swipedView.y - swipedView.height)
            }
            SWIPE_BOTTOM -> {
                swipedAnimation(swipedView.x, swipedView.y + swipedView.height)
            }
            SWIPE_LEFT -> {
                swipedAnimation(swipedView.x - swipedView.width, swipedView.y)
            }
            SWIPE_RIGHT -> {
                swipedAnimation(swipedView.x + swipedView.width, swipedView.y)

            }
        }

        if (nextView != null) {
            restoreScaleAnimation(nextView, restoreScaleAnimationDuration)
        }
    }

    private fun validateScale(value: Float): Float {
        return 0f.coerceAtLeast(1f.coerceAtMost(value))
    }

    private fun transitionAnimation(
        view: View,
        x: Float,
        y: Float,
        duration: Long = 0,
        endAction: (() -> Unit)? = null
    ) {
        view.animate()
            .x(x).y(y)
            .setDuration(duration)
            .withEndAction(endAction)
            .start()
    }

    private fun restoreScaleAnimation(view: View, duration: Long = 0) {
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(duration)
            .start()
    }

    private fun clearView(view: View) {
        view.setOnTouchListener(null)
        view.x = startXPosition ?: 0f
        view.y = startYPosition ?: 0f
    }

    private fun getNextView(recyclerView: RecyclerView): View? {
        if (recyclerView.childCount < 2) return null
        return recyclerView.getChildAt(childCount - 2)
    }

    private fun getTopView(recyclerView: RecyclerView): View? {
        if (recyclerView.childCount == 0) return null
        return recyclerView.getChildAt(childCount - 1)
    }

    private fun getCandidateDirection(
        widthPercent: Float,
        heightPercent: Float
    ): List<Int> {
        val candidates = arrayListOf<Pair<Float, Int>>()
        if (isSwiped(abs(widthPercent))) {
            candidates.add(widthPercent to judgeLeftOrRight(widthPercent))
        }
        if (isSwiped(abs(heightPercent))) {
            candidates.add(heightPercent to judgeTopOrBottom(heightPercent))
        }
        return candidates
            .sortedByDescending { abs(it.first) }
            .map { it.second }
    }

    private fun isEnableDirection(direction: Int) = (swipeableFlag and direction) == direction

}
