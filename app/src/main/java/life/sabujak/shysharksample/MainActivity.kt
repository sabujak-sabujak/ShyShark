package life.sabujak.shysharksample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import life.sabujak.shyshark.listener.OnSwipeListener
import life.sabujak.shyshark.ShySharkLayoutManager.Companion.SWIPE_BOTTOM
import life.sabujak.shyshark.ShySharkLayoutManager.Companion.SWIPE_LEFT
import life.sabujak.shyshark.ShySharkLayoutManager.Companion.SWIPE_RIGHT
import life.sabujak.shyshark.ShySharkLayoutManager.Companion.SWIPE_TOP

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.adapter = SimpleAdapter()
        recyclerView.setOnSwipeListener(object :
            OnSwipeListener {
            override fun swiped(direction: Int) {
                when (direction) {
                    SWIPE_LEFT -> {
                        println("LEFT!!")
                    }
                    SWIPE_RIGHT -> {
                        println("RIGHT")
                    }
                    SWIPE_TOP -> {
                        println("TOP")
                    }
                    SWIPE_BOTTOM -> {
                        println("BOTTOM")
                    }
                }
            }

            override fun changeHorizontalDrag(direction: Int, percent: Float) {
                println("changeHorizontalDrag $direction $percent")
            }

            override fun changeVerticalDrag(direction: Int, percent: Float) {
                println("changeVerticalDrag $direction $percent")
            }
        })
    }
}
