package woowacourse.shopping.ui.cart

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderCartBinding
import woowacourse.shopping.ui.OnItemQuantityChangeListener
import woowacourse.shopping.ui.OnProductItemClickListener
import woowacourse.shopping.ui.model.CartItem

class ShoppingCartItemViewHolder(
    private val binding: HolderCartBinding,
    private val onProductItemClickListener: OnProductItemClickListener,
    private val onItemChargeListener: OnItemQuantityChangeListener,
    private val onCartItemSelectedListener: OnCartItemSelectedListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(cartItem: CartItem) {
        binding.cartItem = cartItem
        binding.onProductItemClickListener = onProductItemClickListener
        binding.onItemChargeListener = onItemChargeListener
        binding.onCartItemSelectedListener = onCartItemSelectedListener
    }
}
