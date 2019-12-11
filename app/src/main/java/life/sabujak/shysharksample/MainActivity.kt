package life.sabujak.shysharksample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import life.sabujak.shyshark.ShySharkLayoutManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.layoutManager = ShySharkLayoutManager()
        recyclerView.adapter = SimpleAdapter()

    }
}
