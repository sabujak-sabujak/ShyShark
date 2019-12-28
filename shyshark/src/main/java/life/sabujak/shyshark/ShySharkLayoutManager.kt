package life.sabujak.shyshark

import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import life.sabujak.shyshark.listener.OnInternalDragListener
import life.sabujak.shyshark.listener.OnSwipeListener
import kotlin.math.abs
import kotlin.math.max

class ShySharkLayoutManager(private val internalDragListener: OnInternalDragListener) :
    RecyclerView.LayoutManager() {

    companion object {
        const val SWIPE_NONE = 0
        const val SWIPE_LEFT = 1
        const val SWIPE_RIGHT = 1 shl 1
        const val SWIPE_TOP = 1 shl 2
        const val SWIPE_BOTTOM = 1 shl 3
    }

    private var swipeFlag = SWIPE_LEFT or SWIPE_RIGHT or SWIPE_TOP or SWIPE_BOTTOM
    private var onSwipeListener: OnSwipeListener? = null
    private var startXPosition = 0f
    private var startYPosition = 0f
    private var topPosition = 0
    private var preloadCount = 3
    private var scaleGap = 0.2f
    private var dragThrashold = .4f


    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        detachAndScrapAttachedViews(recycler!!)
        val itemCount = itemCount
        if (itemCount < 1) {
            return
        }

        val startPosition = (itemCount - 1).coerceAtMost(topPosition + preloadCount - 1)

        for (position in startPosition downTo topPosition) {
            val drawPosition = position - topPosition
            val view = recycler.getViewForPosition(position)!!

            if (position == topPosition) {
                var rawX = 0f
                var rawY = 0f
                view.setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            startXPosition = v.x
                            startYPosition = v.y

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
            addView(view)
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
        }
    }

    fun setOnSwipeListener(onSwipeListener: OnSwipeListener) {
        this.onSwipeListener = onSwipeListener
    }

    private fun getThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return viewHolder.itemView.width * 0.9f
    }

    /**
     * 자식뷰들의 스케일 값을 최상위에 있는 뷰의 드래그만큼 변경해준다.
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
                val scale = Math.max(
                    0f, Math.min(
                        1f,
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

    private fun isSwiped(swipedPercent: Float): Boolean {
        return swipedPercent > dragThrashold
    }

    /**
     * 1.스와이프 방향 체크?
     * 2.
     */
    fun endDrag(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float
    ) {
        val swipeWidthPercent = 1f.coerceAtMost(dX / recyclerView.measuredWidth)
        val swipeHeightPercent = 1f.coerceAtMost(dY / recyclerView.measuredHeight)

        getCandidateDirection(swipeWidthPercent, swipeHeightPercent)
            .filter { isEnableDirection(it) }
            .firstOrNull()
            ?.let {
                onSwiped(
                    viewHolder.itemView,
                    getNextView(recyclerView),
                    it
                )
            }
            ?: kotlin.run {
                transitionAnimation(viewHolder.itemView, startXPosition, startYPosition, 100)
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
        when (direction) {
            SWIPE_TOP -> {
                transitionAnimation(swipedView, swipedView.x, swipedView.y - swipedView.height, 200)
            }
            SWIPE_BOTTOM -> {
                transitionAnimation(swipedView, swipedView.x, swipedView.y + swipedView.height, 200)
            }
            SWIPE_LEFT -> {
                transitionAnimation(swipedView, swipedView.x - swipedView.width, swipedView.y, 200)
            }
            SWIPE_RIGHT -> {
                transitionAnimation(swipedView, swipedView.x + swipedView.width, swipedView.y, 200)

            }
        }

        if (nextView != null) {
            restoreScaleAnimation(nextView, 200)
        }

        android.os.Handler(Looper.getMainLooper()).postDelayed({
            topPosition++
            requestLayout()
            onSwipeListener?.swiped(direction)
        }, 200)

    }

    private fun validateScale(value: Float): Float {
        return Math.max(0f, Math.min(1f, value))
    }

    private fun transitionAnimation(view: View, x: Float, y: Float, duration: Long = 0) {
        view.animate()
            .x(x).y(y)
            .setDuration(duration)
            .start()
    }

    private fun restoreScaleAnimation(view: View, duration: Long = 0) {
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(duration)
            .start()
    }

    private fun getNextView(recyclerView: RecyclerView): View? {
        if (recyclerView.childCount < 2) return null
        return recyclerView.getChildAt(childCount - 2)
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

    private fun isEnableDirection(direction: Int) = (swipeFlag and direction) == direction

}
