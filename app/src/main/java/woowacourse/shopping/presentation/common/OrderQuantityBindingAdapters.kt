package woowacourse.shopping.presentation.common

import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.R
import woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend.OrderCartsUiState

@BindingAdapter("orderQuantity")
fun TextView.setOrderQuantity(orderCartsUiState: OrderCartsUiState) {
    text =
        if (orderCartsUiState is OrderCartsUiState.Success) {
            context.getString(R.string.tv_order).format(orderCartsUiState.totalQuantity)
        } else {
            ""
        }
}
