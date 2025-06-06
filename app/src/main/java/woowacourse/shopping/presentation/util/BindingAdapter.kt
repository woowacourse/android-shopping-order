package woowacourse.shopping.presentation.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.presentation.cart.event.CartEventHandler

@BindingAdapter("loadImage")
fun loadImage(
    imageView: ImageView,
    imageUrl: String?,
) {
    Glide
        .with(imageView)
        .load(imageUrl)
        .placeholder(R.drawable.iced_americano)
        .fallback(R.drawable.iced_americano)
        .error(R.drawable.iced_americano)
        .into(imageView)
}

@BindingAdapter("pageNumber")
fun setPageNumber(
    view: TextView,
    handler: CartEventHandler,
) {
    view.text = (handler.getPage() + 1).toString()
}

@BindingAdapter("enableNextButton")
fun setNextButtonEnabled(
    view: View,
    isEnabled: Boolean,
) {
    view.isEnabled = isEnabled
}

@BindingAdapter("enablePrevButton")
fun setPrevButtonEnabled(
    view: View,
    isEnabled: Boolean,
) {
    view.isEnabled = isEnabled
}
