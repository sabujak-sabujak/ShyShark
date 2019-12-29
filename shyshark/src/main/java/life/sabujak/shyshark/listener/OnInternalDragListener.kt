package life.sabujak.shyshark.listener

import android.view.View

interface OnInternalDragListener {
    fun onDrag(v: View, x: Float, y: Float)
    fun onDragEnded(v: View, x: Float, y: Float)
}