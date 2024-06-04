package woowacourse.shopping.presentation.ui.cart.selection

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.presentation.ui.counter.CounterHandler

class SelectionViewHolder(private val binding: ItemCartBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        cartItem: CartItem,
        eventHandler: SelectionEventHandler,
        countHandler: CounterHandler,
    ) {
        binding.cartItem = cartItem
        binding.eventHandler = eventHandler
        binding.countHandler = countHandler
    }
}
