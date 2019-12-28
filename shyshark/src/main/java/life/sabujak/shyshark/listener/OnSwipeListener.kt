package life.sabujak.shyshark.listener

import android.view.View

interface OnSwipeListener {
    fun swiped(direction: Int)
    fun changeHorizontalDrag(direction: Int, percent: Float)
    fun changeVerticalDrag(direction: Int, percent: Float)
}