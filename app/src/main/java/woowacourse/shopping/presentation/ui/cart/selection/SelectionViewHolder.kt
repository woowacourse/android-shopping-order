package woowacourse.shopping.presentation.ui.cart.selection

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.presentation.ui.cart.CartItemUiModel

class SelectionViewHolder(private val binding: ItemCartBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        cartItem: CartItemUiModel,
        eventHandler: CartItemSelectionEventHandler,
        countHandler: SelectionCountHandler,
    ) {
        binding.cartItem = cartItem
        binding.eventHandler = eventHandler
        binding.countHandler = countHandler
    }

    fun onQuantityChanged(cartItem: CartItemUiModel) {
        binding.cartItem = cartItem
    }

    fun onIsCheckedChanged(cartItem: CartItemUiModel) {
        binding.cartItem = cartItem
    }
}
