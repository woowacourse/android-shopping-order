package woowacourse.shopping.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import woowacourse.shopping.R

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun ImageView.loadImage(imageUrl: String) {
        Glide
            .with(this)
            .load(imageUrl)
            .placeholder(R.drawable.fallback_image)
            .fallback(R.drawable.fallback_image)
            .error(R.drawable.fallback_image)
            .into(this)
    }
}
