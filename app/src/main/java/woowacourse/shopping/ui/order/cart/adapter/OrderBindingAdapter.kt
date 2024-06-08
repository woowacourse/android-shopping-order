package woowacourse.shopping.ui.order.cart.adapter

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.ui.state.OrderState
import woowacourse.shopping.ui.state.UiState

@BindingAdapter("app:isCartEmpty", "app:cartUiState", requireAll = true)
fun <T> setEmptyCartVisibility(
    textView: TextView,
    isCartEmpty: Boolean,
    cartUiState: UiState<T>,
) {
    textView.visibility =
        if (cartUiState is UiState.Success && isCartEmpty) View.VISIBLE else View.GONE
}

@BindingAdapter("app:isCartEmpty", "app:cartUiState", requireAll = true)
fun <T> setCartVisibility(
    recyclerView: RecyclerView,
    isCartEmpty: Boolean,
    cartUiState: UiState<T>,
) {
    recyclerView.visibility =
        if ((cartUiState is UiState.Success && !isCartEmpty) || cartUiState is UiState.Loading) View.VISIBLE else View.GONE
}

@BindingAdapter("app:allCheckBoxVisibility")
fun setAllCheckBoxVisibility(
    view: View,
    state: OrderState,
) {
    view.visibility = if (state is OrderState.Cart) View.VISIBLE else View.GONE
}
