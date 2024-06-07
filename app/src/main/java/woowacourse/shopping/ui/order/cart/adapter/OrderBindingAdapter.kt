package woowacourse.shopping.ui.order.cart.adapter

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.ui.state.OrderState
import woowacourse.shopping.ui.state.UiState

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
