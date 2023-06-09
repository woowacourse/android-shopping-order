package woowacourse.shopping.ui.cart.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.ui.cart.uistate.CartItemUIState

class CartListAdapterDiffUtil : DiffUtil.ItemCallback<CartItemUIState>() {
    override fun areItemsTheSame(oldItem: CartItemUIState, newItem: CartItemUIState): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CartItemUIState, newItem: CartItemUIState): Boolean {
        return oldItem.productId == newItem.productId
    }
}
