package life.sabujak.shyshark

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class ShySharkLayoutManager : RecyclerView.LayoutManager() {

     var topPosition = 0
     var preloadCount = 3
     val scaleGap = 0.2f
     val transYGap = 0

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
                println(view)
                view.scaleX = validateScale(1 - scaleGap * drawPosition)
                view.scaleY = validateScale(1 - scaleGap * drawPosition)
            } else {
                view.scaleX = validateScale(1f)
                view.scaleY = validateScale(1f)
            }
        }
    }

    fun onSwiped() {
        topPosition++
        requestLayout()
    }

    private fun validateScale(value: Float): Float {
        return Math.max(0f, Math.min(1f, value))
    }

    private fun validateTranslation(value: Float): Float {
        return Math.max(0f, value)
    }
}