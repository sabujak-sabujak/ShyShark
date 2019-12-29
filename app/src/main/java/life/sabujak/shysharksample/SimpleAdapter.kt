package life.sabujak.shysharksample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class SimpleAdapter(private val sharkImages:List<Int>) : RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_simple,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = sharkImages.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(position, sharkImages[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textView = itemView.findViewById<TextView>(R.id.textView)
        private val imageView = itemView.findViewById<ImageView>(R.id.imageView)

        fun setData(position: Int, image:Int) {
            textView.text = position.toString()
            Picasso.get()
                .load(image)
                .fit()
                .centerCrop()
                .into(imageView)
        }

    }

}