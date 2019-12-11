package life.sabujak.shyshark

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class ShySharkLayoutManager : RecyclerView.LayoutManager() {
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        // layout algorithm:
        // 1) by checking children and other variables, find an anchor coordinate and an anchor
        //  item position.
        // 2) fill towards start, stacking from bottom
        // 3) fill towards end, stacking from top
        // 4) scroll to fulfill requirements like stack from bottom.
        // create layout state
        super.onLayoutChildren(recycler, state)
        for (i in 0 until itemCount) {
            val view = recycler?.getViewForPosition(i)
            addView(view!!)
            measureChildWithMargins(view, 0, 0)
            layoutDecorated(
                view, 0, 0,
                getDecoratedMeasuredWidth(view),
                getDecoratedMeasuredHeight(view)
            )
        }


    }
}