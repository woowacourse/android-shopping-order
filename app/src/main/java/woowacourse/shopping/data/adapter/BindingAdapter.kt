package woowacourse.shopping.data.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.view.state.OrderState
import woowacourse.shopping.view.state.UiState

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

@BindingAdapter("app:isEmpty", "app:state", requireAll = true)
fun <T> setEmptyCartVisibility(
    textView: TextView,
    isEmpty: Boolean,
    state: UiState<T>,
) {
    textView.visibility =
        if (state is UiState.Success && isEmpty) View.VISIBLE else View.GONE
}

@BindingAdapter("app:isEmpty", "app:state", requireAll = true)
fun <T> setCartVisibility(
    recyclerView: RecyclerView,
    isEmpty: Boolean,
    state: UiState<T>,
) {
    recyclerView.visibility =
        if ((state is UiState.Success && !isEmpty) || state is UiState.Loading) View.VISIBLE else View.GONE
}

@BindingAdapter("app:allCheckBoxVisibility")
fun setAllCheckBoxVisibility(
    view: View,
    state: OrderState,
) {
    view.visibility = if (state is OrderState.Cart) View.VISIBLE else View.GONE
}
