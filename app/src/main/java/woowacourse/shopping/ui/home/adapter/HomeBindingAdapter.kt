package woowacourse.shopping.ui.home.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.ui.state.UiState

@BindingAdapter("app:imageUrl")
fun loadImage(
    view: ImageView,
    url: String?,
) {
    if (!url.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(url)
            .error(R.drawable.ic_launcher_background)
            .into(view)
    }
}

@BindingAdapter("app:price")
fun setPrice(
    view: TextView,
    price: Int,
) {
    view.text = view.context.getString(R.string.price_format, price)
}

@BindingAdapter("app:viewVisibility")
fun setViewVisibility(
    view: View,
    isVisible: Boolean,
) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("app:totalQuantityVisibility")
fun setTotalQuantityVisibility(
    textView: TextView,
    totalQuantity: Int,
) {
    textView.visibility = if (totalQuantity > 0) View.VISIBLE else View.GONE
}

@BindingAdapter("app:recentProductsVisibility")
fun <T> setRecentProductsVisibility(
    view: View,
    state: UiState<T>,
) {
    view.visibility =
        if (state is UiState.Success) View.VISIBLE else View.GONE
}
