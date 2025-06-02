package woowacourse.shopping.presentation.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("imageUrl")
fun setImageBindingAdapter(
    imageView: ImageView,
    imageUrl: String?,
) {
    Glide
        .with(imageView)
        .load(imageUrl)
        .into(imageView)
}
