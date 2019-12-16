package life.sabujak.shyshark

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ShySharkView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {

    private val itemTouchHelperCallback = ShySharkItemTouchHelperCallback().apply {
        onInternalSwipeListener = object : OnInternalSwipeListener {
            override fun onSwiped() {
                (layoutManager as? ShySharkLayoutManager)?.onSwiped()
            }
        }
    }

    init {
        layoutManager = ShySharkLayoutManager()
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
    }
}