package life.sabujak.shysharksample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import life.sabujak.shyshark.ShySharkLayoutManager.Companion.SWIPE_BOTTOM
import life.sabujak.shyshark.ShySharkLayoutManager.Companion.SWIPE_LEFT
import life.sabujak.shyshark.ShySharkLayoutManager.Companion.SWIPE_RIGHT
import life.sabujak.shyshark.ShySharkLayoutManager.Companion.SWIPE_TOP
import life.sabujak.shyshark.listener.OnSwipeListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.adapter = SimpleAdapter(
            arrayListOf(
                R.drawable.shyshark_1,
                R.drawable.shyshark_2,
                R.drawable.shyshark_3,
                R.drawable.shyshark_4,
                R.drawable.shyshark_5,
                R.drawable.shyshark_6,
                R.drawable.shyshark_7,
                R.drawable.shyshark_8,
                R.drawable.shyshark_9
            )
        )
        recyclerView.setOnSwipeListener(object :
            OnSwipeListener {
            override fun swiped(direction: Int) {
                when (direction) {
                    SWIPE_LEFT -> {
                        println("swiped LEFT!!")
                    }
                    SWIPE_RIGHT -> {
                        println("swiped RIGHT")
                    }
                    SWIPE_TOP -> {
                        println("swiped TOP")
                    }
                    SWIPE_BOTTOM -> {
                        println("swiped BOTTOM")
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

        fab_main_good.setOnClickListener {
            recyclerView.performSwipe(SWIPE_RIGHT)
        }
        fab_main_bad.setOnClickListener {
            recyclerView.performSwipe(SWIPE_LEFT)
        }
        fab_main_next.setOnClickListener {
            recyclerView.nextView()
        }
        fab_main_previous.setOnClickListener {
            recyclerView.previousView()
        }
    }
}
