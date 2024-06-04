package woowacourse.shopping.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("imageUrl")
fun setImageViewResource(
    imageView: ImageView,
    resUrl: String?,
) {
    Glide.with(imageView.context)
        .load(resUrl)
        .fallback(android.R.drawable.ic_menu_report_image)
        .error(android.R.drawable.ic_menu_report_image)
        .thumbnail()
        .into(imageView)
}
