package woowacourse.shopping.util

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("app:image")
fun ImageView.setImage(imgUrl: String) {
    Glide.with(context)
        .load(imgUrl)
        .into(this)
}

@BindingAdapter("app:setThrottleFirstOnClick")
inline fun View.setBindingThrottleFirstOnClickListener(
    crossinline block: (View) -> Unit
) {
    val delay: Long = 50L
    var previousClickedTime = 0L
    setOnClickListener { view ->
        val clickedTime = System.currentTimeMillis()
        if (clickedTime - previousClickedTime >= delay) {
            block(view)
            previousClickedTime = clickedTime
        }
    }
}
