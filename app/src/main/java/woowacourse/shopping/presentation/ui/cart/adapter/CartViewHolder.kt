package woowacourse.shopping.presentation.ui.cart.adapter

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.presentation.ui.cart.CartHandler
import woowacourse.shopping.presentation.ui.model.CartModel

class CartViewHolder(
    private val binding: ItemCartBinding,
    private val cartHandler: CartHandler,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CartModel) {
        binding.cartHandler = cartHandler
        binding.cartItem = item
    }
}
