package woowacourse.shopping.ui.cart

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.HolderCartBinding
import woowacourse.shopping.remote.CartItemDto
import woowacourse.shopping.ui.OnItemQuantityChangeListener
import woowacourse.shopping.ui.OnProductItemClickListener

class ShoppingCartItemViewHolder(
    private val binding: HolderCartBinding,
    private val onProductItemClickListener: OnProductItemClickListener,
    private val onItemChargeListener: OnItemQuantityChangeListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(cartItemDto: CartItemDto) {
        binding.cartItemDto = cartItemDto
        binding.onProductItemClickListener = onProductItemClickListener
        binding.onItemChargeListener = onItemChargeListener
    }
}
