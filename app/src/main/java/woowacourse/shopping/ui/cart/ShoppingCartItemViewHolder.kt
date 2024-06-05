package woowacourse.shopping.ui.cart

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderCartBinding
import woowacourse.shopping.ui.model.CartItem

class ShoppingCartItemViewHolder(
    private val binding: HolderCartBinding,
    private val shoppingCartItemListener: ShoppingCartItemListener

) : RecyclerView.ViewHolder(binding.root) {
    fun bind(cartItem: CartItem) {
        binding.cartItem = cartItem
        binding.listener = shoppingCartItemListener
    }
}
